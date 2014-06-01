package bg.znestorov.sofbus24.metro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.StationRouteMap;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

/**
 * Metro Fragment containing information about the metro stations
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroStationFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;
	private MetroLoadStations mls;

	private ArrayAdapter<Station> msAdapterDirection1;
	private ArrayAdapter<Station> msAdapterDirection2;

	private int currentDirection;
	private String searchTextDirection1;
	private String searchTextDirection2;

	private static final String BUNDLE_CURRENT_DRECTION = "CURRENT DIRECTION";
	private static final String BUNDLE_SEARCH_TEXT_DIRECTION_1 = "SEARCH TEXT DIRECTION 1";
	private static final String BUNDLE_SEARCH_TEXT_DIRECTION_2 = "SEARCH TEXT DIRECTION 1";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.activity_metro_fragment,
				container, false);

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

		savedInstanceState.putInt(BUNDLE_CURRENT_DRECTION, currentDirection);
		savedInstanceState.putString(BUNDLE_SEARCH_TEXT_DIRECTION_1,
				searchTextDirection1);
		savedInstanceState.putString(BUNDLE_SEARCH_TEXT_DIRECTION_2,
				searchTextDirection2);
	}

	@Override
	public void update(Activity context) {
		ArrayAdapter<Station> metroStationAdapter = (MetroStationAdapter) getListAdapter();

		if (metroStationAdapter != null) {
			SearchEditText searchEditText = (SearchEditText) context
					.findViewById(R.id.metro_search);

			switch (currentDirection) {
			case 0:
				searchEditText.setText(searchTextDirection1);
				metroStationAdapter.getFilter().filter(searchTextDirection1);
				break;
			default:
				searchEditText.setText(searchTextDirection2);
				metroStationAdapter.getFilter().filter(searchTextDirection2);
				break;
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

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
			currentDirection = savedInstanceState
					.getInt(BUNDLE_CURRENT_DRECTION);
			searchTextDirection1 = savedInstanceState
					.getString(BUNDLE_SEARCH_TEXT_DIRECTION_1);
			searchTextDirection2 = savedInstanceState
					.getString(BUNDLE_SEARCH_TEXT_DIRECTION_2);
		} else {
			currentDirection = 0;
			searchTextDirection1 = "";
			searchTextDirection2 = "";
		}

		// Get the information about each direction
		mls = MetroLoadStations.getInstance(context);
	}

	/**
	 * Initialize the layout fields and assign the appropriate listeners over
	 * them (directions' tabs (TextViews), SerachEditText and EmptyList
	 * (TextView))
	 * 
	 * @param fragmentView
	 *            the current view of the fragment
	 */
	private void initLayoutFields(View fragmentView) {
		TextView textViewDirection1 = (TextView) fragmentView
				.findViewById(R.id.metro_direction1_tab);
		TextView textViewDirection2 = (TextView) fragmentView
				.findViewById(R.id.metro_direction2_tab);
		SearchEditText searchEditText = (SearchEditText) fragmentView
				.findViewById(R.id.metro_search);
		TextView emptyList = (TextView) fragmentView
				.findViewById(R.id.metro_list_empty_text);

		// Create the list adapters
		createListAdapters(emptyList);

		// Use custom ArrayAdapter to show the elements in the ListView
		setListAdapter(emptyList);

		// Set the actions over the TextViews and SearchEditText
		actionsOverDirectionsTextViews(textViewDirection1, textViewDirection2,
				searchEditText, emptyList);
		actionsOverSearchEditText(searchEditText, emptyList);

		// Set the initial state of the fragment
		processOnClickedTab(textViewDirection1, textViewDirection2,
				searchEditText, emptyList);
	}

	/**
	 * Create both custom array adapters {@link MetroStationAdapter} for each
	 * direction
	 * 
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void createListAdapters(TextView emptyList) {
		if (msAdapterDirection1 == null) {
			msAdapterDirection1 = new MetroStationAdapter(context, emptyList,
					mls.getDirectionName(0, false, false),
					mls.getDirectionList(0));
		}

		if (msAdapterDirection2 == null) {
			msAdapterDirection2 = new MetroStationAdapter(context, emptyList,
					mls.getDirectionName(1, false, false),
					mls.getDirectionList(1));
		}
	}

	/**
	 * According to the current direction assign the list fragment the
	 * appropriate adapter
	 * 
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void setListAdapter(TextView emptyList) {
		switch (currentDirection) {
		case 0:
			setListAdapter(msAdapterDirection1);
			break;
		default:
			setListAdapter(msAdapterDirection2);
			break;
		}
	}

	/**
	 * Activate the listeners over the directions' tabs (TextViews)
	 * 
	 * @param textViewDirection1
	 *            first direction tab (TextView)
	 * @param textViewDirection2
	 *            second direction tab (TextView)
	 * @param searchEditText
	 *            the search edit box (EditText)
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void actionsOverDirectionsTextViews(
			final TextView textViewDirection1,
			final TextView textViewDirection2,
			final SearchEditText searchEditText, final TextView emptyList) {
		// Assign the Direction1 TextView a click listener
		textViewDirection1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab1(textViewDirection1, textViewDirection2,
						searchEditText, emptyList);
			}
		});

		// Assign the Direction2 TextView a click listener
		textViewDirection2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processOnClickedTab2(textViewDirection1, textViewDirection2,
						searchEditText, emptyList);
			}
		});
	}

	/**
	 * Take the needed actions once a tab is pressed (selected line 1)
	 * 
	 * @param textViewDirection1
	 *            first direction tab (TextView)
	 * @param textViewDirection2
	 *            second direction tab (TextView)
	 * @param searchEditText
	 *            the search edit box (EditText)
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void processOnClickedTab1(TextView textViewDirection1,
			TextView textViewDirection2, SearchEditText searchEditText,
			TextView emptyList) {
		// Change the view of the tabs (pressed and not pressed)
		setTabActive(textViewDirection1);
		setTabInactive(textViewDirection2);

		// Change the indicator of the pressed tab
		currentDirection = 0;

		// Retain the value of the SearchEditText to a variable, set a new one
		// and move the cursor to the end
		searchTextDirection2 = searchEditText.getText().toString();
		searchEditText.setText(searchTextDirection1);
		searchEditText.setSelection(searchEditText.getText().length());

		// Use custom ArrayAdapter to show the elements in the ListView
		setListAdapter(emptyList);
	}

	/**
	 * Take the needed actions once a tab is pressed (selected line 2)
	 * 
	 * @param textViewDirection1
	 *            first direction tab (TextView)
	 * @param textViewDirection2
	 *            second direction tab (TextView)
	 * @param searchEditText
	 *            the search edit box (EditText)
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void processOnClickedTab2(TextView textViewDirection1,
			TextView textViewDirection2, SearchEditText searchEditText,
			TextView emptyList) {
		// Change the view of the tabs (pressed and not pressed)
		setTabActive(textViewDirection2);
		setTabInactive(textViewDirection1);

		// Change the indicator of the pressed tab
		currentDirection = 1;

		// Retain the value of the SearchEditText to a variable, set a new one
		// and move the cursor to the end
		searchTextDirection1 = searchEditText.getText().toString();
		searchEditText.setText(searchTextDirection2);
		searchEditText.setSelection(searchEditText.getText().length());

		// Use custom ArrayAdapter to show the elements in the ListView
		setListAdapter(emptyList);
	}

	/**
	 * Take the needed actions once a tab is pressed (according to the current
	 * direction)
	 * 
	 * @param textViewDirection1
	 *            first direction tab (TextView)
	 * @param textViewDirection2
	 *            second direction tab (TextView)
	 * @param searchEditText
	 *            the search edit box (EditText)
	 * @param emptyList
	 *            the empty TextView (a text shown when the list fragment is
	 *            empty)
	 */
	private void processOnClickedTab(TextView textViewDirection1,
			TextView textViewDirection2, SearchEditText searchEditText,
			TextView emptyList) {
		switch (currentDirection) {
		case 0:
			processOnClickedTab1(textViewDirection1, textViewDirection2,
					searchEditText, emptyList);
			break;
		default:
			processOnClickedTab2(textViewDirection1, textViewDirection2,
					searchEditText, emptyList);
			break;
		}
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
				ArrayAdapter<Station> metroStationAdapter = (MetroStationAdapter) getListAdapter();
				switch (currentDirection) {
				case 0:
					searchTextDirection1 = searchEditText.getText().toString();
					metroStationAdapter.getFilter()
							.filter(searchTextDirection1);
					break;
				default:
					searchTextDirection2 = searchEditText.getText().toString();
					metroStationAdapter.getFilter()
							.filter(searchTextDirection2);
					break;
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

		@Override
		protected Intent doInBackground(Void... params) {
			Intent metroMapRouteIntent = new Intent(context,
					StationRouteMap.class);

			Vehicle metroVehicle;
			switch (currentDirection) {
			case 0:
				metroVehicle = new Vehicle("1", VehicleType.METRO1,
						mls.getDirectionName(currentDirection, false, true));
				break;
			default:
				metroVehicle = new Vehicle("1", VehicleType.METRO2,
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
			context.startActivity(metroMapRouteIntent);

			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case the orientation is changed once
				// retrieving info
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}
		}
	}

}