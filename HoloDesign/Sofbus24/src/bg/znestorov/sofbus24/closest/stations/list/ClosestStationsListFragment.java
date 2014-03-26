package bg.znestorov.sofbus24.closest.stations.list;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClosestStationsListFragment extends ListFragment {

	private Activity context;

	private LatLng currentLocation;
	private StationsDataSource stationsDatasource;

	private List<Station> closestStations;
	private String closestStationsSearchText = "";

	public ClosestStationsListFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_closest_stations_list_fragment, container,
				false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get Bundle arguments
		Bundle bundle = getArguments();
		currentLocation = (LatLng) bundle
				.get(Constants.BUNDLE_CLOSEST_STATIONS_LIST);

		// Load the closest stations
		stationsDatasource = new StationsDataSource(context);
		closestStations = loadStationsList(1, closestStationsSearchText);

		// Find the ImageView, SearchEditText and TextView in the layout
		ImageView imageView = (ImageView) myFragmentView
				.findViewById(R.id.cs_list_street_view_image);
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.cs_list_search);
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.cs_list_empty_text);

		// Load the current location street view
		loadLocationStreetView(imageView);

		// Set the actions over the TextViews and SearchEditText
		actionsOverSearchEditText(searchEditText, emptyList);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new ClosestStationsListAdapter(context,
				closestStations);
		setListAdapter(adapter);

		return myFragmentView;
	}

	/**
	 * Load the current location StreetView
	 * 
	 * @param imageView
	 *            the ImageView of the StreetView from the layout
	 */
	private void loadLocationStreetView(ImageView imageView) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions displayImageOptions = ActivityUtils
				.displayImageOptions();

		String imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
				currentLocation.latitude + "", currentLocation.longitude + "");
		imageLoader
				.displayImage(imageUrl, imageView, displayImageOptions, null);
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 * @param emptyList
	 *            the emptyList TextView
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
				closestStationsSearchText = searchEditText.getText().toString();

				List<Station> searchStationList = loadStationsList(1,
						closestStationsSearchText);
				ArrayAdapter<Station> adapter = new ClosestStationsListAdapter(
						context, searchStationList);

				setListAdapter(adapter);

				// Set a message if the list is empty
				if (adapter.isEmpty()) {
					emptyList.setText(Html.fromHtml(String.format(
							getString(R.string.cs_list_item_empty_list),
							closestStationsSearchText)));
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
	 * Load all stations according to the searched text ordered by their
	 * position to the current location (shows as much as the stationPage
	 * multiplied by 10)
	 * 
	 * @param stationPage
	 *            shows which part results to show (each part contains 10
	 *            stations)
	 * @param searchText
	 *            the search text (if null - return all stations of the current
	 *            tab type)
	 * @return all stations according to a search text ordered by their position
	 *         to the current location
	 */
	private List<Station> loadStationsList(int stationPage, String searchText) {
		List<Station> stationsList;

		if (stationsDatasource == null) {
			stationsDatasource = new StationsDataSource(context);
		}

		stationsDatasource.open();
		stationsList = stationsDatasource.getClosestStations(currentLocation,
				stationPage, searchText);
		stationsDatasource.close();

		return stationsList;
	}
}
