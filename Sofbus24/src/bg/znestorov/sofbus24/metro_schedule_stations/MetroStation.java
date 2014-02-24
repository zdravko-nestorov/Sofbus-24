package bg.znestorov.sofbus24.metro_schedule_stations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MetroStation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String number;
	private String name;
	private String url;
	private HashMap<Integer, ArrayList<String>> holidaySchedule;
	private HashMap<Integer, ArrayList<String>> weekdaySchedule;

	public MetroStation(String number, String name, String url) {
		this.number = number;
		this.name = name;
		this.url = url;

		this.holidaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();
		this.weekdaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();
		for (int i = 4; i <= 24; i++) {
			this.holidaySchedule.put(i, new ArrayList<String>());
			this.weekdaySchedule.put(i, new ArrayList<String>());
		}
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Integer, ArrayList<String>> getHolidaySchedule() {
		return holidaySchedule;
	}

	public void setHolidaySchedule(
			HashMap<Integer, ArrayList<String>> holidaySchedule) {
		this.holidaySchedule = holidaySchedule;
	}

	public HashMap<Integer, ArrayList<String>> getWeekdaySchedule() {
		return weekdaySchedule;
	}

	public void setWeekdaySchedule(
			HashMap<Integer, ArrayList<String>> weekdaySchedule) {
		this.weekdaySchedule = weekdaySchedule;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isScheduleSet() {
		boolean result = false;

		for (int i = 4; i <= 24; i++) {
			if (!this.holidaySchedule.get(i).isEmpty()
					&& !this.weekdaySchedule.get(i).isEmpty()) {
				result = true;
				break;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return "MetroStation [number=" + number + ", name=" + name + ", url="
				+ url + ", holidaySchedule=" + holidaySchedule
				+ ", weekdaySchedule=" + weekdaySchedule + "]";
	}

}
