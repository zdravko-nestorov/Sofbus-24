package bg.znestorov.sofbus24.gps.station_choice;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.station_database.GPSStation;

// Class for creating the vehicles ListView
public class VBStationChoiceAdapter extends ArrayAdapter<GPSStation> {
	private final Context context;
	private final List<GPSStation> stations;

	public VBStationChoiceAdapter(Context context, List<GPSStation> stations) {
		super(context, R.layout.activity_gps_station_choice, stations);
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

		rowView = setRow(inflater, parent, station);

		return rowView;
	}

	// Station row in the ListView
	public View setRow(LayoutInflater inflater, ViewGroup parent,
			GPSStation station) {
		View rowView = inflater.inflate(
				R.layout.activity_gps_station_choice_list_row, parent, false);

		TextView vehicleGPSStation = (TextView) rowView
				.findViewById(R.id.vb_station_choice_text);

		vehicleGPSStation.setText(station.getName() + " (" + station.getId()
				+ ")");

		return rowView;
	}
}