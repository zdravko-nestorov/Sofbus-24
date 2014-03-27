package bg.znestorov.sofbus24.utils;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfterLast;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.station_database.StationsDataSource;

public class StationCoordinates {

	/**
	 * Getting the coordinates of a station from the database using the station
	 * code
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            the code of the station
	 * @return an array with two elements - the latitude and the longitude of
	 *         the station (if exists)
	 */
	public static String[] getLocation(Context context, String stationCode) {

		// Opening station database
		StationsDataSource datasource = new StationsDataSource(context);
		datasource.open();
		String[] coordinates = new String[2];

		// Getting station from the database via stationCode
		GPSStation station = datasource.getStation(stationCode);

		if (station != null) {
			coordinates[0] = station.getLat();
			coordinates[1] = station.getLon();
		} else {
			coordinates = null;
		}
		datasource.close();

		return coordinates;
	}

	/**
	 * Getting the coordinates of an array list of stations from the database
	 * using their station codes (finding the route of a vehicle)
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param stations
	 *            an array with the station codes
	 * @return a string object containing the route of the vehicle in format -->
	 *         <b>LAT,LON, NAME(CODE);</b>
	 */
	public static String getRoute(Context context, ArrayList<String> stations) {
		// Opening station database
		StationsDataSource datasource = new StationsDataSource(context);
		datasource.open();
		StringBuilder coordinates = new StringBuilder();

		String stationCode;
		for (int i = 0; i < stations.size(); i++) {
			stationCode = stations.get(i);

			if (stationCode.contains("(") && stationCode.contains(")")) {
				stationCode = getValueAfterLast(stationCode, "(");
				stationCode = getValueBefore(stationCode, ")");

				// Getting station from the database via stationCode
				GPSStation station = datasource.getStation(stationCode);

				// Filling coordinates StringBuilder
				if (station != null) {
					coordinates.append(station.getLat()).append(",")
							.append(station.getLon()).append(",")
							.append(station.getName()).append(" (")
							.append(station.getId()).append(")").append(";");
				}
			}
		}

		datasource.close();

		if (coordinates.length() > 0) {
			return coordinates.toString();
		} else {
			return null;
		}
	}

	/**
	 * Getting the coordinates of an HashMap of METRO stations from the database
	 * using their station codes (finding the route of a vehicle)
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param stations
	 *            an array with the station codes
	 * @return a string object containing the route of the vehicle in format -->
	 *         <b>LAT,LON,NAME (CODE);</b>
	 */
	public static String getRoute(Context context,
			HashMap<String, HashMap<String, String>> stationsMap) {
		// Opening station database
		StationsDataSource datasource = new StationsDataSource(context);
		datasource.open();
		StringBuilder coordinates = new StringBuilder();

		String stationCode;
		List<String> stations = new ArrayList<String>(stationsMap.keySet());
		for (int i = 0; i < stations.size(); i++) {
			stationCode = stations.get(i);

			// Getting station from the database via stationCode
			GPSStation station = datasource.getStation(stationCode);

			// Filling coordinates StringBuilder
			if (station != null) {
				coordinates.append(station.getLat()).append(",")
						.append(station.getLon()).append(",")
						.append(station.getName()).append(" (")
						.append(station.getId()).append(")").append(";");
			}
		}

		datasource.close();

		if (coordinates.length() > 0) {
			return coordinates.toString();
		} else {
			return null;
		}
	}

}
