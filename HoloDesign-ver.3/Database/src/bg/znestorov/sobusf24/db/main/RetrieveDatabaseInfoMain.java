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
import bg.znestorov.sofbus24.db.entity.VehicleStation;
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
		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();

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
						logger, type, number);
				if (info != null) {
					Vehicle vehicle = (Vehicle) info.get("vehicle");
					ArrayList<Station> stations = (ArrayList<Station>) info
							.get("stations");
					ArrayList<VehicleStation> vehicleStations = (ArrayList<VehicleStation>) info
							.get("vehice_stations");

					vehiclesList.add(vehicle);
					stationsSet.addAll(stations);
					vehicleStationsList.addAll(vehicleStations);
				}

			}
		}

		// Add the metro stations to the vehicles list
		addMetroVehicles(vehiclesList);

		// Sort the stations list
		ArrayList<Station> stationsList = new ArrayList<Station>(stationsSet);
		ArrayList<Station> metroStations = getMetroStations();
		addMetroStations(stationsList, metroStations);

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

		// Add the metro vehicle stations
		addMetroVehiclesStations(vehicleStationsList, metroStations);

		// Update the databases
		SQLiteJDBC sqLiteJDBC = new SQLiteJDBC(logger, stationsList,
				vehiclesList, vehicleStationsList);
		sqLiteJDBC.initStationsAndVehiclesTables();
	}

	private static void addMetroVehicles(ArrayList<Vehicle> vehiclesList) {
		vehiclesList.add(new Vehicle(VehicleType.METRO1, "1033",
				"�.������ ������-�.�����-�.����������� ����"));
		vehiclesList.add(new Vehicle(VehicleType.METRO2, "1034",
				"�.����������� ����-�.�����-�.������ ������"));
	}

	private static ArrayList<Station> getMetroStations() {

		ArrayList<Station> metroStations = new ArrayList<Station>();
		BufferedReader inputBufferedReader = null;
		try {
			inputBufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File("data/metro_stations.txt")),
					"UTF8"));

			while (inputBufferedReader.ready()) {
				Station station = new Station(inputBufferedReader.readLine());
				metroStations.add(station);
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

		return metroStations;
	}

	private static void addMetroStations(ArrayList<Station> stationsList,
			ArrayList<Station> metroStations) {
		stationsList.addAll(metroStations);
	}

	private static void addMetroVehiclesStations(
			ArrayList<VehicleStation> vehicleStationsList,
			ArrayList<Station> metroStations) {

		for (int i = 0; i < metroStations.size(); i++) {
			Station metroStation = metroStations.get(i);
			VehicleType metroType = metroStation.getType();
			String metroNumber = metroType == VehicleType.METRO1 ? "1033"
					: "1034";

			vehicleStationsList.add(new VehicleStation(metroType, metroNumber,
					metroStation.getNumber(), "-1", "-1", "-1", metroStation
							.getNumber()));
		}

	}
}