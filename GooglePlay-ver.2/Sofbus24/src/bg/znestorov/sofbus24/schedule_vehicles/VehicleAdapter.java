package bg.znestorov.sofbus24.schedule_vehicles;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;

// Class for creating the vehicles ListView
public class VehicleAdapter extends ArrayAdapter<Vehicle> {

	private final Activity context;
	private final ArrayList<Vehicle> list_values;

	static class ViewHolder {
		TextView vehicleNumber;
		TextView vehicleDirection;
		ImageView imageView;
	}

	public VehicleAdapter(Activity context, ArrayList<Vehicle> list_values) {
		super(context, R.layout.activity_vehicle_search, list_values);
		this.context = context;
		this.list_values = list_values;
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Vehicle vehicle = (Vehicle) list_values.get(position);
		final String vehicleType = vehicle.getType();

		return setVehicleRow(convertView, vehicle, vehicleType);
	}

	// Vehicle row in the ListView
	public View setVehicleRow(View convertView, Vehicle vehicle,
			String vehicleType) {
		View rowView = convertView;
		ViewHolder viewHolder;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.activity_vehicle, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.vehicleNumber = (TextView) rowView
					.findViewById(R.id.vehicle_text_view);
			viewHolder.vehicleDirection = (TextView) rowView
					.findViewById(R.id.direction_text_view);
			viewHolder.imageView = (ImageView) rowView
					.findViewById(R.id.vehicle_image_view);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		if (vehicleType.equals(context.getString(R.string.title_bus))) {
			viewHolder.vehicleNumber.setText(vehicleType + " ¹ "
					+ vehicle.getNumber());
			viewHolder.vehicleDirection.setText(vehicle.getDirection());
			viewHolder.imageView.setImageResource(R.drawable.bus_icon);
		} else if (vehicleType
				.equals(context.getString(R.string.title_trolley))) {
			viewHolder.vehicleNumber.setText(vehicleType + " ¹ "
					+ vehicle.getNumber());
			viewHolder.vehicleDirection.setText(vehicle.getDirection());
			viewHolder.imageView.setImageResource(R.drawable.trolley_icon);
		} else {
			viewHolder.vehicleNumber.setText(vehicleType + " ¹ "
					+ vehicle.getNumber());
			viewHolder.vehicleDirection.setText(vehicle.getDirection());
			viewHolder.imageView.setImageResource(R.drawable.tram_icon);
		}

		return rowView;
	}
}