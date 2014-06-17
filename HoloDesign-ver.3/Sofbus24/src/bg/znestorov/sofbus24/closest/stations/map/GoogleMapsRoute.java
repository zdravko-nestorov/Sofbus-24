package bg.znestorov.sofbus24.closest.stations.map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;
import bg.znestorov.sofbus24.main.ClosestStationsMap;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.MapUtils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Asynchronic class used to retrieve the route between a map location and a
 * station
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class GoogleMapsRoute extends AsyncTask<Void, Void, String> {

	private Activity context;
	private Object callerInstance;

	private ProgressDialog progressDialog;
	private String routeUrl;
	private String distance;

	public GoogleMapsRoute(Activity context, Object callerInstance,
			Location currentLocation, LatLng latLng) {
		this.context = context;
		this.callerInstance = callerInstance;

		this.routeUrl = createRouteUrl(currentLocation, latLng);
		this.distance = String.format(context.getString(R.string.app_distance),
				MapUtils.getMapDistance(context,
						new LatLng(currentLocation.getLatitude(),
								currentLocation.getLongitude()), latLng));
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.cs_map_fetch_route));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						cancel(true);
					}
				});
		progressDialog.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		return getJSONFromUrl(routeUrl);
	}

	@Override
	protected void onPostExecute(String jsonResult) {
		super.onPostExecute(jsonResult);

		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			// Workaround used just in case when this activity is destroyed
			// before the dialog
		}

		if (jsonResult != null && !"".equals(jsonResult)) {
			((ClosestStationsMap) callerInstance).visualizeRoute(jsonResult);

			Toast.makeText(context, distance, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context,
					context.getString(R.string.cs_map_fetch_route_error),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			// Workaround used just in case when this activity is destroyed
			// before the dialog
		}
	}

	/**
	 * Create google apis route url containing the points between the current
	 * location and the selected station
	 * 
	 * @param currentLocation
	 *            the current location
	 * @param latLng
	 *            the selected LatLng
	 * @return a google apis route url
	 */
	private String createRouteUrl(Location currentLocation, LatLng latLng) {
		StringBuilder routeUrl = new StringBuilder();
		routeUrl.append("http://maps.googleapis.com/maps/api/directions/json");
		routeUrl.append("?origin=");// from
		routeUrl.append(currentLocation.getLatitude());
		routeUrl.append(",");
		routeUrl.append(currentLocation.getLongitude());
		routeUrl.append("&destination=");// to
		routeUrl.append(latLng.latitude);
		routeUrl.append(",");
		routeUrl.append(latLng.longitude);
		routeUrl.append("&sensor=false&mode=driving&alternatives=true");

		return routeUrl.toString();
	}

	/**
	 * Get the JSON object from the Google apis root url address and transform
	 * it to a string object
	 * 
	 * @param routeUrl
	 *            the google apis route url
	 * @return string representation of the JSON result
	 */
	private String getJSONFromUrl(String routeUrl) {
		InputStream is = null;
		String jsonString;

		try {
			// Create a DefaultHttpClient and make an HTTP request
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(routeUrl);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			// Converting the result to a string object
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "ISO-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			jsonString = sb.toString();
			is.close();
		} catch (Exception e) {
			jsonString = "";
		}

		return jsonString;
	}
}
