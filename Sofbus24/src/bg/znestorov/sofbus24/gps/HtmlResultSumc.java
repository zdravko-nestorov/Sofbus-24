package bg.znestorov.sofbus24.gps;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Utils;

public class HtmlResultSumc {

	// Errors in the HTML source file
	public static final String error_noInfo = "Няма информация";
	public static final String error_retrieve_noInfo = "В момента няма информация за спирка \"%s\". Моля опитайте пак по-късно.";
	public static final String error_noBusStop = "Няма намерена информация";
	public static final String error_retrieve_noBusStop = "Спирката \"%s\" не съществува.";
	public static final String incorrect_retrieve_data = "INCORRECT";

	// Name of the vehicles
	private static final String vehicle_Bus = "Автобус";
	private static final String vehicle_Bus_Check = "втоб";
	private static final String vehicle_Trolley = "Тролей";
	private static final String vehicle_Trolley_Check = "роле";
	private static final String vehicle_Tram = "Трамвай";
	private static final String vehicle_Tram_Check = "рамв";

	// List containing each vehicle with the information for it
	private final ArrayList<GPSStation> listOfVehicles = new ArrayList<GPSStation>();

	// Constructor variables (passed through other class)
	private final Context context;
	private final String stationCode;
	private final String htmlSrc;
	private final String tempHtmlSrc;

	// Needed information for creating body
	private static final String BODY_START = "<div class=\"arrivals\">";
	private static final String BODY_END = "\n</div>";

	// Needed information for fixing body
	private static final String INFO_BEGIN = "<div class=\"arr_info_";
	private static final int INFO_BEGIN_LENGTH = (INFO_BEGIN + "1\">").length();
	private static final String INFO_END = "</div>";
	private static final String INFO_SPLITTER = "<a href=\"|\">|<b>|</b>|</a>&nbsp;-&nbsp;|<br />";
	private static final int INFO_SPLIT_SIZE = 7;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;

	// Time retrieval string from the source file
	private static final String TIME_RETRIEVAL_BEGIN = "<b>Информация към ";
	private static final String TIME_RETRIEVAL_END = "</b>";

	public HtmlResultSumc(Context context, String stationCode, String htmlSrc) {
		this.context = context;
		this.stationCode = stationCode;
		this.htmlSrc = htmlSrc;
		this.tempHtmlSrc = htmlSrc;
	}

	// Define if the result contains needed data or not
	public ArrayList<GPSStation> showResult() {
		GPSStation gpsStation = new GPSStation();

		if (htmlSrc != null && !"".equals(htmlSrc)) {
			int endOfBody = htmlSrc.indexOf(BODY_START);
			int startOfBody = htmlSrc.indexOf(BODY_END, endOfBody);

			if (endOfBody == -1 && startOfBody == -1) {
				if (htmlSrc.contains(error_noInfo)) {
					gpsStation.setId(getStationId(htmlSrc));
					gpsStation.setName(getStationName(htmlSrc));
					gpsStation.setTime_stamp(String.format(
							error_retrieve_noInfo, stationCode));
					listOfVehicles.add(gpsStation);
					return listOfVehicles;
				} else if (htmlSrc.contains(error_noBusStop)) {
					gpsStation.setTime_stamp(String.format(
							error_retrieve_noBusStop, stationCode));
					listOfVehicles.add(gpsStation);
					return listOfVehicles;
				} else {
					gpsStation.setTime_stamp(incorrect_retrieve_data);
					listOfVehicles.add(gpsStation);
					return listOfVehicles;
				}
			}

			String htmlBody = htmlSrc.substring(endOfBody, startOfBody
					+ BODY_END.length());

			return getInfo(htmlBody);
		}

		gpsStation.setTime_stamp(incorrect_retrieve_data);
		listOfVehicles.add(gpsStation);

		return listOfVehicles;
	}

	// Getting the needed information from the HTML source code
	private ArrayList<GPSStation> getInfo(String htmlBody) {
		String vehicleInfo = new String();
		String vehicleType = "Автобус";

		int start = 0;
		int end = htmlBody.indexOf(INFO_BEGIN);

		while (end != -1) {
			end += INFO_BEGIN_LENGTH;
			vehicleInfo = htmlBody.substring(start, end);
			start = htmlBody.indexOf(INFO_END, end);

			if (start != -1) {
				String[] split = htmlBody.substring(end, start).split(
						INFO_SPLITTER, INFO_SPLIT_SIZE);
				if (split.length == INFO_SPLIT_SIZE) {
					GPSStation gpsStation = new GPSStation();

					gpsStation.setId(getStationId(htmlSrc));
					gpsStation.setName(getStationName(htmlSrc));

					if (vehicleInfo.toString().contains(vehicle_Bus_Check)) {
						vehicleType = vehicle_Bus;
						gpsStation.setType(vehicleType);
					} else if (vehicleInfo.toString().contains(
							vehicle_Trolley_Check)) {
						vehicleType = vehicle_Trolley;
						gpsStation.setType(vehicleType);
					} else if (vehicleInfo.toString().contains(
							vehicle_Tram_Check)) {
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
			end = htmlBody.indexOf(INFO_BEGIN, start);
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

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);

		// Get "timeInfoRetrieval" value from the Shared Preferences
		String timeInfoRetrieval = sharedPreferences.getString(
				"timeInfoRetrieval", "time_skgt");

		if (htmlSrc.contains(TIME_RETRIEVAL_BEGIN)
				&& "time_skgt".equals(timeInfoRetrieval)) {
			infoTime = getValueAfter(htmlSrc, TIME_RETRIEVAL_BEGIN);
			infoTime = getValueBefore(infoTime, TIME_RETRIEVAL_END);
			infoTime = infoTime.trim();
		} else {
			infoTime = android.text.format.DateFormat.format("dd.MM.yyy kk:mm",
					new java.util.Date()).toString();
		}

		return infoTime;
	}

	private String getStationName(String htmlSrc) {
		String stationName = getValueAfter(htmlSrc, "<b>спирка");
		stationName = getValueBefore(stationName, "</b>");
		stationName = getValueBefore(stationName, "(").trim();
		stationName = getValueAfter(stationName, "&nbsp;");
		stationName = getValueBefore(stationName, "&nbsp;");

		// Special case when the number of the station is in some stations'
		// names
		if (stationName.length() > 100) {
			stationName = getValueBefore(tempHtmlSrc, "(" + stationCode + ")");

			if (stationName.contains("&nbsp;")) {
				stationName = stationName.substring(0,
						stationName.lastIndexOf("&nbsp;"));
			}

			if (stationName.contains("&nbsp;")) {
				stationName = stationName.substring(stationName
						.lastIndexOf("&nbsp;") + 6);
			}

			if (stationName.contains("<b>")) {
				stationName = stationName.substring(stationName
						.lastIndexOf("<b>") + 3);
				stationName = getValueAfter(stationName, ".");
			}

			stationName = stationName.trim();
		}

		return stationName;
	}

	private String getStationId(String htmlSrc) {
		String stationId = getValueAfter(htmlSrc, "<b>спирка");
		stationId = getValueAfter(stationId, "(");
		stationId = getValueBefore(stationId, ")").trim();

		if (stationId.length() > 100) {
			stationId = stationCode;
		}

		return stationId;
	}

	private String setRemainingTimeStamp(String timeStamp) {
		String remainingTime = "";

		// Get current time or the time from SGKT - according to the Preferences
		String currTime = getInformationTime(htmlSrc);
		while (currTime.contains(" ")) {
			currTime = getValueAfter(currTime, " ");
		}

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);

		// Get "exitAlert" value from the Shared Preferences
		boolean timeGPS = sharedPreferences.getBoolean("timeGPS", false);

		if (timeStamp != null && !"".equals(timeStamp) && timeGPS) {
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
							+ Utils.getDifference(tempTimeStamp[i], currTime);
				}
			}

			for (int i = 0; i < br; i++) {
				remainingTime += ", "
						+ Utils.getDifference(tempTimeStampAM[i], currTime);
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
