package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

import android.content.Context;
import android.content.SharedPreferences;
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

	// Get station name from HTML src
	public static String getStationName(String htmlSrc, String tempHtmlSrc,
			String stationCode, String language) {
		String stationName = getValueAfter(htmlSrc, "<b>спирка");
		stationName = getValueBefore(stationName, "</b>");
		stationName = getValueBefore(stationName, "(").trim();
		stationName = getValueAfter(stationName, "&nbsp;");
		stationName = getValueBefore(stationName, "&nbsp;");

		// Special case when the number of the station is in some stations'
		// names
		if (stationName.length() > 100) {
			stationName = getValueBefore(tempHtmlSrc, "(" + stationCode + ")");

			if (stationName.contains("&nbsp;")) {
				stationName = stationName.substring(0,
						stationName.lastIndexOf("&nbsp;"));
			}

			if (stationName.contains("&nbsp;")) {
				stationName = stationName.substring(stationName
						.lastIndexOf("&nbsp;") + 6);
			}

			if (stationName.contains("<b>")) {
				stationName = stationName.substring(stationName
						.lastIndexOf("<b>") + 3);
				stationName = getValueAfter(stationName, ".");
			}

			stationName = stationName.trim();
		}

		// Check which language is chosen from Preferences
		if ("bg".equals(language)) {
			return stationName;
		} else {
			return TranslatorCyrillicToLatin.translate(stationName);
		}
	}

	// Get station ID from HTML src
	public static String getStationId(String htmlSrc, String stationCode) {
		String stationId = getValueAfter(htmlSrc, "<b>спирка");
		stationId = getValueAfter(stationId, "(");
		stationId = getValueBefore(stationId, ")").trim();

		if (stationId.length() > 100) {
			stationId = stationCode;
		}

		return stationId;
	}

	// Get information time from HTML src
	public static String getInformationTime(String htmlSrc,
			SharedPreferences sharedPreferences) {
		String infoTime = "";

		// Get "timeInfoRetrieval" value from the Shared Preferences
		String timeInfoRetrieval = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_INFO_RETRIEVAL,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_INFO_RETRIEVAL);

		if (htmlSrc.contains(Constants.TIME_RETRIEVAL_BEGIN)
				&& "time_skgt".equals(timeInfoRetrieval)) {
			infoTime = getValueAfter(htmlSrc, Constants.TIME_RETRIEVAL_BEGIN);
			infoTime = getValueBefore(infoTime, Constants.TIME_RETRIEVAL_END);
			infoTime = infoTime.trim();
		} else {
			infoTime = android.text.format.DateFormat.format("dd.MM.yyy kk:mm",
					new java.util.Date()).toString();
		}

		return infoTime;
	}
}
