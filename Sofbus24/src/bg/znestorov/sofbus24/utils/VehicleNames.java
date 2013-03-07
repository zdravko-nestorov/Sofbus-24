package bg.znestorov.sofbus24.utils;

import java.util.ArrayList;

import android.content.Context;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.schedule_vehicles.Vehicle;

// Filling the ArrayList<Vehicle>
public class VehicleNames {

	private String[] bus_number;
	private String[] bus_stations;
	private String[] trolley_number;
	private String[] trolley_stations;
	private String[] tram_number;
	private String[] tram_stations;

	public final static ArrayList<Vehicle> bus = new ArrayList<Vehicle>();
	public final static ArrayList<Vehicle> trolley = new ArrayList<Vehicle>();
	public final static ArrayList<Vehicle> tram = new ArrayList<Vehicle>();

	public VehicleNames(Context context) {
		bus_number = context.getResources().getStringArray(R.array.bus_numbers);
		bus_stations = context.getResources().getStringArray(
				R.array.bus_stations);
		trolley_number = context.getResources().getStringArray(
				R.array.trolley_numbers);
		trolley_stations = context.getResources().getStringArray(
				R.array.trolley_stations);
		tram_number = context.getResources().getStringArray(
				R.array.tram_numbers);
		tram_stations = context.getResources().getStringArray(
				R.array.tram_stations);

		this.emptyArrays();
		this.fillArrays(context);
	}

	private void fillArrays(Context context) {
		for (int i = 0; i < bus_number.length; i++) {
			bus.add(new Vehicle(context.getString(R.string.title_bus),
					bus_number[i], bus_stations[i]));
		}
		for (int i = 0; i < trolley_number.length; i++) {
			trolley.add(new Vehicle(context.getString(R.string.title_trolley),
					trolley_number[i], trolley_stations[i]));
		}
		for (int i = 0; i < tram_number.length; i++) {
			tram.add(new Vehicle(context.getString(R.string.title_tram),
					tram_number[i], tram_stations[i]));
		}
	}

	private void emptyArrays() {
		bus.clear();
		trolley.clear();
		tram.clear();
	}

}
