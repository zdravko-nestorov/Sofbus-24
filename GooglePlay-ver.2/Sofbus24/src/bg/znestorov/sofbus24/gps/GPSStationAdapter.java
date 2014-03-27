package bg.znestorov.sofbus24.gps;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;

// Class for creating the vehicles ListView
public class GPSStationAdapter extends ArrayAdapter<GPSStation> {

	private final Context context;
	private final ArrayList<GPSStation> stations;

	private SharedPreferences sharedPreferences;

	public GPSStationAdapter(Context context, ArrayList<GPSStation> stations) {
		super(context, R.layout.activity_gps_station, stations);
		this.context = context;
		this.stations = stations;
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		GPSStation station = stations.get(position);
		View rowView = convertView;
		rowView = setVehicleRow(inflater, parent, station);

		return rowView;
	}

	// Vehicle row in the ListView
	public View setVehicleRow(LayoutInflater inflater, ViewGroup parent,
			GPSStation station) {
		View rowView = inflater.inflate(R.layout.activity_gps_station, parent,
				false);

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.vehicle_image_view);
		TextView vehicleNumber = (TextView) rowView
				.findViewById(R.id.vehicle_text_view);
		TextView vehicleDirection = (TextView) rowView
				.findViewById(R.id.direction_text_view);
		TextView vehicleTimeStamp = (TextView) rowView
				.findViewById(R.id.time_stamp_text_view);

		String vehicleType = station.getType();
		String vehicleNumberText = station.getNumber();
		String vehicleDirectionText = station.getDirection();

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);

		// Get "exitAlert" value from the Shared Preferences
		String timeGPS = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_GPS,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_GPS);
		String vehicleTimeStampText = "";

		if ("timeGPS_remaining".equals(timeGPS)) {
			vehicleTimeStampText = context.getString(R.string.time_remaining)
					+ station.getTime_stamp();
		} else {
			vehicleTimeStampText = context.getString(R.string.time_arrival)
					+ station.getTime_stamp();
		}

		if (vehicleType.equals(context.getString(R.string.title_bus))) {
			imageView.setImageResource(R.drawable.bus_icon);
			vehicleNumber.setText(vehicleType + " � " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
			vehicleTimeStamp.setText(vehicleTimeStampText);
		} else if (vehicleType
				.equals(context.getString(R.string.title_trolley))) {
			imageView.setImageResource(R.drawable.trolley_icon);
			vehicleNumber.setText(vehicleType + " � " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
			vehicleTimeStamp.setText(vehicleTimeStampText);
		} else {
			imageView.setImageResource(R.drawable.tram_icon);
			vehicleNumber.setText(vehicleType + " � " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
			vehicleTimeStamp.setText(vehicleTimeStampText);
		}
		return rowView;
	}
}