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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.main.History;
import bg.znestorov.sofbus24.schedule.ScheduleVehicleFragment;
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
	private Object callerInstance;
	private ProgressDialog progressDialog;
	private VehicleEntity vehicle;

	public RetrievePublicTransportDirection(Activity context,
			Object callerInstance, ProgressDialog progressDialog,
			VehicleEntity vehicle) {
		this.context = context;
		this.callerInstance = callerInstance;
		this.progressDialog = progressDialog;
		this.vehicle = vehicle;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
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
		super.onPostExecute(ptDirectionsEntity);

		// Check if the information is correctly retrieved from SKGT site
		if (ptDirectionsEntity.isDirectionSet()) {

			// Get the fragment manager to start the dialog fragment
			FragmentManager fragmentManager;
			if (callerInstance instanceof ScheduleVehicleFragment) {
				fragmentManager = ((ScheduleVehicleFragment) callerInstance)
						.getChildFragmentManager();
			} else {
				fragmentManager = ((History) callerInstance)
						.getSupportFragmentManager();
			}

			// Show the dialog fragment with the directions
			try {
				DialogFragment dialogFragment = ChooseDirectionDialog
						.newInstance(ptDirectionsEntity);
				dialogFragment.show(fragmentManager, "dialog");
			} catch (Exception e) {
				/*
				 * Strange bug reported in GooglePlay - may be the orientation
				 * is released before the dialog fragment is shown. In this case
				 * the app crashes because the dialog tries to show after
				 * savedInstanceState() method is called.
				 * 
				 * GooglePlayError: java.lang.IllegalStateException: Can not
				 * perform this action after onSaveInstanceState
				 */
			}
		} else {
			ActivityUtils.showNoInternetOrInfoToast(context);
		}

		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		dismissLoadingView();
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

		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_DIRECTION_BUS_TYPE,
				getVehicleType(vehicle)));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_DIRECTION_LINE, vehicle.getNumber()));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_DIRECTION_SEARCH,
				Constants.SCHECULE_URL_DIRECTION_SEARCH_VALUE));

		String returnURL = Constants.SCHECULE_URL_DIRECTION
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
	private String getVehicleType(VehicleEntity vehicle) {
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

	/**
	 * Create the loading view and lock the screen
	 */
	private void createLoadingView() {
		ActivityUtils.lockScreenOrientation(context);

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

	/**
	 * Dismiss the loading view and unlock the screen
	 */
	private void dismissLoadingView() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		ActivityUtils.unlockScreenOrientation(context);
	}
}
