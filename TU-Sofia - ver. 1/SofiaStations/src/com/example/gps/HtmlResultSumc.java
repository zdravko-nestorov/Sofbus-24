package com.example.gps;

import static com.example.utils.Utils.getValueAfter;
import static com.example.utils.Utils.getValueBefore;

import java.util.ArrayList;

import com.example.station_database.GPSStation;

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
	private final String stationCode;
	private final String htmlSrc;

	// Needed information for creating body
	private static final String BODY_START = "<div class=\"arrivals\">";
	private static final String BODY_END = "\n</div>";

	// Needed information for fixing body
	private static final String INFO_BEGIN = "<div class=\"arr_info_";
	private static final int INFO_BEGIN_LENGTH = (INFO_BEGIN + "1\">").length();
	private static final String INFO_END = "</div>";
	private static final String INFO_SPLITTER = "<a href=\"|\">|<b>|</b>|</a>&nbsp;-&nbsp;|<br />";
	private static final int INFO_SPLIT_SIZE = 7;

	public HtmlResultSumc(String stationCode, String htmlSrc) {
		this.stationCode = stationCode;
		this.htmlSrc = htmlSrc;
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
					gpsStation.setTime_stamp(split[5].trim());
					gpsStation.setDirection(getValueBefore(split[6], "(")
							.trim());
					listOfVehicles.add(gpsStation);
				}
			}
			end = htmlBody.indexOf(INFO_BEGIN, start);
		}

		return listOfVehicles;
	}

	private String getStationName(String htmlSrc) {
		String stationName = getValueAfter(htmlSrc, "<b>спирка");
		stationName = getValueBefore(stationName, "</b>");
		stationName = getValueBefore(stationName, "(").trim();
		stationName = getValueAfter(stationName, "&nbsp;");
		stationName = getValueBefore(stationName, "&nbsp;");

		return stationName;
	}

	private String getStationId(String htmlSrc) {
		String stationId = getValueAfter(htmlSrc, "<b>спирка");
		stationId = getValueAfter(stationId, "(");
		stationId = getValueBefore(stationId, ")").trim();

		return stationId;
	}

}
