package bg.znestorov.sofbus24.metro.stations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.Constants;

/**
 * Responsible for sending a HTTP GET request to the SKGT site and retrieve
 * information about the DIRECTIONs and STATIONs of the METRO
 * 
 * @author zanio
 * 
 */
public class HtmlRequest {

	/**
	 * Retrieve the information from SGKT site
	 * 
	 * @param logger
	 *            the logger, which is creating a stack trace about the progress
	 *            of the program
	 * @return the HTTP response with all needed information
	 */
	public static String retrieveStationsInfo(Logger logger) {

		String htmlResponse = "";

		try {
			logger.info("Start retrieving information about Metro directions and schedule...");
			long startTime = System.currentTimeMillis();

			URL url = new URL(Constants.METRO_SCHEDULE_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", Constants.METRO_USER_AGENT);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));
			long endTime = System.currentTimeMillis();
			logger.info("Server sent a response, which is transformed in UTF-8, for "
					+ ((endTime - startTime) / 1000) + " seconds");

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			htmlResponse = response.toString();
		} catch (Exception e) {
			logger.warning(e.toString());
		}

		return htmlResponse;
	}

}
