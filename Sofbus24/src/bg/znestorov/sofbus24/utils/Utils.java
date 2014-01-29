package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import bg.znestorov.sofbus24.main.R;

/**
 * Creating methods for easy processing data
 * 
 * @author znestorov
 * 
 */
public class Utils {

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

	/**
	 * Filling a number with zeroes ("0") if it is lower than 4 digits
	 * 
	 * @param input
	 *            an input string containg a number
	 * @return a number (in string format) which is always at least 4 digits
	 */
	public static String formatNumberOfDigits(String input) {
		int outputLength = 4;

		String formatType = String.format("%%0%dd", outputLength);

		try {
			input = String.format(formatType, Integer.parseInt(input));
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Format Date (making the minutes in format :XX)
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
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.remaining_minutes);
				}

				diff = "~" + differenceArr[0] + " " + differenceArr[1];
			}
		}

		return diff;
	}

	/**
	 * Get "o" code using the station ID
	 * 
	 * @param htmlSrc
	 *            the source code of the page
	 * @param stationCode
	 *            the code of the station
	 * @return the position of the station in case of multiple results
	 */
	public static String getCodeO(String htmlSrc, String stationCode) {
		String codeO = "1";
		stationCode = Constants.STATION_INFO_END_2 + stationCode
				+ Constants.STATION_INFO_END_3;

		if (htmlSrc.contains(stationCode)) {
			codeO = getValueBeforeLast(htmlSrc, stationCode);

			if (codeO.contains(Constants.SCHEDULE_GPS_FIND_CODEO)) {
				codeO = Integer.toString(codeO
						.split(Constants.SCHEDULE_GPS_FIND_CODEO).length - 1);
			}
		}

		return codeO;
	}

	/**
	 * Parse the HTML source file using the station code to get the station
	 * name. It is transcoded to latin in case of differen than BG language
	 * 
	 * @param htmlSrc
	 *            the source code of the page
	 * @param tempHtmlSrc
	 *            the source code of the page (temp version)
	 * @param stationCode
	 *            the code of the station
	 * @param language
	 *            the chosen language of the application
	 * @return the name of the staion according to the station code
	 */
	public static String getStationName(String htmlSrc, String tempHtmlSrc,
			String stationCode, String language) {
		String stationName = getValueAfter(htmlSrc,
				Constants.STATION_INFO_BEGIN);
		stationName = getValueBefore(stationName, Constants.STATION_INFO_END_1);
		stationName = getValueBeforeLast(stationName,
				Constants.STATION_INFO_END_2).trim();
		stationName = getValueAfter(stationName,
				Constants.STATION_INFO_SEPARATOR_SPACE);
		stationName = getValueBefore(stationName,
				Constants.STATION_INFO_SEPARATOR_SPACE);

		// Special case when the number of the station is in some stations'
		// names
		if (stationName.length() > 100) {
			stationName = getValueBefore(tempHtmlSrc,
					Constants.STATION_INFO_END_2 + stationCode
							+ Constants.STATION_INFO_END_3);

			if (stationName.contains(Constants.STATION_INFO_SEPARATOR_SPACE)) {
				stationName = stationName.substring(0, stationName
						.lastIndexOf(Constants.STATION_INFO_SEPARATOR_SPACE));
			}

			if (stationName.contains(Constants.STATION_INFO_SEPARATOR_SPACE)) {
				stationName = stationName
						.substring(stationName
								.lastIndexOf(Constants.STATION_INFO_SEPARATOR_SPACE) + 6);
			}

			if (stationName.contains(Constants.STATION_INFO_SEPARATOR_BOLD)) {
				stationName = stationName
						.substring(stationName
								.lastIndexOf(Constants.STATION_INFO_SEPARATOR_BOLD) + 3);
				stationName = getValueAfter(stationName,
						Constants.STATION_INFO_SEPARATOR_POINT);
			}

			stationName = stationName.trim();
		}

		if (stationName.length() > 100) {
			stationName = stationCode;
		}

		// Check which language is chosen from Preferences
		if ("bg".equals(language)) {
			return stationName;
		} else {
			return TranslatorCyrillicToLatin.translate(stationName);
		}
	}

	/**
	 * Parse the HTML source file using the station code to get the station Id.
	 * 
	 * @param htmlSrc
	 *            the source code of the page
	 * @param stationCode
	 *            the code of the station
	 * @return the station Id according to the station code
	 */
	public static String getStationId(String htmlSrc, String stationCode) {
		String stationId = getValueAfter(htmlSrc, Constants.STATION_INFO_BEGIN);
		stationId = getValueAfter(stationId, Constants.STATION_INFO_END_2);
		while (stationId.contains(Constants.STATION_INFO_END_2)
				&& !Character.isDigit(stationId.charAt(0))) {
			stationId = getValueAfter(stationId, Constants.STATION_INFO_END_2);
		}
		stationId = getValueBefore(stationId, Constants.STATION_INFO_END_3)
				.trim();

		if (stationId.length() > 100) {
			stationId = stationCode;
		}

		return stationId;
	}

	/**
	 * Parse the HTML source file using the station code to get the station Id.
	 * 
	 * @param htmlSrc
	 *            the source code of the page
	 * @param stationCode
	 *            the code of the station
	 * @param stationCodeO
	 *            the position of the station in case of multiple results
	 * @return the station Id according to the station code
	 */
	public static String getStationId(String htmlSrc, String stationCode,
			String stationCodeO) {
		String stationId = getValueAfter(htmlSrc,
				Constants.STATION_INFO_SEPARATOR_BOLD + stationCodeO
						+ Constants.STATION_INFO_SEPARATOR_POINT);
		stationId = getValueAfter(stationId, Constants.STATION_INFO_END_2);
		while (stationId.contains(Constants.STATION_INFO_END_2)
				&& !Character.isDigit(stationId.charAt(0))) {
			stationId = getValueAfter(stationId, Constants.STATION_INFO_END_2);
		}
		stationId = getValueBefore(stationId, Constants.STATION_INFO_END_3)
				.trim();

		if (stationId.length() > 100) {
			stationId = stationCode;
		}

		return stationId;
	}

	/**
	 * Request the focus and show a keyboard on EditText field
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param editText
	 *            the EditText field
	 */
	public static void showKeyboard(Context context, EditText editText) {
		// Focus the field
		editText.requestFocus();

		// Show soft keyboard for the user to enter the value
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * Request the focus and hide the keyboard on EditText field
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param editText
	 *            the EditText field
	 */
	public static void hideKeyboard(Context context, EditText editText) {
		// Hide soft keyboard
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
