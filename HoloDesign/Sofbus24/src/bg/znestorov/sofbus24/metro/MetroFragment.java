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
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;

public class MetroFragment extends ListFragment {

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
		stationsDatasource.open();
		vehiclesDatasource.open();

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
		processOnClickedTab(searchEditText, stationType);

		// Activate the option menu
		setHasOptionsMenu(true);

		return myFragmentView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		// TODO: Retrieve information about the vehicle

		Toast.makeText(getActivity(), station.getName(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onResume() {
		stationsDatasource.open();
		vehiclesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		stationsDatasource.close();
		vehiclesDatasource.close();
		super.onPause();
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
				processOnClickedTab(searchEditText, VehicleType.METRO1);
			}
		});

		// Assign the Direction2 TextView a click listener
		direction2TextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab(searchEditText, VehicleType.METRO2);
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
				List<Station> searchStationList = stationsDatasource
						.getStationsViaSearch(stationType, searchText);
				ArrayAdapter<Station> adapter = new MetroStationAdapter(
						context, searchStationList);

				setListAdapter(adapter);

				// Set a message if the list is empty
				if (adapter.isEmpty()) {
					emptyList.setText(Html.fromHtml(String.format(
							getString(R.string.metro_item_empty_list),
							searchText, getDirectionName())));
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
	 * Get the direction name
	 * 
	 * @return the direction name of currently active tab
	 */
	private String getDirectionName() {
		return vehiclesDatasource.getVehiclesViaSearch(stationType, "").get(0)
				.getDirection();
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
		ArrayAdapter<Station> adapter;

		// Check which is previous clicked tab, so save the value to the
		// appropriate variable
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

		// Check which tab is clicked
		switch (tabType) {
		case METRO1:
			stationType = VehicleType.METRO1;

			setTabActive(direction1TextView);
			setTabInactive(direction2TextView);

			// Set the Search tab the appropriate search text
			searchEditText.setText(metro1SearchText);

			// Check if a search is already done
			if ("".equals(metro1SearchText)) {
				adapter = new MetroStationAdapter(context, metroDirection1);
			} else {
				adapter = new MetroStationAdapter(context,
						stationsDatasource.getStationsViaSearch(stationType,
								metro1SearchText));
			}

			setListAdapter(adapter);
			break;
		case METRO2:
			stationType = VehicleType.METRO2;

			setTabInactive(direction1TextView);
			setTabActive(direction2TextView);

			// Set the Search tab the appropriate search text
			searchEditText.setText(metro2SearchText);

			// Check if a search is already done
			if ("".equals(metro2SearchText)) {
				adapter = new MetroStationAdapter(context, metroDirection2);
			} else {
				adapter = new MetroStationAdapter(context,
						stationsDatasource.getStationsViaSearch(stationType,
								metro2SearchText));
			}

			setListAdapter(adapter);
			break;
		default:
			stationType = VehicleType.BUS;
			setTabActive(direction1TextView);
			setTabInactive(direction2TextView);
			adapter = new MetroStationAdapter(context, metroDirection1);
			setListAdapter(adapter);
			break;
		}

		// Set the marker at the end
		searchEditText.setSelection(searchEditText.getText().length());
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
