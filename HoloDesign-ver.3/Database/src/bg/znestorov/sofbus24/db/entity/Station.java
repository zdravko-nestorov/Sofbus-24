package bg.znestorov.sofbus24.db.entity;

public class Station {

	private VehicleType type;
	private String number;
	private String name;
	private String latitude;
	private String longitude;
	private int direction;

	// Used to make a relation between the vehicle and the station
	private String stop;

	public Station(VehicleType type, String number, String name, String stop,
			int direction) {
		this.type = type;
		this.number = number;
		this.name = name;
		this.stop = stop;
		this.latitude = "";
		this.longitude = "";
		this.direction = direction;
	}

	public Station(String station) {
		if (station != null && station.split(",").length == 5) {
			String[] stationArr = station.split(",");

			this.type = VehicleType.valueOf(stationArr[1]);
			this.number = stationArr[0];
			this.name = stationArr[2];
			this.latitude = stationArr[3];
			this.longitude = stationArr[4];
			this.stop = stationArr[0];
		}
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public int hashCode() {
		return number.hashCode();
	}

	@Override
	public boolean equals(Object station) {
		if (!(station instanceof Station)) {
			return false;
		}

		Station newStation = (Station) station;
		return this.number.equals(newStation.number);
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\ttype: " + type + "\n\tnumber: "
				+ number + "\n\tname: " + name + "\n\tlatitude: " + latitude
				+ "\n\tlongitude: " + longitude + "\n\tdirection: " + direction
				+ "\n\tstop: " + stop + "\n}";
	}

}
