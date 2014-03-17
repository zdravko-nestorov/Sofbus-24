package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;
import java.util.Locale;

import android.content.Context;
import bg.znestorov.sofbus24.main.R;

public class Utils {

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

}
