package bg.znestorov.sofbus24.about;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import bg.znestorov.sofbus24.databases.Sofbus24SQLite;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Asynchronous class used for retrieving the Databases from URL addresses and
 * replace the existing ones in the memory
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class RetrieveDatabases extends
		AsyncTask<Void, Void, HashMap<String, InputStream>> {

	private FragmentActivity context;
	private ProgressDialog progressDialog;
	private String stationsDatabaseUrl;
	private ConfigEntity newConfig;

	public RetrieveDatabases(FragmentActivity context,
			ProgressDialog progressDialog, String stationsDatabaseUrl,
			ConfigEntity newConfig) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.stationsDatabaseUrl = stationsDatabaseUrl;
		this.newConfig = newConfig;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
	}

	@Override
	protected HashMap<String, InputStream> doInBackground(Void... params) {
		HashMap<String, InputStream> databases = new HashMap<String, InputStream>();

		try {
			if (stationsDatabaseUrl != null) {
				databases.put(Constants.CONFIGURATION_PREF_SOFBUS24_KEY,
						new URL(stationsDatabaseUrl).openStream());
			}

			// Check if DB should be updated
			if (databases.size() > 0) {
				InputStream dbInputStream;

				// Check if the STATIONS DB should be updated
				dbInputStream = databases
						.get(Constants.CONFIGURATION_PREF_SOFBUS24_KEY);
				if (dbInputStream != null) {
					updateStationsDatbase(dbInputStream);
				}
			}
		} catch (Exception e) {
		}

		return databases;
	}

	@Override
	protected void onPostExecute(HashMap<String, InputStream> databases) {
		super.onPostExecute(databases);

		// Check if the information is successfully retrieved or an Internet
		// error occurred
		if (databases.size() > 0) {
			Configuration.editDbConfigurationVersionField(context, newConfig);
			DialogFragment dialogFragment = RestartApplicationDialog
					.newInstance();
			dialogFragment.show(context.getSupportFragmentManager(), "dialog");
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
	 * Replace the existing Stations DB with the one that was download from the
	 * URL address
	 * 
	 * @param dbInputStream
	 *            the stations DB input stream
	 */
	private void updateStationsDatbase(InputStream dbInputStream) {
		context.deleteDatabase("stations.db");
		Sofbus24SQLite myDbHelper = new Sofbus24SQLite(context);
		myDbHelper.createDataBase(dbInputStream);
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