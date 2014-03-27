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
			String weekdayHtmlResponse, String holidayHtmlResponse,
			MetroStation ms) {

		logger.info("Start parsing the information...");

		for (int i = 1; i < 3; i++) {
			String htmlResponse;
			if (i == 1) {
				htmlResponse = weekdayHtmlResponse;
			} else {
				htmlResponse = holidayHtmlResponse;
			}

			Pattern stationPattern = Pattern
					.compile(Constants.METRO_REGEX_TIME);
			Matcher stationMatcher = stationPattern.matcher(htmlResponse);

			while (stationMatcher.find()) {
				String hour = getHour(stationMatcher.group(1));

				if (hour != null) {
					// Check if it is a WEEKDAY or HOLIDAY
					if (i == 1) {
						ms.getWeekdaySchedule().get(Integer.parseInt(hour))
								.add(stationMatcher.group(1));
					} else {
						ms.getHolidaySchedule().get(Integer.parseInt(hour))
								.add(stationMatcher.group(1));
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
