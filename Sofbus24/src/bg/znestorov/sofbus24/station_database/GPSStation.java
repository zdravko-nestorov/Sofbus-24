package bg.znestorov.sofbus24.station_database;

import java.io.Serializable;

// Represent each row in the STATIONS table in STATIONS DB
public class GPSStation implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String type;
	private String number;
	private String direction;
	private String time_stamp;
	private String lat;
	private String lon;
	private String codeO;

	public GPSStation() {
	}

	public GPSStation(String id, String name) {
		this.id = id;
		this.name = name;
		this.codeO = "-1";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String toString() {
		return getName() + " (" + getId() + ")";
	}

	public String getCodeO() {
		return codeO;
	}

	public void setCodeO(String codeO) {
		this.codeO = codeO;
	}

	// Only for testing purpose
	public String print() {
		return getId() + "\n" + getName() + "\n" + getType() + "\n"
				+ getNumber() + "\n" + getDirection() + "\n" + getTime_stamp()
				+ "\n" + getLat() + "\n" + getLon() + "\n" + getCodeO();
	}

}