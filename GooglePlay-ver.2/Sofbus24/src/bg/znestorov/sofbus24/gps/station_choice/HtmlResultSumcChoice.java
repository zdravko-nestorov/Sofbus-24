package bg.znestorov.sofbus24.gps.station_choice;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;
import static bg.znestorov.sofbus24.utils.Utils.getValueBeforeLast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.main.VirtualBoardsStationChoice;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.Utils;

public class HtmlResultSumcChoice {

	// List containing the info for each station
	private final ArrayList<GPSStation> listOfVehicles = new ArrayList<GPSStation>();

	// Constructor variables (passed through other class)
	private final Context context;
	private String stationCode;
	private String htmlSrc;
	private final String tempHtmlSrc;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;
	private String language;

	public HtmlResultSumcChoice(Context context, String stationCode,
			String htmlSrc) {
		this.context = context;
		this.stationCode = stationCode;
		this.htmlSrc = htmlSrc;
		this.tempHtmlSrc = htmlSrc;

		if (stationCode.equals(stationCode.replaceAll("\\D+", ""))) {
			this.stationCode = stationCode;
		} else {
			this.stationCode = Utils.getStationId(this.htmlSrc,
					this.stationCode);
		}

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);

		// Get "language" value from the Shared Preferences
		language = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_LANGUAGE,
				Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);
	}

	// Define if the result contains needed data or not
	public ArrayList<GPSStation> showResult() {
		GPSStation gpsStation = new GPSStation();

		// Check if the htmlSrc is not empty
		if (htmlSrc != null && !"".equals(htmlSrc)) {
			// Check if the HTML is valid and that there are found stations
			if (htmlSrc.contains(Constants.ERORR_NONE)
					&& VirtualBoardsStationChoice.countStarts == 1
					&& !VirtualBoardsStationChoice.checkCodeO) {
				// Get "language" value from the Shared Preferences
				String language = sharedPreferences.getString(
						Constants.PREFERENCE_KEY_LANGUAGE,
						Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);

				if ("bg".equals(language)) {
					return getInfo();
				} else {
					return TranslatorCyrillicToLatin
							.translateGPSStation(getInfo());
				}
			}

			// Case in which there is no info after refresh
			if (htmlSrc.contains(Constants.SEARCH_ERROR_WITH_REFRESH)) {
				gpsStation.setTime_stamp(Constants.SEARCH_ERROR_WITH_REFRESH);
				listOfVehicles.add(gpsStation);
				return listOfVehicles;
				// Strange case in which the station is found, but there is
				// no information (line #1)
			} else if (htmlSrc.contains(Constants.ERROR_NO_INFO_STATION)) {
				gpsStation.setId(Utils.getStationId(htmlSrc, stationCode));
				gpsStation
						.setTime_stamp(Constants.ERROR_RETRIEVE_NO_INFO_STATION);
				listOfVehicles.add(gpsStation);
				return listOfVehicles;
				// Case in which there is no information found
			} else if (htmlSrc.contains(Constants.ERROR_NO_INFO_NOW)) {
				gpsStation.setId(Utils.getStationId(htmlSrc, stationCode));
				gpsStation.setName(Utils.getStationName(htmlSrc, tempHtmlSrc,
						stationCode, language));
				gpsStation.setTime_stamp(String.format(
						Constants.ERROR_RETRIEVE_NO_INFO_NOW, stationCode));
				listOfVehicles.add(gpsStation);
				return listOfVehicles;
				// Case in which there is no such station or station
				// name match
			} else if (htmlSrc.contains(Constants.ERROR_NO_INFO)) {
				if (stationCode.equals(stationCode.replaceAll("\\D+", ""))) {
					gpsStation.setTime_stamp(String.format(
							Constants.ERROR_RETRIEVE_NO_BUS_STOP, stationCode));
					listOfVehicles.add(gpsStation);
					return listOfVehicles;
				} else {
					gpsStation.setTime_stamp(String.format(
							Constants.ERROR_RETRIEVE_NO_STATION_MATCH,
							stationCode));
					listOfVehicles.add(gpsStation);
					return listOfVehicles;
				}
				// All other errors
			} else {
				gpsStation.setTime_stamp(Constants.ERROR_RETRIEVE_NO_DATA);
				listOfVehicles.add(gpsStation);
				return listOfVehicles;
			}
		}

		gpsStation.setTime_stamp(Constants.ERROR_RETRIEVE_NO_DATA);
		listOfVehicles.add(gpsStation);

		return listOfVehicles;
	}

	// Getting the needed information from the HTML source code
	private ArrayList<GPSStation> getInfo() {
		while (htmlSrc.contains(Constants.MULTIPLE_RESULTS_BEGIN)) {
			htmlSrc = getValueAfter(htmlSrc, Constants.MULTIPLE_RESULTS_BEGIN);
		}

		htmlSrc = getValueAfter(htmlSrc, Constants.MULTIPLE_RESULTS_SEPARATOR_1);
		htmlSrc = getValueBeforeLast(htmlSrc, Constants.MULTIPLE_RESULTS_END)
				.trim();

		String[] array = htmlSrc.split(Constants.MULTIPLE_RESULTS_SEPARATOR_1);

		for (int i = 0; i < array.length; i++) {
			array[i] = getValueBefore(array[i],
					Constants.MULTIPLE_RESULTS_SEPARATOR_2);
			array[i] = array[i].replaceAll(Constants.MULTIPLE_RESULTS_SPACE,
					" ");

			String name = getValueBefore(array[i], "(").trim();
			String number = getStationNumber(array[i]);

			if (name != null && !"".equals(name) && number != null
					&& !"".equals(number)) {
				GPSStation tempGPSStation = new GPSStation(number, name);
				tempGPSStation.setTime_stamp("OK");

				listOfVehicles.add(tempGPSStation);
			}
		}

		return listOfVehicles;
	}

	// Get the station number from the html source part
	private String getStationNumber(String htmlSourcePart) {
		String stationNumber = "";

		Pattern pattern = Pattern.compile("\\((\\d{4})\\)");
		Matcher matcher = pattern.matcher(htmlSourcePart);
		try {
			if (matcher.find()) {
				stationNumber = matcher.group(1).trim();
			}
		} catch (Exception e) {
		}

		return stationNumber;
	}
}
