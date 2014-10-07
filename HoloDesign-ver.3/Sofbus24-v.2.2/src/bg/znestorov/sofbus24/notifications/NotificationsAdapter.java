package bg.znestorov.sofbus24.notifications;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.NotificationEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * Array Adapter used to set each row a notifications object from the DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NotificationsAdapter extends ArrayAdapter<NotificationEntity>
		implements Filterable {

	private Activity context;
	private List<NotificationEntity> notificationsList;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		TextView stationName;
		TextView stationNumber;
		ImageView vehicleImage;
		TextView vehicleCaption;
		TextView vehicleDirection;
		TextView currentTime;
	}

	public NotificationsAdapter(Activity context,
			List<NotificationEntity> notificationsList) {
		super(context, R.layout.activity_notifications_all_list_item,
				notificationsList);

		this.context = context;
		this.notificationsList = notificationsList;
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_notifications_all_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.stationName = (TextView) rowView
					.findViewById(R.id.notifications_station_name);
			viewHolder.stationNumber = (TextView) rowView
					.findViewById(R.id.notifications_station_number);
			viewHolder.vehicleImage = (ImageView) rowView
					.findViewById(R.id.notifications_image_vehicle);
			viewHolder.vehicleCaption = (TextView) rowView
					.findViewById(R.id.notifications_vehicle_caption);
			viewHolder.vehicleDirection = (TextView) rowView
					.findViewById(R.id.notifications_vehicle_direction);
			viewHolder.currentTime = (TextView) rowView
					.findViewById(R.id.notifications_current_time);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		String[] notificationInformation = notificationsList.get(position)
				.getInformation();

		// Set the vehicle selection to be selected (allow the text to be
		// moving, so show all the information to the user)
		viewHolder.vehicleDirection.setSelected(true);

		// Set the vehicle info to the layout fields
		viewHolder.stationName.setText(notificationInformation[0]);
		viewHolder.stationNumber.setText(notificationInformation[1]);
		viewHolder.vehicleImage.setImageResource(Integer
				.parseInt(notificationInformation[2]));
		viewHolder.vehicleCaption.setText(notificationInformation[3]);
		viewHolder.vehicleDirection.setText(notificationInformation[4]);
		viewHolder.currentTime.setText(context.getString(
				R.string.vb_time_current_time, Utils.getCurrentDateTime()));

		return rowView;
	}
}