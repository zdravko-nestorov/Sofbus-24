package bg.znestorov.sofbus24.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.NotificationsDataSource;
import bg.znestorov.sofbus24.entity.NotificationEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.notifications.NotificationsChooserDialog.OnNotificationSetListener;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * Custom adapter class to create an expandable list view
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NotificationsChooserAdapter extends BaseExpandableListAdapter {

	private Activity context;
	private NotificationsDataSource notificationsDatasource;
	private NotificationsChooserDialog notificationsChooserDialog;

	private List<String> listDataHeader;
	private HashMap<String, List<String[]>> listDataChild;

	private Long millisSystemTime;
	private ArrayList<Integer> numberPickerValues;

	// Used for optimize performance of the ExpandableListView
	static class HeaderViewHolder {
		TextView headerTextView;
	}

	static class ChildViewHolder {
		// TODO: Find a way to implement in 2.2 (API8)
		NumberPicker minutesPicker;
		Button confirmButton;
	}

	public NotificationsChooserAdapter(Activity context,
			NotificationsChooserDialog notificationsChooserDialog,
			List<String> listDataHeader,
			HashMap<String, List<String[]>> listChildData) {

		this.context = context;
		this.notificationsDatasource = new NotificationsDataSource(context);
		this.notificationsChooserDialog = notificationsChooserDialog;

		this.listDataHeader = listDataHeader;
		this.listDataChild = listChildData;

		// Load the system time in the begining (when the adapter is loaded),
		// because if the user doesn't act, it will use wrong time
		this.millisSystemTime = System.currentTimeMillis();

		// ArrayList containing the state of each NumberPicker
		this.numberPickerValues = new ArrayList<Integer>();
		for (int i = 0; i < listDataHeader.size(); i++) {
			numberPickerValues.add(1);
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return listDataChild.get(listDataHeader.get(groupPosition)).get(
				childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	@SuppressLint("InflateParams")
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.activity_notifications_list_item, null);

			// Configure view holder
			viewHolder = new ChildViewHolder();
			viewHolder.minutesPicker = (NumberPicker) convertView
					.findViewById(R.id.notifications_item_minutes);
			viewHolder.confirmButton = (Button) convertView
					.findViewById(R.id.notifications_item_minutes_confirm);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}

		String arrivalTime = Utils.getValueBefore(
				getGroup(groupPosition).toString(), "(").trim();
		String remainingTime = Utils.getValueBetween(
				getGroup(groupPosition).toString(), "(", ")").trim();
		String[] vehicleInfo = (String[]) getChild(groupPosition, childPosition);
		initChildLayoutFields(viewHolder, groupPosition, arrivalTime,
				remainingTime, vehicleInfo);

		return convertView;
	}

	private void initChildLayoutFields(final ChildViewHolder viewHolder,
			final int groupPosition, final String arrivalTime,
			final String remainingTime, final String[] vehicleInfo) {

		// Get the remaining time in minutes
		final int remainingMinutes = Utils.getRemainingMinutes(remainingTime);

		// Init the NumberPicker and set its options
		viewHolder.minutesPicker.setMinValue(1);
		viewHolder.minutesPicker.setMaxValue(remainingMinutes - 1);
		viewHolder.minutesPicker
				.setValue(numberPickerValues.get(groupPosition));
		viewHolder.minutesPicker
				.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						numberPickerValues.set(groupPosition, newVal);
					}
				});

		// Init the confirm button
		viewHolder.confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Calculate the needed times
				long selectedMinutes = viewHolder.minutesPicker.getValue();
				long millisAfterCurrentTime = (remainingMinutes - selectedMinutes) * 60 * 1000;
				long millisToNotification = millisSystemTime
						+ millisAfterCurrentTime;
				String remainingTimeAfterNotification = Utils
						.formatMillisInTime(context,
								selectedMinutes * 60 * 1000);

				// Check if the user is on this screen for a long time
				if (millisToNotification > System.currentTimeMillis()) {
					// Modify the vehicleInfo and put only the selected time
					vehicleInfo[5] = arrivalTime + " ("
							+ remainingTimeAfterNotification + ")";

					// Creating an intent and set it to a pending intent (to be
					// started when needed)
					AlarmManager alarmManager = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(context,
							NotificationsReceiver.class);
					intent.putExtra(NotificationsDialog.BUNDLE_VEHICLE_INFO,
							vehicleInfo);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							context, setNotification(vehicleInfo), intent,
							Intent.FLAG_ACTIVITY_NEW_TASK);

					// Start the push notification manager
					alarmManager.set(AlarmManager.RTC_WAKEUP,
							millisToNotification, pendingIntent);

					// Show a toast and finish the activity
					Toast.makeText(
							context,
							context.getString(
									R.string.notifications_chooser_message_selected,
									Utils.formatMillisInTime(context,
											millisAfterCurrentTime).replace(
											"~", "")), Toast.LENGTH_LONG)
							.show();

					// Refresh the VirtualBoardsTimeFragment
					((OnNotificationSetListener) notificationsChooserDialog
							.getParentFragment()).onNotificationsSet();
				} else {
					Toast.makeText(
							context,
							context.getString(R.string.notifications_chooser_error_message),
							Toast.LENGTH_LONG).show();
				}

				try {
					notificationsChooserDialog.dismiss();
				} catch (Exception e) {
				}
			}
		});
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return listDataChild.get(listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	@SuppressLint("InflateParams")
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		HeaderViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.activity_notifications_list_group, null);

			// Configure view holder
			viewHolder = new HeaderViewHolder();
			viewHolder.headerTextView = (TextView) convertView
					.findViewById(R.id.notifications_header_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HeaderViewHolder) convertView.getTag();
		}

		String headerTitle = (String) getGroup(groupPosition);
		viewHolder.headerTextView.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private int setNotification(String[] vehicleInfo) {
		int notificationId = -1;

		notificationsDatasource.open();
		NotificationEntity notification = notificationsDatasource
				.createNotification(vehicleInfo);
		if (notification != null) {
			notificationId = notification.getId();
		}
		notificationsDatasource.close();

		return notificationId;
	}
}