package bg.znestorov.sofbus24.history;

import java.io.Serializable;

import bg.znestorov.sofbus24.entity.VehicleTypeEnum;

/**
 * History class representing each station search
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class HistoryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String historyValue;
	private String historyDate;
	private VehicleTypeEnum historyType;

	public HistoryEntity() {
	}

	public HistoryEntity(String historyValue, String historyDate,
			VehicleTypeEnum historyType) {
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

	public VehicleTypeEnum getHistoryType() {
		return historyType;
	}

	public void setHistoryType(VehicleTypeEnum historyType) {
		this.historyType = historyType;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\thistoryValue: " + historyValue
				+ "\n\thistoryDate: " + historyDate + "\n\thistoryType: "
				+ historyType + "\n}";
	}

}
