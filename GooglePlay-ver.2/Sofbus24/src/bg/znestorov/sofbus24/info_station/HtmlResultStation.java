package bg.znestorov.sofbus24.info_station;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.Utils;

// Getting the times of the HtmlRequestStation
public class HtmlResultStation {

	// Shared Preferences (option menu)
	private static SharedPreferences sharedPreferences;

	// Checking the HtmlResult and filling the times in ArrayList<String>
	public static String showResult(Context context, String htmlResult) {
		ArrayList<String> time_stamp = null;

		if (htmlResult != null && !"".equals(htmlResult)) {
			time_stamp = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2})");
			Matcher matcher = pattern.matcher(htmlResult);

			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			while (matcher.find()) {
				int station_hour = Integer.parseInt(matcher.group(1));
				int station_minute = Integer.parseInt(matcher.group(2));

				String tempTimeStamp = matcher.group();
				if (tempTimeStamp.startsWith("00:")) {
					tempTimeStamp = tempTimeStamp.replaceAll("00:", "24:");
				}

				if (station_hour > hour
						&& time_stamp.size() < Constants.MAX_COUNT_SCHEDULE_TIME) {
					time_stamp.add(tempTimeStamp);
				} else if (station_hour == hour
						&& station_minute >= minute
						&& time_stamp.size() < Constants.MAX_COUNT_SCHEDULE_TIME) {
					time_stamp.add(tempTimeStamp);
				}

			}

			// Sort the array in ASC order
			Collections.sort(time_stamp);

			// Get current time
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String currTime = sdf.format(cal.getTime());

			// Get SharedPreferences from option menu
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);

			// Get "exitAlert" value from the Shared Preferences
			String timeSchedule = sharedPreferences.getString(
					Constants.PREFERENCE_KEY_TIME_SCHEDULE,
					Constants.PREFERENCE_DEFAULT_VALUE_TIME_SCHEDULE);

			if (time_stamp.size() > 0
					&& "timeSchedule_remaining".equals(timeSchedule)) {
				for (int i = 0; i < time_stamp.size(); i++) {
					time_stamp.set(i, Utils.getDifference(context,
							time_stamp.get(i), currTime));
				}
			} else {
				for (int i = 0; i < time_stamp.size(); i++) {
					if (time_stamp.get(i).startsWith("24:")) {
						time_stamp.set(i,
								time_stamp.get(i).replaceAll("24:", "00:"));
					}
				}
			}

			// Get "language" value from the Shared Preferences
			String language = sharedPreferences.getString(
					Constants.PREFERENCE_KEY_LANGUAGE,
					Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);

			if ("bg".equals(language)) {
				return time_stamp.toString();
			} else {
				return TranslatorCyrillicToLatin.translate(time_stamp
						.toString());
			}
		}

		return null;
	}

}
