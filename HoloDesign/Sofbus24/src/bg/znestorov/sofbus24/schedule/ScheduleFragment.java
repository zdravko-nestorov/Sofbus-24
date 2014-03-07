package bg.znestorov.sofbus24.schedule;

import java.util.List;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.ActivityUtils;

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

	private VehicleType vehicleType = VehicleType.BUS;

	private String busSearchText = "";
	private String trolleySearchText = "";
	private String tramSearchText = "";

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
		vehiclesDatasource.open();

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
		EditText searchEditText = (EditText) myFragmentView
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
	 * Activate the listeners over the Vehicle TextView fields
	 * 
	 * @param searchEditText
	 *            the text from the searched edit text
	 */
	private void actionsOverVehiclesTextViews(final EditText searchEditText) {
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
	private void actionsOverSearchEditText(final EditText searchEditText,
			final TextView emptyList) {
		searchEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, searchEditText);
				}
			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String searchText = searchEditText.getText().toString();
				List<Vehicle> searchStationList = vehiclesDatasource
						.getVehiclesViaSearch(vehicleType, searchText);
				ArrayAdapter<Vehicle> adapter = new ScheduleStationAdapter(
						context, searchStationList);

				setListAdapter(adapter);

				// Set a message if the list is empty
				if (adapter.isEmpty()) {
					emptyList.setText(Html.fromHtml(String
							.format(getString(R.string.sch_item_empty_list),
									searchText)));
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
	}

	/**
	 * Take needed actions according to the clicked tab
	 * 
	 * @param searchEditText
	 *            the text from the searched edit text
	 * @param tabType
	 *            the choosen type
	 */
	private void processOnClickedTab(EditText searchEditText,
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
				adapter = new ScheduleStationAdapter(context,
						vehiclesDatasource.getVehiclesViaSearch(vehicleType,
								busSearchText));
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
				adapter = new ScheduleStationAdapter(context,
						vehiclesDatasource.getVehiclesViaSearch(vehicleType,
								trolleySearchText));
			}

			setListAdapter(adapter);
			break;
		case TRAM:
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
				adapter = new ScheduleStationAdapter(context,
						vehiclesDatasource.getVehiclesViaSearch(vehicleType,
								tramSearchText));
			}

			adapter = new ScheduleStationAdapter(context, trams);
			setListAdapter(adapter);
			break;
		default:
			vehicleType = VehicleType.BUS;
			setTabActive(busTextView);
			setTabInactive(trolleyTextView);
			setTabInactive(tramTextView);
			adapter = new ScheduleStationAdapter(context, busses);
			setListAdapter(adapter);
			break;
		}

		// Set the marker at the end
		searchEditText.setSelection(searchEditText.getText().length());
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
