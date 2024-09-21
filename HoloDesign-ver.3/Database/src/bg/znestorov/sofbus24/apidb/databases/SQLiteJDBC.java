package bg.znestorov.sofbus24.apidb.databases;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logDuration;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logInfo;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logSevere;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logWarning;
import static bg.znestorov.sofbus24.apidb.utils.Constants.LINE_SEPARATOR;

import bg.znestorov.sofbus24.apidb.entity.DatabaseInfo;
import bg.znestorov.sofbus24.apidb.entity.MetroStationsInfo;
import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.StationType;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.entity.VehicleType;
import bg.znestorov.sofbus24.db.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLiteJDBC {

  private final Set<Station> stationSet;
  private final Set<Vehicle> vehicleSet;

  private Connection connection = null;
  private Statement stmt = null;

  public SQLiteJDBC(Map<String, Station> stationMap, Set<Vehicle> vehicleSet) {
    this.stationSet = new LinkedHashSet<>(stationMap.values());
    this.vehicleSet = vehicleSet;
  }

  public void initDatabase() {

    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:database/api/sofbus24.db");
      connection.setAutoCommit(false);
      logInfo("Opened sofbus24.db database successfully!\n");

      stmt = connection.createStatement();

      long startTime = Utils.getTime();
      initStationsTable();
      logDuration("The 'SOF_STAT' table is initialized for ", startTime);

      startTime = Utils.getTime();
      initVehiclesTable();
      logDuration("The 'SOF_VEHI' table is initialized for ", startTime);

      startTime = Utils.getTime();
      initVehicleStationsTable();
      logDuration("The 'SOF_VEST' table is initialized for ", startTime);

      startTime = Utils.getTime();
      initMetroStationsInfo();
      logDuration("The 'METRO STATIONS' are retrieved for ", startTime);

    } catch (Exception e) {
      logSevere(e.getClass().getName() + ": " + e.getMessage());
    } finally {
      try {
        stmt.close();
        connection.commit();
        connection.close();
      } catch (SQLException e) {
        logSevere(e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private void initStationsTable() {

    int totalStations = stationSet.size();

    AtomicInteger busStations = new AtomicInteger(0);
    AtomicInteger tramStations = new AtomicInteger(0);
    AtomicInteger subwayStations = new AtomicInteger(0);
    AtomicInteger trolleybusStations = new AtomicInteger(0);
    AtomicInteger nightbusStations = new AtomicInteger(0);
    AtomicInteger insertedStations = new AtomicInteger(0);

    stationSet.forEach(station -> {

      int stationSofbusNumber = station.getSofbusNumber();
      String stationSofbusName = station.getSofbusName();
      String stationSofbusLat = station.getSofbusLatitude();
      String stationSofbusLon = station.getSofbusLongitude();
      StationType stationSofbusType = station.getSofbusType();

      int stationSkgtId = station.getId();
      String stationSkgtTitle = station.getTitle();
      String stationSkgtName = station.getName();
      String stationSkgtCode = station.getCode();
      String stationSkgtPosition = String.join(",", station.getPosition());
      int stationSkgtType = station.getType();
      String stationSkgtExtId = station.getExtId();

      try {
        String sql =
            "INSERT INTO SOF_STAT (STAT_NUMBER, STAT_NAME, STAT_LATITUDE, STAT_LONGITUDE, STAT_TYPE, STAT_SKGT_ID, STAT_SKGT_TITLE, STAT_SKGT_NAME, STAT_SKGT_CODE, STAT_SKGT_POSITION, STAT_SKGT_TYPE, STAT_SKGT_EXT_ID)\n"
                + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
        sql = String.format(sql, stationSofbusNumber, stationSofbusName, stationSofbusLat, stationSofbusLon,
            stationSofbusType, stationSkgtId, stationSkgtTitle, stationSkgtName, stationSkgtCode, stationSkgtPosition,
            stationSkgtType, stationSkgtExtId);

        stmt.executeUpdate(sql);
        insertedStations.incrementAndGet();

        // Count the number of different type of stations
        switch (stationSkgtType) {
          case 1:
            busStations.incrementAndGet();
            break;
          case 2:
            tramStations.incrementAndGet();
            break;
          case 3:
            subwayStations.incrementAndGet();
            break;
          case 4:
            trolleybusStations.incrementAndGet();
            break;
          case 5:
            nightbusStations.incrementAndGet();
            break;
        }

      } catch (Exception e) {
        logWarning("There was a problem inserting station: " + station.getTitle());
      }
    });

    DatabaseInfo.getInstance()
        .appendStationsInfo(busStations, nightbusStations, trolleybusStations, tramStations, subwayStations,
            insertedStations);
    logInfo("Total stations (from SKGT) = " + totalStations
        + ", BUS stations (in DB) = " + busStations
        + ", NIGHT BUS stations (in DB) = " + nightbusStations
        + ", TROLLEYBUS stations (in DB) = " + trolleybusStations
        + ", TRAM stations (in DB) = " + tramStations
        + ", METRO stations (in DB) = " + subwayStations
        + ", Inserted stations (in DB) = " + insertedStations);
  }

  private void initVehiclesTable() {

    int totalVehicles = vehicleSet.size();

    AtomicInteger busVehicles = new AtomicInteger(0);
    AtomicInteger tramVehicles = new AtomicInteger(0);
    AtomicInteger subwayVehicles = new AtomicInteger(0);
    AtomicInteger trolleybusVehicles = new AtomicInteger(0);
    AtomicInteger nightbusVehicles = new AtomicInteger(0);
    AtomicInteger insertedVehicles = new AtomicInteger(0);

    vehicleSet.forEach(vehicle -> {

      String vehicleSofbusNumber = vehicle.getSofbusNumber();
      VehicleType vehicleSofbusType = vehicle.getSofbusType();
      String vehicleSofbusDirection = vehicle.getSofbusDirection();

      int vehicleSkgtLineId = vehicle.getLineId();
      String vehicleSkgtName = vehicle.getName();
      String vehicleSkgtExtId = vehicle.getExtId();
      int vehicleSkgtType = vehicle.getType();

      try {
        String sql =
            "INSERT INTO SOF_VEHI (VEHI_NUMBER, VEHI_TYPE, VEHI_DIRECTION, VEHI_SKGT_LINE_ID, VEHI_SKGT_NAME, VEHI_SKGT_EXT_ID, VEHI_SKGT_TYPE)\n"
                + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');";
        sql = String.format(sql, vehicleSofbusNumber, vehicleSofbusType, vehicleSofbusDirection, vehicleSkgtLineId,
            vehicleSkgtName, vehicleSkgtExtId, vehicleSkgtType);

        stmt.executeUpdate(sql);
        insertedVehicles.incrementAndGet();

        // Count the number of different type of stations
        switch (vehicleSkgtType) {
          case 1:
            busVehicles.incrementAndGet();
            break;
          case 2:
            tramVehicles.incrementAndGet();
            break;
          case 3:
            subwayVehicles.incrementAndGet();
            break;
          case 4:
            trolleybusVehicles.incrementAndGet();
            break;
          case 5:
            nightbusVehicles.incrementAndGet();
            break;
        }

      } catch (Exception e) {
        logWarning("There was a problem inserting vehicle: " + vehicle.getSofbusLabel());
      }
    });

    DatabaseInfo.getInstance()
        .appendVehiclesInfo(busVehicles, nightbusVehicles, trolleybusVehicles, tramVehicles, subwayVehicles,
            insertedVehicles);
    logInfo("Total vehicles (from SKGT) = " + totalVehicles
        + ", BUS vehicles (in DB) = " + busVehicles
        + ", NIGHT BUS vehicles (in DB) = " + nightbusVehicles
        + ", TROLLEYBUS vehicles (in DB) = " + trolleybusVehicles
        + ", TRAM vehicles (in DB) = " + tramVehicles
        + ", METRO vehicles (in DB) = " + subwayVehicles
        + ", Inserted vehicles (in DB) = " + insertedVehicles);
  }

  private void initVehicleStationsTable() {

    int totalVehicleStations = getTotalVehicleStations();
    AtomicInteger insertedVehicleStations = new AtomicInteger(0);

    vehicleSet.forEach(vehicle -> {

      int vehicleId = getVehicleId(vehicle);
      vehicle.getRoutes().forEach((vehicleDirection, stations) -> stations.forEach(station -> {

        try {
          String sql = "INSERT INTO SOF_VEST (FK_VEST_VEHI_ID, FK_VEST_STAT_ID, VEST_DIRECTION) "
              + "VALUES (%s, %s, %s);";
          sql = String.format(sql, vehicleId, getStationId(station), vehicleDirection);

          stmt.executeUpdate(sql);
          insertedVehicleStations.incrementAndGet();
        } catch (Exception e) {
          logWarning("There was a problem inserting VehicleStation: "
              + vehicle.getSofbusLabel() + ", " + station.getTitle());
        }
      }));
    });

    DatabaseInfo.getInstance().appendVehiclesStationsInfo(insertedVehicleStations);
    logInfo("Total VehiclesStations (from SKGT) = " + totalVehicleStations
        + ", Inserted VehiclesStations (in DB) = " + insertedVehicleStations);
  }

  private int getTotalVehicleStations() {

    AtomicInteger totalVehicleStations = new AtomicInteger(0);
    vehicleSet.forEach(vehicle -> vehicle.getRoutes().values().forEach(stations ->
        totalVehicleStations.addAndGet(stations.size())
    ));

    return totalVehicleStations.intValue();
  }

  private int getStationId(Station station) {

    int stationId = 0;

    try {
      String sql = "SELECT PK_STAT_ID FROM SOF_STAT WHERE STAT_NUMBER = '%s';";
      sql = String.format(sql, station.getSofbusNumber());
      ResultSet rs = stmt.executeQuery(sql);

      if (rs.next()) {
        stationId = rs.getInt("PK_STAT_ID");
      }
    } catch (SQLException sqle) {
      logWarning("There was a problem finding station: " + station.getTitle());
    }

    return stationId;
  }

  private int getVehicleId(Vehicle vehicle) {

    int vehicleId = 0;

    try {
      String sql = "SELECT PK_VEHI_ID FROM SOF_VEHI WHERE VEHI_NUMBER = '%s' AND VEHI_TYPE = '%s';";
      sql = String.format(sql, vehicle.getSofbusNumber(), vehicle.getSofbusType());
      ResultSet rs = stmt.executeQuery(sql);

      if (rs.next()) {
        vehicleId = rs.getInt("PK_VEHI_ID");
      }
    } catch (SQLException sqle) {
      logWarning("There was a problem finding vehicle: " + vehicle.getSofbusLabel());
    }

    return vehicleId;
  }

  private void initMetroStationsInfo() {

    StringBuilder metroStationsProps = new StringBuilder();

    try {
      String sql =
          "SELECT STAT_NUMBER || '=' || STAT_LATITUDE || ',' || STAT_LONGITUDE FROM SOF_STAT WHERE STAT_TYPE LIKE 'METRO%';";
      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        metroStationsProps.append(rs.getString(1)).append(LINE_SEPARATOR);
      }

    } catch (SQLException sqle) {
      logWarning("There was a problem finding the metro stations coordinates...");
    }

    MetroStationsInfo.getInstance().appendMetroStationsInfo(metroStationsProps.toString().trim());
  }

}
