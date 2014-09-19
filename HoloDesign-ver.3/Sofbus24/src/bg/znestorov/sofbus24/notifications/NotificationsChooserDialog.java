package bg.znestorov.sofbus24.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * DialogFragment used to show a messaged when the elapsed time is passed
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class NotificationsChooserDialog extends DialogFragment {

	private Activity context;
	private String[] vehicleInfo;

	private ExpandableListView expListView;
	private NotificationsChooserAdapter listAdapter;
	private List<String> listDataHeader;
	private HashMap<String, List<String[]>> listDataChild;

	public interface OnNotificationSetListener {
		public void onNotificationsSet();
	}

	public static NotificationsChooserDialog newInstance(String[] vehicleInfo) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(NotificationsDialog.BUNDLE_VEHICLE_INFO,
				vehicleInfo);

		NotificationsChooserDialog notificationsChooserDialog = new NotificationsChooserDialog();
		notificationsChooserDialog.setArguments(bundle);

		return notificationsChooserDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		vehicleInfo = (String[]) getArguments().getSerializable(
				NotificationsDialog.BUNDLE_VEHICLE_INFO);

		// Create the DialogFragment
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = context.getLayoutInflater();
		View view = inflater.inflate(
				R.layout.activity_notifications_list_fragment, null);
		builder.setView(view);

		// Add the dialog fragment a title
		builder.setTitle(getString(R.string.notifications_chooser_title));

		// Initialize the layout fields
		initLayoutFields(inflater, view);

		// Add the Cancel button
		builder.setNeutralButton(R.string.app_button_cancel, null);

		return builder.create();
	}

	private void initLayoutFields(LayoutInflater inflater, View view) {
		TextView stationName = (TextView) view
				.findViewById(R.id.notifications_station_name);
		TextView stationNumber = (TextView) view
				.findViewById(R.id.notifications_station_number);
		ImageView vehicleImage = (ImageView) view
				.findViewById(R.id.notifications_image_vehicle);
		TextView vehicleCaption = (TextView) view
				.findViewById(R.id.notifications_vehicle_caption);
		TextView vehicleDirection = (TextView) view
				.findViewById(R.id.notifications_vehicle_direction);
		TextView currentTime = (TextView) view
				.findViewById(R.id.notifications_current_time);
		expListView = (ExpandableListView) view
				.findViewById(R.id.notifications_exp_list_view);

		// Set the vehicle selection to be selected (allow the text to be
		// moving, so show all the information to the user)
		vehicleDirection.setSelected(true);

		// Set the vehicle info to the layout fields
		stationName.setText(vehicleInfo[0]);
		stationNumber.setText(vehicleInfo[1]);
		vehicleImage.setImageResource(Integer.parseInt(vehicleInfo[2]));
		vehicleCaption.setText(vehicleInfo[3]);
		vehicleDirection.setText(vehicleInfo[4]);
		currentTime.setText(getString(R.string.vb_time_current_time,
				Utils.getCurrentDateTime()));

		// Prepare the list data and creating the list adapter
		prepareListData();

		// Adding list header
		ViewGroup header = (ViewGroup) inflater
				.inflate(R.layout.activity_notifications_list_header,
						expListView, false);
		expListView.addHeaderView(header, null, false);

		// Creating and setting the list adapter
		listAdapter = new NotificationsChooserAdapter(context, this,
				listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);

		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				int listHeaderSize = listDataHeader.size();

				for (int i = 0; i < listHeaderSize; i++) {
					if (i != groupPosition) {
						expListView.collapseGroup(i);
					}
				}
			}
		});

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Toast.makeText(
						context,
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String[]>>();

		// Adding child data
		List<String[]> listChildElements;
		String[] remainingTimes = vehicleInfo[5].split(",");
		String[] arrivalTimes = vehicleInfo[6].split(",");
		for (int i = 0; i < remainingTimes.length; i++) {
			listDataHeader.add(arrivalTimes[i].trim() + " ("
					+ remainingTimes[i].trim() + ")");

			listChildElements = new ArrayList<String[]>();
			listChildElements.add(vehicleInfo);
			listDataChild.put(listDataHeader.get(i), listChildElements);
		}
	}
}