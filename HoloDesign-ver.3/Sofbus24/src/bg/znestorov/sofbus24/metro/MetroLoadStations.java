package bg.znestorov.sofbus24.metro;

import java.util.List;

import android.app.Activity;
import bg.znestorov.sofbus24.databases.StationsDataSource;
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
	private List<Station> metroDirection1;
	private List<Station> metroDirection2;

	protected MetroLoadStations(Activity context) {
		StationsDataSource stationsDatasource = new StationsDataSource(context);

		stationsDatasource.open();
		metroDirection1 = stationsDatasource
				.getStationsViaType(VehicleType.METRO1);
		metroDirection2 = stationsDatasource
				.getStationsViaType(VehicleType.METRO2);
		stationsDatasource.close();
	}

	public static MetroLoadStations getInstance(Activity context) {
		if (instance == null) {
			instance = new MetroLoadStations(context);
		}

		return instance;
	}

	public List<Station> getMetroDirection1() {
		return metroDirection1;
	}

	public void setMetroDirection1(List<Station> metroDirection1) {
		this.metroDirection1 = metroDirection1;
	}

	public List<Station> getMetroDirection2() {
		return metroDirection2;
	}

	public void setMetroDirection2(List<Station> metroDirection2) {
		this.metroDirection2 = metroDirection2;
	}

	@Override
	public String toString() {
		return "MetroLoadStations [metroDirection1=" + metroDirection1
				+ ", metroDirection2=" + metroDirection2 + "]";
	}

}
