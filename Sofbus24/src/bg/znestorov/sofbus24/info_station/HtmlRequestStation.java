package bg.znestorov.sofbus24.info_station;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

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
		String htmlResult = null;

		try {
			// Setting timeout parameters
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					Constants.GLOBAL_TIMEOUT_CONNECTION);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters,
					Constants.GLOBAL_TIMEOUT_SOCKET);
			ConnManagerParams.setTimeout(httpParameters,
					Constants.GLOBAL_TIMEOUT_SOCKET);

			// Creating ThreadSafeClientConnManager
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			final SSLSocketFactory sslSocketFactory = SSLSocketFactory
					.getSocketFactory();
			schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
			ClientConnectionManager cm = new ThreadSafeClientConnManager(
					httpParameters, schemeRegistry);

			// HTTP Client - created once and using cookies
			DefaultHttpClient client = new DefaultHttpClient(cm, httpParameters);
			htmlResult = client.execute(httpGet, new BasicResponseHandler());
			client.getConnectionManager().shutdown();
		} catch (Exception e) {
			htmlResult = null;
			Log.d(TAG, "Could not load data from " + createURL());
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
