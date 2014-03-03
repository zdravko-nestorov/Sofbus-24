package bg.znestorov.sofbus24.schedule;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;

public class ScheduleFragment extends ListFragment {

	private VehiclesDataSource vehiclesDatasource;
	private Context context;

	private TextView busTextView;
	private TextView trolleyTextView;
	private TextView tramTextView;

	private List<Vehicle> busses;
	private List<Vehicle> trolleys;
	private List<Vehicle> trams;

	public ScheduleFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_schedule_fragment,
				container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Fill the list view with the vehicles from DB
		vehiclesDatasource = new VehiclesDataSource(context);
		vehiclesDatasource.open();
		busses = vehiclesDatasource.getAllBusses();
		trolleys = vehiclesDatasource.getAllTrolleys();
		trams = vehiclesDatasource.getAllTrams();

		// Find all of TextView tabs in the layout
		busTextView = (TextView) rootView.findViewById(R.id.schedule_bus_tab);
		trolleyTextView = (TextView) rootView
				.findViewById(R.id.schedule_trolley_tab);
		tramTextView = (TextView) rootView.findViewById(R.id.schedule_tram_tab);

		// Assign the bus TextView a click listener
		busTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(VehicleType.BUS);
			}
		});

		// Assign the trolley TextView a click listener
		trolleyTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(VehicleType.TROLLEY);
			}
		});

		// Assign the tram TextView a click listener
		tramTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(VehicleType.TRAM);
			}
		});

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Vehicle> adapter = new ScheduleStationAdapter(context,
				busses);
		setListAdapter(adapter);

		// Activate the bus tab
		processOnClickedTab(VehicleType.BUS);

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Vehicle vehicle = (Vehicle) getListAdapter().getItem(position);

		// TODO: Retrieve information about the vehicle

		Toast.makeText(getActivity(), vehicle.getNumber(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onResume() {
		vehiclesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		vehiclesDatasource.close();
		super.onPause();
	}

	/**
	 * Take needed actions according to the clicked tab
	 * 
	 * @param tabType
	 * 
	 */
	private void processOnClickedTab(VehicleType tabType) {
		ArrayAdapter<Vehicle> adapter;

		switch (tabType) {
		case BUS:
			setTabActive(busTextView);
			setTabInactive(trolleyTextView);
			setTabInactive(tramTextView);
			adapter = new ScheduleStationAdapter(context, busses);
			setListAdapter(adapter);
			break;
		case TROLLEY:
			setTabInactive(busTextView);
			setTabActive(trolleyTextView);
			setTabInactive(tramTextView);
			adapter = new ScheduleStationAdapter(context, trolleys);
			setListAdapter(adapter);
			break;
		case TRAM:
			setTabInactive(busTextView);
			setTabInactive(trolleyTextView);
			setTabActive(tramTextView);
			adapter = new ScheduleStationAdapter(context, trams);
			setListAdapter(adapter);
			break;
		default:
			setTabActive(busTextView);
			setTabInactive(trolleyTextView);
			setTabInactive(tramTextView);
			adapter = new ScheduleStationAdapter(context, busses);
			setListAdapter(adapter);
			break;
		}
	}

	/**
	 * Set a schedule tab to be active - change the background and text colors
	 * 
	 * @param textView
	 *            the TextView which is selected
	 */
	private void setTabActive(TextView textView) {
		textView.setBackgroundColor(getResources().getColor(
				R.color.schedule_tab_grey));
		textView.setTextColor(getResources().getColor(R.color.white));
	}

	/**
	 * Set a schedule tab to be inactive - change the background and text colors
	 * 
	 * @param textView
	 *            the TextView that has to be deactivated
	 */
	private void setTabInactive(TextView textView) {
		textView.setBackgroundResource(R.drawable.schedule_tab_border);
		textView.setTextColor(getResources()
				.getColor(R.color.schedule_tab_grey));
	}
}
