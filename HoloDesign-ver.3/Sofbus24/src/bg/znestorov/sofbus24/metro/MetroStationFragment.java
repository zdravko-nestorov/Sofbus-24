package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.StationRouteMap;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

/**
 * Metro Station Fragment containing information about the metro stations
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroStationFragment extends ListFragment {

	private Activity context;

	private int currentDirection;
	private MetroLoadStations mls;

	private MetroStationAdapter metroStationAdapter;
	private ArrayList<StationEntity> stationsList = new ArrayList<StationEntity>();

	private String searchText;
	private static final String BUNDLE_SEARCH_TEXT = "SEARCH TEXT";

	public static MetroStationFragment newInstance(int currentDirection) {
		MetroStationFragment metroStationFragment = new MetroStationFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.BUNDLE_METRO_SCHEDULE, currentDirection);
		metroStationFragment.setArguments(bundle);

		return metroStationFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		registerForContextMenu(getListView());
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(
				R.layout.activity_metro_station_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the needed fragment information
		initInformation(savedInstanceState);

		// Find all of TextView and SearchEditText tabs in the layout
		initLayoutFields(fragmentView);

		// Activate the option menu
		setHasOptionsMenu(true);

		return fragmentView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(BUNDLE_SEARCH_TEXT, searchText);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		StationEntity station = (StationEntity) ((MetroStationAdapter) getListAdapter())
				.getItem(position);

		// Getting the Metro schedule from the station URL address
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(Html.fromHtml(String.format(
				getString(R.string.metro_loading_schedule), station.getName(),
				station.getNumber())));
		RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
				context, progressDialog, station);
		retrieveMetroSchedule.execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_metro_map_route:
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog
					.setMessage(getString(R.string.metro_menu_map_route_loading));
			RetrieveMetroRoute retrieveMetroRoute = new RetrieveMetroRoute(
					context, progressDialog);
			retrieveMetroRoute.execute();
			break;
		}

		return true;
	}

	/**
	 * Initialize the MetroLoadStation object and all the data from the
	 * SavedInstanceState object
	 * 
	 * @param savedInstanceState
	 *            object containing the state of the saved values
	 */
	private void initInformation(Bundle savedInstanceState) {
		// Get the values from the Bundle
		if (savedInstanceState != null) {
			searchText = savedInstanceState.getString(BUNDLE_SEARCH_TEXT);
		} else {
			searchText = "";
		}

		// Get the current direction from the Bundle
		currentDirection = getArguments().getInt(
				Constants.BUNDLE_METRO_SCHEDULE);

		// Get the information about the current direction
		mls = MetroLoadStations.getInstance(context);
		stationsList = mls.getDirectionList(currentDirection);
	}

	/**
	 * Initialize the layout fields and assign the appropriate listeners over
	 * them (directions tabs (TextViews), SerachEditText and EmptyList
	 * (TextView))
	 * 
	 * @param fragmentView
	 *            the current view of the fragment
	 */
	private void initLayoutFields(View fragmentView) {
		SearchEditText searchEditText = (SearchEditText) fragmentView
				.findViewById(R.id.metro_station_search);
		TextView emptyList = (TextView) fragmentView
				.findViewById(R.id.metro_station_list_empty_text);

		// Use custom ArrayAdapter to show the elements in the ListView
		setListAdapter(emptyList);

		// Set the actions over the SearchEditText
		actionsOverSearchEditText(searchEditText, emptyList);
	}

	/**
	 * According to the current direction assign the appropriate adapter to the
	 * list fragment
	 * 
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void setListAdapter(TextView emptyList) {
		metroStationAdapter = new MetroStationAdapter(context, emptyList,
				mls.getDirectionName(currentDirection, false, false),
				stationsList);
		setListAdapter(metroStationAdapter);
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText,
			final TextView emptyList) {
		// TODO: Find a way to set an alphanumeric keyboard with numeric as
		// default
		searchEditText.setFilters(new InputFilter[] { ActivityUtils
				.createInputFilter() });
		searchEditText.setText(searchText);

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
				searchText = searchEditText.getText().toString();
				metroStationAdapter.getFilter().filter(searchText);
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
					searchEditText.setSelection(searchEditText.getText()
							.length());
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
	 * Asynchronous class used for retrieving the Metro route
	 * 
	 * @author Zdravko Nestorov
	 */
	public class RetrieveMetroRoute extends AsyncTask<Void, Void, Intent> {

		private Activity context;
		private ProgressDialog progressDialog;

		public RetrieveMetroRoute(Activity context,
				ProgressDialog progressDialog) {
			this.context = context;
			this.progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			createLoadingView();
		}

		@Override
		protected Intent doInBackground(Void... params) {
			Intent metroMapRouteIntent = new Intent(context,
					StationRouteMap.class);

			VehicleEntity metroVehicle;
			switch (currentDirection) {
			case 0:
				metroVehicle = new VehicleEntity("1", VehicleTypeEnum.METRO1,
						mls.getDirectionName(currentDirection, false, true));
				break;
			default:
				metroVehicle = new VehicleEntity("1", VehicleTypeEnum.METRO2,
						mls.getDirectionName(currentDirection, false, true));
				break;
			}

			DirectionsEntity metroDirectionsEntity = new DirectionsEntity(
					metroVehicle, currentDirection,
					mls.getMetroDirectionsNames(), mls.getMetroDirectionsList());
			metroMapRouteIntent.putExtra(Constants.BUNDLE_STATION_ROUTE_MAP,
					metroDirectionsEntity);

			return metroMapRouteIntent;
		}

		@Override
		protected void onPostExecute(Intent metroMapRouteIntent) {
			super.onPostExecute(metroMapRouteIntent);

			context.startActivity(metroMapRouteIntent);
			dismissLoadingView();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissLoadingView();
		}

		/**
		 * Create the loading view and lock the screen
		 */
		private void createLoadingView() {
			ActivityUtils.lockScreenOrientation(context);

			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							cancel(true);
						}
					});
			progressDialog.show();
		}

		/**
		 * Dismiss the loading view and unlock the screen
		 */
		private void dismissLoadingView() {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			ActivityUtils.unlockScreenOrientation(context);
		}
	}

}