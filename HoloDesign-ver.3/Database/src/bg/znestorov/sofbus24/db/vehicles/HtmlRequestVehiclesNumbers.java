package bg.znestorov.sofbus24.db.vehicles;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import bg.znestorov.sofbus24.db.utils.Constants;

public class HtmlRequestVehiclesNumbers {

	public static String retrieveVehiclesNumbers(Logger logger) {

		String htmlResponse = "";

		try {
			logger.info(
					"Start retrieving information about Vehicles numbers...");
			long startTime = System.currentTimeMillis();

			URL url = new URL(Constants.DB_VEHICLES_NUMBERS_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent",
					Constants.DB_VEHICLES_USER_AGENT);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "UTF-8"));
			long endTime = System.currentTimeMillis();
			logger.info(
					"Server sent a response, which is transformed in UTF-8, for "
							+ ((endTime - startTime) / 1000) + " seconds");

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			htmlResponse = response.toString();
		} catch (Exception e) {
			logger.severe(e.toString());
		}

		return htmlResponse;
	}

}
