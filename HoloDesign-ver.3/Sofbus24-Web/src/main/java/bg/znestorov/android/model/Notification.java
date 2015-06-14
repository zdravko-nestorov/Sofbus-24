package bg.znestorov.android.model;

import bg.znestorov.android.utils.Utils;

public class Notification {

	public String date;
	public String type;
	public String data;

	public Notification() {
		this.date = Utils.getCurrentDate();
	}

	public Notification(String date, String type, String data) {
		this.date = date;
		this.type = type;
		this.data = data;
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

	@Override
	public String toString() {
		return "NotificationEntity [date=" + date + ", type=" + type
				+ ", data=" + data + "]";
	}

}