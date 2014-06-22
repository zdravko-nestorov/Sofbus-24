package bg.znestorov.sofbus24.db.entity;

public class Vehicle {

	private VehicleType type;
	private String number;
	private String direction;

	public Vehicle() {
	}

	public Vehicle(VehicleType type, String number, String direction) {
		this.type = type;
		this.number = number;
		this.direction = direction;
	}

	public Vehicle(String type, String number, String direction) {
		if ("1".equals(type)) {
			this.type = VehicleType.BUS;
		} else if ("2".equals(type)) {
			this.type = VehicleType.TROLLEY;
		} else {
			this.type = VehicleType.TRAM;
		}

		this.number = number;
		this.direction = direction;
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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\ttype: " + type + "\n\tnumber: "
				+ number + "\n\tdirection: " + direction + "\n}";
	}

}
