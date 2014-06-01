package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.VehicleType;

/**
 * Singleton used for loading the metro stations on the first creation and used
 * them lately
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroLoadStations {

	private static MetroLoadStations instance = null;

	private ArrayList<String> metroDirectionsNames;
	private ArrayList<ArrayList<Station>> metroDirectionsList;

	protected MetroLoadStations(Activity context) {
		// Get the directions' names
		VehiclesDataSource vehiclesDatasource = new VehiclesDataSource(context);
		vehiclesDatasource.open();
		String directionName1 = vehiclesDatasource
				.getVehicleDirection(VehicleType.METRO1);
		String directionName2 = vehiclesDatasource
				.getVehicleDirection(VehicleType.METRO2);
		vehiclesDatasource.close();

		// Adding the directions names to the ArrayList
		metroDirectionsNames = new ArrayList<String>();
		metroDirectionsNames.add(directionName1);
		metroDirectionsNames.add(directionName2);

		// Get the stations for each direction
		StationsDataSource stationsDatasource = new StationsDataSource(context);

		stationsDatasource.open();
		List<Station> metroDirection1 = stationsDatasource
				.getStationsViaType(VehicleType.METRO1);
		List<Station> metroDirection2 = stationsDatasource
				.getStationsViaType(VehicleType.METRO2);
		Collections.reverse(metroDirection2);
		stationsDatasource.close();

		metroDirectionsList = new ArrayList<ArrayList<Station>>();
		metroDirectionsList.add((ArrayList<Station>) metroDirection1);
		metroDirectionsList.add((ArrayList<Station>) metroDirection2);
	}

	public static MetroLoadStations getInstance(Activity context) {
		if (instance == null) {
			instance = new MetroLoadStations(context);
		}

		return instance;
	}

	public ArrayList<String> getMetroDirectionsNames() {
		return metroDirectionsNames;
	}

	public void setMetroDirectionsNames(ArrayList<String> metroDirectionsNames) {
		this.metroDirectionsNames = metroDirectionsNames;
	}

	public ArrayList<ArrayList<Station>> getMetroDirectionsList() {
		return metroDirectionsList;
	}

	public void setMetroDirectionsList(
			ArrayList<ArrayList<Station>> metroDirectionsList) {
		this.metroDirectionsList = metroDirectionsList;
	}

	/**
	 * Get the direction name via the current direction and format it if needed
	 * 
	 * @param currentDirection
	 *            the chosen direction (0 - METRO1, otherwise - METRO2)
	 * @param formatted
	 *            if the direction name should be formatted
	 * @param truncated
	 *            if the direction name should be truncated (the middle part to
	 *            be removed)
	 * @return the direction name
	 */
	public String getDirectionName(int currentDirection, boolean formatted,
			boolean truncated) {
		VehicleType vehicleType;
		switch (currentDirection) {
		case 0:
			vehicleType = VehicleType.METRO1;
			break;
		default:
			vehicleType = VehicleType.METRO2;
			break;
		}

		return getDirectionName(vehicleType, formatted, truncated);
	}

	/**
	 * Get the direction name via the vehicle type and format it if needed
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @param formatted
	 *            if the direction name should be formatted
	 * @param truncated
	 *            if the direction name should be truncated (the middle part to
	 *            be removed)
	 * @return the direction name
	 */
	public String getDirectionName(VehicleType vehicleType, boolean formatted,
			boolean truncated) {
		// Get the metro direction according to the vehicle type
		String metroDirectionName;
		switch (vehicleType) {
		case METRO1:
			metroDirectionName = metroDirectionsNames.get(0);
			break;
		default:
			metroDirectionName = metroDirectionsNames.get(1);
			break;
		}

		// Check if the direction name should be formatted
		if (formatted) {
			metroDirectionName = metroDirectionName.replaceAll("-", " - ");
		}

		// Check if the direction name should be truncated
		if (truncated) {
			metroDirectionName = metroDirectionName.replaceAll("-.*-", "-");
		}

		// Remove multiple spaces between words
		metroDirectionName = metroDirectionName.trim().replaceAll(" +", " ");

		return metroDirectionName;
	}

	/**
	 * Get a list with the stations for the current direction
	 * 
	 * @param currentDirection
	 *            the chosen direction (0 - METRO1, otherwise - METRO2)
	 * @return a list with the stations for the current direction
	 */
	public ArrayList<Station> getDirectionList(int currentDirection) {
		switch (currentDirection) {
		case 0:
			currentDirection = 0;
			break;
		default:
			currentDirection = 1;
			break;
		}

		return metroDirectionsList.get(currentDirection);
	}

	/**
	 * Get a list with the stations for the current vehicle type
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @return a list with the stations for the current vehicle type
	 */
	public ArrayList<Station> getDirectionList(VehicleType vehicleType) {
		int currentDirection;
		switch (vehicleType) {
		case METRO1:
			currentDirection = 0;
			break;
		default:
			currentDirection = 1;
			break;
		}

		return getDirectionList(currentDirection);
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tmetroDirectionsNames: "
				+ metroDirectionsNames + "\n\tmetroDirectionsList: "
				+ metroDirectionsList + "\n}";
	}

}
