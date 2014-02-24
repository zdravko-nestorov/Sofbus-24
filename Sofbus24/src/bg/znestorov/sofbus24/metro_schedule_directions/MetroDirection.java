package bg.znestorov.sofbus24.metro_schedule_directions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import bg.znestorov.sofbus24.metro_schedule_stations.MetroStation;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * This class contains information about each of the Metro directions
 * 
 * @author zanio
 * 
 */
public class MetroDirection implements Serializable {

	private static final long serialVersionUID = 1L;
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

	/**
	 * Add a station to the Station HashMap structure
	 * 
	 * @param id
	 *            the number of the station
	 * @param name
	 *            the name of the station
	 * @param url
	 *            the url of the station information
	 */
	public void addStation(String id, String name, String url) {
		HashMap<String, String> urlNameMap = new HashMap<String, String>();
		urlNameMap.put(Constants.METRO_STATION_NAME_KEY, name);
		urlNameMap.put(Constants.METRO_STATION_URL_KEY, url);

		this.stations.put(id, urlNameMap);
	}

	/**
	 * Transform the Station HashMap structure to an Array of MetroStation
	 * objects
	 * 
	 * @return an array of MetroStation objects
	 */
	public ArrayList<MetroStation> getStationsAsList() {
		ArrayList<MetroStation> metroStations = new ArrayList<MetroStation>();

		Iterator<Entry<String, HashMap<String, String>>> iterator = this.stations
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, HashMap<String, String>> mapEntry = (Map.Entry<String, HashMap<String, String>>) iterator
					.next();
			String stationNumber = mapEntry.getKey();
			String stationName = mapEntry.getValue().get(
					Constants.METRO_STATION_NAME_KEY);
			String stationUrl = mapEntry.getValue().get(
					Constants.METRO_STATION_URL_KEY);

			MetroStation ms = new MetroStation(stationNumber, stationName,
					stationUrl);
			metroStations.add(ms);
		}

		return metroStations;
	}

	@Override
	public String toString() {
		return "MetroDirection [id=" + id + ", name=" + name + ", stations="
				+ stations + "]";
	}

}
