package bg.znestorov.android.pojo;

public enum NotificationStatus {

	/**
	 * Initial status (only the gcm/send page is opened)
	 */
	INIT,

	/**
	 * The notification is successfully sent
	 */
	SUCCESS,

	/**
	 * There is a problem with sending the notification
	 */
	FAILED_NO_IDS, FAILED_CONNECTION, FAILED_UNKNOWN;

}