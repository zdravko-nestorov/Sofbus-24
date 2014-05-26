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
	private int activeStation;

	private ArrayList<String> vt;
	private ArrayList<String> lid;
	private ArrayList<String> rid;

	private ArrayList<String> directionsNames;
	private ArrayList<ArrayList<Station>> directionsList;

	public DirectionsEntity() {
		this.vt = new ArrayList<String>();
		this.lid = new ArrayList<String>();
		this.rid = new ArrayList<String>();
		this.directionsNames = new ArrayList<String>();
		this.directionsList = new ArrayList<ArrayList<Station>>();
	}

	public DirectionsEntity(DirectionsEntity directionsEntity,
			int activeDirection) {
		this.vehicle = directionsEntity.getVehicle();
		this.activeDirection = activeDirection;
		this.activeStation = directionsEntity.getActiveStation();
		this.vt = directionsEntity.getVt();
		this.lid = directionsEntity.getLid();
		this.rid = directionsEntity.getRid();
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

	public int getActiveStation() {
		return activeStation;
	}

	public void setActiveStation(int activeStation) {
		this.activeStation = activeStation;
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

	public ArrayList<String> getVt() {
		return vt;
	}

	public void setVt(ArrayList<String> vt) {
		this.vt = vt;
	}

	public ArrayList<String> getLid() {
		return lid;
	}

	public void setLid(ArrayList<String> lid) {
		this.lid = lid;
	}

	public ArrayList<String> getRid() {
		return rid;
	}

	public void setRid(ArrayList<String> rid) {
		this.rid = rid;
	}

	/**
	 * Check if the direction entity is set correctly
	 * 
	 * @return if the directions' names and stations are set
	 */
	public boolean isDirectionSet() {
		return directionsNames.size() > 0 && directionsList.size() > 0
				&& directionsNames.size() == directionsList.size();
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tvehicle: " + vehicle
				+ "\n\tactiveDirection: " + activeDirection
				+ "\n\tactiveStation: " + activeStation + "\n\tvt: " + vt
				+ "\n\tlid: " + lid + "\n\trid: " + rid
				+ "\n\tdirectionsNames: " + directionsNames
				+ "\n\tdirectionsList: " + directionsList + "\n}";
	}

}