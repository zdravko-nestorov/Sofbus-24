package bg.znestorov.sofbus24.entity;

import bg.znestorov.sofbus24.utils.Constants;

public class VehicleStationEntity {

	private Integer stop;
	private Integer lid;
	private Integer vt;
	private Integer rid;
	private String url;

	public VehicleStationEntity(Integer stop, Integer lid, Integer vt,
			Integer rid) {
		this.stop = stop;
		this.lid = lid;
		this.vt = vt;
		this.rid = rid;
		this.url = Constants.DROIDTRANS_URL_SCHEDULE;
	}

	public String getStop() {
		return stop + "";
	}

	public void setStop(Integer stop) {
		this.stop = stop;
	}

	public String getLid() {
		return lid + "";
	}

	public void setLid(Integer lid) {
		this.lid = lid;
	}

	public String getVt() {
		return vt + "";
	}

	public void setVt(Integer vt) {
		this.vt = vt;
	}

	public String getRid() {
		return rid + "";
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
				+ "\n\tvt: " + vt + "\n\trid: " + rid + "\n\turl: " + url
				+ "\n}";
	}

}