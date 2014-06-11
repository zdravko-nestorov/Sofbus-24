package bg.znestorov.sofbus24.history;

import bg.znestorov.sofbus24.entity.VehicleType;

/**
 * History class representing each station search
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class HistoryEntity {

	private String historyValue;
	private String historyDate;
	private VehicleType historyType;

	public HistoryEntity() {
	}

	public HistoryEntity(String historyValue, String historyDate,
			VehicleType historyType) {
		this.historyValue = historyValue;
		this.historyDate = historyDate;
		this.historyType = historyType;
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

	public VehicleType getHistoryType() {
		return historyType;
	}

	public void setHistoryType(VehicleType historyType) {
		this.historyType = historyType;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\thistoryValue: " + historyValue
				+ "\n\thistoryDate: " + historyDate + "\n\thistoryType: "
				+ historyType + "\n}";
	}

}
