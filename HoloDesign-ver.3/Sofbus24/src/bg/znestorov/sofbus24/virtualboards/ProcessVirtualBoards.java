package bg.znestorov.sofbus24.virtualboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ProcessVirtualBoards {

	private Activity context;
	private StationsDataSource stationsDatasource;
	private String htmlResult;

	public ProcessVirtualBoards(Activity context, String htmlResult) {
		this.context = context;
		this.stationsDatasource = new StationsDataSource(context);
		this.htmlResult = htmlResult;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public StationsDataSource getStationsDatasource() {
		return stationsDatasource;
	}

	public void setStationsDatasource(StationsDataSource stationsDatasource) {
		this.stationsDatasource = stationsDatasource;
	}

	public String getHtmlResult() {
		return htmlResult;
	}

	public void setHtmlResult(String htmlResult) {
		this.htmlResult = htmlResult;
	}

	/**
	 * Get the station info from the html and format it in a
	 * VirtualBoardsStation object
	 * 
	 * @return a VirtualBoardsStation object, containing all information about
	 *         the station
	 */
	public VirtualBoardsStation getVBSingleStationFromHtml() {
		VirtualBoardsStation vbStation = new VirtualBoardsStation(
				getStationFromHtml(), getSkgtTimeFromHtml(),
				getVehiclesListFromHtml());

		return vbStation;
	}

	/**
	 * Get all information about the station from the html result (name, number
	 * and coordinates)
	 * 
	 * @return the station with all information from the skgt site
	 */
	private Station getStationFromHtml() {
		Station station = new Station();

		Pattern pattern = Pattern.compile(Constants.VB_REGEX_STATION_INFO);
		Matcher matcher = pattern.matcher(htmlResult);

		if (matcher.find()) {
			// Get the station name
			String stationName = matcher.group(1);
			stationName = stationName.trim();

			// Get the station number
			String stationNumber = matcher.group(2);
			stationNumber = Utils.getOnlyDigits(stationNumber);

			// Get lat and lon of the station
			stationsDatasource.open();
			Station dbStation = stationsDatasource.getStation(stationNumber);
			String stationLat = "";
			String stationLon = "";

			if (dbStation != null) {
				stationLat = dbStation.getLat();
				stationLon = dbStation.getLon();
			}
			stationsDatasource.close();

			// Set the station all fields
			station = new Station(stationNumber, stationName, stationLat,
					stationLon, VehicleType.BTT, "1");
		}

		return station;
	}

	/**
	 * Get time of the skgt site (when the information is extracted according to
	 * their system)
	 * 
	 * @return the skgt time
	 */
	private String getSkgtTimeFromHtml() {
		String skgtTime = "";

		Pattern pattern = Pattern.compile(Constants.VB_REGEX_SKGT_TIME);
		Matcher matcher = pattern.matcher(htmlResult);

		if (matcher.find()) {
			skgtTime = matcher.group(1);
			skgtTime = skgtTime.trim();
			skgtTime = skgtTime.replaceAll(" ", ", ");
		}

		return skgtTime;
	}

	/**
	 * Get a list with all vehicles, passing through this station as well as
	 * their times of arrival
	 * 
	 * @return a list with all vehicles through this station
	 */
	private ArrayList<Vehicle> getVehiclesListFromHtml() {
		ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();

		String[] vehiclesPartsHtml = htmlResult
				.split(Constants.VB_REGEX_VEHICLE_PARTS);

		for (int i = 0; i < vehiclesPartsHtml.length; i++) {
			VehicleType vehicleType = getVehicleType(vehiclesPartsHtml[i]);

			// Used to order the vehicles (BUS, TROLLEY, TRAM)
			switch (vehicleType) {
			case BUS:
				vehiclesList.addAll(
						0,
						getVehiclesByTypeFromHtml(vehicleType,
								vehiclesPartsHtml[i]));
				break;
			case TRAM:
				vehiclesList.addAll(getVehiclesByTypeFromHtml(vehicleType,
						vehiclesPartsHtml[i]));
				break;
			default:
				if (vehiclesList.isEmpty()) {
					vehiclesList.addAll(getVehiclesByTypeFromHtml(vehicleType,
							vehiclesPartsHtml[i]));
				} else {
					vehiclesList.addAll(
							1,
							getVehiclesByTypeFromHtml(vehicleType,
									vehiclesPartsHtml[i]));
				}
				break;
			}
		}

		return vehiclesList;
	}

	/**
	 * Get a list with all vehicles for the corresponding type
	 * 
	 * @param vehicleType
	 *            the type of the current vehicle
	 * @param vehiclesPartHtml
	 *            the part of the html code (representing one vehicle type)
	 * @return a list with all information about the passing vehicles through
	 *         this station
	 */
	private LinkedList<Vehicle> getVehiclesByTypeFromHtml(
			VehicleType vehicleType, String vehiclesPartHtml) {
		LinkedList<Vehicle> vehiclesList = new LinkedList<Vehicle>();

		Pattern pattern = Pattern.compile(Constants.VB_REGEX_VEHICLE_INFO);
		Matcher matcher = pattern.matcher(vehiclesPartHtml);

		while (matcher.find()) {
			// Get and format the vehicle number
			String vehicleNumber = matcher.group(1);
			vehicleNumber = Utils.removeSpaces(vehicleNumber);

			// Get and format the vehicle times of arrival
			String vehicleTimes = matcher.group(2);
			vehicleTimes = Utils.removeSpaces(vehicleTimes);
			ArrayList<String> arrivalTimes = new ArrayList<String>(
					Arrays.asList(vehicleTimes.split(",")));

			// Get and format the vehicle direction
			String vehicleDirection = matcher.group(3);
			vehicleDirection = Utils.formatDirectionName(vehicleDirection);

			// Create the vehicle and add it to the list
			Vehicle vehicle = new Vehicle(vehicleNumber, vehicleType,
					vehicleDirection, arrivalTimes);
			vehiclesList.add(vehicle);
		}

		// Sort the list via vehicle number
		Collections.sort(vehiclesList, new Comparator<Vehicle>() {
			@Override
			public int compare(Vehicle vehicle1, Vehicle vehicle2) {
				int vehicle1Number = Integer.parseInt(Utils
						.getOnlyDigits(vehicle1.getNumber()));
				int vehicle2Number = Integer.parseInt(Utils
						.getOnlyDigits(vehicle2.getNumber()));

				return vehicle1Number < vehicle2Number ? -1
						: vehicle1Number > vehicle2Number ? 1 : 0;
			}
		});

		return vehiclesList;
	}

	/**
	 * Get the vehicle type according to its name (Автобус, Тролейбус or
	 * Трамвай)
	 * 
	 * @param vehiclesPartHtml
	 *            the part of the html code (representing one vehicle type)
	 * @return the vehicle type
	 */
	private VehicleType getVehicleType(String vehiclesPartHtml) {
		VehicleType vehicleType;
		String vehicleName = "";

		Pattern pattern = Pattern.compile(Constants.VB_REGEX_VEHICLE_TYPE);
		Matcher matcher = pattern.matcher(vehiclesPartHtml);

		if (matcher.find()) {
			vehicleName = matcher.group(1);
			vehicleName = vehicleName.trim();
			vehicleName = vehicleName.toUpperCase();
		}

		if (vehicleName.contains(Constants.VB_VEHICLE_TYPE_BUS)) {
			vehicleType = VehicleType.BUS;
		} else if (vehicleName.contains(Constants.VB_VEHICLE_TYPE_TROLLEY)) {
			vehicleType = VehicleType.TROLLEY;
		} else {
			vehicleType = VehicleType.TRAM;
		}

		return vehicleType;
	}

	/**
	 * Get all stations from the html result and add it to an array list
	 * 
	 * @return a list with all stations from the skgt site
	 */
	public HashMap<String, Station> getMultipleStationsFromHtml() {
		HashMap<String, Station> stationsMap = new LinkedHashMap<String, Station>();

		Pattern pattern = Pattern
				.compile(Constants.VB_REGEX_MULTIPLE_STATION_INFO);
		Matcher matcher = pattern.matcher(htmlResult);

		while (matcher.find()) {
			// Get the station number
			String stationNumber = matcher.group(1);
			stationNumber = Utils.getOnlyDigits(stationNumber);

			// Get the station name
			String stationName = matcher.group(3);
			stationName = stationName.trim();

			// Get lat and lon of the station
			stationsDatasource.open();
			Station dbStation = stationsDatasource.getStation(stationNumber);
			String stationLat = "";
			String stationLon = "";

			if (dbStation != null) {
				stationLat = dbStation.getLat();
				stationLon = dbStation.getLon();
			}
			stationsDatasource.close();

			// Get the station custom field ('o' hidden variable)
			String stationCustomField = matcher.group(2);
			stationCustomField = Utils.getOnlyDigits(stationCustomField);

			// Create the station and add it to the list
			Station station = new Station(stationNumber, stationName,
					stationLat, stationLon, VehicleType.BTT, stationCustomField);
			stationsMap.put(stationNumber, station);
		}

		return stationsMap;
	}
}
