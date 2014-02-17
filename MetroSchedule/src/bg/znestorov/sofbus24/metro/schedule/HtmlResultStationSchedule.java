package bg.znestorov.sofbus24.metro.schedule;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bg.znestorov.sobusf24.metro.utils.Constants;

/**
 * Process the HTML response for a station. The format of the HTML soruce is as
 * follows:
 * <ul>
 * <li>Direction 1, Weekday</li>
 * <li>Direction 2, Weekday</li>
 * <li>Direction 1, Holiday</li>
 * <li>Direction 2, Holiday</li>
 * </ul>
 * 
 * @author znestorov
 * 
 */
public class HtmlResultStationSchedule {

	public static MetroStation getMetroDirections(Logger logger,
			String htmlSrc, MetroStation ms) {

		logger.info("Start parsing the information...");

		// Split the HTML in five parts (first containing only info about the
		// directions, and the others - about the schedule timing)
		String[] htmlSrcParts = htmlSrc.split(Constants.METRO_REGEX_PARTS);

		if (htmlSrcParts.length >= 5) {

			// This will be executed twice - for weekdays and holidays
			for (int i = 1; i < 4; i = i + 2) {
				// Split the HTML in two parts (first containing only info about
				// each station, and the other - about the schedule timing)
				String[] htmlSrcPartsSchedule = htmlSrcParts[i]
						.split(Constants.METRO_REGEX_STATION_SCHEDULE_PARTS);

				if (htmlSrcPartsSchedule.length == 2) {
					Pattern stationPattern = Pattern
							.compile(Constants.METRO_REGEX_TIME);
					Matcher stationMatcher = stationPattern
							.matcher(htmlSrcPartsSchedule[1]);

					while (stationMatcher.find()) {
						String hour = getHour(stationMatcher.group(1));

						if (hour != null) {
							// Check if it is a WEEKDAY or HOLIDAY
							if (i == 1) {
								ms.getWeekdaySchedule()
										.get(Integer.parseInt(hour))
										.add(stationMatcher.group(1));
							} else {
								ms.getHolidaySchedule()
										.get(Integer.parseInt(hour))
										.add(stationMatcher.group(1));
							}
						}
					}
				}
			}
		}

		if (ms.isScheduleSet()) {
			return ms;
		} else {
			return null;
		}
	}

	private static String getHour(String time) {
		String hour = null;

		if (time != null && time.contains(":")) {
			hour = time.substring(0, time.indexOf(":"));
		}

		return hour;
	}

}
