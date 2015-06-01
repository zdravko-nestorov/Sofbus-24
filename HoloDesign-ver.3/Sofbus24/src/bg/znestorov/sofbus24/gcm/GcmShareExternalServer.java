package bg.znestorov.sofbus24.gcm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Class used to share the registration id with the application server (save the
 * registration ids and send notification to them latelty)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class GcmShareExternalServer {

	/**
	 * Share the registration id with the application server (used to save the
	 * registration ids and send notification to them latelty)
	 * 
	 * @param context
	 *            the current Activity context
	 * @param regId
	 *            the registration id
	 * @return if the reg id is successfully shared with the server
	 */
	public static Boolean shareRegIdWithAppServer(Activity context, String regId) {

		Boolean isSharedSuccessful;
		HttpURLConnection httpConnection = null;

		try {
			URL serverUrl = new URL(createUrlAddress(regId));

			// Map containing the params of the HTTP POST request
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put(Constants.GCM_EXTERNAL_SERVER_URL_REG_ID_ATT, regId);
			Iterator<Entry<String, String>> iterator = paramsMap.entrySet()
					.iterator();

			// Iterate over all params in the Map and put them in the body of
			// the request
			StringBuilder postBody = new StringBuilder();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				postBody.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					postBody.append('&');
				}
			}

			String body = postBody.toString();
			byte[] bytes = body.getBytes();

			// Configure the HTTP connection and send it to the server
			httpConnection = (HttpURLConnection) serverUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setUseCaches(false);
			httpConnection.setFixedLengthStreamingMode(bytes.length);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStream out = httpConnection.getOutputStream();
			out.write(bytes);
			out.close();

			int status = httpConnection.getResponseCode();
			if (status == 200) {
				isSharedSuccessful = true;
			} else {
				isSharedSuccessful = false;
			}

		} catch (IOException e) {
			isSharedSuccessful = false;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}

		return isSharedSuccessful;
	}

	/**
	 * Create the URL address, used to share the registration id with the
	 * external server. To get the final secret value we make the following:<br/>
	 * "URL" + "REG_ID" + "URL_SECRET_VALUE" (the secret word is
	 * 'Sofbus24-SecretWord-ForRegistration' after SHA1) and hash it via SHA1.<br/>
	 * 
	 * The result value is appended as a "sec" param to the end of the URL.
	 * 
	 * @param regId
	 *            the registration id of the device/user
	 * 
	 * @return the url address, used to sed to share the registration id with
	 *         the external server
	 */
	private static String createUrlAddress(String regId) {

		String urlAddress = Constants.GCM_EXTERNAL_SERVER_URL
				+ "?"
				+ Constants.GCM_EXTERNAL_SERVER_URL_SECRET_ATT
				+ "="
				+ getSha1Digest(Constants.GCM_EXTERNAL_SERVER_URL + regId
						+ Constants.GCM_EXTERNAL_SERVER_URL_SECRET_VALUE);

		return urlAddress;
	}

	/**
	 * Make a SHA1 digest of an input string
	 * 
	 * @param input
	 *            the input string
	 * @return the result after SHA1 hash
	 */
	private static String getSha1Digest(String input) {

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

}