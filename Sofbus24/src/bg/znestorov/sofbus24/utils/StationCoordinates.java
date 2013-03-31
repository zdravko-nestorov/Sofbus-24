package bg.znestorov.sofbus24.utils;

import java.util.ArrayList;

import android.content.Context;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.station_database.StationsDataSource;

public class StationCoordinates {

	// Getting the coordinates for the station with code "stationCode"
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

	// Getting the coordinates for the station with code "stationCode"
	public static String getRoute(Context context, ArrayList<String> stations) {
		// Opening station database
		StationsDataSource datasource = new StationsDataSource(context);
		datasource.open();
		StringBuilder coordinates = new StringBuilder();

		String stationCode;
		for (int i = 0; i < stations.size(); i++) {
			stationCode = stations.get(i);

			if (stationCode.contains("(") && stationCode.contains(")")) {
				stationCode = stationCode.substring(
						stationCode.indexOf("(") + 1, stationCode.indexOf(")"));

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

}
