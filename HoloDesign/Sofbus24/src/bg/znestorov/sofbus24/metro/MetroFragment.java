package bg.znestorov.sofbus24.metro;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.activity.ActivityUtils;
import bg.znestorov.sofbus24.activity.DrawableClickListener;
import bg.znestorov.sofbus24.activity.SearchEditText;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;

public class MetroFragment extends ListFragment implements UpdateableFragment {

	private Activity context;

	private TextView direction1TextView;
	private TextView direction2TextView;

	private MetroLoadStations mls;
	private StationsDataSource stationsDatasource;
	private VehiclesDataSource vehiclesDatasource;
	private List<Station> metroDirection1;
	private List<Station> metroDirection2;

	private static VehicleType stationType = VehicleType.METRO1;

	private String metro1SearchText = "";
	private String metro2SearchText = "";

	public MetroFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_metro_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Load the Stations Datasource
		stationsDatasource = new StationsDataSource(context);
		vehiclesDatasource = new VehiclesDataSource(context);

		// Fill the list view with the stations from DB
		mls = MetroLoadStations.getInstance(context);
		metroDirection1 = mls.getMetroDirection1();
		metroDirection2 = mls.getMetroDirection2();

		// Find all of TextView and SearchEditText tabs in the layout
		direction1TextView = (TextView) myFragmentView
				.findViewById(R.id.metro_direction1_tab);
		direction2TextView = (TextView) myFragmentView
				.findViewById(R.id.metro_direction2_tab);
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.metro_search);
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.metro_list_empty_text);

		// Set the actions over the TextViews and SearchEditText
		actionsOverDirectionsTextViews(searchEditText);
		actionsOverSearchEditText(searchEditText, emptyList);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new MetroStationAdapter(context,
				metroDirection1);
		setListAdapter(adapter);

		// Activate the bus tab
		processOnClickedTab(false, searchEditText, stationType);

		// Activate the option menu
		setHasOptionsMenu(true);

		return myFragmentView;
	}

	@Override
	public void update() {
		processOnClickedTab(true, null, stationType);
	}

	@Override
	public void update(Activity context) {
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		// TODO: Retrieve information about the vehicle

		Toast.makeText(getActivity(), station.getName(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_metro_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ArrayAdapter<Station> adapter = (ArrayAdapter<Station>) getListAdapter();

		switch (item.getItemId()) {
		case R.id.metro_menu_map_route:

			// TODO: Retrieve information about the vehicle

			Toast.makeText(getActivity(),
					context.getString(R.string.metro_menu_map_route),
					Toast.LENGTH_SHORT).show();
			break;
		}

		adapter.notifyDataSetChanged();

		return true;
	}

	/**
	 * Activate the listeners over the Metro Station TextView fields
	 * 
	 * @param searchEditText
	 *            the text from the searched edit text
	 */
	private void actionsOverDirectionsTextViews(
			final SearchEditText searchEditText) {
		// Assign the Direction1 TextView a click listener
		direction1TextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(true, searchEditText, VehicleType.METRO1);
			}
		});

		// Assign the Direction2 TextView a click listener
		direction2TextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(true, searchEditText, VehicleType.METRO2);
			}
		});
	}

	/**
	 * Take needed actions according to the clicked tab
	 * 
	 * @param isTabClicked
	 *            mark if the selected tab is clicked or directly loaded from
	 *            the main TabHost
	 * @param searchEditText
	 *            the EditText search field (if null - just refresh the adater)
	 * @param tabType
	 *            the chosen station type (METRO1 or METRO2)
	 */
	private void processOnClickedTab(boolean isTabClicked,
			SearchEditText searchEditText, VehicleType tabType) {
		ArrayAdapter<Station> adapter;

		// Check which is previous clicked tab, so save the value to the
		// appropriate variable (in case of refresh just set an empty string)
		if (searchEditText != null) {
			switch (stationType) {
			case METRO1:
				metro1SearchText = searchEditText.getText().toString();
				break;
			case METRO2:
				metro2SearchText = searchEditText.getText().toString();
				break;
			default:
				metro1SearchText = searchEditText.getText().toString();
				break;
			}
		} else {
			metro1SearchText = "";
		}
		// Check which tab is clicked
		switch (tabType) {
		case METRO1:
			stationType = VehicleType.METRO1;

			setTabActive(direction1TextView);
			setTabInactive(direction2TextView);

			// Set the Search tab the appropriate search text
			if (searchEditText != null) {
				searchEditText.setText(metro1SearchText);
			}

			// Check if a search is already done
			if ("".equals(metro1SearchText)) {
				adapter = new MetroStationAdapter(context, metroDirection1);
			} else {
				adapter = new MetroStationAdapter(context,
						loadStationsList(metro1SearchText));
			}

			setListAdapter(adapter);
			break;
		default:
			stationType = VehicleType.METRO2;

			setTabInactive(direction1TextView);
			setTabActive(direction2TextView);

			// Set the Search tab the appropriate search text
			if (searchEditText != null) {
				searchEditText.setText(metro2SearchText);
			}

			// Check if a search is already done
			if ("".equals(metro2SearchText)) {
				adapter = new MetroStationAdapter(context, metroDirection2);
			} else {
				adapter = new MetroStationAdapter(context,
						loadStationsList(metro2SearchText));
			}

			setListAdapter(adapter);
			break;
		}

		// Set the marker at the end
		if (searchEditText != null) {
			searchEditText.setSelection(searchEditText.getText().length());
		}

		// Check if the tab is clicked or just loaded because the fragment is
		// selected from the TabHost
		if (isTabClicked) {
			showDirectionNameToast();
		}
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

				List<Station> searchStationList = loadStationsList(searchText);
				ArrayAdapter<Station> adapter = new MetroStationAdapter(
						context, searchStationList);

				setListAdapter(adapter);

				// Set a message if the list is empty
				if (adapter.isEmpty()) {
					emptyList.setText(Html.fromHtml(String.format(
							getString(R.string.metro_item_empty_list),
							searchText, getDirectionName(stationType, false))));
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
	 * Get the direction name and format it if needed via the vehicle type. If
	 * now vehicle type is entered - get the default one
	 * 
	 * @param vehicleType
	 *            the vehicle type (in case of current active tab use the global
	 *            variable - vehicleType)
	 * @param formatted
	 *            if the name should be formatted
	 * @return the direction name
	 */
	private String getDirectionName(VehicleType vehicleType, boolean formatted) {
		// If no vehicle type is passed as a parameter, set the default one
		if (vehicleType == null) {
			vehicleType = stationType;
		}

		// Get the name of the current direction
		String directionName = loadVehiclesList(vehicleType, "").get(0)
				.getDirection();

		// Check if the direction name should be formatted
		if (formatted) {
			directionName = directionName.replaceAll("-", " - ");
		}

		return directionName;
	}

	/**
	 * Show a Toast with the name of the metro direction
	 * 
	 * @param activityContext
	 *            the current activity context
	 */
	public void showDirectionNameToast() {
		Toast directionToast = Toast.makeText(context,
				getDirectionName(stationType, true), Toast.LENGTH_LONG);
		directionToast.show();
	}

	/**
	 * Load all stations according to a search text (if it is left as empty -
	 * all stations of the current tab type are loaded)
	 * 
	 * @param searchText
	 *            the search text (if null - return all stations of the current
	 *            tab type)
	 * @return all stations according to a search text
	 */
	private List<Station> loadStationsList(String searchText) {
		List<Station> stationsList;

		if (stationsDatasource == null) {
			stationsDatasource = new StationsDataSource(context);
		}

		stationsDatasource.open();
		stationsList = stationsDatasource.getStationsViaSearch(stationType,
				searchText);
		stationsDatasource.close();

		return stationsList;
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
	 * Set a metro tab to be active - change the background and text colors
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
	 * Set a metro tab to be inactive - change the background and text colors
	 * 
	 * @param textView
	 *            the TextView that has to be deactivated
	 */
	private void setTabInactive(TextView textView) {
		textView.setBackgroundResource(R.drawable.inner_tab_border);
		textView.setTextColor(getResources().getColor(R.color.inner_tab_grey));
	}

}
