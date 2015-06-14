package bg.znestorov.android.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Registration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;

	private String regId;
	private String deviceModel;
	private String deviceOsVersion;
	private Date timestamp;

	public Registration() {
		this.timestamp = new Date();
	}

	public Registration(Key id, String regId, String deviceModel,
			String deviceOsVersion, Date timestamp) {
		this.id = id;
		this.regId = regId;
		this.deviceModel = deviceModel;
		this.deviceOsVersion = deviceOsVersion;
		this.timestamp = timestamp;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDeviceOsVersion() {
		return deviceOsVersion;
	}

	public void setDeviceOsVersion(String deviceOsVersion) {
		this.deviceOsVersion = deviceOsVersion;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Registration [id=" + id + ", regId=" + regId + ", deviceModel="
				+ deviceModel + ", deviceOsVersion=" + deviceOsVersion
				+ ", timestamp=" + timestamp + "]";
	}

}