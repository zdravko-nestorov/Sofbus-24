package bg.znestorov.sofbus24.db.databases;

import bg.znestorov.sofbus24.db.coordinates.StationCoordinates;
import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;
import bg.znestorov.sofbus24.db.entity.VehicleStation;
import bg.znestorov.sofbus24.db.entity.VehicleType;
import bg.znestorov.sofbus24.db.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class SQLiteJDBC {

  /**
   * List with stations which coordinates are updated by me. If a station number is included in this list means that the
   * station coordinates won't be updated with the ones from the "skgt_stations.xml" The varible doesn't need to
   * contains the METRO stations numbers - they are not reflected in the xml file.<br/>
   *
   * This Set has to be updated only if I decide to update the coordinates of an already inserted station (the newly
   * inserted are firstly updated by the script and after that I insert the coordinates of the rest manually - it means
   * that they don't exist in the xml file).
   */
  private static final Set<String> STATIONS_UPDATED_BY_ME = new HashSet<String>();

  static {
    /*
     * The stations are location in "Students City"
     */
    STATIONS_UPDATED_BY_ME.add("1396");
    STATIONS_UPDATED_BY_ME.add("1397");
    STATIONS_UPDATED_BY_ME.add("524");
    STATIONS_UPDATED_BY_ME.add("530");
    STATIONS_UPDATED_BY_ME.add("1691");
    STATIONS_UPDATED_BY_ME.add("1692");
    STATIONS_UPDATED_BY_ME.add("1610");
    STATIONS_UPDATED_BY_ME.add("1611");
    STATIONS_UPDATED_BY_ME.add("533");
    STATIONS_UPDATED_BY_ME.add("534");
    STATIONS_UPDATED_BY_ME.add("741");
    STATIONS_UPDATED_BY_ME.add("742");
  }

  private Logger logger;
  private ArrayList<Station> stationsList;
  private ArrayList<Vehicle> vehiclesList;
  private ArrayList<VehicleStation> vehicleStationsList;
  private Connection c = null;
  private Statement stmt = null;
  private long startTime;
  private long endTime;

  public SQLiteJDBC(Logger logger, ArrayList<Station> stationsList,
      ArrayList<Vehicle> vehiclesList,
      ArrayList<VehicleStation> vehicleStationsList) {

    this.logger = logger;
    this.stationsList = stationsList;
    this.vehiclesList = vehiclesList;
    this.vehicleStationsList = vehicleStationsList;
  }

  public void initStationsAndVehiclesTables() {

    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:database/html/sofbus24.db");
      c.setAutoCommit(false);
      logger.info("Opened sofbus24.db database successfully!\n");

      stmt = c.createStatement();

      startTime = Utils.getTime();
      initVehiclesTable();
      endTime = Utils.getTime();
      logger.info("The 'VEHI' table is initialized for "
          + ((endTime - startTime) / 1000) + " seconds...\n");

      startTime = Utils.getTime();
      initStationsTable();
      endTime = Utils.getTime();
      logger.info("The 'STAT' table is initialized for "
          + ((endTime - startTime) / 1000) + " seconds...\n");

      startTime = Utils.getTime();
      initVehicleStationsTable();
      endTime = Utils.getTime();
      logger.info("The 'VEST' table is initialized for "
          + ((endTime - startTime) / 1000) + " seconds...\n");

    } catch (Exception e) {
      logger.severe(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    } finally {
      try {
        stmt.close();
        c.commit();
        c.close();
      } catch (SQLException e) {
        logger.severe(e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private void initVehiclesTable() throws Exception {

    int totalVehicles = vehiclesList != null ? vehiclesList.size() : 0;
    int insertedVehicles = 0;

    /*
     * IMPORTANT: Delete the table of content only if the vehicles database
     * is not empty or if the database is not updated for a long time. In
     * case of consecutive actions - just update the database, do not create
     * a new one.
     */
    stmt.executeUpdate("DELETE FROM SOF_VEHI;");
    c.commit();

    for (Vehicle vehicle : vehiclesList) {

      String vehicleNumber = vehicle.getNumber();
      VehicleType vehicleType = vehicle.getType();
      String vehicleDirection = vehicle.getDirection();

      // Do not insert the BUS with number 21 (such vehicle does not
      // exist). Bus 5-TM is changing each weekend its directions
      if (!"5-��".equals(vehicleNumber) && !"21".equals(vehicleNumber)) {

        String sql = "SELECT * FROM SOF_VEHI WHERE VEHI_NUMBER = '%s' AND VEHI_TYPE = '%s';";

        // Change the number of the vehicle in case of BUS 22 (it has to
        // became 21-22)
        if (vehicleType == VehicleType.BUS
            && "22".equals(vehicleNumber)) {
          sql = String.format(sql, "21-22", VehicleType.BUS);
        } else {
          sql = String.format(sql, vehicle.getNumber(),
              vehicle.getType());
        }

        try {

          if (!stmt.executeQuery(sql).next()) {
            if (vehicleType == VehicleType.BUS
                && "22".equals(vehicleNumber)) {
              vehicleNumber = "21-22";
            }

            sql = "INSERT INTO SOF_VEHI (VEHI_NUMBER, VEHI_TYPE, VEHI_DIRECTION) "
                + "VALUES ('%s', '%s', '%s');";
            sql = String.format(sql, vehicleNumber, vehicleType,
                vehicleDirection);
            stmt.executeUpdate(sql);
            insertedVehicles++;
          }
        } catch (Exception e) {
        }
      }
    }

    logger.info("Total vehicles (from SKGT) = " + totalVehicles
        + ", Inserted vehicles (in DB) = " + insertedVehicles
        + ", Not found vehicles (in DB) = "
        + (totalVehicles - insertedVehicles));
  }

  private void initStationsTable() throws Exception {

    String sql = null;

    int totalStationsSKGT = stationsList != null ? stationsList.size() : 0;
    int totalStationsDB = 0;
    int insertedStations = 0;
    int updatedStations = 0;
    int updatedStationsCoordinates = 0;
    int deletedStations = 0;

    List<String> deletedStationsList = new ArrayList<String>();
    Set<String> skgtStationsNumbersList = new HashSet<String>();

    /*
     * IMPORTANT: A big part of the stations already exist in the DB. So if
     * a station already exists in the DB, just update the station name,
     * because from time to time, SKGT changes the names of station with a
     * fency new ones. In case the station is a new one, insert it - just
     * the number and the name, the coordinates will be populated lately
     * (automatically - in case the station exists in the
     * "skgt_stations.xml" file, or manually - in all other cases)
     */
    for (Station station : stationsList) {

      sql = "SELECT * FROM SOF_STAT WHERE STAT_NUMBER = %s;";
      sql = String.format(sql, station.getNumber());

      try {

        if (!stmt.executeQuery(sql).next()) {
          sql = "INSERT INTO SOF_STAT (STAT_NUMBER, STAT_NAME, STAT_LATITUDE, STAT_LONGITUDE, STAT_TYPE) "
              + "VALUES ('%s', '%s', '%s', '%s', '%s');";
          sql = String.format(sql,
              Integer.parseInt(station.getNumber()),
              station.getName(), station.getLatitude(),
              station.getLongitude(), station.getType());

          // Add the station number to the list
          skgtStationsNumbersList.add(station.getNumber());

          stmt.executeUpdate(sql);
          insertedStations++;
        } else {
          throw new Exception();
        }
      } catch (Exception e1) {
        sql = "UPDATE SOF_STAT SET STAT_NAME = '%s' WHERE STAT_NUMBER = %s;";
        sql = String.format(sql, station.getName(),
            station.getNumber());

        try {
          stmt.executeUpdate(sql);
          updatedStations++;
        } catch (Exception e2) {
          logger.severe("Problem with updating a station with number="
              + station.getNumber());
        }
      }
    }

    /*
     * IMPORTANT: In case we create the DB from a scratch (no records
     * inside), we should add some special stations (which are found in the
     * SKGT site in very rare cases or at very specific moments). To prevent
     * this, we just add them manually to the list of stations. If we are
     * making just an update (done in most of the cases), it is not a
     * problem to add this stations again - nothing will change
     */
    skgtStationsNumbersList.addAll(getAllExceptionStationNumbers());

    /*
     * IMPORTANT: Get all stations from the database and delete the not
     * needed. Do this only in case when a new version of Sofbus is
     * distributed. In case of consecutive actions - do not delete the not
     * used stations - make the param false (may be they didn't appear in
     * the search process)
     */
    boolean isDeleteNeeded = false;
    if (isDeleteNeeded) {
      try {
        sql = "SELECT * FROM SOF_STAT;";

        ResultSet stationsResultSet = stmt.executeQuery(sql);
        stationsList.clear();
        stationsList.addAll(getAllStationsFromDb(stationsResultSet));

        for (Station station : stationsList) {
          totalStationsDB++;
          String stationNumber = station.getNumber();

          if (!skgtStationsNumbersList.contains(stationNumber)) {
            sql = "DELETE FROM SOF_STAT WHERE STAT_NUMBER='%s';";
            sql = String.format(sql, stationNumber);

            try {
              stmt.executeUpdate(sql);
              deletedStations++;
              deletedStationsList.add(station.getName() + " ("
                  + station.getNumber() + ")");
            } catch (Exception e1) {
              logger.severe(
                  "Problem with deleting a station with number="
                      + station.getNumber());
            }
          }
        }
      } catch (Exception e1) {
        logger.severe(
            "Problem with retrieving all stations from the database.");
      }
    }

    /*
     * IMPORTANT: Update the coordinates of the stations in case they exist
     * in the xml file (skt_stations.xml). This is needed only when a new
     * version of the file is sent by Nikolay Alexandrov. Otherwise set to
     * FALSE to make the database initialization process faster
     */
    boolean isUpdateStationsCoordintesNeeded = true;
    if (isUpdateStationsCoordintesNeeded) {
      try {
        sql = "SELECT * FROM SOF_STAT;";

        ResultSet stationsResultSet = stmt.executeQuery(sql);
        stationsList.clear();
        stationsList.addAll(getAllStationsFromDb(stationsResultSet));

        StationCoordinates stationCoordinates = new StationCoordinates(
            logger);

        for (Station station : stationsList) {

          // Check if the station is already updated by me (see above)
          if (!isStationUpdatedByMe(station)) {
            station = stationCoordinates.getStationFromXml(station);

            // Check if the station exists in the xml file
            if (station != null) {
              sql = "UPDATE SOF_STAT SET STAT_LATITUDE = '%s', STAT_LONGITUDE = '%s' WHERE STAT_NUMBER = %s;";
              sql = String.format(sql, station.getLatitude(),
                  station.getLongitude(),
                  station.getNumber());

              try {
                stmt.executeUpdate(sql);
                updatedStationsCoordinates++;
              } catch (Exception e1) {
                logger.severe(
                    "Problem with updating the coordinates of a station with number="
                        + station.getNumber());
              }
            }
          }
        }

      } catch (Exception e1) {
        logger.severe(
            "Problem with retrieving all stations from the database.");
      }
    }

    logger.info("Total stations (from SKGT) = "
        + (totalStationsSKGT + getAllExceptionStationNumbers().size())
        + ", Total stations (from DB) = " + totalStationsDB
        + ", Inserted stations (in DB) = " + insertedStations
        + ", Updated stations (in DB) = " + updatedStations
        + ", Updated stations coordinates (in DB) = "
        + updatedStationsCoordinates + ", Deleted stations (from DB) = "
        + deletedStations + ", Not found stations (in DB) = "
        + (totalStationsSKGT - insertedStations - updatedStations));

    if (deletedStationsList.size() > 0) {
      logger.info("List with the deleted stations:\n"
          + deletedStationsList.toString());
    }
  }

  private boolean isStationUpdatedByMe(Station station) {
    return STATIONS_UPDATED_BY_ME
        .contains(Utils.removeLeadingZeros(station.getNumber()));
  }

  private List<Station> getAllStationsFromDb(ResultSet stationsResultSet)
      throws SQLException {
    List<Station> stationsList = new ArrayList<Station>();

    while (stationsResultSet.next()) {
      stationsList.add(new Station(VehicleType.BTT,
          Utils.formatNumberOfDigits(
              stationsResultSet.getString("STAT_NUMBER"), 4),
          stationsResultSet.getString("STAT_NAME"), "", -1));
    }

    return stationsList;
  }

  private Set<String> getAllExceptionStationNumbers() {
    Set<String> skgtExceptionStationsNumbersList = new HashSet<String>();

    skgtExceptionStationsNumbersList.add("0012");
    skgtExceptionStationsNumbersList.add("0013");
    skgtExceptionStationsNumbersList.add("0059");
    skgtExceptionStationsNumbersList.add("0060");
    skgtExceptionStationsNumbersList.add("0374");
    skgtExceptionStationsNumbersList.add("0375");
    skgtExceptionStationsNumbersList.add("0476");
    skgtExceptionStationsNumbersList.add("0477");
    skgtExceptionStationsNumbersList.add("0540");
    skgtExceptionStationsNumbersList.add("0541");
    skgtExceptionStationsNumbersList.add("0546");
    skgtExceptionStationsNumbersList.add("0547");
    skgtExceptionStationsNumbersList.add("0592");
    skgtExceptionStationsNumbersList.add("0593");
    skgtExceptionStationsNumbersList.add("0679");
    skgtExceptionStationsNumbersList.add("0680");
    skgtExceptionStationsNumbersList.add("0727");
    skgtExceptionStationsNumbersList.add("0728");
    skgtExceptionStationsNumbersList.add("0744");
    skgtExceptionStationsNumbersList.add("0745");
    skgtExceptionStationsNumbersList.add("0754");
    skgtExceptionStationsNumbersList.add("0755");
    skgtExceptionStationsNumbersList.add("0832");
    skgtExceptionStationsNumbersList.add("0833");
    skgtExceptionStationsNumbersList.add("0847");
    skgtExceptionStationsNumbersList.add("0848");
    skgtExceptionStationsNumbersList.add("0872");
    skgtExceptionStationsNumbersList.add("0873");
    skgtExceptionStationsNumbersList.add("1323");
    skgtExceptionStationsNumbersList.add("1635");
    skgtExceptionStationsNumbersList.add("1636");
    skgtExceptionStationsNumbersList.add("1709");
    skgtExceptionStationsNumbersList.add("1710");
    skgtExceptionStationsNumbersList.add("1837");
    skgtExceptionStationsNumbersList.add("1838");
    skgtExceptionStationsNumbersList.add("1915");
    skgtExceptionStationsNumbersList.add("1916");
    skgtExceptionStationsNumbersList.add("1987");
    skgtExceptionStationsNumbersList.add("1988");
    skgtExceptionStationsNumbersList.add("2574");
    skgtExceptionStationsNumbersList.add("2575");
    skgtExceptionStationsNumbersList.add("2576");
    skgtExceptionStationsNumbersList.add("2577");

    return skgtExceptionStationsNumbersList;
  }

  private void initVehicleStationsTable() throws Exception {

    int totalVehicleStations = vehicleStationsList != null
        ? vehicleStationsList.size() : 0;
    int insertedVehicleStations = 0;

    stmt.executeUpdate("DELETE FROM SOF_VEST;");
    c.commit();

    // Sort the VehicleStations list (via Vehicle ID)
    vehicleStationsList.sort(new Comparator<VehicleStation>() {

      @Override
      public int compare(VehicleStation vs1, VehicleStation vs2) {
        try {
          return Integer.valueOf(getVehicleId(vs1))
              .compareTo(Integer.valueOf(getVehicleId(vs2)));
        } catch (SQLException e) {
          return 0;
        }
      }
    });

    for (VehicleStation vehicleStation : vehicleStationsList) {

      String vehicleNumber = vehicleStation.getVehicleNumber();

      // Do not insert the BUS with number 21 (such vehicle does not
      // exist). Bus 5-TM is changing each weekend its directions
      if (!"5-��".equals(vehicleNumber) && !"21".equals(vehicleNumber)) {

        String sql = "INSERT INTO SOF_VEST (FK_VEST_VEHI_ID, FK_VEST_STAT_ID, VEST_DIRECTION) "
            + "VALUES (%s, %s, %s);";
        sql = String.format(sql, getVehicleId(vehicleStation),
            getStationId(vehicleStation),
            vehicleStation.getDirection());

        try {
          stmt.executeUpdate(sql);

          insertedVehicleStations++;
        } catch (Exception e) {
        }
      }
    }

    logger.info("Total vehicleStations (from SKGT) = "
        + totalVehicleStations + ", Inserted vehicleStations (in DB) = "
        + insertedVehicleStations
        + ", Duplicated vehicleStations (in DB) = "
        + (totalVehicleStations - insertedVehicleStations));
  }

  private int getVehicleId(VehicleStation vehicleStation)
      throws SQLException {

    int vehicleId = 0;

    String sql = "SELECT PK_VEHI_ID FROM SOF_VEHI WHERE VEHI_NUMBER = '%s' AND VEHI_TYPE = '%s';";
    VehicleType vehicleType = vehicleStation.getVehicleType();
    String vehicleNumber = vehicleStation.getVehicleNumber();

    // Change the number of the vehicle in case of BUS 22 (it has to
    // became 21-22)
    if (vehicleType == VehicleType.BUS && "22".equals(vehicleNumber)) {
      sql = String.format(sql, "21-22", vehicleType);
    } else {
      sql = String.format(sql, vehicleNumber, vehicleType);
    }

    ResultSet rs = stmt.executeQuery(sql);
    while (rs.next()) {
      vehicleId = rs.getInt("PK_VEHI_ID");
    }

    return vehicleId;
  }

  private int getStationId(VehicleStation vehicleStation)
      throws SQLException {

    int stationId = 0;

    String sql = "SELECT PK_STAT_ID FROM SOF_STAT WHERE STAT_NUMBER = '%s';";
    sql = String.format(sql, vehicleStation.getStationNumber());
    ResultSet rs = stmt.executeQuery(sql);

    while (rs.next()) {
      stationId = rs.getInt("PK_STAT_ID");
    }

    return stationId;
  }

}
