package bg.znestorov.android.model;

public class RegistrationSuccess implements WebServiceResult {

	private Boolean isSuccessful;
	private String regId;

	public RegistrationSuccess(String regId) {
		this.isSuccessful = true;
		this.regId = regId;
	}

	public Boolean getIsSuccessful() {
		return isSuccessful;
	}

	public void setIsSuccessful(Boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	@Override
	public String toString() {
		return "RegistrationSuccess [isSuccessful=" + isSuccessful + ", regId="
				+ regId + "]";
	}

}