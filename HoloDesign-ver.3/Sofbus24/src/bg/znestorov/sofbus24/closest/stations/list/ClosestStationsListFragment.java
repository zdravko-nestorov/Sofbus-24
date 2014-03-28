package bg.znestorov.sofbus24.closest.stations.list;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ClosestStationsListFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;

	private LatLng currentLocation;
	private StationsDataSource stationsDatasource;

	private List<Station> closestStations;
	private static String closestStationsSearchText = "";

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

		// Find the ImageView, ProgressBar, SearchEditText and TextView in the
		// layout
		ImageView imageView = (ImageView) myFragmentView
				.findViewById(R.id.cs_list_street_view_image);
		ProgressBar progressBar = (ProgressBar) myFragmentView
				.findViewById(R.id.cs_list_street_view_progress);
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.cs_list_search);

		// Load the current location street view
		loadLocationStreetView(imageView, progressBar);

		// Set the actions over the SearchEditText
		actionsOverSearchEditText(searchEditText);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new ClosestStationsListAdapter(context,
				currentLocation, closestStations);
		setListAdapter(adapter);
		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visibleItemCount > 0) {
					int lastInScreen = firstVisibleItem + visibleItemCount;
					if (lastInScreen == totalItemCount) {
						Log.d("ASDASDAS", lastInScreen + "");
					}
				}
			}
		});

		return myFragmentView;
	}

	@Override
	public void update(Activity context, Object obj) {
		if (this.context == null) {
			this.context = context;
		}

		ImageView emptyListImage = (ImageView) context
				.findViewById(R.id.cs_list_empty_image);
		TextView emptyListText = (TextView) context
				.findViewById(R.id.cs_list_empty_text);
		ProgressBar emptyProgressBar = (ProgressBar) context
				.findViewById(R.id.cs_list_empty_progress);

		// Check if the update method is called just to reset the current
		// fragmnt or to update it (null - to reset, any other - to update)
		if (obj == null) {
			SearchEditText searchEditText = (SearchEditText) context
					.findViewById(R.id.cs_list_search);

			emptyListImage.setVisibility(View.GONE);
			emptyListText.setVisibility(View.GONE);
			emptyProgressBar.setVisibility(View.VISIBLE);

			closestStationsSearchText = "";

			searchEditText.setText(closestStationsSearchText);
			setListAdapter(null);

			new RetrieveCurrentPosition(context, this, null).execute();
		} else {
			ImageView imageView = (ImageView) context
					.findViewById(R.id.cs_list_street_view_image);
			ProgressBar progressBar = (ProgressBar) context
					.findViewById(R.id.cs_list_street_view_progress);

			emptyListImage.setVisibility(View.VISIBLE);
			emptyListText.setVisibility(View.VISIBLE);
			emptyProgressBar.setVisibility(View.GONE);

			currentLocation = (LatLng) obj;
			closestStations = loadStationsList(1, closestStationsSearchText);

			imageView.setImageResource(android.R.color.transparent);
			loadLocationStreetView(imageView, progressBar);

			ArrayAdapter<Station> adapter = new ClosestStationsListAdapter(
					context, currentLocation, closestStations);
			setListAdapter(adapter);
		}
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
	 * Load the current location StreetView
	 * 
	 * @param imageView
	 *            the ImageView of the StreetView from the layout
	 * @param progressBar
	 *            the ProgressBar shown when the image is loading
	 */
	private void loadLocationStreetView(ImageView imageView,
			final ProgressBar progressBar) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions displayImageOptions = ActivityUtils
				.displayImageOptions();

		String imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
				currentLocation.latitude + "", currentLocation.longitude + "");
		imageLoader.displayImage(imageUrl, imageView, displayImageOptions,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						progressBar.setVisibility(View.GONE);
					}
				});
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
				closestStationsSearchText = searchEditText.getText().toString();

				List<Station> searchStationList = loadStationsList(1,
						closestStationsSearchText);
				ArrayAdapter<Station> adapter = new ClosestStationsListAdapter(
						context, currentLocation, searchStationList);

				setListAdapter(adapter);
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
