package bg.znestorov.sofbus24.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used for transferring information from ScheduleFragment (HomeScreen) to the
 * PublicTransport activity
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class DirectionsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Vehicle vehicle;
	private int activeDirection;

	private ArrayList<String> directionsNames;
	private ArrayList<ArrayList<Station>> directionsList;

	public DirectionsEntity() {
	}

	public DirectionsEntity(DirectionsEntity directionsEntity,
			int activeDirection) {
		this.vehicle = directionsEntity.getVehicle();
		this.activeDirection = activeDirection;
		this.directionsNames = directionsEntity.getDirectionsNames();
		this.directionsList = directionsEntity.getDirectionsList();
	}

	public DirectionsEntity(Vehicle vehicle, int activeDirection,
			ArrayList<String> directionsNames,
			ArrayList<ArrayList<Station>> directionsList) {
		this.vehicle = vehicle;
		this.activeDirection = activeDirection;
		this.directionsNames = directionsNames;
		this.directionsList = directionsList;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public int getActiveDirection() {
		return activeDirection;
	}

	public void setActiveDirection(int activeDirection) {
		this.activeDirection = activeDirection;
	}

	public ArrayList<String> getDirectionsNames() {
		return directionsNames;
	}

	public void setDirectionsNames(ArrayList<String> directionsNames) {
		this.directionsNames = directionsNames;
	}

	public ArrayList<ArrayList<Station>> getDirectionsList() {
		return directionsList;
	}

	public void setDirectionsList(ArrayList<ArrayList<Station>> directionsList) {
		this.directionsList = directionsList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tvehicle: " + vehicle
				+ "\n\tactiveDirection: " + activeDirection
				+ "\n\tdirectionsNames: " + directionsNames
				+ "\n\tdirectionsList: " + directionsList + "\n}";
	}

}
