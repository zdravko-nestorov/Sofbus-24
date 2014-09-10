package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager.LayoutParams;
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

			DialogFragment notificationsVBTimeDialog = NotificationsDialog
					.newInstance(vehicleInfo);
			notificationsVBTimeDialog.show(getSupportFragmentManager(),
					"NotificationsVBTimeDialog");
		}
	}

}