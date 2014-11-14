package bg.znestorov.sofbus24.droidtrans;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleStationEntity;
import bg.znestorov.sofbus24.entity.VirtualBoardsStationEntity;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Async class used to retrieve the droidtrans schedule from the SKGT site
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveDroidTransSchedule extends
		AsyncTask<Void, Void, VirtualBoardsStationEntity> {

	private Activity context;
	private ProgressDialog progressDialog;

	private VehicleStationEntity vehicleStationEntity;
	private StationEntity station;

	public RetrieveDroidTransSchedule(Activity context,
			ProgressDialog progressDialog,
			VehicleStationEntity vehicleStationEntity, StationEntity station) {

		this.context = context;
		this.progressDialog = progressDialog;

		this.vehicleStationEntity = vehicleStationEntity;
		this.station = station;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
	}

	@Override
	protected VirtualBoardsStationEntity doInBackground(Void... params) {

		VirtualBoardsStationEntity vbStation = new VirtualBoardsStationEntity(
				station);
		final DefaultHttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost httpPost = createScheduleRequest();
			String htmlResult = httpClient.execute(httpPost,
					new BasicResponseHandler());
			// TODO
		} catch (Exception e) {
			vbStation = null;
		} finally {
			// On ICS and later network operations can't be done on the UI
			// thread (GooglePlay bug: NetworkOnMainThreadException)
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					httpClient.getConnectionManager().shutdown();
					return null;
				}
			}.execute();
		}

		return vbStation;
	}

	@Override
	protected void onPostExecute(final VirtualBoardsStationEntity ptStation) {
		super.onPostExecute(ptStation);

		// TODO

		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		dismissLoadingView();
	}

	/**
	 * Adding the User-Agent, the Referrer and the parameters to the HttpPost
	 * 
	 * @return an HTTP POST object, created with the needed parameters
	 */
	private HttpPost createScheduleRequest() {
		final HttpPost result = new HttpPost(Constants.VB_URL);
		result.addHeader("User-Agent", Constants.VB_URL_USER_AGENT);
		result.addHeader("Referer", Constants.VB_URL_REFERER);

		try {
			final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					assignHttpPostParameters(), "UTF-8");
			result.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Not supported default encoding?\n"
					+ e, e);
		}

		return result;
	}

	/**
	 * Creating a list with BasicNameValuePair parameters, used for preparing
	 * the HTTP POST request
	 * 
	 * @return a list with BasicNameValuePair parameters for the HTTP POST
	 *         request
	 */
	private List<BasicNameValuePair> assignHttpPostParameters() {

		List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>();
		result.addAll(Arrays.asList(new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_STOP,
				vehicleStationEntity.getStop()), new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_LID,
				vehicleStationEntity.getLid()), new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_VT,
				vehicleStationEntity.getVt()), new BasicNameValuePair(
				Constants.SCHECULE_URL_STATION_SCHEDULE_RID,
				vehicleStationEntity.getRid())));

		return result;
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
		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			/**
			 * Fixing a strange error that is happening sometimes when the
			 * dialog is dismissed. I guess sometimes activity gets finished
			 * before the dialog successfully dismisses.
			 * 
			 * java.lang.IllegalArgumentException: View not attached to window
			 * manager
			 */
		}

		ActivityUtils.unlockScreenOrientation(context);
	}
}