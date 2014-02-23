package bg.znestorov.sofbus24.metro;

import java.util.HashMap;
import java.util.LinkedHashMap;

import bg.znestorov.sofbus24.utils.Constants;

/**
 * This class contains information about each of the Metro directions
 * 
 * @author zanio
 * 
 */
public class MetroDirection {

	private String id;
	private String name;

	/**
	 * Object with the following structure:<br>
	 * [station ID = [name=<b>station name</b>, url=<b>station URL address</b>]]
	 */
	private HashMap<String, HashMap<String, String>> stations = new LinkedHashMap<String, HashMap<String, String>>();

	public MetroDirection() {
	}

	public MetroDirection(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public MetroDirection(String id, String name,
			HashMap<String, HashMap<String, String>> stations) {
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

	public HashMap<String, HashMap<String, String>> getStations() {
		return stations;
	}

	public void setStations(HashMap<String, HashMap<String, String>> stations) {
		this.stations = stations;
	}

	public void addStation(String id, String name, String url) {
		HashMap<String, String> urlNameMap = new HashMap<String, String>();
		urlNameMap.put(Constants.METRO_STATION_NAME_KEY, name);
		urlNameMap.put(Constants.METRO_STATION_URL_KEY, url);

		this.stations.put(id, urlNameMap);
	}

	@Override
	public String toString() {
		return "MetroDirection [id=" + id + ", name=" + name + ", stations="
				+ stations + "]";
	}

}
