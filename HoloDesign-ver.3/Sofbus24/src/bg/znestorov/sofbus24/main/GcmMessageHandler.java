package bg.znestorov.sofbus24.main;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import bg.znestorov.sofbus24.entity.NotificationTypeEnum;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {

	private Context context;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		// Get the current service context
		context = GcmMessageHandler.this;

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		// Get the extras (information received from the intent)
		Bundle extras = intent.getExtras();
		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				checkReceivedMsg(extras.getString(Constants.GCM_MESSAGE_KEY));
			}
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	/**
	 * Check what is the received message, so proceed accordingly - update the
	 * application or update the database
	 * 
	 * @param message
	 *            the received message
	 */
	private void checkReceivedMsg(String message) {

		NotificationTypeEnum notificationType;
		try {
			notificationType = NotificationTypeEnum.valueOf(message);
		} catch (Exception e) {
			notificationType = NotificationTypeEnum.NONE;
		}

		// TODO: Continue accordingly
		Log.e(message, message);
	}

}