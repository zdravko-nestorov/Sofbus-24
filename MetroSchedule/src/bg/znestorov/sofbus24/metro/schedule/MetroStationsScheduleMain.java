package bg.znestorov.sofbus24.metro.schedule;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class MetroStationsScheduleMain {

	public static void saveStationsScheduleToAFile(Logger logger,
			String directionId, String directionName,
			Map.Entry<String, String> station, Properties coordinatesProp) {
		String htmlResponse = HtmlRequestStationSchedule.retrieveStationsInfo(
				logger, directionId, station);

		if ("".equals(htmlResponse)) {
			logger.warning("Problem with retrieving information about station name = "
					+ station.getValue() + " and number = " + station.getKey());
			return;
		}

		MetroStation ms = HtmlResultStationSchedule.getMetroDirections(logger,
				htmlResponse,
				new MetroStation(station.getKey(), station.getValue(),
						directionName));

		if (ms == null) {
			logger.warning("Problem with parsing the schedule information from SGKT...");
			return;
		}

		WriteScheduleToXMLFile.saveToXMLFile(logger, ms, coordinatesProp);
	}

}
