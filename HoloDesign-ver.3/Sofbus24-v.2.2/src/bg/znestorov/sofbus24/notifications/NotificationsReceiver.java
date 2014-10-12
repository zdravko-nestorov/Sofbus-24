package bg.znestorov.sofbus24.notifications;

import java.io.Serializable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Fragment Activity used to start a new service, responsible for showing the
 * dialog notification
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class NotificationsReceiver extends BroadcastReceiver {

	public static final String BUNDLE_RECEIVER_KEY = "NotificationsReceiver";
	public static final String BUNDLE_SERVICE_VALUE = "NotificationsService";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (bundle != null) {
			Serializable bundleInfo = bundle
					.getSerializable(NotificationsDialog.BUNDLE_VEHICLE_INFO);

			if (bundleInfo != null) {
				String[] vehicleInfo = (String[]) bundleInfo;

				Intent serviceIntent = new Intent(context,
						NotificationsService.class);
				serviceIntent.putExtra(BUNDLE_RECEIVER_KEY,
						BUNDLE_SERVICE_VALUE);
				serviceIntent.putExtra(NotificationsDialog.BUNDLE_VEHICLE_INFO,
						vehicleInfo);
				context.startService(serviceIntent);
			}
		}
	}
}