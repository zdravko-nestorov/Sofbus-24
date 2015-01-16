package bg.znestorov.sofbus24.about;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Class responsible for creating the configuration file
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Configuration {

	// The Android's default system path of the database
	private static String CONFIGURATION_PATH = "//data//data//bg.znestorov.sofbus24.main//shared_prefs//";
	private static String CONFIGURATION_NAME = "configuration.xml";

	/**
	 * Creates a configuration file by copying the ready one in the assets
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void createConfiguration(Activity context) {
		boolean configExist = checkConfiguration();

		if (!configExist) {
			try {
				copyConfiguration(context);
			} catch (IOException e) {
				throw new Error("Error copying configuration: \n"
						+ e.getStackTrace());
			}
		} else if (!isConfigurationCorrect(context)) {
			editDbConfigurationVersionField(context, 1);
		}
	}

	/**
	 * Check if the configuration file already exist to avoid re-copying the
	 * file each time the application is opened
	 * 
	 * @return if the configuration file exists or not
	 */
	private static boolean checkConfiguration() {
		File dbFile = new File(CONFIGURATION_PATH + CONFIGURATION_NAME);

		return dbFile.exists();
	}

	/**
	 * Copies the configuration file from the local assets-folder in the system
	 * folder, from where it can be accessed and handled. This is done by
	 * transferring ByteStream.
	 * 
	 * @param context
	 *            current Activity context
	 * @throws IOException
	 */
	private static void copyConfiguration(Activity context) throws IOException {

		// Create the folder and the configuration file (empty one), if it is
		// not already created
		context.getSharedPreferences(Constants.CONFIGURATION_PREF_NAME,
				Context.MODE_PRIVATE);

		// Open the local configuration file as the input stream
		InputStream myInput = context.getAssets().open(CONFIGURATION_NAME);

		// Path to the just created empty configuration file
		String outFileName = CONFIGURATION_PATH + CONFIGURATION_NAME;

		// Open the empty configuration file as an output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// Transfer the bytes from the InputFile to the OutputFile
		byte[] buffer = new byte[1024];
		int length;

		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	private static boolean isConfigurationCorrect(Activity context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);
		String sofbus24DbVersion = sharedPreferences.getString(
				Constants.CONFIGURATION_PREF_SOFBUS24_KEY, null);

		boolean isConfigurationCorrect = false;
		if (sofbus24DbVersion != null) {
			isConfigurationCorrect = true;
		}

		return isConfigurationCorrect;
	}

	/**
	 * Edit the configuration file
	 * 
	 * @param context
	 *            the current Activity context
	 * @param newConfig
	 *            the new Configuration object
	 */
	public static void editConfiguration(Activity context,
			ConfigEntity newConfig) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);

		Editor edit = sharedPreferences.edit();
		edit.clear();
		edit.putString(Constants.CONFIGURATION_PREF_FAVOURITES_VISIBILITY_KEY,
				newConfig.isFavouritesVisibilå() + "");
		edit.putString(Constants.CONFIGURATION_PREF_FAVOURITES_POSITION_KEY,
				newConfig.getFavouritesPosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SEARCH_VISIBILITY_KEY,
				newConfig.isSearchVisibile() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SEARCH_POSITION_KEY,
				newConfig.getSearchPosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SCHEDULE_VISIBILITY_KEY,
				newConfig.isScheduleVisibile() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SCHEDULE_POSITION_KEY,
				newConfig.getSchedulePosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_METRO_VISIBILITY_KEY,
				newConfig.isMetroVisibile() + "");
		edit.putString(Constants.CONFIGURATION_PREF_METRO_POSITION_KEY,
				newConfig.getMetroPosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SOFBUS24_KEY,
				newConfig.getSofbus24DbVersion() + "");
		edit.commit();
	}

	/**
	 * Edit the configuration file (only for DBs)
	 * 
	 * @param context
	 *            the current Activity context
	 * @param newConfig
	 *            the new Configuration object
	 */
	public static void editDbConfigurationVersionField(Activity context,
			ConfigEntity newConfig) {
		editDbConfigurationVersionField(context,
				newConfig.getSofbus24DbVersion());
	}

	/**
	 * Edit the configuration file (only for DBs)
	 * 
	 * @param context
	 *            the current Activity context
	 * @param sofbus24DbVersion
	 *            the new database version
	 */
	public static void editDbConfigurationVersionField(Activity context,
			int sofbus24DbVersion) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);

		Editor edit = sharedPreferences.edit();
		edit.putString(Constants.CONFIGURATION_PREF_SOFBUS24_KEY,
				sofbus24DbVersion + "");
		edit.commit();
	}

	/**
	 * Edit the configuration file (only for Sofbus24 start screen tabs)
	 * 
	 * @param context
	 *            the current Activity context
	 * @param newConfig
	 *            the new Configuration object
	 */
	public static void editTabConfigurationFileds(Activity context,
			ConfigEntity newConfig) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);

		Editor edit = sharedPreferences.edit();
		edit.putString(Constants.CONFIGURATION_PREF_FAVOURITES_VISIBILITY_KEY,
				newConfig.isFavouritesVisibilå() + "");
		edit.putString(Constants.CONFIGURATION_PREF_FAVOURITES_POSITION_KEY,
				newConfig.getFavouritesPosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SEARCH_VISIBILITY_KEY,
				newConfig.isSearchVisibile() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SEARCH_POSITION_KEY,
				newConfig.getSearchPosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SCHEDULE_VISIBILITY_KEY,
				newConfig.isScheduleVisibile() + "");
		edit.putString(Constants.CONFIGURATION_PREF_SCHEDULE_POSITION_KEY,
				newConfig.getSchedulePosition() + "");
		edit.putString(Constants.CONFIGURATION_PREF_METRO_VISIBILITY_KEY,
				newConfig.isMetroVisibile() + "");
		edit.putString(Constants.CONFIGURATION_PREF_METRO_POSITION_KEY,
				newConfig.getMetroPosition() + "");
		edit.commit();
	}
}
