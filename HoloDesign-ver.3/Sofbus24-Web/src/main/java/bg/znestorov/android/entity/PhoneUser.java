package bg.znestorov.android.entity;

import java.io.Serializable;
import java.util.Date;

public class PhoneUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String regId;
	private final String deviceModel;
	private final String deviceOsVersion;
	private final String timestamp;

	public PhoneUser(String regId, String deviceModel, String deviceOsVersion,
			String timestamp) {
		super();
		this.regId = regId;
		this.deviceModel = deviceModel;
		this.deviceOsVersion = deviceOsVersion;
		this.timestamp = timestamp;
	}

	public PhoneUser(String regId, String deviceModel, String deviceOsVersion) {
		super();
		this.regId = regId;
		this.deviceModel = deviceModel;
		this.deviceOsVersion = deviceOsVersion;
		this.timestamp = new Date().toString();
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

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "PhoneUser [regId=" + regId + ", deviceModel=" + deviceModel
				+ ", deviceOsVersion=" + deviceOsVersion + ", timestamp="
				+ timestamp + "]";
	}

}