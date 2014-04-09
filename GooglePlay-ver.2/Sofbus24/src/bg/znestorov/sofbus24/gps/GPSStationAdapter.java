package bg.znestorov.sofbus24.gps;

import java.util.ArrayList;

import android.app.Activity;
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

	private final Activity context;
	private final ArrayList<GPSStation> stations;

	private SharedPreferences sharedPreferences;

	static class ViewHolder {
		ImageView imageView;
		TextView vehicleNumber;
		TextView vehicleDirection;
		TextView vehicleTimeStamp;
	}

	public GPSStationAdapter(Activity context, ArrayList<GPSStation> stations) {
		super(context, R.layout.activity_gps_station, stations);
		this.context = context;
		this.stations = stations;
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GPSStation station = stations.get(position);

		return setVehicleRow(convertView, station);
	}

	// Vehicle row in the ListView
	public View setVehicleRow(View convertView, GPSStation station) {
		View rowView = convertView;
		ViewHolder viewHolder;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.activity_gps_station, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) rowView
					.findViewById(R.id.vehicle_image_view);
			viewHolder.vehicleNumber = (TextView) rowView
					.findViewById(R.id.vehicle_text_view);
			viewHolder.vehicleDirection = (TextView) rowView
					.findViewById(R.id.direction_text_view);
			viewHolder.vehicleTimeStamp = (TextView) rowView
					.findViewById(R.id.time_stamp_text_view);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

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
			viewHolder.imageView.setImageResource(R.drawable.bus_icon);
			viewHolder.vehicleNumber.setText(vehicleType + " ¹ "
					+ vehicleNumberText);
			viewHolder.vehicleDirection.setText(vehicleDirectionText);
			viewHolder.vehicleTimeStamp.setText(vehicleTimeStampText);
		} else if (vehicleType
				.equals(context.getString(R.string.title_trolley))) {
			viewHolder.imageView.setImageResource(R.drawable.trolley_icon);
			viewHolder.vehicleNumber.setText(vehicleType + " ¹ "
					+ vehicleNumberText);
			viewHolder.vehicleDirection.setText(vehicleDirectionText);
			viewHolder.vehicleTimeStamp.setText(vehicleTimeStampText);
		} else {
			viewHolder.imageView.setImageResource(R.drawable.tram_icon);
			viewHolder.vehicleNumber.setText(vehicleType + " ¹ "
					+ vehicleNumberText);
			viewHolder.vehicleDirection.setText(vehicleDirectionText);
			viewHolder.vehicleTimeStamp.setText(vehicleTimeStampText);
		}
		return rowView;
	}
}