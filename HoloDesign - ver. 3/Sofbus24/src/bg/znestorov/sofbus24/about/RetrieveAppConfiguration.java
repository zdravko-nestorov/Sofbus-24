package bg.znestorov.sofbus24.about;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import bg.znestorov.sofbus24.entity.Config;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Async class used for retrieving the application configuration from a URL
 * address and parse it to a Config object
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveAppConfiguration extends AsyncTask<Void, Void, Config> {

	private Activity context;
	private ProgressDialog progressDialog;
	private boolean updateApp;

	public RetrieveAppConfiguration(Activity context,
			ProgressDialog progressDialog, boolean updateApp) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.updateApp = updateApp;
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
	protected Config doInBackground(Void... params) {
		Config appConfig = null;

		try {
			// Get the configuration as InputSource from the station URL
			// address
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new URL(Constants.CONFIGURATION_URL)
					.openStream());

			// Create a Config object from the Document
			appConfig = new Config(doc);
		} catch (Exception e) {
			appConfig = new Config();
		}

		return appConfig;
	}

	@Override
	protected void onPostExecute(Config newConfig) {
		progressDialog.dismiss();

		// Check if the information is successfully retrieved or an Internet
		// error occurred
		if (newConfig.isValidConfig()) {
			Config currentConfig = new Config(context);

			if (updateApp) {
				updateApp(currentConfig, newConfig);
			} else {
				updateDb(currentConfig, newConfig);
			}
		} else {
			ActivityUtils.showNoInternetAlertDialog(context);
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
	 * Update the current DB if needed
	 * 
	 * @param currentConfig
	 *            the current application config
	 * @param newConfig
	 *            the new application config
	 */
	private void updateDb(Config currentConfig, final Config newConfig) {
		String stationsDatabaseUrl = null;
		String vehiclesDatabaseUrl = null;

		if (currentConfig.getStationsDbVersion() < newConfig
				.getStationsDbVersion()) {
			stationsDatabaseUrl = Constants.CONFIGURATION_STATIONS_DB_URL;
		}

		if (currentConfig.getVehiclesDbVersion() < newConfig
				.getVehiclesDbVersion()) {
			vehiclesDatabaseUrl = Constants.CONFIGURATION_VEHICLES_DB_URL;
		}

		// Check if new DB is available
		if (stationsDatabaseUrl != null || vehiclesDatabaseUrl != null) {
			final String finalStationsDatabaseUrl = stationsDatabaseUrl;
			final String finalVehiclesDatabaseUrl = vehiclesDatabaseUrl;

			OnClickListener positiveOnClickListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog.setMessage(Html.fromHtml(context
							.getString(R.string.about_update_db_copy)));
					RetrieveDatabases retrieveDatabases = new RetrieveDatabases(
							context, progressDialog, finalStationsDatabaseUrl,
							finalVehiclesDatabaseUrl, newConfig);
					retrieveDatabases.execute();
				}
			};

			ActivityUtils.showCustomAlertDialog(context,
					android.R.drawable.ic_menu_info_details,
					context.getString(R.string.app_dialog_title_important),
					context.getString(R.string.about_update_db_new),
					context.getString(R.string.app_button_yes),
					positiveOnClickListener,
					context.getString(R.string.app_button_no), null);
		} else {
			ActivityUtils.showCustomAlertDialog(context,
					android.R.drawable.ic_menu_info_details,
					context.getString(R.string.app_dialog_title_info),
					context.getString(R.string.about_update_db_last), null,
					null, context.getString(R.string.app_button_ok), null);
		}
	}

	/**
	 * Update the application
	 * 
	 * @param currentConfig
	 *            the current application config
	 * @param newConfig
	 *            the new application config
	 */
	private void updateApp(Config currentConfig, Config newConfig) {
		// Check if new application version is available
		if (currentConfig.getVersionCode() < newConfig.getVersionCode()) {
			OnClickListener positiveOnClickListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String appPackageName = context.getPackageName();

					try {
						context.startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://details?id="
										+ appPackageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
						context.startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://play.google.com/store/apps/details?id="
										+ appPackageName)));
					}
				}
			};

			ActivityUtils.showCustomAlertDialog(context,
					android.R.drawable.ic_menu_info_details, context
							.getString(R.string.app_dialog_title_important),
					Html.fromHtml(String.format(
							context.getString(R.string.about_update_app_new),
							newConfig.getVersionName())), context
							.getString(R.string.app_button_yes),
					positiveOnClickListener, context
							.getString(R.string.app_button_no), null);
		} else {
			ActivityUtils.showCustomAlertDialog(context,
					android.R.drawable.ic_menu_info_details, context
							.getString(R.string.app_dialog_title_info), Html
							.fromHtml(String.format(context
									.getString(R.string.about_update_app_last),
									currentConfig.getVersionName())), null,
					null, context.getString(R.string.app_button_ok), null);
		}
	}
}
