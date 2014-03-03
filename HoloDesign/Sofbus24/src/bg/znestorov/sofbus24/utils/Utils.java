package bg.znestorov.sofbus24.utils;

import java.util.Locale;

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

}
