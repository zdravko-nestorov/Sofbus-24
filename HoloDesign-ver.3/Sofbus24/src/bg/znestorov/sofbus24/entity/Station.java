package bg.znestorov.sofbus24.entity;

import java.io.Serializable;

import bg.znestorov.sofbus24.utils.Utils;

/**
 * Class representing a station structure with all common fields (implements
 * Serializable, so can be transferred between activities)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Station implements Serializable {

	private static final long serialVersionUID = 1L;

	private String number;
	private String name;
	private String lat;
	private String lon;
	private VehicleType type;

	/**
	 * It is used in two cases with different meanings:
	 * <ul>
	 * <li><b>Bus, Trolley, Tram</b> - the position of the station in case of
	 * multiple results</li>
	 * <li><b>Metro</b> - the URL address of the station</li>
	 * </ul>
	 */
	private String customField;

	public Station() {
	}

	public Station(String number, String name, String lat, String lon,
			VehicleType type, String customField) {
		this.number = number;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.type = type;
		this.customField = customField;
	}

	public String getNumber() {
		return Utils.formatNumberOfDigits(number, 4);
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

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tnumber: " + number + "\n\tname: "
				+ name + "\n\tlat: " + lat + "\n\tlon: " + lon + "\n\ttype: "
				+ type + "\n\tcustomField: " + customField + "\n}";
	}
}
