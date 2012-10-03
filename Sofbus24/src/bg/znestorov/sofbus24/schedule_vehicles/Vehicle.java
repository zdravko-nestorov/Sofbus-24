package bg.znestorov.sofbus24.schedule_vehicles;

// Class containing the information from for each Vehicle
public class Vehicle {

	private String type;
	private String number;
	private String direction;

	public Vehicle(String type, String number, String direction) {
		this.type = type;
		this.number = number;
		this.direction = direction;
	}

	public String getType() {
		return type;
	}

	public String getNumber() {
		return number;
	}

	public String getDirection() {
		return direction;
	}

}
