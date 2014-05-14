package bg.znestorov.sofbus24.closest.stations.list;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

import com.google.android.gms.maps.model.LatLng;

public class ClosestStationsListFragment extends ListFragment {

	private Activity context;

	private LatLng currentLocation;
	private StationsDataSource stationsDatasource;

	private int closestStationsCount;
	private String closestStationsSearchText = "";

	private boolean isListFullLoaded = false;
	private ArrayAdapter<Station> closestStationsAdapter;

	private static final String SAVED_STATE_STATIONS_COUNT_KEY = "Closest stations count";
	private static final String SAVED_STATE_SEARCH_TEXT_KEY = "Closest Stations Search Text";

	public static ClosestStationsListFragment newInstance(LatLng currentLocation) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(Constants.BUNDLE_CLOSEST_STATIONS_LIST,
				currentLocation);

		ClosestStationsListFragment closestStationsListFragment = new ClosestStationsListFragment();
		closestStationsListFragment.setArguments(bundle);

		return closestStationsListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create endless ListView (has to be in this method, as it is called
		// after the view is created)
		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (view.getLastVisiblePosition() + 1 > totalItemCount - 5
						&& !isListFullLoaded) {
					int pageToLoad = (totalItemCount + 10) / 10;
					List<Station> closestStations = loadStationsList(true,
							pageToLoad, closestStationsSearchText);

					if (closestStations.size() > 0) {
						closestStationsAdapter.addAll(closestStations);
						closestStationsCount = closestStationsAdapter
								.getCount();
					} else {
						isListFullLoaded = true;
					}
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_closest_stations_list_fragment, container,
				false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Find the SearchEditText in the layout
		SearchEditText searchEditText = (SearchEditText) context
				.findViewById(R.id.cs_list_search);

		// Get Bundle arguments
		Bundle bundle = getArguments();
		currentLocation = (LatLng) bundle
				.get(Constants.BUNDLE_CLOSEST_STATIONS_LIST);

		// Get the already loaded stations (in case of orientation change)
		if (savedInstanceState != null) {
			closestStationsCount = savedInstanceState
					.getInt(SAVED_STATE_STATIONS_COUNT_KEY);
			closestStationsSearchText = savedInstanceState
					.getString(SAVED_STATE_SEARCH_TEXT_KEY);
		} else {
			closestStationsCount = 10;
			closestStationsSearchText = searchEditText.getText().toString();
		}

		// Set the actions over the SearchEditText
		actionsOverSearchEditText(searchEditText);

		// Load the closest stations
		stationsDatasource = new StationsDataSource(context);

		// Set the ArrayList to the Fragment
		setListFragmentAdapter();

		return myFragmentView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putInt(SAVED_STATE_STATIONS_COUNT_KEY,
				closestStationsCount);
		savedInstanceState.putString(SAVED_STATE_SEARCH_TEXT_KEY,
				closestStationsSearchText);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		// Getting the time of arrival of the vehicles
		String stationCustomField = station.getCustomField();
		String metroCustomField = String.format(Constants.METRO_STATION_URL,
				station.getNumber());

		// Check if the type of the station - BTT or METRO
		if (!stationCustomField.equals(metroCustomField)) {
			// TODO: Retrieve information about the station
			Toast.makeText(context, station.getName(), Toast.LENGTH_SHORT)
					.show();
		} else {
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(Html.fromHtml(String.format(
					context.getString(R.string.metro_loading_schedule),
					station.getName(), station.getNumber())));
			RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
					context, progressDialog, station);
			retrieveMetroSchedule.execute();
		}
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText) {
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
				// Check if the text is really changed or this is called because
				// of the activity started
				if (!closestStationsSearchText.equals(s.toString())) {
					closestStationsCount = 10;
					closestStationsSearchText = searchEditText.getText()
							.toString();
					setListFragmentAdapter();
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
	 * Assign an ArrayList to the ListFragment
	 */
	private void setListFragmentAdapter() {
		List<Station> searchStationList = loadStationsList(false,
				closestStationsCount, closestStationsSearchText);

		closestStationsAdapter = new ClosestStationsListAdapter(context,
				currentLocation, searchStationList);
		setListAdapter(closestStationsAdapter);
	}

	/**
	 * Load all stations according to the searched text ordered by their
	 * position to the current location (shows as much as the stationPage
	 * multiplied by 10)
	 * 
	 * @param loadByPage
	 *            point if the method is called to load a page or a whole list
	 *            with stations
	 * @param stationPageOrCount
	 *            shows which part results to show (each part contains 10
	 *            stations)
	 * @param searchText
	 *            the search text (if null - return all stations of the current
	 *            tab type)
	 * @return all stations according to a search text ordered by their position
	 *         to the current location
	 */
	private List<Station> loadStationsList(boolean loadByPage,
			int stationPageOrCount, String searchText) {
		List<Station> stationsList;

		if (stationsDatasource == null) {
			stationsDatasource = new StationsDataSource(context);
		}

		stationsDatasource.open();
		if (loadByPage) {
			// Set to false at each time of new search by page
			isListFullLoaded = false;
			stationsList = stationsDatasource.getClosestStationsByPage(
					currentLocation, stationPageOrCount, searchText);
		} else {
			stationsList = stationsDatasource.getClosestStations(
					currentLocation, stationPageOrCount, searchText);
		}
		stationsDatasource.close();

		return stationsList;
	}
}
