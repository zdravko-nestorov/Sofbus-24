package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.PublicTransport;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

public class ScheduleFragment extends ListFragment {

	private Activity context;

	private TextView busTextView;
	private TextView trolleyTextView;
	private TextView tramTextView;

	private ScheduleLoadVehicles slv;
	private VehiclesDataSource vehiclesDatasource;
	private List<Vehicle> busses;
	private List<Vehicle> trolleys;
	private List<Vehicle> trams;

	private static VehicleType vehicleType = VehicleType.BUS;

	private static String busSearchText = "";
	private static String trolleySearchText = "";
	private static String tramSearchText = "";

	public ScheduleFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_schedule_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Load the Vehicle Datasource
		vehiclesDatasource = new VehiclesDataSource(context);

		// Fill the list view with the vehicles from DB
		slv = ScheduleLoadVehicles.getInstance(context);
		busses = slv.getBusses();
		trolleys = slv.getTrolleys();
		trams = slv.getTrams();

		// Find all of TextView tabs in the layout
		busTextView = (TextView) myFragmentView
				.findViewById(R.id.schedule_bus_tab);
		trolleyTextView = (TextView) myFragmentView
				.findViewById(R.id.schedule_trolley_tab);
		tramTextView = (TextView) myFragmentView
				.findViewById(R.id.schedule_tram_tab);
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.schedule_search);
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.schedule_list_empty_text);

		// Set the actions over the TextViews and EditText
		actionsOverVehiclesTextViews(searchEditText);
		actionsOverSearchEditText(searchEditText, emptyList);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Vehicle> adapter = new ScheduleStationAdapter(context,
				busses);
		setListAdapter(adapter);

		// Activate the bus tab
		processOnClickedTab(searchEditText, vehicleType);

		return myFragmentView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Vehicle vehicle = (Vehicle) getListAdapter().getItem(position);

		// TODO: Retrieve information about the vehicle
		ArrayList<String> directionsNames = new ArrayList<String>();
		directionsNames.add(loadVehiclesList(VehicleType.METRO1, "").get(0)
				.getDirection().replaceAll("-.*?-", " - "));
		directionsNames.add(loadVehiclesList(VehicleType.METRO2, "").get(0)
				.getDirection().replaceAll("-.*?-", " - "));

		MetroLoadStations mls = MetroLoadStations.getInstance(context);
		ArrayList<ArrayList<Station>> directionsList = new ArrayList<ArrayList<Station>>();
		directionsList.add((ArrayList<Station>) mls.getMetroDirection1());
		directionsList.add((ArrayList<Station>) mls.getMetroDirection2());

		Intent publicTransport = new Intent(context, PublicTransport.class);
		DirectionsEntity ptDirectionsEntity = new DirectionsEntity(vehicle,
				1, directionsNames, directionsList);
		publicTransport.putExtra(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
				ptDirectionsEntity);
		startActivity(publicTransport);

		Toast.makeText(getActivity(), vehicle.getNumber(), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * Activate the listeners over the Vehicle TextView fields
	 * 
	 * @param searchEditText
	 *            the text from the searched edit text
	 */
	private void actionsOverVehiclesTextViews(
			final SearchEditText searchEditText) {
		// Assign the bus TextView a click listener
		busTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(searchEditText, VehicleType.BUS);
			}
		});

		// Assign the trolley TextView a click listener
		trolleyTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(searchEditText, VehicleType.TROLLEY);
			}
		});

		// Assign the tram TextView a click listener
		tramTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(searchEditText, VehicleType.TRAM);
			}
		});
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText,
			final TextView emptyList) {
		searchEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		// Add on focus listener
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, searchEditText);
				}
			}
		});

		// Add on text changes listener
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String searchText = searchEditText.getText().toString();
				List<Vehicle> searchStationList = loadVehiclesList(vehicleType,
						searchText);
				ArrayAdapter<Vehicle> adapter = new ScheduleStationAdapter(
						context, searchStationList);

				setListAdapter(adapter);

				// Set a message if the list is empty
				if (adapter.isEmpty()) {
					emptyList.setText(Html.fromHtml(String.format(
							getString(R.string.sch_item_empty_list),
							searchText, getActiveTabName())));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
		});

		// Add a drawable listeners (search and clear icons)
		searchEditText.setDrawableClickListener(new DrawableClickListener() {
			@Override
			public void onClick(DrawablePosition target) {
				switch (target) {
				case LEFT:
					searchEditText.requestFocus();
					ActivityUtils.showKeyboard(context, searchEditText);
					break;
				case RIGHT:
					searchEditText.setText("");
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * Get the name of the active tab
	 * 
	 * @return the name of the active tab section
	 */
	private String getActiveTabName() {
		String activeTabName = "";

		switch (vehicleType) {
		case BUS:
			activeTabName = getString(R.string.sch_search_tab_bus);
			break;
		case TROLLEY:
			activeTabName = getString(R.string.sch_search_tab_trolley);
			break;
		case TRAM:
			activeTabName = getString(R.string.sch_search_tab_tram);
			break;
		default:
			activeTabName = getString(R.string.sch_search_tab_bus);
			break;
		}

		return activeTabName;
	}

	/**
	 * Take needed actions according to the clicked tab
	 * 
	 * @param searchEditText
	 *            the text from the searched edit text
	 * @param tabType
	 *            the chosen type
	 */
	private void processOnClickedTab(SearchEditText searchEditText,
			VehicleType tabType) {
		ArrayAdapter<Vehicle> adapter;

		// Check which is previous clicked tab, so save the value to the
		// appropriate variable
		switch (vehicleType) {
		case BUS:
			busSearchText = searchEditText.getText().toString();
			break;
		case TROLLEY:
			trolleySearchText = searchEditText.getText().toString();
			break;
		case TRAM:
			tramSearchText = searchEditText.getText().toString();
			break;
		default:
			busSearchText = searchEditText.getText().toString();
			break;
		}

		// Check which tab is clicked
		switch (tabType) {
		case BUS:
			vehicleType = VehicleType.BUS;

			setTabActive(busTextView);
			setTabInactive(trolleyTextView);
			setTabInactive(tramTextView);

			// Set the Search tab the appropriate search text
			searchEditText.setText(busSearchText);

			// Check if a search is already done
			if ("".equals(busSearchText)) {
				adapter = new ScheduleStationAdapter(context, busses);
			} else {
				adapter = new ScheduleStationAdapter(context, loadVehiclesList(
						vehicleType, busSearchText));
			}

			setListAdapter(adapter);
			break;
		case TROLLEY:
			vehicleType = VehicleType.TROLLEY;

			setTabInactive(busTextView);
			setTabActive(trolleyTextView);
			setTabInactive(tramTextView);

			// Set the Search tab the appropriate search text
			searchEditText.setText(trolleySearchText);

			// Check if a search is already done
			if ("".equals(trolleySearchText)) {
				adapter = new ScheduleStationAdapter(context, trolleys);
			} else {
				adapter = new ScheduleStationAdapter(context, loadVehiclesList(
						vehicleType, trolleySearchText));
			}

			setListAdapter(adapter);
			break;
		default:
			vehicleType = VehicleType.TRAM;
			setTabInactive(busTextView);
			setTabInactive(trolleyTextView);
			setTabActive(tramTextView);

			// Set the Search tab the appropriate search text
			searchEditText.setText(tramSearchText);

			// Check if a search is already done
			if ("".equals(tramSearchText)) {
				adapter = new ScheduleStationAdapter(context, trams);
			} else {
				adapter = new ScheduleStationAdapter(context, loadVehiclesList(
						vehicleType, tramSearchText));
			}

			setListAdapter(adapter);
			break;
		}

		// Set the marker at the end
		searchEditText.setSelection(searchEditText.getText().length());
	}

	/**
	 * Load all vehicles according to a vehicle type and a search text
	 * 
	 * @param vehicleType
	 *            the type of the searched vehicles
	 * @param searchText
	 *            the search text (if null - return all stations of the vehicle
	 *            type)
	 * @return all vehicles according to a vehicle type and a search text
	 */
	private List<Vehicle> loadVehiclesList(VehicleType vehicleType,
			String searchText) {
		List<Vehicle> vehiclesList;

		if (vehiclesDatasource == null) {
			vehiclesDatasource = new VehiclesDataSource(context);
		}

		vehiclesDatasource.open();
		vehiclesList = vehiclesDatasource.getVehiclesViaSearch(vehicleType,
				searchText);
		vehiclesDatasource.close();

		return vehiclesList;
	}

	/**
	 * Set a schedule tab to be active - change the background and text colors
	 * 
	 * @param textView
	 *            the TextView which is selected
	 */
	private void setTabActive(TextView textView) {
		textView.setBackgroundColor(getResources().getColor(
				R.color.inner_tab_grey));
		textView.setTextColor(getResources().getColor(R.color.white));
	}

	/**
	 * Set a schedule tab to be inactive - change the background and text colors
	 * 
	 * @param textView
	 *            the TextView that has to be deactivated
	 */
	private void setTabInactive(TextView textView) {
		textView.setBackgroundResource(R.drawable.inner_tab_border);
		textView.setTextColor(getResources().getColor(R.color.inner_tab_grey));
	}
}
