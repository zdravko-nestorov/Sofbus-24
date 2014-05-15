package bg.znestorov.sofbus24.publictransport;

import java.io.Serializable;
import java.util.ArrayList;

import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.Vehicle;

/**
 * Used for transferring information from ScheduleFragment (HomeScreen) to the
 * PublicTransport activity
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class PublicTransportDirections implements Serializable {

	private static final long serialVersionUID = 1L;

	private Vehicle vehicle;
	private int activeDirection;

	private ArrayList<String> directionsNames;
	private ArrayList<ArrayList<Station>> directionsList;

	public PublicTransportDirections() {
	}

	public PublicTransportDirections(Vehicle vehicle, int activeDirection,
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
		return "PublicTransportDirections [vehicle=" + vehicle
				+ ", activeDirection=" + activeDirection + ", directionsNames="
				+ directionsNames + ", directionsList=" + directionsList + "]";
	}

}
