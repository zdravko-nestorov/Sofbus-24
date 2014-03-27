package bg.znestorov.sofbus24.info_station;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;
import bg.znestorov.sofbus24.schedule_stations.Station;
import bg.znestorov.sofbus24.utils.Constants;

public class HtmlRequestStation {

	// LogCat TAG
	private static final String TAG = "HtmlRequestStation";

	// Constructor parameters
	private Station station;

	// Getting the station
	public HtmlRequestStation(Station station) {
		this.station = station;
	}

	// Getting the source file of the HTTP request
	public String getInformation(HttpGet httpGet) {
		// HTTP Client - created once (final)
		final DefaultHttpClient client = new DefaultHttpClient();
		
		// Create a response handler
		String htmlResult = null;

		try {
			htmlResult = client.execute(httpGet, new BasicResponseHandler());
		} catch (Exception e) {
			htmlResult = null;
			Log.d(TAG, "Could not load data from " + createURL());
		} finally {
			client.getConnectionManager().shutdown();
		}

		return htmlResult;
	}

	// Create CAPTCHA HTTPGet request
	public HttpGet createStationRequest() throws URISyntaxException {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(createURL()));

		return httpGet;
	}

	// Creating the URL
	private String createURL() {
		final List<NameValuePair> result = new ArrayList<NameValuePair>();

		result.add(new BasicNameValuePair(Constants.QUERY_STOP, station
				.getStop()));
		result.add(new BasicNameValuePair(Constants.QUERY_CHECK, "Check"));
		result.add(new BasicNameValuePair(Constants.QUERY_VT, station.getVt()));
		result.add(new BasicNameValuePair(Constants.QUERY_LID, station.getLid()));
		result.add(new BasicNameValuePair(Constants.QUERY_RID, station.getRid()));

		String returnURL = Constants.SCHEDULE_URL
				+ URLEncodedUtils.format(result, "UTF-8");

		return returnURL;
	}

}
