package bg.znestorov.sofbus24.metro.schedule;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bg.znestorov.sobusf24.metro.utils.Constants;
import bg.znestorov.sobusf24.metro.utils.Utils;

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
				boolean isIncompleteCourse = !Utils
						.isEmpty(stationMatcher.group(1));
				String hour = getHour(stationMatcher.group(2));

				if (hour != null) {

					// If the time is 00:xx, change to 24:xx
					if (Integer.parseInt(hour) == 0) {
						hour = "24";
					}

					// Check if it is a WEEKDAY or HOLIDAY
					if (i == 1) {
						ms.getWeekdaySchedule().get(Integer.parseInt(hour))
								.add(stationMatcher.group(2) + getHints(
										isIncompleteCourse, ms.getDirection()));
					} else {
						ms.getHolidaySchedule().get(Integer.parseInt(hour))
								.add(stationMatcher.group(2) + getHints(
										isIncompleteCourse, ms.getDirection()));
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

	private static String getHints(boolean isIncompleteCourse,
			String directionName) {

		StringBuilder incompleteCourse = new StringBuilder("");

		if (isIncompleteCourse) {
			incompleteCourse.append("|IC");
		}

		if ("�.������-�.�����-�.������ �����".equals(directionName)) {
			incompleteCourse.append("|SA");
		} else if ("�.������-�.�����-�.������ ����".equals(directionName)) {
			incompleteCourse.append("|BP");
		}

		return incompleteCourse.toString();
	}

}