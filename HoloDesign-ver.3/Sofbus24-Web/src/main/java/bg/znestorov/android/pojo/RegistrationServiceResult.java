package bg.znestorov.android.pojo;

public class RegistrationServiceResult {

	private String errorCode;
	private String errorMessage;
	private String registrationId;
	private String deviceModel;
	private String deviceOsVersion;

	public RegistrationServiceResult(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public RegistrationServiceResult(String errorCode, String errorMessage,
			String registrationId, String deviceModel, String deviceOsVersion) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.registrationId = registrationId;
		this.deviceModel = deviceModel;
		this.deviceOsVersion = deviceOsVersion;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
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

	@Override
	public String toString() {
		return "WebServiceResult [errorCode=" + errorCode + ", errorMessage="
				+ errorMessage + ", registrationId=" + registrationId
				+ ", deviceModel=" + deviceModel + ", deviceOsVersion="
				+ deviceOsVersion + "]";
	}

}