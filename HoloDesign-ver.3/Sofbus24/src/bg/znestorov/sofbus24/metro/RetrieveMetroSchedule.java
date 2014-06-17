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
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.Station;
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
public class RetrieveMetroSchedule extends AsyncTask<Void, Void, MetroStation> {

	private Activity context;
	private ProgressDialog progressDialog;
	private Station station;

	public RetrieveMetroSchedule(Activity context,
			ProgressDialog progressDialog, Station station) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.station = station;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

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
	protected MetroStation doInBackground(Void... params) {
		MetroStation ms = null;

		try {
			// Get the time schedule as InputSource from the station URL
			// address
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new URL(station.getCustomField())
					.openStream());

			// Set Direction and time schedule to the station
			ms = new MetroStation(station);
			ms.setDirection(doc);
			ms.setWeekdaySchedule(doc);
			ms.setHolidaySchedule(doc);
		} catch (Exception e) {
			ms = new MetroStation();
		}

		return ms;
	}

	@Override
	protected void onPostExecute(MetroStation ms) {
		super.onPostExecute(ms);

		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			// Workaround used just in case the orientation is changed once
			// retrieving info
		}

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
}
