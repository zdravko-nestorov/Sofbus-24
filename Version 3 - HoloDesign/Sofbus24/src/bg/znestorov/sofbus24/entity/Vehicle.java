package bg.znestorov.sofbus24.entity;

import java.io.Serializable;

/**
 * Class representing a vehicle structure with all common fields (implements
 * Serializable, so can be transferred between activities)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Vehicle implements Serializable {

	private static final long serialVersionUID = 1L;

	private String number;
	private VehicleType type;
	private String direction;

	public Vehicle() {
	}

	public Vehicle(String number, VehicleType type, String direction) {
		this.number = number;
		this.type = type;
		this.direction = direction;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "Vehicle [number=" + number + ", type=" + type + ", direction="
				+ direction + "]";
	}

}
