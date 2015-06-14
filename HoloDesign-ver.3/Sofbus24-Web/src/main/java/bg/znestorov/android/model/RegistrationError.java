package bg.znestorov.android.model;

public class RegistrationError implements WebServiceResult {

	private Boolean isSuccessful;
	private String errorMessage;

	public RegistrationError(String errorMessage) {
		this.isSuccessful = false;
		this.errorMessage = errorMessage;
	}

	public Boolean getIsSuccessful() {
		return isSuccessful;
	}

	public void setIsSuccessful(Boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "RegistrationError [isSuccessful=" + isSuccessful
				+ ", errorMessage=" + errorMessage + "]";
	}

}