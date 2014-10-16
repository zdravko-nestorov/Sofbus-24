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
import bg.znestorov.sofbus24.db.entity.VehicleType;

public class SQLiteJDBC {

	public static void createVehiclesDatabase(Logger logger, ArrayList<Vehicle> vehiclesList) {
		Connection c = null;
		Statement stmt = null;

		int totalVehicles = 0;
		int insertedVehicles = 0;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:database/vehicles.db");
			c.setAutoCommit(false);
			logger.info("Opened vehicles database successfully");

			stmt = c.createStatement();
			/*
			 * IMPORTANT: Delete the table of content only if the vehicles
			 * database is not empty or if the database is not updated for a
			 * long time. In case of consecutive actions - just update the
			 * database, do not create a new one.
			 * 
			 * stmt.executeUpdate("DELETE FROM vehicles;"); c.commit();
			 */

			for (Vehicle vehicle : vehiclesList) {
				String sql = "INSERT INTO vehicles (number, type, direction) " + "VALUES ('%s', '%s', '%s');";
				sql = String.format(sql, vehicle.getNumber(), vehicle.getType(), vehicle.getDirection());
				try {
					totalVehicles++;
					stmt.executeUpdate(sql);
					insertedVehicles++;
				} catch (Exception e) {
				}
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		logger.info("Total vehicles (from SKGT) = " + totalVehicles + ", Inserted vehicles (in DB) = " + insertedVehicles + ", Not found vehicles (in DB) = "
				+ (totalVehicles - insertedVehicles));
	}

	public static void createStationsDatabase(Logger logger, ArrayList<Station> stationsList) {
		String sql = null;
		Connection c = null;
		Statement stmt = null;

		int totalStationsSKGT = 0;
		int totalStationsDB = 0;
		int insertedStations = 0;
		int updatedStations = 0;
		int deletedStations = 0;

		List<String> deletedStationsList = new ArrayList<String>();
		Set<String> skgtStationsNumbersList = new HashSet<String>();

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:database/stations.db");
			c.setAutoCommit(false);
			logger.info("Opened stations database successfully");

			stmt = c.createStatement();

			// Insert or update the stations in the database
			for (Station station : stationsList) {
				sql = "INSERT INTO stations (number, name, latitude, longitude, type) " + "VALUES ('%s', '%s', '%s', '%s', '%s');";
				sql = String.format(sql, station.getNumber(), station.getName(), station.getLatitude(), station.getLongitude(), station.getType());

				// Add the station number to the list
				skgtStationsNumbersList.add(station.getNumber());

				try {
					totalStationsSKGT++;
					stmt.executeUpdate(sql);
					insertedStations++;
				} catch (Exception e1) {
					sql = "UPDATE stations SET name = '%s' WHERE number='%s';";
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
			 * distributed. In case of consecutive actions - do not delete the
			 * not used stations - make the param false (may be they didn't
			 * appear in the search process)
			 */
			boolean isDeleteNeeded = false;
			if (isDeleteNeeded) {
				try {
					sql = "SELECT * FROM stations;";

					ResultSet stationsResultSet = stmt.executeQuery(sql);
					stationsList.clear();
					stationsList.addAll(getAllStationsFromDb(stationsResultSet));

					for (Station station : stationsList) {
						totalStationsDB++;
						String stationNumber = station.getNumber();

						if (!skgtStationsNumbersList.contains(stationNumber)) {
							sql = "DELETE FROM stations WHERE number='%s';";
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

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
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
			stationsList.add(new Station(VehicleType.BTT, Utils.formatNumberOfDigits(stationsResultSet.getString("number"), 4), stationsResultSet
					.getString("name")));
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
}