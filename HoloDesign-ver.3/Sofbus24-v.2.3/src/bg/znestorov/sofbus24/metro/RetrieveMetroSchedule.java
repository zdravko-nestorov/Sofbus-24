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
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.MetroScheduleEntity;
import bg.znestorov.sofbus24.entity.MetroStationEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.main.MetroSchedule;
import bg.znestorov.sofbus24.main.MetroScheduleDialog;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Async class used for retrieving the Metro schedule from a URL address and
 * parse it to a MetroStation object
 * 
 * @author Zdravko Nestorov
 * @version 2.0
 * 
 */
public class RetrieveMetroSchedule extends
		AsyncTask<Void, Void, MetroScheduleEntity> {

	private Activity context;
	private GlobalEntity globalContext;
	private ProgressDialog progressDialog;

	private StationEntity station;

	public RetrieveMetroSchedule(Activity context,
			ProgressDialog progressDialog, StationEntity station) {
		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();
		this.progressDialog = progressDialog;
		this.station = station;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
	}

	@Override
	protected MetroScheduleEntity doInBackground(Void... params) {
		MetroScheduleEntity metroSchedule = getMetroScheduleEntity();

		try {
			for (int i = 0; i < metroSchedule.getMetroStationsSize(); i++) {
				MetroStationEntity metroStation = metroSchedule
						.getStationEntity(i);

				// Get the time schedule as InputSource from the station URL
				// address
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new URL(metroStation
						.getCustomField()).openStream());

				// Set Direction and time schedule to the station
				metroSchedule.setDirection(i, doc);
				metroSchedule.setWeekdaySchedule(i, doc);
				metroSchedule.setHolidaySchedule(i, doc);
			}
		} catch (Exception e) {
			metroSchedule = null;
		}

		return metroSchedule;
	}

	@Override
	protected void onPostExecute(MetroScheduleEntity metroSchedule) {
		super.onPostExecute(metroSchedule);

		// Check if the information is successfully retrieved or an Internet
		// error occurred
		if (metroSchedule != null && metroSchedule.isMetroInformationValid()) {
			Utils.addStationInHistory(context,
					metroSchedule.getChoosenStationEntity());

			Intent metroScheduleIntent;
			if (globalContext.isPhoneDevice()) {
				metroScheduleIntent = new Intent(context, MetroSchedule.class);
			} else {
				metroScheduleIntent = new Intent(context,
						MetroScheduleDialog.class);
			}
			metroScheduleIntent.putExtra(Constants.BUNDLE_METRO_SCHEDULE,
					metroSchedule);
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

	/**
	 * Get a metro schedule entity with all the information about the station in
	 * both directions
	 * 
	 * @return a MetroScheduleEntity with all the information about the station
	 *         in both directions
	 */
	private MetroScheduleEntity getMetroScheduleEntity() {
		int choosenDirection;
		int oppositeStationNumber;

		// Get the number of the choosen station
		int choosenStationNumber = Integer.parseInt(Utils.getOnlyDigits(station
				.getNumber()));

		// Check the number of the choosen station, so determine the direction
		// and the number of the opposite station
		if (choosenStationNumber % 2 == 1) {
			choosenDirection = 0;
			oppositeStationNumber = choosenStationNumber + 1;
		} else {
			choosenDirection = 1;
			oppositeStationNumber = choosenStationNumber - 1;
		}

		// Get the data of the oppsoite station from the database
		StationsDataSource stationsDatasource = new StationsDataSource(context);
		stationsDatasource.open();
		StationEntity oppsoiteMetroStation = stationsDatasource
				.getStation(oppositeStationNumber);
		stationsDatasource.close();

		// Construct the MetroScheduleEntity with the need information
		MetroScheduleEntity metroSchedule = new MetroScheduleEntity(
				choosenDirection, new MetroStationEntity(station),
				new MetroStationEntity(oppsoiteMetroStation));

		return metroSchedule;
	}
}