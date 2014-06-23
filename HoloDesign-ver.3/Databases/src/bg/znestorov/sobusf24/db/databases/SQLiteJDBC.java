package bg.znestorov.sobusf24.db.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
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
			/*
			 * IMPORTANT: DO NOT DELETE THE TABLE CONTENT
			 * stmt.executeUpdate("DELETE FROM vehicles;"); c.commit();
			 */

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

		logger.info("Total vehicles = " + totalVehicles
				+ ", Inserted vehicles = " + insertedVehicles
				+ ", Not found vehicles = "
				+ (totalVehicles - insertedVehicles));
	}

	public static void createStationsDatabase(Logger logger,
			ArrayList<Station> stationsList) {
		Connection c = null;
		Statement stmt = null;

		int totalStations = 0;
		int insertedStations = 0;
		int updatedStations = 0;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:database/stations.db");
			c.setAutoCommit(false);
			logger.info("Opened vehicles database successfully");

			stmt = c.createStatement();
			/*
			 * IMPORTANT: DO NOT DELETE THE TABLE CONTENT
			 * stmt.executeUpdate("DELETE FROM vehicles;"); c.commit();
			 */

			for (Station station : stationsList) {
				String sql = "INSERT INTO stations (number, name, latitude, longitude, type) "
						+ "VALUES ('%s', '%s', '%s', '%s', '%s');";
				sql = String.format(sql, station.getNumber(),
						station.getName(), station.getLatitude(),
						station.getLongitude(), station.getType());
				try {
					totalStations++;
					stmt.executeUpdate(sql);
					insertedStations++;
				} catch (Exception e1) {
					sql = "UPDATE stations set name = '%s' where number='%s';";
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

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		logger.info("Total stations = " + totalStations
				+ ", Inserted stations = " + insertedStations
				+ ", Updated stations = " + updatedStations
				+ ", Not found stations = "
				+ (totalStations - insertedStations - updatedStations));
	}
}
