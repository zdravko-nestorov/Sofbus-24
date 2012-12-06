package bg.znestorov.sofbus24.gps;

import java.util.ArrayList;

import bg.znestorov.sofbus24.station_database.GPSStation;

public class HtmlResultSumcChoice {

	// Errors in the HTML source file
	public static final String error_noInfo = "Няма намерена информация";
	public static final String error_retrieve_noInfo = "Няма намерена информация за \"%s\".";
	public static final String incorrect_retrieve_data = "INCORRECT";

	// List containing the info for each station
	private final ArrayList<GPSStation> listOfVehicles = new ArrayList<GPSStation>();

	// Constructor variables (passed through other class)
	private final String stationCode;
	private final String htmlSrc;

	public HtmlResultSumcChoice(String stationCode, String htmlSrc) {
		this.stationCode = stationCode;
		this.htmlSrc = htmlSrc;
	}

	// Define if the result contains needed data or not
	public ArrayList<GPSStation> showResult() {
		GPSStation gpsStation = new GPSStation();

		// Check if the htmlSrc is empty
		if (htmlSrc != null && !"".equals(htmlSrc)) {
			if (htmlSrc.contains(error_noInfo)) {
				gpsStation.setTime_stamp(String.format(error_retrieve_noInfo,
						stationCode));
				listOfVehicles.add(gpsStation);
				return listOfVehicles;
			} else {
				return getInfo(htmlSrc);
			}
		}

		gpsStation.setTime_stamp(incorrect_retrieve_data);
		listOfVehicles.add(gpsStation);

		return listOfVehicles;
	}

	// Getting the needed information from the HTML source code
	private ArrayList<GPSStation> getInfo(String htmlBody) {

		return listOfVehicles;
	}

}
