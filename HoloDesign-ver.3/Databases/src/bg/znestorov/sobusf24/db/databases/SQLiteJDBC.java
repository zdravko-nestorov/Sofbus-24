package bg.znestorov.sobusf24.db.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;

public class SQLiteJDBC {

	public static void createVehiclesDatabase(Logger logger,
			ArrayList<Vehicle> vehiclesList) {
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
			stmt.executeUpdate("DELETE FROM vehicles;");
			c.commit();

			for (Vehicle vehicle : vehiclesList) {
				String sql = "INSERT INTO vehicles (number, type, direction) "
						+ "VALUES ('%s', '%s', '%s');";
				sql = String.format(sql, vehicle.getNumber(),
						vehicle.getType(), vehicle.getDirection());
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

		logger.info("Total vehicles (from SKGT) = " + totalVehicles
				+ ", Inserted vehicles (in DB) = " + insertedVehicles
				+ ", Not found vehicles (in DB) = "
				+ (totalVehicles - insertedVehicles));
	}

	public static void createStationsDatabase(Logger logger,
			ArrayList<Station> stationsList) {
		String sql = null;
		Connection c = null;
		Statement stmt = null;

		int totalStations = 0;
		int insertedStations = 0;
		int updatedStations = 0;
		int deletedStations = 0;

		Set<String> stationsNumbersList = new HashSet<String>();

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:database/stations.db");
			c.setAutoCommit(false);
			logger.info("Opened stations database successfully");

			stmt = c.createStatement();
			/*
			 * IMPORTANT: DO NOT DELETE THE TABLE CONTENT
			 * stmt.executeUpdate("DELETE FROM stations;"); c.commit();
			 */

			// Insert or update the stations in the database
			for (Station station : stationsList) {
				sql = "INSERT INTO stations (number, name, latitude, longitude, type) "
						+ "VALUES ('%s', '%s', '%s', '%s', '%s');";
				sql = String.format(sql, station.getNumber(),
						station.getName(), station.getLatitude(),
						station.getLongitude(), station.getType());

				// Add the station number to the list
				stationsNumbersList.add(station.getNumber());

				try {
					totalStations++;
					stmt.executeUpdate(sql);
					insertedStations++;
				} catch (Exception e1) {
					sql = "UPDATE stations SET name = '%s' WHERE number='%s';";
					sql = String.format(sql, station.getName(),
							station.getNumber());

					try {
						stmt.executeUpdate(sql);
						updatedStations++;
					} catch (Exception e2) {
						logger.info("Problem with updating a station with number="
								+ station.getNumber());
					}
				}
			}

			// Get all stations from the database and delete the not needed
			try {
				sql = "SELECT * FROM stations;";
				stmt.executeQuery(sql);
				// TODO: Get all stations
				
				for (Station station : stationsList) {
					String stationNumber = station.getNumber();

					if (!stationsNumbersList.contains(stationNumber)) {
						sql = "DELETE FROM stations WHERE number='%s';";
						sql = String.format(sql, stationNumber);

						try {
							stmt.executeUpdate(sql);
							deletedStations++;
						} catch (Exception e2) {
							logger.info("Problem with deleting a station with number="
									+ station.getNumber());
						}
					}
				}
			} catch (Exception e2) {
				logger.info("Problem with retrieving all stations from the database.");
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		logger.info("Total stations (from SKGT) = " + totalStations
				+ ", Inserted stations (in DB) = " + insertedStations
				+ ", Updated stations (in DB) = " + updatedStations
				+ ", Deleted stations (from DB) = " + deletedStations
				+ ", Not found stations (in DB) = "
				+ (totalStations - insertedStations - updatedStations));
	}
}
