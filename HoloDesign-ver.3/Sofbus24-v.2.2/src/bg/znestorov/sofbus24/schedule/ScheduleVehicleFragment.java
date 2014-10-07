package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.PublicTransportStationEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.publictransport.ChooseDirectionDialog;
import bg.znestorov.sofbus24.publictransport.RetrievePublicTransportDirection;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
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
	private StationsDataSource stationsDatasource;
	private String language;

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
		stationsDatasource = new StationsDataSource(context);
		language = LanguageChange.getUserLocale(context);

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

		String vehicleNumber = vehicle.getNumber();
		if ("10-ТМ".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity10TM(vehicle));
		} else if ("44-Б".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity44B(vehicle));
		} else if ("21-22".equals(vehicleNumber)) {
			vehicle.setNumber("22");
			proceedStandardCase(vehicle, rowCaption);
		} else {
			proceedStandardCase(vehicle, rowCaption);
		}
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 10-TM
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 10-TM
	 * @return a direction entity for this bus
	 */
	private DirectionsEntity createDirectionEntity10TM(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("1");
		lid.add("145");
		rid.add("1981");
		directionsNames.add(translateString("Ул. Филип Кутев - Хотел Хилтън"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("6359",
				translateString("Ул. Филип Кутев"), "29000"));
		stationsList.add(createPublicTransportStation("2654",
				translateString("Кв.Хладилника"), "17308"));
		stationsList.add(createPublicTransportStation("0342",
				translateString("Бул.Никола Вапцаров"), "17294"));
		stationsList.add(createPublicTransportStation("2039",
				translateString("Ул.Люботрън"), "25748"));
		stationsList.add(createPublicTransportStation("0923",
				translateString("Кемпински хотел Зографски"), "26999"));
		stationsList.add(createPublicTransportStation("2330",
				translateString("Хотел Хемус"), "18120"));
		stationsList.add(createPublicTransportStation("0397",
				translateString("Хотел Хилтън"), "18115"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("145");
		rid.add("1982");
		directionsNames.add(translateString("Хотел Хилтън - Ул. Филип Кутев"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0397",
				translateString("Хотел Хилтън"), "18115"));
		stationsList.add(createPublicTransportStation("1322",
				translateString("Хотел Хемус"), "25739"));
		stationsList.add(createPublicTransportStation("0922",
				translateString("Кемпински хотел Зографски"), "25756"));
		stationsList.add(createPublicTransportStation("2038",
				translateString("Ул.Люботрън"), "25747"));
		stationsList.add(createPublicTransportStation("0343",
				translateString("Бул.Никола Вапцаров"), "25751"));
		stationsList.add(createPublicTransportStation("2655",
				translateString("Кв.Хладилника"), "17311"));
		stationsList.add(createPublicTransportStation("6359",
				translateString("Ул. Филип Кутев"), "29000"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 44-B
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 44-B
	 * @return a direction entity for this bus
	 */
	private DirectionsEntity createDirectionEntity44B(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("1");
		lid.add("204");
		rid.add("1874");
		directionsNames
				.add(translateString("Автостанция Банкя - Кв. Градоман"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0050",
				translateString("Автостанция Банкя"), "22111"));
		stationsList.add(createPublicTransportStation("0503",
				translateString("Центъра Банкя"), "21906"));
		stationsList.add(createPublicTransportStation("0965",
				translateString("Ул. Странджа"), "22054"));
		stationsList.add(createPublicTransportStation("1433",
				translateString("Ул. Родина"), "22045"));
		stationsList.add(createPublicTransportStation("1425",
				translateString("Начало кв. Михайлово"), "22039"));
		stationsList.add(createPublicTransportStation("1674",
				translateString("Стопанство Михайлово"), "22033"));
		stationsList.add(createPublicTransportStation("0446",
				translateString("Ул. Алеко Константинов"), "22027"));
		stationsList.add(createPublicTransportStation("1921",
				translateString("Ул. Даме Груев"), "22019"));
		stationsList.add(createPublicTransportStation("0870",
				translateString("Ул. Топлика"), "22015"));
		stationsList.add(createPublicTransportStation("1992",
				translateString("Ул. Китката"), "22007"));
		stationsList.add(createPublicTransportStation("0982",
				translateString("Края на с. Михайлово"), "22006"));
		stationsList.add(createPublicTransportStation("0832",
				translateString("Кв. Градоман"), "23665"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("145");
		rid.add("1982");
		directionsNames
				.add(translateString("Кв. Градоман - Автостанция Банкя"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0832",
				translateString("Кв. Градоман"), "23665"));
		stationsList.add(createPublicTransportStation("6351",
				translateString("Края на с. Михайлово"), "22013"));
		stationsList.add(createPublicTransportStation("1991",
				translateString("Ул.Китката"), "22008"));
		stationsList.add(createPublicTransportStation("0869",
				translateString("Ул.Топлика"), "22016"));
		stationsList.add(createPublicTransportStation("1920",
				translateString("Ул.Даме Груев"), "22020"));
		stationsList.add(createPublicTransportStation("0445",
				translateString("Ул.Алеко Константинов"), "22028"));
		stationsList.add(createPublicTransportStation("1673",
				translateString("Стопанство Михайлово"), "22034"));
		stationsList.add(createPublicTransportStation("1424",
				translateString("Начало кв.Михайлово"), "22040"));
		stationsList.add(createPublicTransportStation("1432",
				translateString("Ул.Родина"), "22046"));
		stationsList.add(createPublicTransportStation("0964",
				translateString("Ул.Странджа"), "22055"));
		stationsList.add(createPublicTransportStation("0502",
				translateString("Центъра Банкя"), "21907"));
		stationsList.add(createPublicTransportStation("0051",
				translateString("Автостанция Банкя"), "22109"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Translate the string if needed
	 * 
	 * @param input
	 *            the input string
	 * @return the translated string (if needed)
	 */
	private String translateString(String input) {
		String output;
		if (!"bg".equals(language)) {
			output = TranslatorCyrillicToLatin.translate(context, input);
		} else {
			output = input;
		}

		return output;
	}

	/**
	 * Create a PublicTransportStationEntity using the given number, name and id
	 * 
	 * @param number
	 *            the station number
	 * @param name
	 *            the station name
	 * @param id
	 *            the station id
	 * @return the PublicTransportStation entity
	 */
	private PublicTransportStationEntity createPublicTransportStation(
			String number, String name, String id) {
		stationsDatasource.open();

		StationEntity station = stationsDatasource.getStation(number);
		if (station == null) {
			station = new StationEntity();
		}
		station.setNumber(number);
		station.setName(name);

		stationsDatasource.close();

		return new PublicTransportStationEntity(station, id);

	}

	/**
	 * Retrieve information about the selected vehicle in the special cases
	 * 
	 * @param ptDirectionsEntity
	 *            the DirectionEntity in the special case
	 */
	private void proceedSpecialCase(DirectionsEntity ptDirectionsEntity) {
		DialogFragment dialogFragment = ChooseDirectionDialog
				.newInstance(ptDirectionsEntity);
		dialogFragment.show(getChildFragmentManager(), "dialog");
	}

	/**
	 * Retrieve information about the selected vehicle in the standard cases
	 * 
	 * @param vehicle
	 *            the selected vehicle
	 * @param rowCaption
	 *            the selectedrow caption
	 */
	private void proceedStandardCase(VehicleEntity vehicle, String rowCaption) {
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
