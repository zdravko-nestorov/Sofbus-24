package bg.znestorov.sofbus24.about;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.text.Html;
import bg.znestorov.sofbus24.databases.StationsSQLite;
import bg.znestorov.sofbus24.databases.VehiclesSQLite;
import bg.znestorov.sofbus24.entity.Config;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Async class used for retrieving the Databases from URL addresses and replace
 * the existing ones in the memory
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveDatabases extends
		AsyncTask<Void, Void, HashMap<String, InputStream>> {

	private Activity context;
	private ProgressDialog progressDialog;
	private String stationsDatabaseUrl;
	private String vehiclesDatabaseUrl;
	private Config newConfig;

	public RetrieveDatabases(Activity context, ProgressDialog progressDialog,
			String stationsDatabaseUrl, String vehiclesDatabaseUrl,
			Config newConfig) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.stationsDatabaseUrl = stationsDatabaseUrl;
		this.vehiclesDatabaseUrl = vehiclesDatabaseUrl;
		this.newConfig = newConfig;
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
	protected HashMap<String, InputStream> doInBackground(Void... params) {
		HashMap<String, InputStream> databases = new HashMap<String, InputStream>();

		try {
			if (stationsDatabaseUrl != null) {
				databases.put(Constants.CONFIGURATION_PREF_STATIONS_KEY,
						new URL(stationsDatabaseUrl).openStream());
			}

			if (vehiclesDatabaseUrl != null) {
				databases.put(Constants.CONFIGURATION_PREF_VEHICLES_KEY,
						new URL(vehiclesDatabaseUrl).openStream());
			}
		} catch (Exception e) {
		}

		// Check if DB should be updated
		if (databases.size() > 0) {
			InputStream dbInputStream;

			// Check if the STATIONS DB should be updated
			dbInputStream = databases
					.get(Constants.CONFIGURATION_PREF_STATIONS_KEY);
			if (dbInputStream != null) {
				updateStationsDatbase(dbInputStream);
			}

			// Check if the VEHICLES DB should be updated
			dbInputStream = databases
					.get(Constants.CONFIGURATION_PREF_VEHICLES_KEY);
			if (dbInputStream != null) {
				updateVehiclesDatbase(dbInputStream);
			}
		}

		return databases;
	}

	@Override
	protected void onPostExecute(HashMap<String, InputStream> databases) {
		try {
			progressDialog.dismiss();

			// Check if the information is successfully retrieved or an Internet
			// error occurred
			if (databases.size() > 0) {
				Configuration.editConfiguration(context, newConfig);

				// Showing an AlertDialog with info that the app must be
				// restarted
				OnClickListener positiveOnClickListener = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityUtils.restartApplication(context);
					}
				};

				ActivityUtils.showCustomAlertDialog(context,
						android.R.drawable.ic_menu_info_details,
						context.getString(R.string.app_dialog_title_important),
						Html.fromHtml(context
								.getString(R.string.about_update_db_restart)),
						context.getString(R.string.app_button_yes),
						positiveOnClickListener, context
								.getString(R.string.app_button_no), null);
			} else {
				ActivityUtils.showNoInternetAlertDialog(context);
			}
		} catch (Exception e) {
			// Workaround used just in case the orientation is changed once
			// retrieving info
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
	 * Replace the existing Stations DB with the one downoladed from the URL
	 * address
	 * 
	 * @param dbInputStream
	 *            the stations DB input stream
	 */
	private void updateStationsDatbase(InputStream dbInputStream) {
		context.deleteDatabase("stations.db");
		StationsSQLite myDbHelper = new StationsSQLite(context);
		myDbHelper.createDataBase(dbInputStream);
	}

	/**
	 * Replace the existing Vehicles DB with the one downoladed from the URL
	 * address
	 * 
	 * @param dbInputStream
	 *            the vehicles DB input stream
	 */
	private void updateVehiclesDatbase(InputStream dbInputStream) {
		context.deleteDatabase("vehicles.db");
		VehiclesSQLite myDbHelper = new VehiclesSQLite(context);
		myDbHelper.createDataBase(dbInputStream);

	}
}
