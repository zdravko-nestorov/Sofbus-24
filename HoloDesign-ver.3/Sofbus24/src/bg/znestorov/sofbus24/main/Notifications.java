package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import bg.znestorov.sofbus24.notifications.NotificationsDialog;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

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
		ActivityUtils.showAsPopup(this, true);
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);

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