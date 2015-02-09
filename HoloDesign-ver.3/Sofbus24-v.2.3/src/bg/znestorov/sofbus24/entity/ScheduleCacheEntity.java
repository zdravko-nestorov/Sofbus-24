package bg.znestorov.sofbus24.entity;

/**
 * Class used to retrieve data from the Schedule database
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleCacheEntity {

	private String htmlResponse;
	private String timestamp;

	public ScheduleCacheEntity(String htmlResponse, String timestamp) {
		super();
		this.htmlResponse = htmlResponse;
		this.timestamp = timestamp;
	}

	public String getHtmlResponse() {
		return htmlResponse;
	}

	public void setHtmlResponse(String htmlResponse) {
		this.htmlResponse = htmlResponse;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\thtmlResponse: " + htmlResponse
				+ "\n\ttimestamp: " + timestamp + "\n}";
	}

}