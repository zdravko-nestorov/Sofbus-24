package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;
import java.util.Locale;

import android.content.Context;
import bg.znestorov.sofbus24.main.R;

public class Utils {

	/**
	 * Function that extracts only digits from a given String. In case of an
	 * empty string - return "".
	 * 
	 * @param value
	 *            the input String
	 * @return the digits from the String
	 */
	public static String getOnlyDigits(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\D+", "");
		} else {
			value = "";
		}

		return value;
	}

	/**
	 * Function that remove all whitespaces and non visible characters such as
	 * tab, \n from a string text. In case of an empty string - return "".
	 * 
	 * @param value
	 *            the input String
	 * @return the digits from the String
	 */
	public static String removeSpaces(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\s+", "");
		} else {
			value = "";
		}

		return value;
	}

	/**
	 * Get a value from a string BEFORE some REGEX
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value BEFORE the REGEX, or the value in case of no
	 *         REGEX found
	 */
	public static String getValueBefore(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.indexOf(regex));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string BEFORE some REGEX (LAST)
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value BEFORE the REGEX (LAST), or the value in case
	 *         of no REGEX found
	 */
	public static String getValueBeforeLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.lastIndexOf(regex));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string AFTER some REGEX
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value AFTER the REGEX, or the value in case of no
	 *         REGEX found
	 */
	public static String getValueAfter(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.indexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string AFTER some REGEX (LAST)
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value AFTER the REGEX (LAST), or the value in case
	 *         of no REGEX found
	 */
	public static String getValueAfterLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.lastIndexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	/**
	 * Filling a number with zeroes ("0") if it is lower than "outputLength"
	 * digits
	 * 
	 * @param context
	 *            the current activity context
	 * @param input
	 *            an input string containing a number
	 * @return a number (in string format) which is always at least
	 *         "outputLength" digits
	 */
	public static String formatNumberOfDigits(String input, int outputLength) {
		String formatType = String.format(Locale.getDefault(), "%%0%dd",
				outputLength);

		try {
			input = String.format(formatType, Integer.parseInt(input));
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Remove the leading zeros in alphanumeric text with regex
	 * 
	 * @param input
	 *            an input string containing a number
	 * @return a number (in string format) with removed leading zeroes (if
	 *         exist)
	 */
	public static String removeLeadingZeroes(String input) {
		try {
			input = input.replaceFirst("^0+(?!$)", "");
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Get the difference between two hours in format HH:MM
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param afterTime
	 *            a time after the current moment in format HH:MM
	 * @param currTime
	 *            the current time in format HH:MM
	 * @return the difference between the times
	 */
	public static String getTimeDifference(Context context, String afterTime,
			String currTime) {
		String diff = "";
		int afterTimeMilis = 0;
		int currTimeMilis = 0;

		// In cases when it is after midnight
		if (afterTime.startsWith("00:") && !currTime.startsWith("00:")) {
			afterTime = afterTime.replaceAll("00:", "24:");
		}

		try {
			afterTimeMilis = new BigDecimal(afterTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(afterTime.split(":")[1]).intValue();
			currTimeMilis = new BigDecimal(currTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(currTime.split(":")[1]).intValue();

			diff = (afterTimeMilis - currTimeMilis) + "";

			if (!diff.contains("-")) {
				diff = (new BigDecimal(diff).intValue() / 60) + ":"
						+ (new BigDecimal(diff).intValue() % 60);
				diff = formatTime(context, diff);
			} else {
				diff = "---";
			}
		} catch (Exception e) {

		}

		return diff;
	}

	/**
	 * Format the remaining time in format ~1h,20m
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param difference
	 *            the difference between the times (current one and the one
	 *            after)
	 * @return the difference in format ~1h,20m
	 */
	public static String formatTime(Context context, String difference) {
		String diff = "";
		String[] differenceArr = difference.split(":");

		if (differenceArr.length == 2) {

			if ("".equals(differenceArr[0]) || "0".equals(differenceArr[0])) {
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.app_remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.app_remaining_minutes);
				}

				diff = "~" + differenceArr[1];
			} else {
				differenceArr[0] = differenceArr[0]
						+ context.getString(R.string.app_remaining_hours);
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.app_remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.app_remaining_minutes);
				}

				diff = "~" + differenceArr[0] + " " + differenceArr[1];
			}
		}

		return diff;
	}

	/**
	 * Function that format the direction name of the station or vehicle to be
	 * in correct format. In case of an empty string - return "".
	 * 
	 * @param directionName
	 *            the name of the direction
	 * @return the digits from the String
	 */
	public static String formatDirectionName(String directionName) {
		if (directionName != null && !"".equals(directionName)) {
			directionName = directionName.replaceAll(" - ", "@");
			directionName = directionName.replaceAll("- ", "@");
			directionName = directionName.replaceAll(" -", "@");

			String[] directionNameParts = directionName.trim().split("@");

			switch (directionNameParts.length) {
			case 1:
				directionName = directionNameParts[0];

				break;
			case 2:
			case 4:
				directionNameParts[0] = directionNameParts[0].trim();
				directionNameParts[0] = directionNameParts[0].replaceAll("\\(",
						" (");

				directionNameParts[1] = directionNameParts[1].trim();
				directionNameParts[1] = getValueBefore(directionNameParts[1],
						"(");
				directionNameParts[1] = getValueBefore(directionNameParts[1],
						"/");

				directionName = directionNameParts[0] + " - "
						+ directionNameParts[1];
				directionName = directionName.replaceAll(" +", " ");

				break;
			case 3:
			case 6:
				boolean isDirectionThreeParts = true;
				if (directionNameParts[0].equals(directionNameParts[2])) {
					isDirectionThreeParts = false;
				}

				directionNameParts[0] = directionNameParts[0].trim();
				directionNameParts[0] = directionNameParts[0].replaceAll("\\(",
						" (");

				directionNameParts[1] = directionNameParts[1].trim();

				directionNameParts[2] = directionNameParts[2].trim();
				directionNameParts[2] = getValueBefore(directionNameParts[2],
						"(");
				directionNameParts[2] = getValueBefore(directionNameParts[2],
						"/");

				if (isDirectionThreeParts) {
					directionName = directionNameParts[0] + " - "
							+ directionNameParts[1] + " - "
							+ directionNameParts[2];
				} else {
					directionName = directionNameParts[0] + " - "
							+ directionNameParts[1];
				}
				directionName = directionName.replaceAll(" +", " ");

				break;
			default:
				break;
			}
		} else {
			directionName = "";
		}

		return directionName;
	}

}
