package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import bg.znestorov.sofbus24.main.R;

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

	// Get value AFTER some REGEX (Last)
	public static String getValueAfterLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.lastIndexOf(regex) + regex.length());
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

	// Format the number to be always 4 digits (if lower than 4)
	public static String formatNumberOfDigits(String input) {
		int outputLength = 4;

		String formatType = String.format("%%0%dd", outputLength);

		try {
			input = String.format(formatType, Integer.parseInt(input));
		} catch (Exception e) {
		}

		return input;
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

	// Get "o" code using the station ID
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

	// Get station name from HTML source
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

	// Get station ID from HTML source
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

	// Get station ID from HTML source
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

	// Request focus and show keyboard on EditText
	public static void showKeyboard(Context context, EditText editText) {
		// Focus the field
		editText.requestFocus();

		// Show soft keyboard for the user to enter the value
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	// Request focus and show keyboard on EditText
	public static void hideKeyboard(Context context, EditText editText) {
		// Hide soft keyboard.
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
