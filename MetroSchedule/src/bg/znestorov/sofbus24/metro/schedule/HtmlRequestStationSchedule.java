package bg.znestorov.sofbus24.metro.schedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.Constants;

public class HtmlRequestStationSchedule {

	/**
	 * Retrieve the information from SGKT site
	 * 
	 * @param logger
	 *            the logger, which is creating a stack trace about the progress
	 *            of the program
	 * @return the HTTP response with all needed information
	 */
	public static String retrieveStationsInfo(Logger logger,
			String directionId, Map.Entry<String, String> station) {

		String htmlResponse = "";

		try {
			logger.info("Start retrieving schedule...");

			URL url = new URL(String.format(Constants.STATION_SCHEDULE_URL,
					directionId, station.getKey()));
			logger.info("Station URL address = " + url.toString());

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", Constants.METRO_USER_AGENT);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));

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
