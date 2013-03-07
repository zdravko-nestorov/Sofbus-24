package bg.znestorov.sofbus24.schedule_stations;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;

// Class for creating the vehicles ListView
public class StationAdapter extends ArrayAdapter<Station> {
	private final Context context;
	private final ArrayList<Station> stations;

	public StationAdapter(Context context, ArrayList<Station> stations) {
		super(context, R.layout.activity_vehicle, stations);
		this.context = context;
		this.stations = stations;
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Station station = stations.get(position);
		View rowView = convertView;

		// Checking what to put - vehicle or separator row
		if (station.getStation().equals("0")) {
			rowView = setSeparatorRow(inflater, parent, station);
		} else {
			rowView = setDirectionRow(inflater, parent, station);
		}

		return rowView;
	}

	// Direction row in the ListView
	public View setDirectionRow(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(R.layout.activity_station, parent,
				false);

		TextView vehicleStation = (TextView) rowView
				.findViewById(R.id.list_item_section_text);
		vehicleStation.setText(station.getStation());

		return rowView;
	}

	// Separator row in the ListView
	public View setSeparatorRow(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(R.layout.activity_station_separator,
				parent, false);

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		TextView vehicleNumber = (TextView) rowView
				.findViewById(R.id.vehicle_text_view);
		TextView vehicleDirection = (TextView) rowView
				.findViewById(R.id.direction_text_view);

		String vehicleType = station.getVehicleType();
		String vehicleNumberText = station.getVehicleNumber();
		String vehicleDirectionText = station.getDirection();

		if (vehicleType.equals(context.getString(R.string.title_bus))) {
			vehicleNumber.setText(vehicleType + " ¹ " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
		} else if (vehicleType
				.equals(context.getString(R.string.title_trolley))) {
			vehicleNumber.setText(vehicleType + " ¹ " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
		} else {
			vehicleNumber.setText(vehicleType + " ¹ " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
		}

		return rowView;
	}
}