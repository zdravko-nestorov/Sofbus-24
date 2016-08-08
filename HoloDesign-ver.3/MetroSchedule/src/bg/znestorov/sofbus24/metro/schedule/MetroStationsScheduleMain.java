package bg.znestorov.sofbus24.metro.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.Constants;
import bg.znestorov.sobusf24.metro.utils.Utils;

public class MetroStationsScheduleMain {

	public static MetroStation retrieveStationsSchedule(Logger logger, String directionId, String directionName, Map.Entry<String, String> station) {

		String weekdayHtmlResponse = HtmlRequestStationSchedule.retrieveStationsInfo(logger, directionId, station, Constants.STATION_WEEKDAY_SCHEDULE_URL);
		String holidayHtmlResponse = HtmlRequestStationSchedule.retrieveStationsInfo(logger, directionId, station, Constants.STATION_HOLIDAY_SCHEDULE_URL);

		if ("".equals(weekdayHtmlResponse) || "".equals(holidayHtmlResponse)) {
			logger.warning("Problem with retrieving information about station name = " + station.getValue() + " and number = " + station.getKey());
			return null;
		}

		MetroStation ms = HtmlResultStationSchedule.getMetroDirections(logger, weekdayHtmlResponse, holidayHtmlResponse,
				new MetroStation(station.getKey(), station.getValue(), directionName));

		if (ms == null) {
			logger.warning("Problem with parsing the schedule information from SGKT...");
			return null;
		}

		return ms;
	}

	public static void saveStationsScheduleToAFile(Logger logger, ArrayList<ArrayList<MetroStation>> metroDirectionsStations, Properties coordinatesProp) {

		// Check if there are four metro directions
		if (metroDirectionsStations.size() == 4) {

			// Iterate over the first two directions and compare and merge the
			// same stations
			for (int i = 0; i < 2; i++) {

				ArrayList<MetroStation> metroStations1 = metroDirectionsStations.get(i);
				ArrayList<MetroStation> metroStations2 = metroDirectionsStations.get(i + 2);
				int count = metroStations1.size() > metroStations2.size() ? metroStations1.size() : metroStations2.size();

				// In case of second direction (and fourth one) - reverse the
				// order of the stations, so match the same ones
				if (i == 1) {
					Collections.reverse(metroStations1);
					Collections.reverse(metroStations2);
				}

				// Iterate over the stations for the current direction and save
				// them to a file
				for (int j = 0; j < count; j++) {

					MetroStation ms1 = null;
					if (metroStations1.size() > j) {
						ms1 = metroStations1.get(j);
					}

					MetroStation ms2 = null;
					if (metroStations2.size() > j) {
						ms2 = metroStations2.get(j);
					}

					// Check if both stations are found - merge them and save to
					// a file. Otherwise - check which station exists and save
					// it to a file
					if (ms1 != null && ms2 != null) {

						// If the station numbers are the same - merge the
						// station schedule and write it to a file. Otherwise -
						// write both stations to two separate files
						if (ms1.getNumber().equals(ms2.getNumber())) {

							// If the station number is the same - change the
							// direction number (because the train can go to
							// Bussines Park or Airport)
							ms1.setDirection(Utils.formatDirection(ms1.getDirection()));
							WriteScheduleToXMLFile.saveToXMLFile(logger, ms1.merge(ms2), coordinatesProp);
						} else {
							WriteScheduleToXMLFile.saveToXMLFile(logger, ms1, coordinatesProp);
							WriteScheduleToXMLFile.saveToXMLFile(logger, ms2, coordinatesProp);
						}
					} else if (ms1 != null) {
						WriteScheduleToXMLFile.saveToXMLFile(logger, ms1, coordinatesProp);
					} else {
						WriteScheduleToXMLFile.saveToXMLFile(logger, ms2, coordinatesProp);
					}
				}
			}

		} else {
			logger.warning("Problem with merging the schedule information for the stations...");
		}
	}
}
