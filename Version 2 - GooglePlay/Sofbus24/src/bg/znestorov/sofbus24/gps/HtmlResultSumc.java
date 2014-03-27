package bg.znestorov.sofbus24.gps;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.Utils;

public class HtmlResultSumc {

	// Name of the vehicles
	private static String vehicle_Bus;
	private static String vehicle_Trolley;
	private static String vehicle_Tram;

	// List containing each vehicle with the information for it
	private final ArrayList<GPSStation> listOfVehicles = new ArrayList<GPSStation>();

	// Constructor variables (passed through other class)
	private final Context context;
	private String stationCode;
	private final String htmlSrc;
	private final String tempHtmlSrc;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;
	private String language;

	public HtmlResultSumc(Context context, String stationCode, String htmlSrc) {
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

		// Get String values from "strings" XML
		vehicle_Bus = context.getString(R.string.title_bus);
		vehicle_Trolley = context.getString(R.string.title_trolley);
		vehicle_Tram = context.getString(R.string.title_tram);

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
		int endOfBody = htmlSrc.indexOf(Constants.BODY_START);
		int startOfBody = htmlSrc.indexOf(Constants.BODY_END, endOfBody);

		String htmlBody = htmlSrc.substring(endOfBody, startOfBody
				+ Constants.BODY_END.length());

		// Check which language is chosen from Preferences
		if ("bg".equals(language)) {
			return getInfo(htmlBody);
		} else {
			return TranslatorCyrillicToLatin
					.translateGPSStation(getInfo(htmlBody));
		}
	}

	// Getting the needed information from the HTML source code
	private ArrayList<GPSStation> getInfo(String htmlBody) {
		String vehicleInfo = new String();
		String vehicleType = context.getString(R.string.title_bus);

		int start = 0;
		int end = htmlBody.indexOf(Constants.SUMC_INFO_BEGIN);

		while (end != -1) {
			end += Constants.SUMC_INFO_BEGIN_LENGTH;
			vehicleInfo = htmlBody.substring(start, end);
			start = htmlBody.indexOf(Constants.SUMC_INFO_END, end);

			if (start != -1) {
				String[] split = htmlBody.substring(end, start).split(
						Constants.SUMC_INFO_SPLITTER,
						Constants.SUMC_INFO_SPLIT_SIZE);
				if (split.length == Constants.SUMC_INFO_SPLIT_SIZE) {
					GPSStation gpsStation = new GPSStation();

					gpsStation.setId(Utils.getStationId(htmlSrc, stationCode));
					gpsStation.setName(Utils.getStationName(htmlSrc,
							tempHtmlSrc, stationCode, language));

					if (vehicleInfo.toString().contains(
							Constants.VEHICLE_BUS_CHECK)) {
						vehicleType = vehicle_Bus;
						gpsStation.setType(vehicleType);
					} else if (vehicleInfo.toString().contains(
							Constants.VEHICLE_TROLLEY_CHECK)) {
						vehicleType = vehicle_Trolley;
						gpsStation.setType(vehicleType);
					} else if (vehicleInfo.toString().contains(
							Constants.VEHICLE_TRAM_CHECK)) {
						vehicleType = vehicle_Tram;
						gpsStation.setType(vehicleType);
					} else {
						gpsStation.setType(vehicleType);
					}
					gpsStation.setNumber(split[3].trim());
					gpsStation.setTime_stamp(setRemainingTimeStamp(split[5]
							.trim()));
					gpsStation.setDirection(getValueBefore(split[6], "(")
							.trim());
					listOfVehicles.add(gpsStation);
				}
			}
			end = htmlBody.indexOf(Constants.SUMC_INFO_BEGIN, start);
		}

		Collections.sort(listOfVehicles, new Comparator<GPSStation>() {
			public int compare(GPSStation station1, GPSStation station2) {
				// Get first vehicle number (only digits)
				String station1Number = station1.getNumber();
				station1Number = station1Number.replaceAll("\\D+", "");

				// Get second vehicle number (only digits)
				String station2Number = station2.getNumber();
				station2Number = station2Number.replaceAll("\\D+", "");

				// Compare vehicles' numbers
				try {
					return Integer.valueOf(station1Number).compareTo(
							Integer.valueOf(station2Number));
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});

		Collections.sort(listOfVehicles, new Comparator<GPSStation>() {
			public int compare(GPSStation station1, GPSStation station2) {
				// Get first vehicle type
				String station1Type = station1.getType();

				// Get second vehicle type
				String station2Type = station2.getType();

				// Compare vehicles' types
				return station1Type.compareTo(station2Type);
			}
		});

		return listOfVehicles;
	}

	public String getInformationTime(String htmlSrc) {
		String infoTime = "";

		// Get "timeInfoRetrieval" value from the Shared Preferences
		String timeInfoRetrieval = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_INFO_RETRIEVAL,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_INFO_RETRIEVAL);

		if (htmlSrc.contains(Constants.TIME_RETRIEVAL_BEGIN)
				&& "time_skgt".equals(timeInfoRetrieval)) {
			infoTime = getValueAfter(htmlSrc, Constants.TIME_RETRIEVAL_BEGIN);
			infoTime = getValueBefore(infoTime, Constants.TIME_RETRIEVAL_END);
			infoTime = infoTime.trim();
		} else {
			infoTime = android.text.format.DateFormat.format("dd.MM.yyy kk:mm",
					new java.util.Date()).toString();
		}

		return infoTime;
	}

	private String setRemainingTimeStamp(String timeStamp) {
		String remainingTime = "";

		// Get current time or the time from SGKT - according to the Preferences
		String currTime = getInformationTime(htmlSrc);
		while (currTime.contains(" ")) {
			currTime = getValueAfter(currTime, " ");
		}

		// Get "exitAlert" value from the Shared Preferences
		String timeGPS = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_GPS,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_GPS);

		if (timeStamp != null && !"".equals(timeStamp)
				&& "timeGPS_remaining".equals(timeGPS)) {
			String[] tempTimeStamp = timeStamp.split(",");
			String[] tempTimeStampAM = new String[tempTimeStamp.length];
			int br = 0;

			for (int i = 0; i < tempTimeStamp.length; i++) {
				if (tempTimeStamp[i].startsWith("00:")) {
					tempTimeStamp[i] = tempTimeStamp[i]
							.replaceAll("00:", "24:");

					tempTimeStampAM[br] = tempTimeStamp[i];
					br++;
				} else {
					remainingTime += ", "
							+ Utils.getDifference(this.context,
									tempTimeStamp[i], currTime);
				}
			}

			for (int i = 0; i < br; i++) {
				remainingTime += ", "
						+ Utils.getDifference(this.context, tempTimeStampAM[i],
								currTime);
			}

			if (remainingTime.startsWith(",")) {
				remainingTime = remainingTime.substring(1).trim();
			}
		} else {
			String[] tempTimeStamp = timeStamp.split(",");
			String[] tempTimeStampAM = new String[tempTimeStamp.length];
			int br = 0;

			for (int i = 0; i < tempTimeStamp.length; i++) {
				if (tempTimeStamp[i].startsWith("00:")) {
					tempTimeStampAM[br] = tempTimeStamp[i];
					br++;
				} else {
					remainingTime += ", " + tempTimeStamp[i];
				}
			}

			for (int i = 0; i < br; i++) {
				remainingTime += ", " + tempTimeStampAM[i];
			}

			if (remainingTime.startsWith(",")) {
				remainingTime = remainingTime.substring(1).trim();
			}
		}

		return remainingTime;
	}

}
