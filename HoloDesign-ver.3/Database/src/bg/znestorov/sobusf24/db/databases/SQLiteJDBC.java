package bg.znestorov.sobusf24.db.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.db.utils.Utils;
import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;
import bg.znestorov.sofbus24.db.entity.VehicleStation;
import bg.znestorov.sofbus24.db.entity.VehicleType;

public class SQLiteJDBC {

	private Logger logger;
	private ArrayList<Station> stationsList;
	private ArrayList<Vehicle> vehiclesList;
	private ArrayList<VehicleStation> vehicleStationsList;

	private Connection c = null;
	private Statement stmt = null;

	private long startTime;
	private long endTime;

	public SQLiteJDBC(Logger logger, ArrayList<Station> stationsList, ArrayList<Vehicle> vehiclesList, ArrayList<VehicleStation> vehicleStationsList) {
		this.logger = logger;
		this.stationsList = stationsList;
		this.vehiclesList = vehiclesList;
		this.vehicleStationsList = vehicleStationsList;
	}

	public void initStationsAndVehiclesTables() {

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:database/sofbus24.db");
			c.setAutoCommit(false);
			logger.info("Opened sofbus24.db database successfully!\n");

			stmt = c.createStatement();

			startTime = Utils.getTime();
			initVehiclesTable();
			endTime = Utils.getTime();
			logger.info("The 'VEHI' table is initialized for " + ((endTime - startTime) / 1000) + " seconds...\n");

			startTime = Utils.getTime();
			initStationsTable();
			endTime = Utils.getTime();
			logger.info("The 'STAT' table is initialized for " + ((endTime - startTime) / 1000) + " seconds...\n");

			startTime = Utils.getTime();
			initVehicleStationsTable();
			endTime = Utils.getTime();
			logger.info("The 'VEST' table is initialized for " + ((endTime - startTime) / 1000) + " seconds...\n");

		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.commit();
				c.close();
			} catch (SQLException e) {
				logger.info(e.getClass().getName() + ": " + e.getMessage());
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
			// exist). Busses 5-TM and 8-TM are in the site, but not real
			if (!"5-“Ã".equals(vehicleNumber) && !"8-“Ã".equals(vehicleNumber) && !"21".equals(vehicleNumber)) {
				String sql = "SELECT * FROM SOF_VEHI WHERE VEHI_NUMBER = '%s' AND VEHI_TYPE = '%s';";

				// Change the number of the vehicle in case of BUS 22 (it has to
				// became 21-22)
				if (vehicleType == VehicleType.BUS && "22".equals(vehicleNumber)) {
					sql = String.format(sql, "21-22", VehicleType.BUS);
				} else {
					sql = String.format(sql, vehicle.getNumber(), vehicle.getType());
				}

				try {

					if (!stmt.executeQuery(sql).next()) {
						if (vehicleType == VehicleType.BUS && "22".equals(vehicleNumber)) {
							vehicleNumber = "21-22";
						}

						sql = "INSERT INTO SOF_VEHI (VEHI_NUMBER, VEHI_TYPE, VEHI_DIRECTION) " + "VALUES ('%s', '%s', '%s');";
						sql = String.format(sql, vehicleNumber, vehicleType, vehicleDirection);
						stmt.executeUpdate(sql);
						insertedVehicles++;
					}
				} catch (Exception e) {
				}
			}
		}

		logger.info("Total vehicles (from SKGT) = " + totalVehicles + ", Inserted vehicles (in DB) = " + insertedVehicles + ", Not found vehicles (in DB) = "
				+ (totalVehicles - insertedVehicles));
	}

	private void initStationsTable() throws Exception {

		String sql = null;

		int totalStationsSKGT = stationsList != null ? stationsList.size() : 0;
		int totalStationsDB = 0;
		int insertedStations = 0;
		int updatedStations = 0;
		int deletedStations = 0;

		List<String> deletedStationsList = new ArrayList<String>();
		Set<String> skgtStationsNumbersList = new HashSet<String>();

		// Insert or update the stations in the database
		for (Station station : stationsList) {

			sql = "SELECT * FROM SOF_STAT WHERE STAT_NUMBER = %s;";
			sql = String.format(sql, station.getNumber());

			try {

				if (!stmt.executeQuery(sql).next()) {
					sql = "INSERT INTO SOF_STAT (STAT_NUMBER, STAT_NAME, STAT_LATITUDE, STAT_LONGITUDE, STAT_TYPE) " + "VALUES ('%s', '%s', '%s', '%s', '%s');";

					// Fix the bug with the insertion of the STAT_TYPE column in
					// the SOF_STAT table (if it is even - should be METRO1,
					// otherwise - METRO2)
					Integer stationNumber = Integer.parseInt(station.getNumber());
					VehicleType vehicleType = station.getType();

					if (vehicleType == VehicleType.METRO1 || vehicleType == VehicleType.METRO2) {
						if (stationNumber % 2 == 1) {
							vehicleType = VehicleType.METRO1;
						} else {
							vehicleType = VehicleType.METRO2;
						}
					}

					sql = String.format(sql, stationNumber, station.getName(), station.getLatitude(), station.getLongitude(), vehicleType);

					// Add the station number to the list
					skgtStationsNumbersList.add(station.getNumber());

					stmt.executeUpdate(sql);
					insertedStations++;
				} else {
					throw new Exception();
				}
			} catch (Exception e1) {
				sql = "UPDATE SOF_STAT SET STAT_NAME = '%s' WHERE STAT_NUMBER = %s;";
				sql = String.format(sql, station.getName(), station.getNumber());

				try {
					stmt.executeUpdate(sql);
					updatedStations++;
				} catch (Exception e2) {
					logger.info("Problem with updating a station with number=" + station.getNumber());
				}
			}
		}

		// Add all exceptions to the list
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
							deletedStationsList.add(station.getName() + " (" + station.getNumber() + ")");
						} catch (Exception e2) {
							logger.info("Problem with deleting a station with number=" + station.getNumber());
						}
					}
				}
			} catch (Exception e2) {
				logger.info("Problem with retrieving all stations from the database.");
			}
		}

		logger.info("Total stations (from SKGT) = " + (totalStationsSKGT + getAllExceptionStationNumbers().size()) + ", Total stations (from DB) = "
				+ totalStationsDB + ", Inserted stations (in DB) = " + insertedStations + ", Updated stations (in DB) = " + updatedStations
				+ ", Deleted stations (from DB) = " + deletedStations + ", Not found stations (in DB) = "
				+ (totalStationsSKGT - insertedStations - updatedStations));

		if (deletedStationsList.size() > 0) {
			logger.info("List with the deleted stations:\n" + deletedStationsList.toString());
		}
	}

	private static List<Station> getAllStationsFromDb(ResultSet stationsResultSet) throws SQLException {
		List<Station> stationsList = new ArrayList<Station>();

		while (stationsResultSet.next()) {
			stationsList.add(new Station(VehicleType.BTT, Utils.formatNumberOfDigits(stationsResultSet.getString("STAT_NUMBER"), 4), stationsResultSet
					.getString("STAT_NAME"), "", -1));
		}

		return stationsList;
	}

	private static Set<String> getAllExceptionStationNumbers() {
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

		int totalVehicleStations = vehicleStationsList != null ? vehicleStationsList.size() : 0;
		int insertedVehicleStations = 0;

		stmt.executeUpdate("DELETE FROM SOF_VEST;");
		c.commit();

		for (VehicleStation vehicleStation : vehicleStationsList) {

			/**
			 * Code used to fill the DB with all data from the site - NOT NEEDED
			 * ANYMORE <code>
			String sql = "INSERT INTO SOF_VEST (FK_VEST_VEHI_ID, FK_VEST_STAT_ID, VEST_DIRECTION, VEST_STOP, VEST_LID, VEST_VT, VEST_RID) "
					+ "VALUES (%s, %s, %s, %s, %s, %s, %s);";
			sql = String.format(sql, getVehicleId(vehicleStation), getStationId(vehicleStation), vehicleStation.getDirection(), vehicleStation.getStop(),
					vehicleStation.getLid(), vehicleStation.getVt(), vehicleStation.getRid());
			</code>
			 */

			String sql = "INSERT INTO SOF_VEST (FK_VEST_VEHI_ID, FK_VEST_STAT_ID, VEST_DIRECTION) " + "VALUES (%s, %s, %s);";
			sql = String.format(sql, getVehicleId(vehicleStation), getStationId(vehicleStation), vehicleStation.getDirection());

			try {
				stmt.executeUpdate(sql);

				insertedVehicleStations++;
			} catch (Exception e) {
			}
		}

		logger.info("Total vehicleStations (from SKGT) = " + totalVehicleStations + ", Inserted vehicleStations (in DB) = " + insertedVehicleStations
				+ ", Duplicated vehicleStations (in DB) = " + (totalVehicleStations - insertedVehicleStations));
	}

	private int getVehicleId(VehicleStation vehicleStation) throws SQLException {

		int vehicleId = 0;

		String sql = "SELECT PK_VEHI_ID FROM SOF_VEHI WHERE VEHI_NUMBER = '%s' AND VEHI_TYPE = '%s';";
		sql = String.format(sql, vehicleStation.getVehicleNumber(), vehicleStation.getVehicleType());
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			vehicleId = rs.getInt("PK_VEHI_ID");
		}

		return vehicleId;
	}

	private int getStationId(VehicleStation vehicleStation) throws SQLException {

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