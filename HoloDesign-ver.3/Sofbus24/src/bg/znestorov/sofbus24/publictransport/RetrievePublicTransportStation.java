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
import bg.znestorov.sofbus24.entity.PublicTransportStationEntity;
import bg.znestorov.sofbus24.main.PublicTransportSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
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
		AsyncTask<Void, Void, PublicTransportStationEntity> {

	private Activity context;
	private ProgressDialog progressDialog;

	private PublicTransportStationEntity ptStation;
	private DirectionsEntity ptDirectionsEntity;

	private int activeDirection;

	public RetrievePublicTransportStation(Activity context,
			ProgressDialog progressDialog, PublicTransportStationEntity ptStation,
			DirectionsEntity ptDirectionsEntity) {
		this.context = context;
		this.progressDialog = progressDialog;

		this.ptStation = ptStation;
		this.ptDirectionsEntity = ptDirectionsEntity;

		this.activeDirection = ptDirectionsEntity.getActiveDirection();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
	}

	@Override
	protected PublicTransportStationEntity doInBackground(Void... params) {
		DefaultHttpClient stationHttpClient = new DefaultHttpClient();

		try {
			HttpGet stationHttpRequest = createStationRequest();
			String htmlResult = stationHttpClient.execute(stationHttpRequest,
					new BasicResponseHandler());

			ProcessPublicTransportStation processPtStation = new ProcessPublicTransportStation(
					context, ptStation, htmlResult);
			ptStation = processPtStation.getStationFromHtml();
		} catch (Exception e) {
			ptStation = new PublicTransportStationEntity();
		} finally {
			stationHttpClient.getConnectionManager().shutdown();
		}

		return ptStation;
	}

	@Override
	protected void onPostExecute(final PublicTransportStationEntity ptStation) {
		super.onPostExecute(ptStation);

		// Check if the information is correctly retrieved from SKGT site
		if (ptStation.isScheduleSet()) {
			Utils.addStationInHistory(context, ptStation);

			Intent ptScheduleIntent = new Intent(context,
					PublicTransportSchedule.class);
			ptScheduleIntent.putExtra(
					Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE, ptStation);
			context.startActivity(ptScheduleIntent);
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