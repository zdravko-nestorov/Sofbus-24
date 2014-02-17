package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;

/**
 * This class contains information about each of the Metro directions
 * 
 * @author zanio
 * 
 */
public class MetroDirection {

	private String id;
	private String name;
	private ArrayList<String> stationNames = new ArrayList<String>();
	private ArrayList<String> stationNumbers = new ArrayList<String>();

	public MetroDirection() {
	}

	public MetroDirection(String id, String name,
			ArrayList<String> stationNames, ArrayList<String> stationNumbers) {
		this.id = id;
		this.name = name;
		this.stationNames = stationNames;
		this.stationNumbers = stationNumbers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getStationNames() {
		return stationNames;
	}

	public void setStationNames(ArrayList<String> stations) {
		this.stationNames = stations;
	}

	public ArrayList<String> getStationNumbers() {
		return stationNumbers;
	}

	public void setStationNumbers(ArrayList<String> stationNumbers) {
		this.stationNumbers = stationNumbers;
	}

	@Override
	public String toString() {
		return "MetroDirection [id=" + id + ", name=" + name + ", stations="
				+ stationNames + ", stationNumbers=" + stationNumbers + "]";
	}

}
