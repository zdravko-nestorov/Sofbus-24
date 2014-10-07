package bg.znestorov.sofbus24.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import bg.znestorov.sofbus24.main.Notifications;

/**
 * Service used to start a new fragment dialog with a notification (it is called
 * when the elapsed time is passed)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NotificationsService extends IntentService {

	public NotificationsService() {
		super("NotificationsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String receiverType = intent.getExtras().getString(
				NotificationsReceiver.BUNDLE_RECEIVER_KEY);

		if (receiverType == null) {
			return;
		}

		if (NotificationsReceiver.BUNDLE_SERVICE_VALUE.equals(receiverType)) {
			Bundle bundle = intent.getExtras();
			String[] vehicleInfo = (String[]) bundle
					.getSerializable(NotificationsDialog.BUNDLE_VEHICLE_INFO);

			Intent newIntent = new Intent(this, Notifications.class);
			newIntent.putExtra(NotificationsDialog.BUNDLE_VEHICLE_INFO,
					vehicleInfo);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(newIntent);
		}
	}
}