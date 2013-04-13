package bg.znestorov.sofbus24.schedule_vehicles;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;

// Class for creating the vehicles ListView
public class VehicleAdapter extends ArrayAdapter<Vehicle> {

	private final Context context;
	private final ArrayList<Vehicle> list_values;

	public VehicleAdapter(Context context, ArrayList<Vehicle> list_values) {
		super(context, R.layout.activity_vehicle_search, list_values);
		this.context = context;
		this.list_values = list_values;
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final Vehicle vehicle = (Vehicle) list_values.get(position);
		final String vehicleType = vehicle.getType();

		View rowView = convertView;
		rowView = setVehicleRow(inflater, parent, vehicle, vehicleType);

		return rowView;
	}

	// Vehicle row in the ListView
	public View setVehicleRow(LayoutInflater inflater, ViewGroup parent,
			Vehicle vehicle, String vehicleType) {
		View rowView = inflater.inflate(R.layout.activity_vehicle, parent,
				false);

		TextView vehicleNumber = (TextView) rowView
				.findViewById(R.id.vehicle_text_view);
		TextView vehicleDirection = (TextView) rowView
				.findViewById(R.id.direction_text_view);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.vehicle_image_view);

		if (vehicleType.equals(context.getString(R.string.title_bus))) {
			vehicleNumber.setText(vehicleType + " � " + vehicle.getNumber());
			vehicleDirection.setText(vehicle.getDirection());
			imageView.setImageResource(R.drawable.bus_icon);
		} else if (vehicleType
				.equals(context.getString(R.string.title_trolley))) {
			vehicleNumber.setText(vehicleType + " � " + vehicle.getNumber());
			vehicleDirection.setText(vehicle.getDirection());
			imageView.setImageResource(R.drawable.trolley_icon);
		} else {
			vehicleNumber.setText(vehicleType + " � " + vehicle.getNumber());
			vehicleDirection.setText(vehicle.getDirection());
			imageView.setImageResource(R.drawable.tram_icon);
		}

		return rowView;
	}
}