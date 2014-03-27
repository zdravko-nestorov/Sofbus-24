package com.example.info_station;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
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

import com.example.schedule_stations.Station;

public class HtmlRequestStation {

	// LogCat TAG
	private static final String TAG = "HtmlRequestStation";

	// URL and variables
	private static final String URL = "http://m.sumc.bg/schedules/vehicle?";
	private static final String QUERY_STOP = "stop";
	private static final String QUERY_CHECK = "ch";
	private static final String QUERY_VT = "vt";
	private static final String QUERY_LID = "lid";
	private static final String QUERY_RID = "rid";

	// Constructor parameters
	private Station station;

	// Getting the station
	public HtmlRequestStation(Station station) {
		this.station = station;
	}

	// Getting the source file of the HTTP request
	public String getInformation() {
		String htmlResult = null;

		try {
			// Setting timeout parameters
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

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

			HttpGet request = new HttpGet();
			request.setURI(new URI(createURL()));
			htmlResult = client.execute(request, new BasicResponseHandler());
			client.getConnectionManager().shutdown();
		} catch (Exception e) {
			htmlResult = null;
			Log.d(TAG, "Could not load data from " + createURL());
		}

		return htmlResult;
	}

	// Creating the URL
	private String createURL() {
		final List<NameValuePair> result = new ArrayList<NameValuePair>();

		result.add(new BasicNameValuePair(QUERY_STOP, station.getStop()));
		result.add(new BasicNameValuePair(QUERY_CHECK, "Check"));
		result.add(new BasicNameValuePair(QUERY_VT, station.getVt()));
		result.add(new BasicNameValuePair(QUERY_LID, station.getLid()));
		result.add(new BasicNameValuePair(QUERY_RID, station.getRid()));

		String returnURL = URL + URLEncodedUtils.format(result, "UTF-8");

		return returnURL;
	}

}
