package bg.znestorov.android.entity;

import java.io.Serializable;
import java.util.Date;

public class PhoneUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String regId;
	private final String deviceModel;
	private final String deviceOsVersion;
	private final String registrationDate;
	private final String lastPushNotificationDate;

	public PhoneUser(String regId, String deviceModel, String deviceOsVersion,
			String registrationDate, String lastPushNotificationDate) {
		super();
		this.regId = regId;
		this.deviceModel = deviceModel;
		this.deviceOsVersion = deviceOsVersion;
		this.registrationDate = registrationDate;
		this.lastPushNotificationDate = lastPushNotificationDate;
	}

	public PhoneUser(String regId, String deviceModel, String deviceOsVersion) {
		super();
		this.regId = regId;
		this.deviceModel = deviceModel;
		this.deviceOsVersion = deviceOsVersion;
		this.registrationDate = new Date().toString();
		this.lastPushNotificationDate = null;
	}

	public String getRegId() {
		return regId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public String getDeviceOsVersion() {
		return deviceOsVersion;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public String getLastPushNotificationDate() {
		return lastPushNotificationDate;
	}

	@Override
	public String toString() {
		return "PhoneUser [regId=" + regId + ", deviceModel=" + deviceModel
				+ ", deviceOsVersion=" + deviceOsVersion
				+ ", registrationDate=" + registrationDate
				+ ", lastPushNotificationDate=" + lastPushNotificationDate
				+ "]";
	}

}