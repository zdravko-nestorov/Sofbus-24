package bg.znestorov.sofbus24.gps;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;

import bg.znestorov.sofbus24.station_database.GPSStation;

public class HtmlResultSumcChoice {

	// Errors in the HTML source file
	public static final String error_noInfo = "Няма намерена информация";
	public static final String error_retrieve_noInfo = "Няма намерени съвпадения за \"%s\".";
	public static final String incorrect_retrieve_data = "INCORRECT";

	// Needed information for creating the list of stations
	public static final String info_ok = "Намерени са";

	// START and END of the needed information
	private static final String BEGIN = "<br /><br />";
	private static final String END = "</div>";

	// Separators
	private static final String SEPARATOR = "&nbsp;спирка&nbsp;";
	private static final String SEPARATOR_2 = "</b>";
	private static final String SPACE = "&nbsp;";

	// List containing the info for each station
	private final ArrayList<GPSStation> listOfVehicles = new ArrayList<GPSStation>();

	// Constructor variables (passed through other class)
	private final String stationCode;
	private String htmlSrc;

	public HtmlResultSumcChoice(String stationCode, String htmlSrc) {
		this.stationCode = stationCode;
		this.htmlSrc = htmlSrc;
	}

	// Define if the result contains needed data or not
	public ArrayList<GPSStation> showResult() {
		GPSStation gpsStation = new GPSStation();

		// Check if the htmlSrc is not empty
		if (htmlSrc != null && !"".equals(htmlSrc)) {
			// Check if htmlSrc contains needed information
			if (!htmlSrc.contains(info_ok)) {
				// Check if no stations are found
				if (htmlSrc.contains(error_noInfo)) {
					gpsStation.setTime_stamp(String.format(
							error_retrieve_noInfo, stationCode));
					listOfVehicles.add(gpsStation);

					return listOfVehicles;
					// Catch any other error
				} else {
					gpsStation.setTime_stamp(incorrect_retrieve_data);
					listOfVehicles.add(gpsStation);

					return listOfVehicles;
				}
			}

			return getInfo();
		}

		gpsStation.setTime_stamp(incorrect_retrieve_data);
		listOfVehicles.add(gpsStation);

		return listOfVehicles;
	}

	// Getting the needed information from the HTML source code
	private ArrayList<GPSStation> getInfo() {
		while (htmlSrc.contains(BEGIN)) {
			htmlSrc = getValueAfter(htmlSrc, BEGIN);
		}

		htmlSrc = getValueAfter(htmlSrc, SEPARATOR);
		htmlSrc = getValueBefore(htmlSrc, END).trim();

		String[] array = htmlSrc.split(SEPARATOR);

		for (int i = 0; i < array.length; i++) {
			array[i] = getValueBefore(array[i], SEPARATOR_2);
			array[i] = array[i].replaceAll(SPACE, " ");

			String name = getValueBefore(array[i], "(").trim();
			String number = getValueAfter(array[i], "(");
			number = getValueBefore(number, ")").trim();

			if (name != null && !"".equals(name) && number != null
					&& !"".equals(number)) {
				GPSStation tempGPSStation = new GPSStation(number, name, "1");
				tempGPSStation.setTime_stamp("OK");

				listOfVehicles.add(tempGPSStation);
			}
		}

		return listOfVehicles;
	}
}
