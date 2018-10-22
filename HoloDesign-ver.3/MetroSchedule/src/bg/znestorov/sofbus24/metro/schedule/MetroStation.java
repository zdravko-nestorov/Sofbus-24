package bg.znestorov.sofbus24.metro.schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

import bg.znestorov.sofbus24.metro.utils.Utils;

public class MetroStation {

	private String number;
	private String name;
	private String direction;
	private HashMap<Integer, ArrayList<String>> holidaySchedule;
	private HashMap<Integer, ArrayList<String>> weekdaySchedule;

	public MetroStation(String number, String name, String direction) {

		this.number = number;
		this.name = Utils.formatName(name);
		this.direction = direction;

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
		this.name = Utils.formatName(name);
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public HashMap<Integer, ArrayList<String>> getHolidaySchedule() {
		return holidaySchedule;
	}

	public void setHolidaySchedule(HashMap<Integer, ArrayList<String>> holidaySchedule) {
		this.holidaySchedule = holidaySchedule;
	}

	public HashMap<Integer, ArrayList<String>> getWeekdaySchedule() {
		return weekdaySchedule;
	}

	public void setWeekdaySchedule(HashMap<Integer, ArrayList<String>> weekdaySchedule) {
		this.weekdaySchedule = weekdaySchedule;
	}

	public boolean isScheduleSet() {
		boolean result = false;

		for (int i = 4; i <= 24; i++) {
			if (!this.holidaySchedule.get(i).isEmpty() || !this.weekdaySchedule.get(i).isEmpty()) {
				result = true;
				break;
			}
		}

		return result;
	}

	public MetroStation merge(MetroStation ms) {

		Comparator<String> comparator = new Comparator<String>() {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm");

			@Override
			public int compare(String time1, String time2) {
				try {
					return dateFormat.parse(time1).compareTo(dateFormat.parse(time2));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		};

		for (int i = 4; i <= 24; i++) {
			holidaySchedule.get(i).addAll(ms.getHolidaySchedule().get(i));
			Collections.sort(holidaySchedule.get(i), comparator);

			weekdaySchedule.get(i).addAll(ms.getWeekdaySchedule().get(i));
			Collections.sort(weekdaySchedule.get(i), comparator);
		}

		return this;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tnumber: " + number + "\n\tname: " + name + "\n\tdirection: " + direction + "\n\tholidaySchedule: "
				+ holidaySchedule + "\n\tweekdaySchedule: " + weekdaySchedule
				+ "\n}";
	}

}