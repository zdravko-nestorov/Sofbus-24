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
				throw new Error("Error copying configuration");
			}
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
		// Create the folder if it is not already created
		File prefFolder = new File(CONFIGURATION_PATH);
		if (!prefFolder.exists()) {
			prefFolder.mkdir();
		}

		// Open the local DB as the input stream
		InputStream myInput = context.getAssets().open(CONFIGURATION_NAME);

		// Path to the just created empty DB
		String outFileName = CONFIGURATION_PATH + CONFIGURATION_NAME;

		// Open the empty DB as the output stream
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

	/**
	 * Edit the configuration file
	 * 
	 * @param context
	 *            the current Activity context
	 * @param newConfig
	 *            the new Configuration object
	 */
	public static void editConfiguration(Activity context, ConfigEntity newConfig) {
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
		edit.putString(Constants.CONFIGURATION_PREF_STATIONS_KEY,
				newConfig.getStationsDbVersion() + "");
		edit.putString(Constants.CONFIGURATION_PREF_VEHICLES_KEY,
				newConfig.getVehiclesDbVersion() + "");
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
	public static void editDbConfigurationFileds(Activity context,
			ConfigEntity newConfig) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);

		Editor edit = sharedPreferences.edit();
		edit.putString(Constants.CONFIGURATION_PREF_STATIONS_KEY,
				newConfig.getStationsDbVersion() + "");
		edit.putString(Constants.CONFIGURATION_PREF_VEHICLES_KEY,
				newConfig.getVehiclesDbVersion() + "");
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
