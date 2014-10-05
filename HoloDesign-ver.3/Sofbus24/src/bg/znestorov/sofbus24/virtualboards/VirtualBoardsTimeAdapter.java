package bg.znestorov.sofbus24.virtualboards;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.NotificationsDataSource;
import bg.znestorov.sofbus24.entity.NotificationEntity;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VirtualBoardsStationEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.notifications.NotificationsChooserDialog;
import bg.znestorov.sofbus24.notifications.NotificationsReceiver;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * Array Adapted user to set each row a vehicle with its arrival times from the
 * SKGT site
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VirtualBoardsTimeAdapter extends ArrayAdapter<VehicleEntity>
		implements Filterable {

	private Activity context;
	private NotificationsDataSource notificationsDatasource;
	private VirtualBoardsTimeFragment virtualBoardsTimeFragment;

	private VirtualBoardsStationEntity vbTimeStation;
	private String timeType;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView vehicleImage;
		TextView stationCaption;
		TextView stationDirection;
		TextView stationTime;
		ImageView stationAlarm;
	}

	public VirtualBoardsTimeAdapter(Activity context,
			VirtualBoardsTimeFragment virtualBoardsTimeFragment,
			VirtualBoardsStationEntity vbTimeStation) {
		super(context, R.layout.activity_virtual_boards_time_list_item,
				vbTimeStation.getVehiclesList());

		this.context = context;
		this.notificationsDatasource = new NotificationsDataSource(context);
		this.virtualBoardsTimeFragment = virtualBoardsTimeFragment;

		this.vbTimeStation = vbTimeStation;

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.timeType = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_TYPE,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_TYPE);
	}

	/**
	 * Creating the elements of the ListView
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_virtual_boards_time_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.vehicleImage = (ImageView) rowView
					.findViewById(R.id.vb_time_item_image_vehicle);
			viewHolder.stationCaption = (TextView) rowView
					.findViewById(R.id.vb_time_item_vehicle_caption);
			viewHolder.stationDirection = (TextView) rowView
					.findViewById(R.id.vb_time_item_vehicle_direction);
			viewHolder.stationTime = (TextView) rowView
					.findViewById(R.id.cs_list_item_vehicle_time);
			viewHolder.stationAlarm = (ImageView) rowView
					.findViewById(R.id.vb_time_item_image_alarm);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		VehicleEntity stationVehicle = vbTimeStation.getVehiclesList().get(
				position);

		viewHolder.vehicleImage
				.setImageResource(getVehicleImage(stationVehicle));
		viewHolder.stationCaption.setText(getVehicleCaption(stationVehicle));
		viewHolder.stationDirection.setText(stationVehicle.getDirection());
		viewHolder.stationTime.setText(getRowTimeCaption(stationVehicle));

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		/**
		 * TODO: Need to be fixed for the next version (it is not needed for now
		 * as on a device restart the application throws an exception)
		 * 
		 * setStationAlarm(viewHolder.stationAlarm, stationVehicle);
		 */

		return rowView;
	}

	/**
	 * Choose the corresponding image from the resources according to the
	 * vehicle type
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return the corresponding image to the vehicle
	 */
	private int getVehicleImage(VehicleEntity stationVehicle) {
		int vehicleImage;

		switch (stationVehicle.getType()) {
		case BUS:
			vehicleImage = R.drawable.ic_bus;
			break;
		case TROLLEY:
			vehicleImage = R.drawable.ic_trolley;
			break;
		default:
			vehicleImage = R.drawable.ic_tram;
			break;
		}

		return vehicleImage;
	}

	/**
	 * Create the vehicle caption using the vehicle type and vehicle number
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return the vehicle caption
	 */
	private String getVehicleCaption(VehicleEntity stationVehicle) {
		String vehicleCaption;
		String vehicleTypeText;

		switch (stationVehicle.getType()) {
		case BUS:
			vehicleTypeText = context.getString(R.string.vb_time_bus);
			break;
		case TROLLEY:
			vehicleTypeText = context.getString(R.string.vb_time_trolley);
			break;
		default:
			vehicleTypeText = context.getString(R.string.vb_time_tram);
			break;
		}

		vehicleCaption = String.format(vehicleTypeText,
				stationVehicle.getNumber());

		return vehicleCaption;
	}

	/**
	 * Create a separated string, using the elements from the list
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return a separated string with the arrival times
	 */
	private String getArrivalTimes(VehicleEntity stationVehicle) {
		ArrayList<String> arrivalTimesList = stationVehicle.getArrivalTimes();
		StringBuilder arrivalTimes = new StringBuilder("");

		for (int i = 0; i < arrivalTimesList.size(); i++) {
			arrivalTimes.append(arrivalTimesList.get(i)).append(", ");
		}

		// In very rare cases there are no results and the arrivalTimes array is
		// empty (GooglePlay bug: StringIndexOutOfBoundsException)
		if (arrivalTimes.length() > 1) {
			arrivalTimes.deleteCharAt(arrivalTimes.length() - 2).trimToSize();
		}

		if (arrivalTimes.length() == 0) {
			arrivalTimes.append("---");
		}

		return arrivalTimes.toString();
	}

	/**
	 * Create a separated string, using the elements from the list (removing the
	 * empty ones)
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return a separated string with the arrival times
	 */
	private String getArrivalTimesWithoutEmpty(VehicleEntity stationVehicle) {
		ArrayList<String> arrivalTimesList = stationVehicle.getArrivalTimes();
		String currentTime = Utils.getCurrentTime();
		StringBuilder arrivalTimes = new StringBuilder("");

		for (int i = 0; i < arrivalTimesList.size(); i++) {
			String timeToUse = Utils.getTimeDifference(context,
					arrivalTimesList.get(i), currentTime);
			if (timeToUse != null && !"".equals(timeToUse)
					&& !"---".equals(timeToUse)) {
				int remainingMinutes = Utils.getRemainingMinutes(timeToUse);

				if (remainingMinutes > 1) {
					arrivalTimes.append(arrivalTimesList.get(i)).append(", ");
				}
			}
		}

		// In very rare cases there are no results and the arrivalTimes array is
		// empty (GooglePlay bug: StringIndexOutOfBoundsException)
		if (arrivalTimes.length() > 1) {
			arrivalTimes.deleteCharAt(arrivalTimes.length() - 2).trimToSize();
		}

		return arrivalTimes.toString();
	}

	/**
	 * Create a separated string, using the elements from the list
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return a separated string with the remaining times
	 */
	private String getRemainingTimes(VehicleEntity stationVehicle) {
		ArrayList<String> arrivalTimesList = stationVehicle.getArrivalTimes();
		String currentTime = Utils.getValueAfterLast(
				vbTimeStation.getTime(context), ",").trim();
		StringBuilder arrivalTimes = new StringBuilder("");

		for (int i = 0; i < arrivalTimesList.size(); i++) {
			String timeToUse = Utils.getTimeDifference(context,
					arrivalTimesList.get(i), currentTime);
			arrivalTimes.append(timeToUse).append(", ");
		}

		if (arrivalTimes.length() > 1) {
			arrivalTimes.deleteCharAt(arrivalTimes.length() - 2).trimToSize();
		}

		if (arrivalTimes.length() == 0) {
			arrivalTimes.append("---");
		}

		return arrivalTimes.toString();
	}

	/**
	 * Create a separated string, using the elements from the list (removing the
	 * empty ones)
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return a separated string with the remaining times
	 */
	private String getRemainingTimesWithoutEmpty(VehicleEntity stationVehicle) {
		ArrayList<String> arrivalTimesList = stationVehicle.getArrivalTimes();
		String currentTime = Utils.getCurrentTime();
		StringBuilder arrivalTimes = new StringBuilder("");

		for (int i = 0; i < arrivalTimesList.size(); i++) {
			String timeToUse = Utils.getTimeDifference(context,
					arrivalTimesList.get(i), currentTime);
			if (timeToUse != null && !"".equals(timeToUse)
					&& !"---".equals(timeToUse)) {
				int remainingMinutes = Utils.getRemainingMinutes(timeToUse);

				if (remainingMinutes > 1) {
					arrivalTimes.append(timeToUse).append(", ");
				}
			}
		}

		if (arrivalTimes.length() > 1) {
			arrivalTimes.deleteCharAt(arrivalTimes.length() - 2).trimToSize();
		}

		return arrivalTimes.toString();
	}

	/**
	 * Create the text for the last TextView of the row (containing the times of
	 * arrival or remaining times)
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return the last TextView text of each row (containing the times of
	 *         arrival or remaining times)
	 */
	private Spanned getRowTimeCaption(VehicleEntity stationVehicle) {
		Spanned rowTimeCaption;

		if (timeType.equals(Constants.PREFERENCE_DEFAULT_VALUE_TIME_TYPE)) {
			rowTimeCaption = Html.fromHtml(String.format(
					context.getString(R.string.vb_time_item_remaining_time),
					getRemainingTimes(stationVehicle)));
		} else {
			rowTimeCaption = Html.fromHtml(String.format(
					context.getString(R.string.vb_time_item_time_of_arrival),
					getArrivalTimes(stationVehicle)));
		}

		return rowTimeCaption;
	}

	/**
	 * Set on click listener over the selected vehicle
	 * 
	 * @param stationAlarm
	 *            station alarm image view
	 * @param stationVehicle
	 *            the station vehicle
	 */
	@SuppressWarnings("unused")
	private void setStationAlarm(ImageView stationAlarm,
			final VehicleEntity stationVehicle) {

		setStationImage(stationAlarm, stationVehicle);

		stationAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setStationAlarm(stationVehicle);
			}
		});
	}

	/**
	 * Set an image on the current row
	 * 
	 * @param stationAlarm
	 *            station alarm image view
	 * @param stationVehicle
	 *            the station vehicle
	 */
	private void setStationImage(ImageView stationAlarm,
			VehicleEntity stationVehicle) {
		notificationsDatasource.open();

		NotificationEntity notification = notificationsDatasource
				.getNotification(vbTimeStation.getNumber() + "~"
						+ stationVehicle.getNumber());
		if (notification == null) {
			stationAlarm.setImageResource(R.drawable.ic_alarm_off);
		} else {
			stationAlarm.setImageResource(R.drawable.ic_alarm_on);
		}

		notificationsDatasource.close();
	}

	/**
	 * Set the alarm over the current selected row
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 */
	private void setStationAlarm(VehicleEntity stationVehicle) {
		String arrivalTimes = getArrivalTimesWithoutEmpty(stationVehicle);
		String remainingTime = getRemainingTimesWithoutEmpty(stationVehicle);

		// Check if there is some remaining time
		if (remainingTime != null && !"".equals(remainingTime)) {

			// Check if the user is on this screen for a long time
			if (stationVehicle.getArrivalTimes().size() < remainingTime
					.split(",").length + 2) {
				String[] vehicleInfo = new String[] {
						vbTimeStation.getName(),
						context.getString(R.string.history_item_station_number,
								vbTimeStation.getNumber()),
						getVehicleImage(stationVehicle) + "",
						getVehicleCaption(stationVehicle),
						stationVehicle.getDirection(),
						remainingTime,
						arrivalTimes,
						vbTimeStation.getNumber() + "~"
								+ stationVehicle.getNumber() };

				DialogFragment notificationsVBTimeDialog = NotificationsChooserDialog
						.newInstance(vehicleInfo);
				notificationsVBTimeDialog.setTargetFragment(
						virtualBoardsTimeFragment, 0);
				notificationsVBTimeDialog.show(
						virtualBoardsTimeFragment.getChildFragmentManager(),
						"NotificationsVBTimeDialog");
			} else {
				Toast.makeText(
						context,
						context.getString(R.string.notifications_chooser_error_message),
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(context,
					context.getString(R.string.notifications_error_message),
					Toast.LENGTH_LONG).show();
		}

		cancelNotification(stationVehicle);
	}

	/**
	 * Set the alarm over the current selected row
	 * 
	 * @param position
	 *            the position of the station vehicle
	 */
	public void setStationAlarm(int position) {
		setStationAlarm(vbTimeStation.getVehiclesList().get(position));
	}

	/**
	 * Cancel the notification for the selected vehicle
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 */
	private void cancelNotification(VehicleEntity stationVehicle) {
		notificationsDatasource.open();

		// Check if a notification is already set in the DB
		NotificationEntity notification = notificationsDatasource
				.getNotification(vbTimeStation.getNumber() + "~"
						+ stationVehicle.getNumber());
		if (notification != null) {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, NotificationsReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					notification.getId(), intent, PendingIntent.FLAG_NO_CREATE);

			// Delete the notification from the database
			notificationsDatasource.deleteNotification(notification);

			// Check if a notification was already set
			if (pendingIntent != null) {
				alarmManager.cancel(pendingIntent);
				pendingIntent.cancel();

				Toast.makeText(
						context,
						context.getString(R.string.notifications_chooser_cancel),
						Toast.LENGTH_LONG).show();
			}
		}

		notificationsDatasource.close();

		// Refresh the list
		notifyDataSetChanged();
	}
}