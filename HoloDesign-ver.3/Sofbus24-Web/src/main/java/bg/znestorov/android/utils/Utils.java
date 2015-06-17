package bg.znestorov.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	/**
	 * Tests if the supplied string is NULL or 0-length.
	 * 
	 * @param String
	 *            input
	 * @return boolean TRUE if empty, otherwise FALSE
	 */
	public static boolean isEmpty(String input) {
		return input == null || input.trim().length() == 0;
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
	 * Get the value of the request parameter
	 * 
	 * @param paramName
	 *            the parameter name
	 * @param responseBody
	 *            the response body
	 * @return the value of the request parameter
	 */
	public static String getRequestParamValue(String paramName,
			String responseBody) {

		String requestParamValue = "";
		if (!isEmpty(paramName) && !isEmpty(responseBody)) {

			String[] responseBodyArr = responseBody.split("&");
			for (String param : responseBodyArr) {

				if (param.toUpperCase().contains(paramName.toUpperCase())
						&& param.contains("=")) {
					requestParamValue = getValueAfter(param, "=");
					break;
				}
			}
		}

		return requestParamValue;
	}

	/**
	 * Make a SHA1 digest of an input string
	 * 
	 * @param input
	 *            the input string
	 * @return the result after SHA1 hash
	 */
	public static String getSha1Digest(String input) {

		String sha1Digest = "";

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			// It should never get in here
		}

		md.reset();
		byte[] buffer = input.getBytes();
		md.update(buffer);
		byte[] digest = md.digest();

		// Converts the Byte Array to Hex String
		for (int i = 0; i < digest.length; i++) {
			sha1Digest += Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1);
		}

		return sha1Digest;
	}

	/**
	 * Get the current date in format DD.MM.YYY
	 * 
	 * @return the current date in format DD.MM.YYY
	 */
	public static String getCurrentDate() {

		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();

		return dateFormat.format(date);
	}

	/**
	 * Get the current date and time in format DD.MM.YYY HH:MM:SS
	 * 
	 * @return the current date and time in format DD.MM.YYY HH:MM:SS
	 */
	public static String getCurrentDateTime() {

		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = new Date();

		return dateFormat.format(date);
	}

}