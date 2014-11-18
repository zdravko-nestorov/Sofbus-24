package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import bg.znestorov.sofbus24.databases.DroidTransDataSource;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HtmlRequestCodesEnum;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.UpdateTypeEnum;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.entity.WheelStateEntity;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.navigation.NavDrawerArrayAdapter;
import bg.znestorov.sofbus24.navigation.NavDrawerHelper;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.virtualboards.RetrieveVirtualBoards;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Represents the funcionality of the DroidTrans application (not active in the
 * market). The main functionality of the class is to start a location update
 * listener to find the current location (if enabled) and the nearest station to
 * it. If no location is found in next 3 seconds, check if there is available
 * LastKnownLocation and find the nearest station to it. Also the class saves
 * the state of the wheels on orientation change.
 * 
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class DroidTrans extends SherlockFragmentActivity implements
		LocationListener {

	private FragmentActivity context;
	private GlobalEntity globalContext;

	private StationsDataSource stationsDatasource;
	private VehiclesDataSource vehiclesDatasource;
	private DroidTransDataSource droidtransDatasource;

	private ActionBar actionBar;
	private boolean isDroidTransHomeScreen;

	private WheelView vehicleTypesWheel;
	private WheelView vehicleNumbersWheel;
	private WheelView vehicleDirectionsWheel;
	private WheelView vehicleStationsWheel;
	private Button vehicleSchedule;

	private boolean scrolling = false;
	private WheelStateEntity wheelState;

	private ArrayList<VehicleTypeEnum> vehicleTypes;
	private ArrayList<String> vehicleNumbers;
	private ArrayList<String> vehicleDirections;
	private ArrayList<StationEntity> vehicleStations;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerArrayAdapter mMenuAdapter;
	private ArrayList<String> navigationItems;

	private LocationManager locationManager;

	private static final long MIN_DISTANCE_FOR_UPDATE = 20;
	private static final long MIN_TIME_FOR_UPDATE = 1000 * 2;
	private static final long WAIT_TIME = 500;

	private static final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
	private static final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

	private static final String BUNDLE_WHEEL_STATE = "BUNDLE WHEEL STATE";
	public static final String BUNDLE_IS_DROID_TRANS_HOME_SCREEN = "IS DROID TRANS HOME SCREEN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_droidtrans);

		// Get the current activity context and check if this activity is the
		// home screen
		context = DroidTrans.this;
		globalContext = (GlobalEntity) getApplicationContext();
		stationsDatasource = new StationsDataSource(context);
		vehiclesDatasource = new VehiclesDataSource(context);
		droidtransDatasource = new DroidTransDataSource(context);
		isDroidTransHomeScreen = getIntent().getExtras() != null ? getIntent()
				.getExtras().getBoolean(BUNDLE_IS_DROID_TRANS_HOME_SCREEN,
						false) : false;

		// Get the wheels state
		if (savedInstanceState == null) {
			wheelState = new WheelStateEntity();
		} else {
			wheelState = (WheelStateEntity) savedInstanceState
					.getSerializable(BUNDLE_WHEEL_STATE);
		}

		initActionBar();
		initLayoutFields();

		if (isDroidTransHomeScreen) {
			if (savedInstanceState == null) {
				Utils.checkForUpdate(context, UpdateTypeEnum.APP);
			}

			initNavigationDrawer();
		}

		initLocationListener();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (globalContext.isHasToRestart()) {
			context.setResult(HomeScreenSelect.RESULT_CODE_ACTIVITY_RESTART);
			context.finish();
		} else if (locationManager != null) {
			initLocationListener();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		removeLocationListener();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

		setWheelStateEntity(wheelState);
		savedInstanceState.putSerializable(BUNDLE_WHEEL_STATE, wheelState);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		setResult(HomeScreenSelect.RESULT_CODE_ACTIVITY_FINISH, new Intent());
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_droidtrans_actions,
				menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem favourites = menu.findItem(R.id.action_favourites);
		MenuItem csMap = menu.findItem(R.id.action_closest_stations_map);

		if (isDroidTransHomeScreen) {
			favourites.setVisible(true);
			csMap.setVisible(true);
		} else {
			favourites.setVisible(false);
			csMap.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			if (isDroidTransHomeScreen) {
				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			} else {
				finish();
			}

			return true;
		case R.id.action_favourites:
			Intent favouritesIntent;
			if (globalContext.isPhoneDevice()) {
				favouritesIntent = new Intent(context, Favourites.class);
			} else {
				favouritesIntent = new Intent(context, FavouritesDialog.class);
			}

			startActivity(favouritesIntent);

			return true;
		case R.id.action_closest_stations_map:
			ActivityUtils.startClosestStationsMap(context,
					getSupportFragmentManager(), false);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Pass any configuration change to the drawer toggles
		if (mDrawerToggle != null) {
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	/**
	 * Set up the action bar
	 */
	private void initActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.droid_trans_title));
	}

	/**
	 * Get all vehicle types
	 */
	private void getVehicleTypes() {
		droidtransDatasource.open();
		vehicleTypes = droidtransDatasource.getVehicleTypes();
		droidtransDatasource.close();
	}

	/**
	 * Get the vehicle numbers for the selected vehicle type
	 * 
	 * @param vehicleType
	 *            the selected vehicle type
	 */
	private void getVehicleNumbers(VehicleTypeEnum vehicleType) {
		droidtransDatasource.open();
		vehicleNumbers = droidtransDatasource.getVehicleNumbers(vehicleType);
		droidtransDatasource.close();
	}

	/**
	 * Get the vehicle directions for the selected vehicle type and number
	 * 
	 * @param vehicleType
	 *            the selected vehicle type
	 * @param vehicleNumber
	 *            the selected vehicle number
	 * 
	 */
	private void getVehicleDirections(VehicleTypeEnum vehicleType,
			String vehicleNumber) {
		droidtransDatasource.open();
		vehicleDirections = droidtransDatasource.getVehicleDirections(
				vehicleType, vehicleNumber);
		droidtransDatasource.close();
	}

	/**
	 * Get all stations for the selected vehicle in the desired location
	 * 
	 * @param vehicleType
	 *            the selected vehicle type
	 * @param vehicleNumber
	 *            the selected vehicle number
	 * @param vehicleDirection
	 *            the desired direction
	 */
	private void getStationsList(VehicleTypeEnum vehicleType,
			String vehicleNumber, Integer vehicleDirection) {

		switch (vehicleType) {
		case METRO:
			vehicleStations = MetroLoadStations.getInstance(context)
					.getMetroDirectionsListFormatted()
					.get(vehicleDirection - 1);
			break;
		default:
			droidtransDatasource.open();
			vehicleStations = droidtransDatasource.getVehicleStations(
					vehicleType, vehicleNumber, vehicleDirection);
			droidtransDatasource.close();
			break;
		}
	}

	/**
	 * Initialize the layout fields
	 */
	private void initLayoutFields() {

		vehicleTypesWheel = (WheelView) findViewById(R.id.droidtrans_vehicle_types);
		vehicleTypesWheel.setVisibleItems(4);

		vehicleNumbersWheel = (WheelView) findViewById(R.id.droidtrans_vehicle_numbers);
		vehicleNumbersWheel.setVisibleItems(5);

		vehicleDirectionsWheel = (WheelView) findViewById(R.id.droidtrans_directions);
		vehicleDirectionsWheel.setVisibleItems(2);

		vehicleStationsWheel = (WheelView) findViewById(R.id.droidtrans_stations);
		vehicleDirectionsWheel.setVisibleItems(5);

		vehicleSchedule = (Button) findViewById(R.id.droidtrans_schedule);

		updateVehicleWheels();
		setVehicleWheelsListeners();
		setVehicleWheelsState(null);
		retrieveVehicleSchedule();
	}

	/**
	 * Set up all wheel listeners (over each of the wheels)
	 */
	private void setVehicleWheelsListeners() {
		setVehicleWheelsListener(0);
		setVehicleWheelsListener(1);
		setVehicleWheelsListener(2);
	}

	/**
	 * Set up one of the vehicle wheel listeners
	 */
	private void setVehicleWheelsListener(final int wheelViewPosition) {

		WheelView wheelView;
		switch (wheelViewPosition) {
		case 0:
			wheelView = vehicleTypesWheel;
			break;
		case 1:
			wheelView = vehicleNumbersWheel;
			break;
		default:
			wheelView = vehicleDirectionsWheel;
			break;
		}

		wheelView.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateVehicleWheel(wheelViewPosition);
				}
			}
		});

		wheelView.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateVehicleWheel(wheelViewPosition);
			}
		});
	}

	/**
	 * Update all information in the vehicles wheels
	 */
	private void updateVehicleWheels() {

		updateVehicleWheelTypes();

		updateVehicleWheel(0);
		updateVehicleWheel(1);
		updateVehicleWheel(2);
	}

	/**
	 * Update the wheel view according to its position
	 * 
	 * @param wheelViewPosition
	 *            the position of the wheel view<br/>
	 *            <ul>
	 *            <li>0 - update vehicle numbers</li>
	 *            <li>1 - update vehicle directions</li>
	 *            <li>2 - update vehicle stations</li>
	 *            </ul>
	 */
	private void updateVehicleWheel(int wheelViewPosition) {

		switch (wheelViewPosition) {
		case 0:
			updateVehicleWheelNumbers();
			updateVehicleWheelDirections();
			updateVehicleWheelStations();
			break;
		case 1:
			updateVehicleWheelDirections();
			updateVehicleWheelStations();
			break;
		default:
			updateVehicleWheelStations();
			break;
		}
	}

	/**
	 * Set the state entity values
	 * 
	 * @param wheelState
	 *            the wheel state entity
	 */
	private void setWheelStateEntity(WheelStateEntity wheelState) {
		wheelState.setVehiclesType(vehicleTypesWheel.getCurrentItem());
		wheelState.setVehiclesNumber(vehicleNumbersWheel.getCurrentItem());
		wheelState
				.setVehiclesDirection(vehicleDirectionsWheel.getCurrentItem());
		wheelState.setStationsNumbers(vehicleStationsWheel.getCurrentItem());
	}

	/**
	 * Set the state of the wheels (the current items)
	 * 
	 * @param currentLocation
	 *            the current location of the user
	 */
	private void setVehicleWheelsState(Location currentLocation) {

		// In case of a new location found and no wheels state set, change the
		// WheelStateEntity object values
		if (currentLocation != null && !wheelState.isWheelStateSet()) {

			WheelStateEntity currentWheelState = new WheelStateEntity();
			setWheelStateEntity(currentWheelState);

			// Check if the current state of the wheels is the base position
			// (all are at the 0 position)
			if (currentWheelState.isWheelStateInBasePosition()) {
				changeWheelsStateValuesByLocation(currentLocation);
				changeWheelsStateValues();
			}
		} else if (wheelState.isWheelStateSet()) {
			changeWheelsStateValues();
			wheelState.reset();
		}
	}

	/**
	 * Get the wheel views positions according to the current location
	 * 
	 * @param currentLocation
	 *            the current user location
	 */
	private void changeWheelsStateValuesByLocation(Location currentLocation) {

		int vehiclesTypeWheelPosition;
		int vehiclesNumberWheelPosition;
		int vehiclesDirectionWheelPosition;
		int stationsNumbersWheelPosition;

		// Get the closest station
		stationsDatasource.open();
		StationEntity closestStation = stationsDatasource.getClosestStation(
				context, currentLocation);
		stationsDatasource.close();

		// Get the vehicle passing through the closest station
		vehiclesDatasource.open();
		VehicleEntity closestVehicle = vehiclesDatasource
				.getVehicleViaStation(closestStation);
		vehiclesTypeWheelPosition = getVehicleTypePosition(closestVehicle
				.getType());
		vehiclesDirectionWheelPosition = Integer.parseInt(closestVehicle
				.getDirection());
		vehiclesDatasource.close();

		// Get the position of the wheel views
		droidtransDatasource.open();
		vehiclesNumberWheelPosition = droidtransDatasource
				.getVehicleNumbersPosition(
						getVehicleType(closestVehicle.getType()),
						closestVehicle.getNumber());
		stationsNumbersWheelPosition = droidtransDatasource
				.getVehicleStationPosition(
						getVehicleType(closestVehicle.getType()),
						closestVehicle.getNumber(),
						vehiclesDirectionWheelPosition,
						closestStation.getNumber());
		droidtransDatasource.close();

		// Set the values into the WheelState object
		wheelState = new WheelStateEntity(vehiclesTypeWheelPosition,
				vehiclesNumberWheelPosition,
				vehiclesDirectionWheelPosition - 1,
				stationsNumbersWheelPosition);
	}

	/**
	 * Get the vehicle type accordingly
	 * 
	 * @param vehicleType
	 *            the current vehicle type
	 * @return the output vehicle type
	 */
	private VehicleTypeEnum getVehicleType(VehicleTypeEnum vehicleType) {

		if (vehicleType == VehicleTypeEnum.METRO1
				|| vehicleType == VehicleTypeEnum.METRO2) {
			vehicleType = VehicleTypeEnum.METRO;
		}

		return vehicleType;
	}

	/**
	 * Get the vehcile type position in the wheel view via the vehicle type
	 * 
	 * @param vehicleType
	 *            the current vehicle type
	 * @return the position of the vehicle type in the wheel view
	 */
	private int getVehicleTypePosition(VehicleTypeEnum vehicleType) {

		int vehicleTypePosition;
		if (vehicleType == VehicleTypeEnum.METRO1
				|| vehicleType == VehicleTypeEnum.METRO2) {
			vehicleType = VehicleTypeEnum.METRO;
		}

		switch (vehicleType) {
		case BUS:
			vehicleTypePosition = 0;
			break;
		case TROLLEY:
			vehicleTypePosition = 1;
			break;
		case TRAM:
			vehicleTypePosition = 2;
			break;
		default:
			vehicleTypePosition = 3;
			break;
		}

		return vehicleTypePosition;
	}

	/**
	 * In case of a location found or an orientation changes, change the
	 * WheelStateEntity object values
	 */
	private void changeWheelsStateValues() {
		vehicleTypesWheel.setCurrentItem(wheelState.getVehiclesType());
		vehicleNumbersWheel.setCurrentItem(wheelState.getVehiclesNumber());
		vehicleDirectionsWheel
				.setCurrentItem(wheelState.getVehiclesDirection());
		vehicleStationsWheel.setCurrentItem(wheelState.getStationsNumbers());
	}

	/**
	 * Update the vehicles wheel type
	 */
	private void updateVehicleWheelTypes() {

		getVehicleTypes();

		VehiclesAdapter vehiclesAdapter = new VehiclesAdapter(context,
				vehicleTypes);
		vehiclesAdapter.setTextSize(18);
		vehicleTypesWheel.setViewAdapter(vehiclesAdapter);
	}

	/**
	 * Update the vehicles wheel numbers
	 */
	private void updateVehicleWheelNumbers() {

		VehicleTypeEnum vehicleType = getCurrentVehicleType();
		getVehicleNumbers(vehicleType);

		String[] vehicleNumbersArray = getVehicleNumberArray(vehicleType);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				vehicleNumbersArray);
		adapter.setTextSize(18);
		vehicleNumbersWheel.setViewAdapter(adapter);

		switch (vehicleType) {
		case METRO:
			vehicleNumbersWheel.setCyclic(false);
			break;
		default:
			vehicleNumbersWheel.setCyclic(true);
			break;
		}

		vehicleNumbersWheel.setCurrentItem(0);
	}

	/**
	 * Get the text that the WheelView will show for each station
	 * 
	 * @return the name (title) of the station
	 */
	/**
	 * Get the number of the vehicle
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @return the number
	 */
	private String[] getVehicleNumberArray(VehicleTypeEnum vehicleType) {

		String[] vehicleNumbersArray;
		switch (vehicleType) {
		case METRO:
		case METRO1:
		case METRO2:
			vehicleNumbersArray = new String[] { getString(R.string.droid_trans_type_metro_line_1) };
			break;
		default:
			vehicleNumbersArray = new String[vehicleNumbers.size()];
			vehicleNumbersArray = vehicleNumbers.toArray(vehicleNumbersArray);
			break;
		}

		return vehicleNumbersArray;
	}

	/**
	 * Update the vehicles wheel numbers
	 */
	private void updateVehicleWheelDirections() {

		VehicleTypeEnum vehicleType = getCurrentVehicleType();
		String vehicleNumber = getCurrentVehicleNumber();
		getVehicleDirections(vehicleType, vehicleNumber);

		String[] vehicleDirectionsArray = new String[vehicleDirections.size()];
		vehicleDirectionsArray = vehicleDirections
				.toArray(vehicleDirectionsArray);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				vehicleDirectionsArray);
		adapter.setTextSize(12);
		vehicleDirectionsWheel.setViewAdapter(adapter);
		vehicleDirectionsWheel.setCurrentItem(0);
	}

	/**
	 * Update the stations numbers
	 */
	private void updateVehicleWheelStations() {

		VehicleTypeEnum vehicleType = getCurrentVehicleType();
		String vehicleNumber = getCurrentVehicleNumber();
		Integer vehicleDirection = getCurrentVehicleDirection();
		getStationsList(vehicleType, vehicleNumber, vehicleDirection);

		String[] vehicleStationsArray = getStationsNameArray();
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				vehicleStationsArray);
		adapter.setTextSize(13);
		vehicleStationsWheel.setViewAdapter(adapter);
		vehicleStationsWheel.setCurrentItem(0);
	}

	/**
	 * Retrieve the schedule of the vehicle
	 */
	private void retrieveVehicleSchedule() {

		vehicleSchedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ProgressDialog progressDialog = new ProgressDialog(context);

				VehicleTypeEnum vehicleType = getCurrentVehicleType();
				StationEntity station = getCurrentStation();

				switch (vehicleType) {
				case METRO:
					progressDialog.setMessage(Html.fromHtml(String.format(
							getString(R.string.metro_loading_schedule),
							station.getName(), station.getNumber())));
					RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
							context, progressDialog, station);
					retrieveMetroSchedule.execute();
					break;
				default:
					RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
							context, this, station,
							HtmlRequestCodesEnum.SINGLE_RESULT);
					retrieveVirtualBoards.getSumcInformation();
					break;
				}
			}
		});
	}

	/**
	 * Get the current vehicle type
	 * 
	 * @return the current vehicle type
	 */
	private VehicleTypeEnum getCurrentVehicleType() {
		return vehicleTypes.get(vehicleTypesWheel.getCurrentItem());
	}

	/**
	 * Get the current vehicle number
	 * 
	 * @return the current vehicle number
	 */
	private String getCurrentVehicleNumber() {
		return vehicleNumbers.get(vehicleNumbersWheel.getCurrentItem());
	}

	/**
	 * Get the current vehicle direction
	 * 
	 * @return the current vehicle direction
	 */
	private Integer getCurrentVehicleDirection() {
		return vehicleDirectionsWheel.getCurrentItem() + 1;
	}

	/**
	 * Get the current stations number
	 * 
	 * @return the current stations number
	 */
	private StationEntity getCurrentStation() {
		return vehicleStations.get(vehicleStationsWheel.getCurrentItem());
	}

	/**
	 * Get the text that the WheelView will show for each station
	 * 
	 * @return the name (title) of the station
	 */
	private String[] getStationsNameArray() {

		String[] stationName = new String[vehicleStations.size()];

		for (int i = 0; i < vehicleStations.size(); i++) {
			StationEntity station = vehicleStations.get(i);
			stationName[i] = String.format("%s (%s)", station.getName(),
					station.getNumber());
		}

		return stationName;
	}

	/**
	 * Initialize the navigation drawer
	 */
	private void initNavigationDrawer() {

		actionBar = getSupportActionBar();
		actionBar.setTitle(getString(R.string.app_sofbus24));

		// Enable ActionBar app icon to behave as action to toggle nav
		// drawerActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// Generate the titles of each row
		navigationItems = Utils.initNavigationDrawerItems(context);

		// Locate the DrawerLayout in the layout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer_shadow,
				GravityCompat.START);

		// Locate ListView in the layout
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer_listview);
		mMenuAdapter = new NavDrawerArrayAdapter(context, navigationItems);
		mDrawerList.setAdapter(mMenuAdapter);
		mDrawerList.setOnItemClickListener(new NavDrawerHelper(context,
				mDrawerLayout, mDrawerList, navigationItems)
				.getDrawerItemClickListener());

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(context, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_navigation_drawer_open,
				R.string.app_navigation_drawer_close) {

			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				mMenuAdapter.notifyDataSetChanged();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/**
	 * Adapter for countries
	 */
	private class VehiclesAdapter extends AbstractWheelTextAdapter {

		private ArrayList<VehicleTypeEnum> vehicleTypes;
		private ArrayList<Integer> vehicleImages;

		/**
		 * Constructor
		 */
		protected VehiclesAdapter(Activity context,
				ArrayList<VehicleTypeEnum> vehicleTypes) {

			super(context, R.layout.activity_droidtrans_list_item, NO_RESOURCE);

			this.vehicleTypes = new ArrayList<VehicleTypeEnum>(
					new LinkedHashSet<VehicleTypeEnum>(vehicleTypes));
			this.vehicleImages = getVehicleImages();

			setItemTextResource(R.id.droidtrans_vehicle_type_text);
		}

		private ArrayList<Integer> getVehicleImages() {

			ArrayList<Integer> vehicleImages = new ArrayList<Integer>();

			for (int i = 0; i < vehicleTypes.size(); i++) {
				switch (i) {
				case 0:
					vehicleImages.add(R.drawable.ic_menu_map_traffic);
					break;
				case 1:
					vehicleImages.add(R.drawable.ic_menu_map_traffic);
					break;
				case 2:
					vehicleImages.add(R.drawable.ic_menu_map_traffic);
					break;
				default:
					vehicleImages.add(R.drawable.ic_menu_map_traffic);
					break;
				}
			}

			return vehicleImages;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {

			View view = super.getItem(index, cachedView, parent);
			ImageView img = (ImageView) view
					.findViewById(R.id.droidtrans_vehicle_type_img);
			img.setImageResource(vehicleImages.get(index));

			return view;
		}

		@Override
		public int getItemsCount() {
			return vehicleTypes.size();
		}

		@Override
		protected CharSequence getItemText(int index) {

			CharSequence vehicleType;
			switch (vehicleTypes.get(index)) {
			case BUS:
				vehicleType = getString(R.string.droid_trans_type_bus);
				break;
			case TROLLEY:
				vehicleType = getString(R.string.droid_trans_type_trolley);
				break;
			case TRAM:
				vehicleType = getString(R.string.droid_trans_type_tram);
				break;
			default:
				vehicleType = getString(R.string.droid_trans_type_metro);
				break;
			}

			return vehicleType;
		}
	}

	/**
	 * Initialize a location listener to find the closest location
	 */
	private void initLocationListener() {

		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

		// Check if there is any location manager available
		if (locationManager != null) {

			// Check if the GPS provider is ebabled
			if (locationManager.isProviderEnabled(GPS_PROVIDER)) {

				// Request location updates from the available provider
				locationManager.requestLocationUpdates(GPS_PROVIDER,
						MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);

				// Start a new thread - just to wait 1 second and after that
				// proceed with the LastKnownLocation
				Handler handler = new Handler();
				Runnable myrunnable = new Runnable() {
					public void run() {
						try {
							Location location = locationManager
									.getLastKnownLocation(GPS_PROVIDER);

							if (location != null) {
								setVehicleWheelsState(location);
							}
						} catch (Exception e) {
						}
					}
				};

				handler.postDelayed(myrunnable, WAIT_TIME);
			}

			// Wait 500 ms and if no location is found activate the NETWORK
			// provider to listen for new updates
			Handler handler = new Handler();
			Runnable myrunnable = new Runnable() {
				public void run() {
					try {

						// Check if the NETWORK provider is enabled
						if (locationManager.isProviderEnabled(NETWORK_PROVIDER)) {

							// Remove the GPS listener
							removeLocationListener();

							// Request location updates from the available
							// provider
							locationManager.requestLocationUpdates(
									NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE,
									MIN_DISTANCE_FOR_UPDATE, DroidTrans.this);

							// Start a new thread - just to wait 1 second and
							// after that proceed with the LastKnownLocation
							Handler handler = new Handler();
							Runnable myrunnable = new Runnable() {
								public void run() {
									try {
										Location location = locationManager
												.getLastKnownLocation(NETWORK_PROVIDER);

										if (location != null) {
											setVehicleWheelsState(location);
										}
									} catch (Exception e) {
									}
								}
							};

							handler.postDelayed(myrunnable, WAIT_TIME);
						}
					} catch (Exception e) {
					}
				}
			};

			handler.postDelayed(myrunnable, WAIT_TIME);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		setVehicleWheelsState(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * Remove the location listener
	 */
	private void removeLocationListener() {

		if (locationManager != null) {
			try {
				locationManager.removeUpdates(this);
			} catch (Exception e) {
			}
		}
	}

}