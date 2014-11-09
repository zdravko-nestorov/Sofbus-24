package bg.znestorov.sofbus24.updates.check;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Static class used to interact with the Check For Update preferences
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class CheckForUpdatesPreferences {

	/**
	 * Get the last check date for updates
	 * 
	 * @param context
	 *            the current Activity context
	 * @return the last check date for updates
	 */
	public static String getLastCheckDate(Activity context) {
		SharedPreferences applicationUpdate = context.getSharedPreferences(
				Constants.CHECK_FOR_UPDATES_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		String lastCheckDate = applicationUpdate.getString(
				Constants.CHECK_FOR_UPDATES_PREFERENCES_LAST_CHECK, "");

		return lastCheckDate;
	}

	/**
	 * Checks if the application has already checked for update for the current
	 * day (this way we prevent multiple checkings in one day)
	 * 
	 * @param context
	 *            current Activity context
	 * @param currentDate
	 *            the current date
	 * @return if the application has already checked for update
	 */
	public static boolean isUpdateAlreadyChecked(Activity context,
			String currentDate) {
		boolean isUpdateAlreadyChecked = false;

		String lastCheckDate = getLastCheckDate(context);
		if (lastCheckDate.equals(currentDate)) {
			isUpdateAlreadyChecked = true;
		}

		return isUpdateAlreadyChecked;
	}

	/**
	 * Change the the last check date for updates in the SharedPreferences file
	 * 
	 * @param context
	 *            the current Activity context
	 * @param lastCheckDate
	 *            the last check date for updates
	 */
	public static void setLastCheckDate(Activity context, String lastCheckDate) {
		SharedPreferences applicationUpdate = context.getSharedPreferences(
				Constants.CHECK_FOR_UPDATES_PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		Editor editor = applicationUpdate.edit();
		editor.putString(Constants.CHECK_FOR_UPDATES_PREFERENCES_LAST_CHECK,
				lastCheckDate);
		editor.commit();
	}
}