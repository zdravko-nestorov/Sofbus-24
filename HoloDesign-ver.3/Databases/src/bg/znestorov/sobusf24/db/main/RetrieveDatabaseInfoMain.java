package bg.znestorov.sobusf24.db.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.db.databases.SQLiteJDBC;
import bg.znestorov.sobusf24.db.information.InformationMain;
import bg.znestorov.sobusf24.db.utils.Constants;
import bg.znestorov.sobusf24.db.utils.LogFormatter;
import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;
import bg.znestorov.sofbus24.db.entity.VehicleType;
import bg.znestorov.sofbus24.db.vehicles.VehiclesNumbersMain;

public class RetrieveDatabaseInfoMain {

	private static Logger logger;
	private static FileHandler fh;

	public static void main(String[] args) {
		try {
			logger = Logger.getLogger("VEHICLES AND STATION INFORMATION");
			logger.setUseParentHandlers(false);
			fh = new FileHandler(Constants.DB_LOG_FILE);
			logger.addHandler(fh);
			LogFormatter formatter = new LogFormatter();
			fh.setFormatter(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("***RETRIEVE VEHICLES NUMBERS***\n");
		HashMap<Integer, ArrayList<String>> vehiclesMap = VehiclesNumbersMain
				.getVehiclesNumbers(logger);
		retrieveVehicles(vehiclesMap);
	}

	@SuppressWarnings("unchecked")
	private static void retrieveVehicles(
			HashMap<Integer, ArrayList<String>> vehiclesMap) {
		logger.info("***RETRIEVE VEHICLES AND STATIONS***\n");

		ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();
		Set<Station> stationsSet = new LinkedHashSet<Station>();

		Iterator<Entry<Integer, ArrayList<String>>> vehiclesIterator = vehiclesMap
				.entrySet().iterator();

		// Iterate over the vehicles map (for each type)
		while (vehiclesIterator.hasNext()) {
			Map.Entry<Integer, ArrayList<String>> vehiclesEntry = (Map.Entry<Integer, ArrayList<String>>) vehiclesIterator
					.next();
			String type = vehiclesEntry.getKey() + "";

			// Iterate each number for this type
			for (String number : vehiclesEntry.getValue()) {
				logger.info("Retrieving information for vehicle[Type=" + type
						+ ", Number=" + number + "]");

				HashMap<String, Object> info = InformationMain.getInformation(
						logger, type + "", number);
				if (info != null) {
					Vehicle vehicle = (Vehicle) info.get("vehicle");
					ArrayList<Station> stations = (ArrayList<Station>) info
							.get("stations");

					vehiclesList.add(vehicle);
					stationsSet.addAll(stations);
				}

			}
		}

		// Add the metro stations to the vehicles list
		addMetroVehicles(vehiclesList);

		// Sort the stations list
		ArrayList<Station> stationsList = new ArrayList<Station>(stationsSet);
		addMetroStations(stationsList);

		Comparator<Station> stationsComparator = new Comparator<Station>() {
			@Override
			public int compare(Station station1, Station station2) {
				long value1Long = Long.parseLong(station1.getNumber());
				long value2Long = Long.parseLong(station2.getNumber());

				return compare(value1Long, value2Long);
			}

			private int compare(long a, long b) {
				return a < b ? -1 : a > b ? 1 : 0;
			}
		};
		Collections.sort(stationsList, stationsComparator);

		// Update the databases
		SQLiteJDBC.createVehiclesDatabase(logger, vehiclesList);
		SQLiteJDBC.createStationsDatabase(logger, stationsList);
	}

	private static void addMetroVehicles(ArrayList<Vehicle> vehiclesList) {
		vehiclesList.add(new Vehicle(VehicleType.METRO1, "1033",
				"м.Джеймс Баучер-м.Обеля-м.Цариградско шосе"));
		vehiclesList.add(new Vehicle(VehicleType.METRO2, "1034",
				"м.Цариградско шосе-м.Обеля-м.Джеймс Баучер"));
	}

	private static void addMetroStations(ArrayList<Station> stationsList) {
		BufferedReader inputBufferedReader = null;
		try {
			inputBufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File("data/metro_stations.txt")),
					"UTF8"));

			while (inputBufferedReader.ready()) {
				stationsList.add(new Station(inputBufferedReader.readLine()));
			}
		} catch (Exception e) {
			logger.info("Problem with reading the file with metro stations...");
		} finally {
			if (inputBufferedReader != null) {
				try {
					inputBufferedReader.close();
				} catch (IOException e) {
					logger.info("Problem with closing the file with metro stations...");
				}
			}
		}
	}
}
