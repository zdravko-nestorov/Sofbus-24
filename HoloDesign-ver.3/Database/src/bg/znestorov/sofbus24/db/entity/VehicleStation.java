package bg.znestorov.sofbus24.db.entity;

public class VehicleStation {

	private VehicleType vehicleType;
	private String vehicleNumber;
	private String stationNumber;
	private String vt;
	private String lid;
	private String rid;
	private String stop;
	private Integer direction;

	public VehicleStation(VehicleType vehicleType, String vehicleNumber,
			String stationNumber, String vt, String lid, String rid,
			String stop, Integer direction) {
		this.vehicleType = vehicleType;
		this.vehicleNumber = vehicleNumber;
		this.stationNumber = stationNumber;
		this.vt = vt;
		this.lid = lid;
		this.rid = rid;
		this.stop = stop;
		this.direction = direction;
	}

	public VehicleStation(VehicleType vehicleType, String vehicleNumber,
			String stationNumber, Integer direction) {
		this.vehicleType = vehicleType;
		this.vehicleNumber = vehicleNumber;
		this.stationNumber = stationNumber;
		this.direction = direction;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getStationNumber() {
		return stationNumber;
	}

	public void setStationNumber(String stationNumber) {
		this.stationNumber = stationNumber;
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

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tvehicleType: " + vehicleType
				+ "\n\tvehicleNumber: " + vehicleNumber + "\n\tstationNumber: "
				+ stationNumber + "\n\tvt: " + vt + "\n\tlid: " + lid
				+ "\n\trid: " + rid + "\n\tstop: " + stop + "\n\tdirection: "
				+ direction + "\n}";
	}
}
