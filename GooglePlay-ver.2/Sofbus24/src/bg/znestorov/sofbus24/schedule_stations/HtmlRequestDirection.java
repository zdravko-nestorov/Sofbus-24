package bg.znestorov.sofbus24.schedule_stations;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

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

import android.content.Context;
import android.util.Log;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

public class HtmlRequestDirection {

	// LogCat TAG
	private static final String TAG = "HtmlRequestDirection";

	private String vehicleType;
	private String vehicleNumber;

	// Getting the vehicle type and number
	public HtmlRequestDirection(Context context, String vehicleChoice) {

		this.vehicleType = getValueBefore(vehicleChoice, "$");

		if (this.vehicleType.equals(context.getString(R.string.title_bus))) {
			this.vehicleType = "1";
		} else if (this.vehicleType.equals(context
				.getString(R.string.title_trolley))) {
			this.vehicleType = "2";
		} else {
			this.vehicleType = "0";
		}

		this.vehicleNumber = getValueAfter(vehicleChoice, "$");
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
	public HttpGet createDirectionRequest() throws URISyntaxException {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(createURL()));

		return httpGet;
	}

	// Creating the URL
	private String createURL() {
		final List<NameValuePair> result = new ArrayList<NameValuePair>();

		result.add(new BasicNameValuePair(Constants.QUERY_BUS_TYPE, vehicleType));
		result.add(new BasicNameValuePair(Constants.QUERY_LINE, vehicleNumber));
		result.add(new BasicNameValuePair(Constants.QUERY_SEARCH, "Search"));

		String returnURL = Constants.DIRECTION_URL
				+ URLEncodedUtils.format(result, "UTF-8");

		return returnURL;
	}

}
