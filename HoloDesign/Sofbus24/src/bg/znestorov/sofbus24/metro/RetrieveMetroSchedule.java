package bg.znestorov.sofbus24.metro;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.MetroSchedule;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Async class used for retrieving the Metro schedule from a URL address and
 * parse it to a MetroStation object
 * 
 * @author Zdravko Nestorov
 * 
 */
public class RetrieveMetroSchedule extends AsyncTask<Void, Void, MetroStation> {

	private Context context;
	private ProgressDialog progressDialog;
	private Station station;

	public RetrieveMetroSchedule(Context context,
			ProgressDialog progressDialog, Station station) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.station = station;
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
		progressDialog.dismiss();

		// Check if the information is successfully retrieved or an Internet
		// error occurred
		if (ms.isScheduleSet()) {
			Intent metroScheduleIntent = new Intent(context,
					MetroSchedule.class);
			metroScheduleIntent.putExtra(Constants.BUNDLE_METRO_SCHEDULE, ms);
			context.startActivity(metroScheduleIntent);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setIcon(android.R.drawable.ic_menu_info_details)
					.setTitle(
							context.getString(R.string.app_dialog_title_error))
					.setMessage(context.getString(R.string.app_internet_error))
					.setNegativeButton(
							context.getString(R.string.app_button_ok), null)
					.show();
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
