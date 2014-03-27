package bg.znestorov.sofbus24.schedule_stations;

import java.io.Serializable;
import java.util.ArrayList;

// Class containing the information from "http://m.sumc.bg/schedules?tt=xxx&ln=xxx&s=Search" for the chosen vehicle
// Implementing Serializable, so can transfer the object from one activity to another
public class Direction implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String vehicleType;
	private String vehicleNumber;
	private String direction;
	private String vt;
	private String lid;
	private String rid;
	private ArrayList<String> stop;
	private ArrayList<String> stations;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getVt() {
		return vt;
	}

	public void setVt(String vt) {
		this.vt = vt;
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public ArrayList<String> getStop() {
		return stop;
	}

	public void setStop(ArrayList<String> stop) {
		this.stop = stop;
	}

	public ArrayList<String> getStations() {
		return stations;
	}

	public void setStations(ArrayList<String> stations) {
		this.stations = stations;
	}

	// For testing purpose
	public String toString() {
		return getId() + "\n" + getVehicleType() + "\n" + getVehicleNumber()
				+ "\n" + getDirection() + "\n" + getVt() + "\n" + getLid()
				+ "\n" + getLid() + "\n" + getStop() + "\n" + getStations();
	}
}
