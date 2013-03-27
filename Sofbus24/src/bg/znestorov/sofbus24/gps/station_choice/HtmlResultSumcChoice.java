package bg.znestorov.sofbus24.gps.station_choice;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;

public class HtmlResultSumcChoice {

	// Errors in the HTML source file
	public static String error_noInfo = "���� �������� ����������";
	public static String error_retrieve_noInfo = "���� �������� ���������� �� \"%s\".";
	public static String incorrect_retrieve_data = "INCORRECT";

	// Needed information for creating the list of stations
	public static final String info_ok = "�������� ��";

	// START and END of the needed information
	private static final String BEGIN = "<br /><br />";
	private static final String END = "</div>";

	// Separators
	private static final String SEPARATOR = "&nbsp;������&nbsp;";
	private static final String SEPARATOR_2 = "</b>";
	private static final String SPACE = "&nbsp;";

	// List containing the info for each station
	private final ArrayList<GPSStation> listOfVehicles = new ArrayList<GPSStation>();

	// Constructor variables (passed through other class)
	private final Context context;
	private final String stationCode;
	private String htmlSrc;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;

	public HtmlResultSumcChoice(Context context, String stationCode,
			String htmlSrc) {
		this.context = context;
		this.stationCode = stationCode;
		this.htmlSrc = htmlSrc;

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);
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

			// Get "language" value from the Shared Preferences
			String language = sharedPreferences.getString(
					Constants.PREFERENCE_KEY_LANGUAGE,
					Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);

			if ("bg".equals(language)) {
				return getInfo();
			} else {
				return TranslatorCyrillicToLatin.translateGPSStation(getInfo());
			}
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
				GPSStation tempGPSStation = new GPSStation(number, name);
				tempGPSStation.setTime_stamp("OK");

				listOfVehicles.add(tempGPSStation);
			}
		}

		return listOfVehicles;
	}
}
