package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import bg.znestorov.sofbus24.databases.DroidTransDataSource;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.navigation.NavDrawerArrayAdapter;
import bg.znestorov.sofbus24.navigation.NavDrawerHelper;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DroidTrans extends SherlockFragmentActivity {

	private FragmentActivity context;
	private DroidTransDataSource droidtransDatasource;

	private ActionBar actionBar;
	private boolean isDroidTransHomeScreen;

	private WheelView vehicleTypesWheel;
	private WheelView vehicleNumbersWheel;
	private WheelView vehicleDirectionsWheel;
	private WheelView vehicleStationsWheel;
	private boolean scrolling = false;

	private ArrayList<VehicleTypeEnum> vehicleTypes;
	private ArrayList<String> vehicleNumbers;
	private ArrayList<String> vehicleDirections;
	private ArrayList<StationEntity> vehicleStations;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerArrayAdapter mMenuAdapter;
	private ArrayList<String> navigationItems;

	public static final String BUNDLE_IS_DROID_TRANS_HOME_SCREEN = "IS DROID TRANS HOME SCREEN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_droidtrans);

		// Get the current activity context and check if this activity is the
		// home screen
		context = DroidTrans.this;
		droidtransDatasource = new DroidTransDataSource(context);
		isDroidTransHomeScreen = getIntent().getExtras() != null ? getIntent()
				.getExtras().getBoolean(BUNDLE_IS_DROID_TRANS_HOME_SCREEN,
						false) : false;

		initActionBar();
		initLayoutFields();

		if (isDroidTransHomeScreen) {
			initNavigationDrawer();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(Sofbus24.RESULT_CODE_ACTIVITY_FINISH, new Intent());
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_droidtrans_actions,
				menu);

		return super.onCreateOptionsMenu(menu);
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
		droidtransDatasource.open();
		vehicleStations = droidtransDatasource.getVehicleStations(vehicleType,
				vehicleNumber, vehicleDirection);
		droidtransDatasource.close();
	}

	/**
	 * Initialize the layout fields
	 */
	private void initLayoutFields() {

		getVehicleTypes();
		vehicleTypesWheel = (WheelView) findViewById(R.id.droidtrans_vehicle_types);
		vehicleTypesWheel.setVisibleItems(4);
		vehicleTypesWheel.setViewAdapter(new VehiclesAdapter(context,
				vehicleTypes));

		vehicleNumbersWheel = (WheelView) findViewById(R.id.droidtrans_vehicle_numbers);
		vehicleNumbersWheel.setVisibleItems(5);

		vehicleDirectionsWheel = (WheelView) findViewById(R.id.droidtrans_directions);
		vehicleDirectionsWheel.setVisibleItems(2);

		vehicleStationsWheel = (WheelView) findViewById(R.id.droidtrans_stations);
		vehicleDirectionsWheel.setVisibleItems(5);

		updateVehicleWheels();
		setVehicleWheelsListeners();
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
	 * Update the vehicles wheel numbers
	 */
	private void updateVehicleWheelNumbers() {

		VehicleTypeEnum vehicleType = vehicleTypes.get(vehicleTypesWheel
				.getCurrentItem());
		getVehicleNumbers(vehicleType);

		String[] vehicleNumbersArray = getVehicleNumberArray(vehicleType);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				vehicleNumbersArray);
		adapter.setTextSize(13);
		vehicleNumbersWheel.setViewAdapter(adapter);
		vehicleNumbersWheel.setCurrentItem(0);
		vehicleNumbersWheel.setCurrentItem(vehicleNumbersArray.length / 2);

		switch (vehicleType) {
		case METRO:
			vehicleNumbersWheel.setCyclic(false);
			break;
		default:
			vehicleNumbersWheel.setCyclic(true);
			break;
		}
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

		VehicleTypeEnum vehicleType = vehicleTypes.get(vehicleTypesWheel
				.getCurrentItem());
		String vehicleNumber = vehicleNumbers.get(vehicleNumbersWheel
				.getCurrentItem());
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

		VehicleTypeEnum vehicleType = vehicleTypes.get(vehicleTypesWheel
				.getCurrentItem());
		String vehicleNumber = vehicleNumbers.get(vehicleNumbersWheel
				.getCurrentItem());
		Integer vehicleDirection = vehicleDirectionsWheel.getCurrentItem() + 1;
		getStationsList(vehicleType, vehicleNumber, vehicleDirection);

		String[] vehicleStationsArray = getStationsNameArray();
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				vehicleStationsArray);
		adapter.setTextSize(13);
		vehicleStationsWheel.setViewAdapter(adapter);
		vehicleStationsWheel.setCurrentItem(0);
		vehicleStationsWheel.setCurrentItem(vehicleStationsArray.length / 2);
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
		mDrawerList.setOnItemClickListener(new NavDrawerHelper(context, null,
				null, mDrawerLayout, mDrawerList, navigationItems)
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

}