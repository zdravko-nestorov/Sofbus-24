package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;

/**
 * Singleton used for loading the vehicles on the first creation and used them
 * lately
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleLoadVehicles {

	private static ScheduleLoadVehicles instance = null;

	private List<VehicleEntity> busses;
	private List<VehicleEntity> trolleys;
	private List<VehicleEntity> trams;

	protected ScheduleLoadVehicles(Context context) {
		VehiclesDataSource vehiclesDatasource = new VehiclesDataSource(context);
		vehiclesDatasource.open();

		busses = vehiclesDatasource.getVehiclesViaSearch(VehicleTypeEnum.BUS,
				"");
		trolleys = vehiclesDatasource.getVehiclesViaSearch(
				VehicleTypeEnum.TROLLEY, "");
		trams = vehiclesDatasource.getVehiclesViaSearch(VehicleTypeEnum.TRAM,
				"");

		vehiclesDatasource.close();
	}

	public static ScheduleLoadVehicles getInstance(Context context) {
		if (instance == null) {
			instance = new ScheduleLoadVehicles(context);
		}

		return instance;
	}

	public List<VehicleEntity> getBusses() {
		return busses;
	}

	public void setBusses(List<VehicleEntity> busses) {
		this.busses = busses;
	}

	public List<VehicleEntity> getTrolleys() {
		return trolleys;
	}

	public void setTrolleys(List<VehicleEntity> trolleys) {
		this.trolleys = trolleys;
	}

	public List<VehicleEntity> getTrams() {
		return trams;
	}

	public void setTrams(List<VehicleEntity> trams) {
		this.trams = trams;
	}

	/**
	 * Get a list with the vehicles for the current vehicle type (integer code)
	 * 
	 * @param vehicleType
	 *            the vehicle type (integer code)
	 * @return a list with the vehicles for the current vehicle type
	 */
	public ArrayList<VehicleEntity> getVehiclesList(int vehicleType) {
		ArrayList<VehicleEntity> vehiclesList = new ArrayList<VehicleEntity>();
		switch (vehicleType) {
		case 0:
			vehiclesList.addAll(busses);
			break;
		case 1:
			vehiclesList.addAll(trolleys);
			break;
		default:
			vehiclesList.addAll(trams);
			break;
		}

		return vehiclesList;
	}

	/**
	 * Get a list with the stations for the current vehicle type
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @return a list with the vehicles for the current vehicle type
	 */
	public ArrayList<VehicleEntity> getDirectionList(VehicleTypeEnum vehicleType) {
		int currentDirection;
		switch (vehicleType) {
		case BUS:
			currentDirection = 0;
			break;
		case TROLLEY:
			currentDirection = 1;
			break;
		default:
			currentDirection = 2;
			break;
		}

		return getVehiclesList(currentDirection);
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tbusses: " + busses
				+ "\n\ttrolleys: " + trolleys + "\n\ttrams: " + trams + "\n}";
	}

}
