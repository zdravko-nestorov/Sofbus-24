package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

import android.location.Location;
import bg.znestorov.sofbus24.entity.Station;

import com.google.android.gms.maps.model.LatLng;

/**
 * Utils method containing functions concerning GoogleMaps stuff
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MapUtils {

	/**
	 * Get the distance between LatLng and Station object
	 * 
	 * @param latLng1
	 *            LatLng object
	 * @param station
	 *            Station object
	 * @return distance between the input objects (in case of an error - return
	 *         empty string)
	 */
	public static String getMapDistance(LatLng latLng1, Station station) {
		try {
			return getDistance(getLocation(latLng1), getLocation(station))
					.toString();
		} catch (Exception e) {
			return "∞";
		}
	}

	/**
	 * Get the distance between two LatLng objects
	 * 
	 * @param latLng1
	 *            LatLng object
	 * @param latLng2
	 *            LatLng object
	 * @return distance between the input objects (in case of an error - return
	 *         empty string)
	 */
	public static String getMapDistance(LatLng latLng1, LatLng latLng2) {
		try {
			return getDistance(getLocation(latLng1), getLocation(latLng2))
					.toString();
		} catch (Exception e) {
			return "∞";
		}
	}

	/**
	 * Create Location object via LatLng one
	 * 
	 * @param latLng
	 *            the input LatLng object
	 * @return a location object base on the input LatLng one
	 */
	public static Location getLocation(LatLng latLng) {
		Location location = new Location("");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);

		return location;
	}

	/**
	 * Create Location object via Station one
	 * 
	 * @param station
	 *            the input Station object
	 * @return a location object base on the input Station one
	 */
	public static Location getLocation(Station station) {
		Location location = new Location("");
		location.setLatitude(Double.parseDouble(station.getLat()));
		location.setLongitude(Double.parseDouble(station.getLon()));

		return location;
	}

	/**
	 * Get the distance between two locations and convert it to long with two
	 * decimal digits
	 * 
	 * @param location1
	 *            first location
	 * @param location2
	 *            second location
	 * @return the distance between the locations
	 */
	public static Float getDistance(Location location1, Location location2) {
		Float distanceTo = location1.distanceTo(location2);
		BigDecimal bd = new BigDecimal(distanceTo);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		distanceTo = rounded.floatValue();

		return distanceTo;
	}
}
