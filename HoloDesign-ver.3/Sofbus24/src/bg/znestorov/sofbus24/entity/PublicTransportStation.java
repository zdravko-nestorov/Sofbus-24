package bg.znestorov.sofbus24.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Class representing each station of the public tranport (add direction and
 * schedule)
 * 
 * @author Zdravko Nestorov
 * 
 */
public class PublicTransportStation extends Station implements Serializable {

	private static final long serialVersionUID = 1L;

	private String direction;
	private HashMap<Integer, ArrayList<String>> schedule;

	public PublicTransportStation() {
		super();

		this.schedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.schedule.put(i, new ArrayList<String>());
		}
	}

	public PublicTransportStation(Station station) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.schedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.schedule.put(i, new ArrayList<String>());
		}
	}

	public PublicTransportStation(Station station, String direction) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.direction = direction;
		this.schedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.schedule.put(i, new ArrayList<String>());
		}
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public HashMap<Integer, ArrayList<String>> getSchedule() {
		return schedule;
	}

	public void setSchedule(HashMap<Integer, ArrayList<String>> schedule) {
		this.schedule = schedule;
	}

	/**
	 * Check if the schedule is set to the PublicTransportStation object
	 * 
	 * @return if the object is filled with the time schedule
	 */
	public boolean isScheduleSet() {
		boolean result = false;

		for (int i = 4; i <= 24; i++) {
			if (!this.schedule.get(i).isEmpty()) {
				result = true;
				break;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return "PublicTransportStation [direction=" + direction + ", schedule="
				+ schedule + "]";
	}

}
