package bg.znestorov.sofbus24.entity;

public class VehicleStationEntity {

	private Integer stop;
	private Integer lid;
	private Integer vt;
	private Integer rid;

	public VehicleStationEntity(Integer stop, Integer lid, Integer vt,
			Integer rid) {
		this.stop = stop;
		this.lid = lid;
		this.vt = vt;
		this.rid = rid;
	}

	public Integer getStop() {
		return stop;
	}

	public void setStop(Integer stop) {
		this.stop = stop;
	}

	public Integer getLid() {
		return lid;
	}

	public void setLid(Integer lid) {
		this.lid = lid;
	}

	public Integer getVt() {
		return vt;
	}

	public void setVt(Integer vt) {
		this.vt = vt;
	}

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	/**
	 * Check if the vehicle is a Metro or not
	 * 
	 * @return if the vehicle is a Metro
	 */
	public boolean isMetroVehicleStation() {

		boolean isMetroVehicleStation;
		if (this.rid < 0) {
			isMetroVehicleStation = true;
		} else {
			isMetroVehicleStation = false;
		}

		return isMetroVehicleStation;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tstop: " + stop + "\n\tlid: " + lid
				+ "\n\tvt: " + vt + "\n\trid: " + rid + "\n}";
	}

}