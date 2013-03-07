package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

import bg.znestorov.sofbus24.main.R;
import android.content.Context;

// Creating methods for easy processing data
public class Utils {

	// Get value BEFORE some REGEX
	public static String getValueBefore(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.indexOf(regex));
		} else {
			return value;
		}
	}

	// Get value BEFORE some REGEX (Last)
	public static String getValueBeforeLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.lastIndexOf(regex));
		} else {
			return value;
		}
	}

	// Get value AFTER some REGEX
	public static String getValueAfter(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.indexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	// Get the difference between two hours in format HH:MM
	public static String getDifference(Context context, String afterTime,
			String currTime) {
		String diff = "";
		int afterTimeMilis = 0;
		int currTimeMilis = 0;

		try {
			afterTimeMilis = new BigDecimal(afterTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(afterTime.split(":")[1]).intValue();
			currTimeMilis = new BigDecimal(currTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(currTime.split(":")[1]).intValue();

			diff = (afterTimeMilis - currTimeMilis) + "";
			diff = (new BigDecimal(diff).intValue() / 60) + ":"
					+ (new BigDecimal(diff).intValue() % 60);
			diff = formatTime(context, diff);
		} catch (Exception e) {

		}

		return diff;
	}

	// Format Date (making the minutes in format :XX)
	public static String formatTime(Context context, String difference) {
		String diff = "";
		String[] differenceArr = difference.split(":");

		if (differenceArr.length == 2) {

			if ("".equals(differenceArr[0]) || "0".equals(differenceArr[0])) {
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.remaining_minutes);
				} else {
					if (differenceArr[1].contains("-")) {
						differenceArr[1] = "0"
								+ context.getString(R.string.remaining_minutes);
					} else {
						differenceArr[1] = differenceArr[1]
								+ context.getString(R.string.remaining_minutes);
					}
				}

				diff = "~" + differenceArr[1];
			} else {
				differenceArr[0] = differenceArr[0]
						+ context.getString(R.string.remaining_hours);
				;
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.remaining_minutes);
					;
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.remaining_minutes);
					;
				}

				diff = "~" + differenceArr[0] + " " + differenceArr[1];
			}
		}

		return diff;
	}

	// Get "o" code using the station ID
	public static String getCodeO(String htmlSrc, String stationCode) {
		String codeO = "1";
		stationCode = "(" + stationCode + ")";

		if (htmlSrc.contains(stationCode)) {
			codeO = getValueBeforeLast(htmlSrc, stationCode);

			if (codeO.contains(Constants.SCHEDULE_GPS_FIND_CODEO)) {
				codeO = Integer.toString(codeO
						.split(Constants.SCHEDULE_GPS_FIND_CODEO).length - 1);
			}
		}

		return codeO;
	}
}
