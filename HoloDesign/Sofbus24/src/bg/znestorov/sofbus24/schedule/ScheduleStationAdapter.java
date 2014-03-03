package bg.znestorov.sofbus24.schedule;

import java.util.List;

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

	private final Context context;
	private final List<Vehicle> vehicles;

	public ScheduleStationAdapter(Context context, List<Vehicle> vehicles) {
		super(context, R.layout.activity_schedule_list_item, vehicles);
		this.context = context;
		this.vehicles = vehicles;
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Vehicle vehicle = vehicles.get(position);
		View rowView = convertView;
		rowView = setVehiclesRow(inflater, parent, vehicle);

		return rowView;
	}

	/**
	 * Vehicle row in the ListView
	 * 
	 * @param inflater
	 *            process the XML file for the visual part
	 * @param parent
	 *            used to create a multiple-exclusion scope for a set of radio
	 *            buttons (not used)
	 * @param vehicle
	 *            the station object on the current row
	 * @return a view representing the look on the screen
	 */
	public View setVehiclesRow(LayoutInflater inflater, ViewGroup parent,
			final Vehicle vehicle) {
		View rowView = inflater.inflate(R.layout.activity_schedule_list_item,
				parent, false);

		// Set the vehicle caption and direction
		ImageView vehicleType = (ImageView) rowView
				.findViewById(R.id.schedule_item_vehicle_type);
		TextView vehicleCaption = (TextView) rowView
				.findViewById(R.id.schedule_item_vehicle_caption);
		TextView vehicleDirection = (TextView) rowView
				.findViewById(R.id.schedule_item_vehicle_direction);

		int vehicleImage = getVehicleImage(context, vehicle);
		String vehicleCaptionText = getVehicleCaption(context, vehicle);
		String vehicleDirectionText = vehicle.getDirection();

		vehicleType.setImageResource(vehicleImage);
		vehicleCaption.setText(vehicleCaptionText);
		vehicleDirection.setText(vehicleDirectionText);

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