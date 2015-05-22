package bg.znestorov.sofbus24.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import bg.znestorov.sofbus24.entity.NotificationTypeEnum;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Responsible for the iteractions with the SharedPreference file, containing an
 * information about the Google Cloud Messaging services
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class GcmPreferences {

	/**
	 * Get the GCM registration id from the SharedPreferences file (the id is
	 * connected with the application version, so needs additional checks in
	 * case of a new version deployed on GooglePlayStore)
	 * 
	 * @param context
	 *            the current activity context
	 * @return the registration id from the SharedPreferences file
	 */
	public static String getReistrationId(Activity context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.GCM_PREFERENCES_NAME, Context.MODE_PRIVATE);

		String registrationId = sharedPreferences.getString(
				Constants.GCM_PREFERENCES_REG_ID, "");
		if (registrationId.isEmpty()) {
			registrationId = "";
		}

		int registeredVersion = sharedPreferences.getInt(
				Constants.GCM_PREFERENCES_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			registrationId = "";
		}

		return registrationId;
	}

	/**
	 * Store the GCM registration id and the application version in a
	 * SharedPreference file (the registration id is dependant from the
	 * application version and each time a new version of the app is deployed in
	 * the GooglePlay store, a new registration of the app is needed)
	 * 
	 * @param context
	 *            the current activity context
	 * @param regId
	 *            the registration id received from GCM service
	 */
	public static void storeRegistrationId(Activity context, String regId) {

		SharedPreferences prefs = context.getSharedPreferences(
				Constants.GCM_PREFERENCES_NAME, Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.GCM_PREFERENCES_REG_ID, regId);
		editor.putInt(Constants.GCM_PREFERENCES_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Get the date of the last received push notification in the
	 * SharedPreference file. The date in the file will be changed only if a
	 * period of 3 days is passed before the last saved notification
	 * 
	 * @param context
	 *            the current activity context
	 * @return the date of last saved push notification
	 */
	public static String getNotificationDate(Activity context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.GCM_PREFERENCES_NAME, Context.MODE_PRIVATE);

		String notificationDate = sharedPreferences.getString(
				Constants.GCM_PREFERENCES_NOTIFICATION_DATE, "");
		if (notificationDate.isEmpty()) {
			notificationDate = "";
		}

		return notificationDate;
	}

	/**
	 * Get the type of the last received push notification in the
	 * SharedPreferences file. The notification type in the file will be changed
	 * only if a period of 3 days is passed before the last saved notification
	 * 
	 * @param context
	 *            the current activity context
	 * @return the date of last saved push notification
	 */
	public static String getNotificationType(Activity context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.GCM_PREFERENCES_NAME, Context.MODE_PRIVATE);

		String notificationType = sharedPreferences.getString(
				Constants.GCM_PREFERENCES_NOTIFICATION_TYPE, "");
		if (notificationType.isEmpty()) {
			notificationType = "";
		}

		return notificationType;
	}

	/**
	 * Store the date and type of the received notification, if satisfies the
	 * conditions - if 3 days past since the last notification, change the date
	 * in the SharedPreferences file and set the notification type accordingly.
	 * Otherwise, do not edit the file
	 * 
	 * @param context
	 *            the current activity context
	 * @param notificationDate
	 *            the date of the received notification
	 * @param notificationType
	 *            the type of the received notification
	 */
	public static void storeNotificationData(Activity context,
			String notificationDate, NotificationTypeEnum notificationType) {

		SharedPreferences prefs = context.getSharedPreferences(
				Constants.GCM_PREFERENCES_NAME, Context.MODE_PRIVATE);

		// TODO: Check if 3 days are past since the last update
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.GCM_PREFERENCES_NOTIFICATION_DATE,
				notificationDate);
		editor.putString(Constants.GCM_PREFERENCES_NOTIFICATION_TYPE,
				notificationType.toString());
		editor.commit();
	}

	/**
	 * Get the application version code (in case of a problem, set '0' as a
	 * version code)
	 * 
	 * @param context
	 *            the current activity context
	 * @return the version code of the application
	 */
	private static int getAppVersion(Activity context) {

		int versionCode;
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			versionCode = 0;
		}

		return versionCode;
	}

}