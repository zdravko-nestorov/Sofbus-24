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
import android.content.Intent;
import android.os.AsyncTask;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.main.PublicTransportSchedule;
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
public class RetrievePublicTransportStation extends
		AsyncTask<Void, Void, PublicTransportStation> {

	private Activity context;
	private ProgressDialog progressDialog;

	private PublicTransportStation ptStation;
	private DirectionsEntity ptDirectionsEntity;

	private int activeDirection;

	public RetrievePublicTransportStation(Activity context,
			ProgressDialog progressDialog, PublicTransportStation ptStation,
			DirectionsEntity ptDirectionsEntity) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.ptStation = ptStation;
		this.ptDirectionsEntity = ptDirectionsEntity;

		activeDirection = ptDirectionsEntity.getActiveDirection();
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
	protected PublicTransportStation doInBackground(Void... params) {
		DefaultHttpClient stationHttpClient = new DefaultHttpClient();

		try {
			HttpGet stationHttpRequest = createStationRequest();
			String htmlResult = stationHttpClient.execute(stationHttpRequest,
					new BasicResponseHandler());

			ProcessPublicTransportStation processPtDirection = new ProcessPublicTransportStation(
					context, ptStation, htmlResult);
			ptStation = processPtDirection.getStationFromHtml();
		} catch (Exception e) {
			ptStation = new PublicTransportStation();
		} finally {
			stationHttpClient.getConnectionManager().shutdown();
		}

		return ptStation;
	}

	@Override
	protected void onPostExecute(final PublicTransportStation ptStation) {
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			// Workaround used just in case the orientation is changed once
			// retrieving info
		}

		// Check if the information is correctly retrieved from SKGT site
		if (ptStation.isScheduleSet()) {
			Intent ptScheduleIntent = new Intent(context,
					PublicTransportSchedule.class);
			ptScheduleIntent.putExtra(
					Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE, ptStation);
			context.startActivity(ptScheduleIntent);
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
	private HttpGet createStationRequest() throws URISyntaxException {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(createStationUrlAddress()));

		return httpGet;
	}

	/**
	 * Create the station URL address
	 * 
	 * @return the URL address of the selected station
	 */
	private String createStationUrlAddress() {
		final List<NameValuePair> result = new ArrayList<NameValuePair>();

		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_STOP, ptStation.getId()));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_CH,
				Constants.SCHECULE_URL_STATION_SCHEDULE_CH_VALUE));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_VT, ptDirectionsEntity
						.getVt().get(activeDirection)));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_VT, ptDirectionsEntity
						.getVt().get(activeDirection)));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_LID, ptDirectionsEntity
						.getLid().get(activeDirection)));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_RID, ptDirectionsEntity
						.getRid().get(activeDirection)));
		result.add(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_H,
				Constants.SCHECULE_URL_STATION_SCHEDULE_H_VALUE));

		String returnURL = Constants.SCHECULE_URL_STATION_SCHEDULE
				+ URLEncodedUtils.format(result, "UTF-8");

		return returnURL;
	}
}