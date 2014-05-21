package bg.znestorov.sofbus24.schedule;

import java.util.List;

import android.content.Context;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;

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

	private List<Vehicle> busses;
	private List<Vehicle> trolleys;
	private List<Vehicle> trams;

	protected ScheduleLoadVehicles(Context context) {
		VehiclesDataSource vehiclesDatasource = new VehiclesDataSource(context);
		vehiclesDatasource.open();

		busses = vehiclesDatasource.getVehiclesViaSearch(VehicleType.BUS, "");
		trolleys = vehiclesDatasource.getVehiclesViaSearch(VehicleType.TROLLEY,
				"");
		trams = vehiclesDatasource.getVehiclesViaSearch(VehicleType.TRAM, "");

		vehiclesDatasource.close();
	}

	public static ScheduleLoadVehicles getInstance(Context context) {
		if (instance == null) {
			instance = new ScheduleLoadVehicles(context);
		}

		return instance;
	}

	public List<Vehicle> getBusses() {
		return busses;
	}

	public void setBusses(List<Vehicle> busses) {
		this.busses = busses;
	}

	public List<Vehicle> getTrolleys() {
		return trolleys;
	}

	public void setTrolleys(List<Vehicle> trolleys) {
		this.trolleys = trolleys;
	}

	public List<Vehicle> getTrams() {
		return trams;
	}

	public void setTrams(List<Vehicle> trams) {
		this.trams = trams;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tbusses: " + busses
				+ "\n\ttrolleys: " + trolleys + "\n\ttrams: " + trams + "\n}";
	}

}
