package bg.znestorov.sofbus24.metro.schedule;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.Constants;

public class MetroStationsScheduleMain {

	public static void saveStationsScheduleToAFile(Logger logger,
			String directionId, String directionName,
			Map.Entry<String, String> station, Properties coordinatesProp) {

		String weekdayHtmlResponse = HtmlRequestStationSchedule
				.retrieveStationsInfo(logger, directionId, station,
						Constants.STATION_WEEKDAY_SCHEDULE_URL);
		String holidayHtmlResponse = HtmlRequestStationSchedule
				.retrieveStationsInfo(logger, directionId, station,
						Constants.STATION_HOLIDAY_SCHEDULE_URL);

		if ("".equals(weekdayHtmlResponse) || "".equals(holidayHtmlResponse)) {
			logger.warning("Problem with retrieving information about station name = "
					+ station.getValue() + " and number = " + station.getKey());
			return;
		}

		MetroStation ms = HtmlResultStationSchedule.getMetroDirections(logger,
				weekdayHtmlResponse, holidayHtmlResponse, new MetroStation(
						station.getKey(), station.getValue(), directionName));

		if (ms == null) {
			logger.warning("Problem with parsing the schedule information from SGKT...");
			return;
		}

		WriteScheduleToXMLFile.saveToXMLFile(logger, ms, coordinatesProp);
	}

}
