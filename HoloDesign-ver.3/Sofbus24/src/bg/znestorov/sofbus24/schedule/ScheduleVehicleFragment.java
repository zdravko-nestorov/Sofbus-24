package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.publictransport.RetrievePublicTransportDirection;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

/**
 * Schedule Vehiles Fragment containing information about the public transport
 * vehicles
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleVehicleFragment extends ListFragment implements
		OnItemClickListener {

	private Activity context;

	private SearchEditText searchEditText;
	private GridView gridViewScheduleVehicles;
	private View emptyView;
	private TextView emptyTextView;

	private int currentVehicle;
	private ScheduleLoadVehicles slv;

	private ScheduleVehicleAdapter scheduleVehicleAdapter;
	private ArrayList<VehicleEntity> stationsList = new ArrayList<VehicleEntity>();

	private String searchText;
	private static final String BUNDLE_SEARCH_TEXT = "SEARCH TEXT";

	public static ScheduleVehicleFragment newInstance(int currentVehicle) {
		ScheduleVehicleFragment scheduleStationFragment = new ScheduleVehicleFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
				currentVehicle);
		scheduleStationFragment.setArguments(bundle);

		return scheduleStationFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(
				R.layout.activity_schedule_vehicle_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the needed fragment information
		initInformation(savedInstanceState);

		// Find all of TextView and SearchEditText tabs in the layout
		initLayoutFields(fragmentView);

		return fragmentView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(BUNDLE_SEARCH_TEXT, searchText);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ScheduleVehicleAdapter scheduleStationAdapter = (ScheduleVehicleAdapter) getListAdapter();
		onListItemClick(scheduleStationAdapter, position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ScheduleVehicleAdapter scheduleStationAdapter = (ScheduleVehicleAdapter) gridViewScheduleVehicles
				.getAdapter();
		onListItemClick(scheduleStationAdapter, position);
	}

	/**
	 * Retieve an information about the selected vehicle
	 * 
	 * @param scheduleStationAdapter
	 *            the ScheduleVehicleAdapter
	 * @param position
	 *            the position of the selected vehicle
	 */
	private void onListItemClick(ScheduleVehicleAdapter scheduleStationAdapter,
			int position) {
		VehicleEntity vehicle = (VehicleEntity) scheduleStationAdapter
				.getItem(position);
		String rowCaption = scheduleStationAdapter.getVehicleCaption(context,
				vehicle);

		// Getting the PublicTransport schedule from the SKGT site
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(Html.fromHtml(String.format(
				getString(R.string.pt_item_loading_schedule), rowCaption)));
		RetrievePublicTransportDirection retrievePublicTransportDirection = new RetrievePublicTransportDirection(
				context, this, progressDialog, vehicle);
		retrievePublicTransportDirection.execute();
	}

	/**
	 * Initialize the ScheduleLoadVehicles object and all the data from the
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

		// Get the current vehicle from the Bundle
		currentVehicle = getArguments().getInt(
				Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE);

		// Get the information about the current direction
		slv = ScheduleLoadVehicles.getInstance(context);
		stationsList = slv.getVehiclesList(currentVehicle);
	}

	/**
	 * Initialize the layout fields and assign the appropriate listeners over
	 * them (vehicles tabs (TextViews), SerachEditText and EmptyList (TextView))
	 * 
	 * @param fragmentView
	 *            the current view of the fragment
	 */
	private void initLayoutFields(View fragmentView) {
		searchEditText = (SearchEditText) fragmentView
				.findViewById(R.id.schedule_vehicle_search);
		emptyView = fragmentView
				.findViewById(R.id.schedule_vehicle_list_empty_view);
		emptyTextView = (TextView) fragmentView
				.findViewById(R.id.schedule_vehicle_list_empty_text);

		// Set on click listener over the grid view and hide the empty view in
		// the bgining (if the ListFragment uses a GridView)
		gridViewScheduleVehicles = (GridView) fragmentView
				.findViewById(R.id.schedule_vehicle_list_grid_view);
		if (gridViewScheduleVehicles != null) {
			gridViewScheduleVehicles.setOnItemClickListener(this);
			emptyView.setVisibility(View.GONE);
		}

		// Use custom ArrayAdapter to show the elements in the ListView
		setAdapter();

		// Set the actions over the SearchEditText
		actionsOverSearchEditText();
	}

	/**
	 * According to the current vehicle assign the appropriate adapter to the
	 * list fragment
	 */
	private void setAdapter() {
		scheduleVehicleAdapter = new ScheduleVehicleAdapter(context, emptyView,
				emptyTextView, getVehicleName(), stationsList);

		if (gridViewScheduleVehicles == null) {
			setListAdapter(scheduleVehicleAdapter);
		} else {
			gridViewScheduleVehicles.setAdapter(scheduleVehicleAdapter);
		}
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 */
	private void actionsOverSearchEditText() {
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
				scheduleVehicleAdapter.getFilter().filter(searchText);
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
	 * Get the name of the tab according to the currentVehicle
	 * 
	 * @return the name of the tab
	 */
	private String getVehicleName() {
		String activeTabName = "";

		switch (currentVehicle) {
		case 0:
			activeTabName = getString(R.string.sch_search_tab_bus);
			break;
		case 1:
			activeTabName = getString(R.string.sch_search_tab_trolley);
			break;
		default:
			activeTabName = getString(R.string.sch_search_tab_tram);
			break;
		}

		return activeTabName;
	}
}
