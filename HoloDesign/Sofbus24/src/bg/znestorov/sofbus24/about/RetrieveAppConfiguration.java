package bg.znestorov.sofbus24.about;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import bg.znestorov.sofbus24.activity.ActivityUtils;
import bg.znestorov.sofbus24.entity.Config;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

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
			ActivityUtils.showNoInternetDialog(context);
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
	private void updateDb(Config currentConfig, Config newConfig) {

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
		if (currentConfig.getVersionCode() < newConfig.getVersionCode()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setIcon(android.R.drawable.ic_menu_info_details)
					.setTitle(
							context.getString(R.string.app_dialog_title_important))
					.setMessage(
							Html.fromHtml(String.format(context
									.getString(R.string.about_update_app_new),
									newConfig.getVersionName())))
					.setPositiveButton(
							context.getString(R.string.app_button_yes),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String appPackageName = context
											.getPackageName();

									try {
										context.startActivity(new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("market://details?id="
														+ appPackageName)));
									} catch (android.content.ActivityNotFoundException anfe) {
										context.startActivity(new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("http://play.google.com/store/apps/details?id="
														+ appPackageName)));
									}
								}
							})
					.setNegativeButton(
							context.getString(R.string.app_button_no), null)
					.show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setIcon(android.R.drawable.ic_menu_info_details)
					.setTitle(context.getString(R.string.app_dialog_title_info))
					.setMessage(
							Html.fromHtml(String.format(context
									.getString(R.string.about_update_app_last),
									currentConfig.getVersionName())))
					.setNegativeButton(
							context.getString(R.string.app_button_ok), null)
					.show();
		}
	}
}
