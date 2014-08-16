package bg.znestorov.sofbus24.updates.check;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import bg.znestorov.sofbus24.about.UpdateApplicationDialog;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Asynchronous class used for checking for updates from a URL address and parse
 * it to a configuration object
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class CheckForUpdatesAsync extends AsyncTask<Void, Void, ConfigEntity> {

	private FragmentActivity context;

	public CheckForUpdatesAsync(FragmentActivity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ActivityUtils.lockScreenOrientation(context);
	}

	@Override
	protected ConfigEntity doInBackground(Void... params) {
		ConfigEntity appConfig = null;

		try {
			// Get the configuration as an InputStream from the station URL
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new URL(Constants.CONFIGURATION_URL)
					.openStream());

			// Create a configuration object from the Document
			appConfig = new ConfigEntity(doc);
		} catch (Exception e) {
			appConfig = new ConfigEntity();
		}

		return appConfig;
	}

	@Override
	protected void onPostExecute(ConfigEntity newConfig) {
		super.onPostExecute(newConfig);

		updateApp(newConfig);
		ActivityUtils.unlockScreenOrientation(context);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		ActivityUtils.unlockScreenOrientation(context);
	}

	/**
	 * Check if the application should be updated and show an DialogFragment if
	 * needed
	 * 
	 * @param newConfig
	 *            the new application configuration
	 */
	private void updateApp(ConfigEntity newConfig) {
		ConfigEntity currentConfig = new ConfigEntity(context);
		if (newConfig.isValidConfig()
				&& currentConfig.getVersionCode() < newConfig.getVersionCode()) {
			DialogFragment dialogFragment = UpdateApplicationDialog
					.newInstance(String.format(
							context.getString(R.string.about_update_app_new),
							newConfig.getVersionName()));
			dialogFragment.show(context.getSupportFragmentManager(),
					"dialogFragment");
		}
	}
}