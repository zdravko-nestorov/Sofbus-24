package bg.znestorov.sofbus24.metro.stations;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class contains information about each of the Metro directions
 */
public class MetroDirection {

	private String id;
	private String name;
	private HashMap<String, String> stations = new LinkedHashMap<String, String>();

	public MetroDirection() {
	}

	public MetroDirection(String id, String name,
			HashMap<String, String> stations) {
		this.id = id;
		this.name = name;
		this.stations = stations;
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

	public HashMap<String, String> getStations() {
		return stations;
	}

	public void setStations(HashMap<String, String> stations) {
		this.stations = stations;
	}

	@Override
	public String toString() {
		return "MetroDirection [id=" + id + ", name=" + name + ", stations="
				+ stations + "]";
	}

}
