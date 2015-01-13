package bg.znestorov.sofbus24.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing a vehicle structure with all common fields (implements
 * Serializable, so can be transferred between activities)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VehicleEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String number;
	private VehicleTypeEnum type;
	private String direction;
	private ArrayList<String> arrivalTimes;

	public VehicleEntity() {
	}

	public VehicleEntity(VehicleTypeEnum type) {
		this.type = type;
		this.arrivalTimes = new ArrayList<String>();
	}

	public VehicleEntity(String number, VehicleTypeEnum type, String direction) {
		this.number = number;
		this.type = type;
		this.direction = direction;
		this.arrivalTimes = new ArrayList<String>();
	}

	public VehicleEntity(String number, VehicleTypeEnum type, String direction,
			ArrayList<String> arrivalTimes) {
		this.number = number;
		this.type = type;
		this.direction = direction;
		this.arrivalTimes = arrivalTimes;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public VehicleTypeEnum getType() {
		return type;
	}

	public void setType(VehicleTypeEnum type) {
		this.type = type;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public ArrayList<String> getArrivalTimes() {
		return arrivalTimes;
	}

	public void setArrivalTimes(ArrayList<String> arrivalTimes) {
		this.arrivalTimes = arrivalTimes;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tnumber: " + number + "\n\ttype: "
				+ type + "\n\tdirection: " + direction + "\n\tarrivalTimes: "
				+ arrivalTimes + "\n}";
	}

}
