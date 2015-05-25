package bg.znestorov.sofbus24.gcm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

		// Map containing the params of the HTTP POST request
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("regId", regId);

		try {
			URL serverUrl = new URL(Constants.GCM_EXTERNAL_SERVER_URL);

			StringBuilder postBody = new StringBuilder();
			Iterator<Entry<String, String>> iterator = paramsMap.entrySet()
					.iterator();

			// Iterate over all params in the Map and put them in the body of
			// the request
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

			// Create the HTTP connection and send it to the server
			HttpURLConnection httpConnection = null;
			try {
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
			} finally {
				if (httpConnection != null) {
					httpConnection.disconnect();
				}
			}

		} catch (IOException e) {
			isSharedSuccessful = false;
		}

		return isSharedSuccessful;
	}

}