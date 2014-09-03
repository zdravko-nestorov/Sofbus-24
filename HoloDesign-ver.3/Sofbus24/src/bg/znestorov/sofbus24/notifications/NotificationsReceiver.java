package bg.znestorov.sofbus24.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import bg.znestorov.sofbus24.main.Notifications;

/**
 * Fragment Activity used to start a new fragment dialog with the notification
 * (it is called when the elapsed time is passed)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class NotificationsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String[] vehicleInfo = (String[]) bundle
				.getSerializable(NotificationsDialog.BUNDLE_VEHICLE_INFO);

		Intent newIntent = new Intent(context, Notifications.class);
		newIntent.putExtra(NotificationsDialog.BUNDLE_VEHICLE_INFO,
				vehicleInfo);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newIntent);
	}

}