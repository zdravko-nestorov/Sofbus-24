package bg.znestorov.sofbus24.publictransport;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.main.PublicTransport;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Async class used to retrieve the public transport directions from the SKGT
 * site (directions' names and stations)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrievePublicTransportDirection extends
		AsyncTask<Void, Void, DirectionsEntity> {

	private Activity context;
	private ProgressDialog progressDialog;
	private Vehicle vehicle;

	public RetrievePublicTransportDirection(Activity context,
			ProgressDialog progressDialog, Vehicle vehicle) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.vehicle = vehicle;
	}

	@Override
	protected void onPreExecute() {
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
	protected DirectionsEntity doInBackground(Void... params) {
		DirectionsEntity ptDirectionsEntity;
		DefaultHttpClient directionHttpClient = new DefaultHttpClient();

		try {
			HttpGet directionHttpRequest = createDirectionRequest();
			String htmlResult = directionHttpClient.execute(
					directionHttpRequest, new BasicResponseHandler());

			ProcessPublicTransportDirection processPtDirection = new ProcessPublicTransportDirection(
					context, vehicle, htmlResult);
			ptDirectionsEntity = processPtDirection.getDirectionsFromHtml();
		} catch (Exception e) {
			ptDirectionsEntity = new DirectionsEntity();
		} finally {
			directionHttpClient.getConnectionManager().shutdown();
		}

		return ptDirectionsEntity;
	}

	@Override
	protected void onPostExecute(final DirectionsEntity ptDirectionsEntity) {
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			// Workaround used just in case the orientation is changed once
			// retrieving info
		}

		// Check if the information is correctly retrieved from SKGT site
		if (ptDirectionsEntity.isDirectionSet()) {
			ArrayAdapter<String> ptDirectionsAdapter = new ArrayAdapter<String>(
					context,
					R.layout.activity_public_transport_directions_item,
					ptDirectionsEntity.getDirectionsNames());

			// Create a ListView, containing all directions from the HTML result
			new AlertDialog.Builder(context)
					.setTitle(R.string.sch_item_direction_choice)
					.setAdapter(ptDirectionsAdapter,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									ptDirectionsEntity.setActiveDirection(i);
									Intent publicTransport = new Intent(
											context, PublicTransport.class);
									publicTransport
											.putExtra(
													Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
													ptDirectionsEntity);
									context.startActivity(publicTransport);
								}
							}).show();
		} else {
			ActivityUtils.showNoInternetOrInfoAlertDialog(context);
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
	 * Create HttpGet request to retrieve the information about the selected
	 * vehicle
	 * 
	 * @return a HttpGet request for the selected vehicle
	 * @throws URISyntaxException
	 */
	private HttpGet createDirectionRequest() throws URISyntaxException {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(createDirectionUrlAddress()));

		return httpGet;
	}

	/**
	 * Create the direction URL address
	 * 
	 * @return the URL address of the directions for the selected vehicle
	 */
	private String createDirectionUrlAddress() {
		final List<NameValuePair> result = new ArrayList<NameValuePair>();

		result.add(new BasicNameValuePair(Constants.URL_DIRECTION_BUS_TYPE,
				getVehicleType(vehicle)));
		result.add(new BasicNameValuePair(Constants.URL_DIRECTION_LINE, vehicle
				.getNumber()));
		result.add(new BasicNameValuePair(Constants.URL_DIRECTION_SEARCH,
				Constants.URL_DIRECTION_SEARCH_VALUE));

		String returnURL = Constants.URL_DIRECTION
				+ URLEncodedUtils.format(result, "UTF-8");

		return returnURL;
	}

	/**
	 * Get the corresponding vehicle type code for the vehicle type<br>
	 * <ul>
	 * <li>BUS - vehicle type code "1"</li>
	 * <li>TROLLEY - vehicle type code "2"</li>
	 * <li>TRAM - vehicle type code "0"</li>
	 * </ul>
	 * 
	 * @param vehicle
	 *            the selected vehicle
	 * @return the corresponding vehicle type code
	 */
	private String getVehicleType(Vehicle vehicle) {
		String vehicleType;

		switch (vehicle.getType()) {
		case BUS:
			vehicleType = "1";
			break;
		case TROLLEY:
			vehicleType = "2";
			break;
		default:
			vehicleType = "0";
			break;
		}

		return vehicleType;
	}
}
