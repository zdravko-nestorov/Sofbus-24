package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

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

	// Get value AFTER some REGEX
	public static String getValueAfter(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.indexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	// Get the difference between two hours in format HH:MM
	public static String getDiff(String afterTime, String currTime) {
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
			diff = formatTime(diff);
		} catch (Exception e) {

		}

		diff = afterTimeMilis + " - " + currTimeMilis + " = " + diff;

		return diff;
	}

	// Format Date (making the minutes in format :XX)
	public static String formatTime(String difference) {
		String diff = "";
		String[] differenceArr = difference.split(":");

		if (differenceArr.length == 2) {
			if (differenceArr[1].length() == 0) {
				differenceArr[1] = "00";
			} else if (differenceArr[1].length() == 1) {
				differenceArr[1] = "0" + differenceArr[1];
			}

			diff = differenceArr[0] + ":" + differenceArr[1];
		}

		return diff;
	}
}
