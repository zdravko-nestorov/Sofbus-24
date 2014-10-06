package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager.LayoutParams;
import bg.znestorov.sofbus24.databases.NotificationsDataSource;
import bg.znestorov.sofbus24.notifications.NotificationsDialog;
import bg.znestorov.sofbus24.utils.LanguageChange;

/**
 * Fragment Activity used to start a new fragment dialog with the notification
 * (it is called when the elapsed time is passed)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class Notifications extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Wake up the device and turn the screen on
		getWindow().addFlags(
				LayoutParams.FLAG_DISMISS_KEYGUARD
						| LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| LayoutParams.FLAG_TURN_SCREEN_ON
						| LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Change the language
		LanguageChange.selectLocale(this);

		// Show the dialog fragment only if the activity is started for the
		// first time
		if (savedInstanceState == null) {
			String[] vehicleInfo = (String[]) getIntent().getExtras()
					.getSerializable(NotificationsDialog.BUNDLE_VEHICLE_INFO);

			// Remove the notification from the db and check the result. If
			// successful - start a notification dialog fragment
			if (removeNotification(vehicleInfo)) {
				DialogFragment notificationsVBTimeDialog = NotificationsDialog
						.newInstance(vehicleInfo);
				notificationsVBTimeDialog.show(getSupportFragmentManager(),
						"NotificationsVBTimeDialog");
			}
		}
	}

	private boolean removeNotification(String[] vehicleInfo) {
		boolean isNotificationRemoved;

		NotificationsDataSource notificationsDatasource = new NotificationsDataSource(
				this);

		// Open the DB
		notificationsDatasource.open();

		// Check if the notification is already in the DB
		if (notificationsDatasource.getNotification(vehicleInfo[7]) != null) {
			notificationsDatasource.deleteNotification(vehicleInfo[7]);
			isNotificationRemoved = true;
		} else {
			isNotificationRemoved = false;
		}

		// Close the DB
		notificationsDatasource.close();

		return isNotificationRemoved;
	}

}