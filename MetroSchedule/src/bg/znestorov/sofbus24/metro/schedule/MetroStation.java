package bg.znestorov.sofbus24.metro.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MetroStation {

	private String number;
	private String name;
	private HashMap<Integer, ArrayList<String>> holidaySchedule;
	private HashMap<Integer, ArrayList<String>> weekdaySchedule;

	public MetroStation(String number, String name) {
		this.number = number;
		this.name = name;

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
		return "MetroStation [number=" + number + ", name=" + name
				+ ", holidaySchedule=" + holidaySchedule + ", weekdaySchedule="
				+ weekdaySchedule + "]";
	}

}
