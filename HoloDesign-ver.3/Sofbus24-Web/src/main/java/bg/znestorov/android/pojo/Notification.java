package bg.znestorov.android.pojo;

import java.util.Arrays;
import java.util.List;

import bg.znestorov.android.utils.Utils;

import com.google.gson.GsonBuilder;

public class Notification {

	private String date;
	private String type;
	private String data;
	private String[] registration_ids;

	public Notification() {
		this.date = Utils.getCurrentDate();
		this.registration_ids = new String[1];
	}

	public Notification(String date, String type, String data,
			String[] registration_ids) {
		this.date = date;
		this.type = type;
		this.data = data;
		this.registration_ids = registration_ids;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String[] getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(String[] registration_ids) {
		this.registration_ids = registration_ids;
	}

	public void setRegistration_ids(String registration_ids) {

		if (!Utils.isEmpty(registration_ids)) {
			this.registration_ids = registration_ids.replaceAll(" ", "").split(
					",");
		} else {
			this.registration_ids = new String[0];
		}
	}

	public void setRegistration_ids(List<String> registration_ids) {

		if (registration_ids != null && registration_ids.size() > 0) {
			this.registration_ids = registration_ids
					.toArray(new String[registration_ids.size()]);
		} else {
			this.registration_ids = new String[0];
		}
	}

	/**
	 * Format the class to notification JSON (so can send to GCM)
	 * 
	 * @return the notification in JSON format
	 */
	public String toJson() {
		return new GsonBuilder().setPrettyPrinting().create()
				.toJson(new NotificationJson(this));
	}

	/**
	 * Format the class to be shown as tooltip
	 * 
	 * @return the notification in tooltip format
	 */
	public String toTooltip() {
		return "Notification [Date="
				+ date
				+ ", Type="
				+ type
				+ ", Data="
				+ (data != null ? data.replaceAll("\n", "")
						.replaceAll("\"", "").replaceAll(" +", " ") : "") + "]";
	}

	@Override
	public String toString() {
		return "Notification [date=" + date + ", type=" + type + ", data="
				+ data + ", registration_ids="
				+ Arrays.toString(registration_ids) + "]";
	}

	private class NotificationJson {

		private Notification data;
		private String[] registration_ids;

		public NotificationJson(Notification notification) {
			this.registration_ids = notification.getRegistration_ids();

			// Remove the registration ids from the notification data
			notification.setRegistration_ids("");
			this.data = notification;
		}

		@Override
		public String toString() {
			return "NotificationJson [data=" + data + ", registration_ids="
					+ Arrays.toString(registration_ids) + "]";
		}

	}

}