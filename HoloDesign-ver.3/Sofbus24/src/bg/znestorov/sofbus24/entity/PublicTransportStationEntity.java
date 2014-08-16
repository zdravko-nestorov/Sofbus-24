package bg.znestorov.sofbus24.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Class representing each station of the public transport (add direction and
 * schedule)
 * 
 * @author Zdravko Nestorov
 * 
 */
public class PublicTransportStationEntity extends StationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String direction;
	private HashMap<Integer, ArrayList<String>> schedule;

	public PublicTransportStationEntity() {
		super();
		this.setScheduleMap();
	}

	public PublicTransportStationEntity(StationEntity station) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.schedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.schedule.put(i, new ArrayList<String>());
		}
	}

	public PublicTransportStationEntity(StationEntity station, String id) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.id = id;
		this.schedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.schedule.put(i, new ArrayList<String>());
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	 * Set the schedule to the relevant index of the HashMap schedule
	 * 
	 * @param hour
	 *            the hour of the schedule time
	 * @param time
	 *            the whole schedule time in format [hh:mm]
	 */
	public void setScheduleHour(int hour, String time) {
		this.schedule.get(hour).add(time);
	}

	/**
	 * Clear the schedule and prepare the instance to set a new one
	 */
	public void resetSchedule() {
		this.setScheduleMap();
	}

	/**
	 * Reset the schedule map to the default condition
	 */
	private void setScheduleMap() {
		if (this.schedule != null) {
			this.schedule.clear();
		} else {
			this.schedule = new LinkedHashMap<Integer, ArrayList<String>>();
		}

		for (int i = 4; i <= 24; i++) {
			this.schedule.put(i, new ArrayList<String>());
		}
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
		return getClass().getName() + " {\n\tid: " + id + "\n\tdirection: "
				+ direction + "\n\tschedule: " + schedule + "\n}";
	}

}