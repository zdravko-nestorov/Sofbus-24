package bg.znestorov.android.model;

public class NotificationWrapper {

	private Notification data;
	private String[] registration_ids;

	public NotificationWrapper() {
	}

	public NotificationWrapper(Notification data,
			String[] registration_ids) {
		this.data = data;
		this.registration_ids = registration_ids;
	}

	public Notification getData() {
		return data;
	}

	public void setData(Notification data) {
		this.data = data;
	}

	public String[] getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(String[] registration_ids) {
		this.registration_ids = registration_ids;
	}

	@Override
	public String toString() {
		return "NotificationWrapperBean [data=" + data + ", registration_ids="
				+ registration_ids + "]";
	}

}