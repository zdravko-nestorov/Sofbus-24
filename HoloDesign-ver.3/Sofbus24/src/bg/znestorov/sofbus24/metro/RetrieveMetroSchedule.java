package bg.znestorov.sofbus24.metro;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import bg.znestorov.sofbus24.entity.MetroStationEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.main.MetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Async class used for retrieving the Metro schedule from a URL address and
 * parse it to a MetroStation object
 * 
 * @author Zdravko Nestorov
 * 
 */
public class RetrieveMetroSchedule extends AsyncTask<Void, Void, MetroStationEntity> {

	private Activity context;
	private ProgressDialog progressDialog;
	private StationEntity station;

	public RetrieveMetroSchedule(Activity context,
			ProgressDialog progressDialog, StationEntity station) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.station = station;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
	}

	@Override
	protected MetroStationEntity doInBackground(Void... params) {
		MetroStationEntity ms = null;

		try {
			// Get the time schedule as InputSource from the station URL
			// address
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new URL(station.getCustomField())
					.openStream());

			// Set Direction and time schedule to the station
			ms = new MetroStationEntity(station);
			ms.setDirection(doc);
			ms.setWeekdaySchedule(doc);
			ms.setHolidaySchedule(doc);
		} catch (Exception e) {
			ms = new MetroStationEntity();
		}

		return ms;
	}

	@Override
	protected void onPostExecute(MetroStationEntity ms) {
		super.onPostExecute(ms);

		// Check if the information is successfully retrieved or an Internet
		// error occurred
		if (ms.isScheduleSet()) {
			Utils.addStationInHistory(context, ms);

			Intent metroScheduleIntent = new Intent(context,
					MetroSchedule.class);
			metroScheduleIntent.putExtra(Constants.BUNDLE_METRO_SCHEDULE, ms);
			context.startActivity(metroScheduleIntent);
		} else {
			ActivityUtils.showNoInternetToast(context);
		}

		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		dismissLoadingView();
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
