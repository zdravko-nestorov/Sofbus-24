package bg.znestorov.sofbus24.schedule;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.main.R;

/**
 * Array Adapted user for set each row a station from the Vehicles DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleStationAdapter extends ArrayAdapter<Vehicle> {

	private final Activity context;
	private final List<Vehicle> vehicles;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView vehicleType;
		TextView vehicleCaption;
		TextView vehicleDirection;
	}

	public ScheduleStationAdapter(Activity context, List<Vehicle> vehicles) {
		super(context, R.layout.activity_schedule_list_item, vehicles);
		this.context = context;
		this.vehicles = vehicles;
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.activity_schedule_list_item,
					null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.vehicleType = (ImageView) rowView
					.findViewById(R.id.schedule_item_vehicle_type);
			viewHolder.vehicleCaption = (TextView) rowView
					.findViewById(R.id.schedule_item_vehicle_caption);
			viewHolder.vehicleDirection = (TextView) rowView
					.findViewById(R.id.schedule_item_vehicle_direction);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		Vehicle vehicle = vehicles.get(position);
		int vehicleImage = getVehicleImage(context, vehicle);
		String vehicleCaptionText = getVehicleCaption(context, vehicle);
		String vehicleDirectionText = vehicle.getDirection();

		viewHolder.vehicleType.setImageResource(vehicleImage);
		viewHolder.vehicleCaption.setText(vehicleCaptionText);
		viewHolder.vehicleDirection.setText(vehicleDirectionText);

		return rowView;
	}

	/**
	 * Get the vehicle image according to the vehicle type
	 * 
	 * @param context
	 *            the current activity context
	 * @param vehicle
	 *            the vehicle on the current row
	 * @return the vehicle image id
	 */
	private Integer getVehicleImage(Context context, Vehicle vehicle) {
		Integer vehicleImage;

		switch (vehicle.getType()) {
		case BUS:
			vehicleImage = R.drawable.ic_bus;
			break;
		case TROLLEY:
			vehicleImage = R.drawable.ic_trolley;
			break;
		case TRAM:
			vehicleImage = R.drawable.ic_tram;
			break;
		default:
			vehicleImage = R.drawable.ic_bus;
			break;
		}

		return vehicleImage;
	}

	/**
	 * Get the vehicle caption according to the vehicle type
	 * 
	 * @param context
	 *            the current activity context
	 * @param vehicle
	 *            the vehicle on the current row
	 * @return the vehicle caption in format: <b>Bus ¹xxx</b>
	 */
	private String getVehicleCaption(Context context, Vehicle vehicle) {
		String vehicleCaption;

		switch (vehicle.getType()) {
		case BUS:
			vehicleCaption = String.format(
					context.getString(R.string.sch_item_bus),
					vehicle.getNumber());
			break;
		case TROLLEY:
			vehicleCaption = String.format(
					context.getString(R.string.sch_item_trolley),
					vehicle.getNumber());
			break;
		case TRAM:
			vehicleCaption = String.format(
					context.getString(R.string.sch_item_tram),
					vehicle.getNumber());
			break;
		default:
			vehicleCaption = String.format(
					context.getString(R.string.sch_item_bus),
					vehicle.getNumber());
			break;
		}

		return vehicleCaption;
	}
}