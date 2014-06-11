package bg.znestorov.sofbus24.history;

/**
 * History class representing each station search
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class History {

	private String historyValue;
	private String historyDate;

	public History() {
	}

	public History(String historyValue, String historyDate) {
		this.historyValue = historyValue;
		this.historyDate = historyDate;
	}

	public String getHistoryValue() {
		return historyValue;
	}

	public void setHistoryValue(String historyValue) {
		this.historyValue = historyValue;
	}

	public String getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(String historyDate) {
		this.historyDate = historyDate;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\thistoryName: " + historyValue
				+ "\n\thistoryDate: " + historyDate + "\n}";
	}

}
