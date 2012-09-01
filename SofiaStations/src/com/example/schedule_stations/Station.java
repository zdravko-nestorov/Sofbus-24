package com.example.schedule_stations;

import java.io.Serializable;

public class Station implements Serializable {

	private static final long serialVersionUID = 1L;
	private String vehicleType;
	private String vehicleNumber;
	private String direction;
	private String vt;
	private String lid;
	private String rid;
	private String stop;
	private String station;
	private String time_stamp;
	private String[] coordinates;

	public Station(Direction direction, int index) {
		this.vehicleType = direction.getVehicleType();
		this.vehicleNumber = direction.getVehicleNumber();
		this.direction = direction.getDirection();
		this.vt = direction.getVt();
		this.lid = direction.getLid();
		this.rid = direction.getRid();
		this.stop = direction.getStop().get(index);
		this.station = direction.getStations().get(index);
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public String getDirection() {
		return direction;
	}

	public String getVt() {
		return vt;
	}

	public String getLid() {
		return lid;
	}

	public String getRid() {
		return rid;
	}

	public String getStop() {
		return stop;
	}

	public String getStation() {
		return station;
	}

	public String getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}

	public String[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String[] coordinates) {
		this.coordinates = coordinates;
	}

	// For testing purpose
	public String toString() {
		return getVehicleType() + "\n" + getVehicleNumber() + "\n"
				+ getDirection() + "\n" + getVt() + "\n" + getLid() + "\n"
				+ getLid() + "\n" + getStop() + "\n" + getStation() + "\n"
				+ getTime_stamp() + "\n" + getCoordinates();
	}

}
