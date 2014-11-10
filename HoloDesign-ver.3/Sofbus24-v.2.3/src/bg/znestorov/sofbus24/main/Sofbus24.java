package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.closest.stations.map.RetrieveCurrentLocation;
import bg.znestorov.sofbus24.closest.stations.map.RetrieveCurrentLocationTimeout;
import bg.znestorov.sofbus24.databases.Sofbus24DatabaseUtils;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.home.screen.Sofbus24Fragment;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.navigation.NavDrawerArrayAdapter;
import bg.znestorov.sofbus24.navigation.NavDrawerHomeScreenPreferences;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.GooglePlayServicesErrorDialog;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class Sofbus24 extends SherlockFragmentActivity {

	private FragmentActivity context;
	private GlobalEntity globalContext;
	private ActionBar actionBar;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerArrayAdapter mMenuAdapter;
	private ArrayList<String> navigationItems;

	private static final String TAG_SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT = "SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_sofbus24);

		// Get the application and curren context;
		globalContext = (GlobalEntity) getApplicationContext();
		context = Sofbus24.this;

		// Initialize the ActionBar and the NavigationDrawer
		initNavigationDrawer();

		// Get the fields in the layout
		ProgressBar sofbusLoading = (ProgressBar) findViewById(R.id.sofbus24_loading);

		if (savedInstanceState == null) {
			// Check for updates (only when the application is started for the
			// first time)
			Utils.checkForUpdate(context);

			// Creates the configuration file
			Configuration.createConfiguration(context);

			// Retrieve the information from the DB and set up the layout fields
			CreateDatabases createDatabases = new CreateDatabases(context,
					sofbusLoading);
			createDatabases.execute();
		} else {
			startHomeScreen(savedInstanceState, sofbusLoading);
		}
	}

	@Override
	public void onBackPressed() {
		ActivityUtils.closeApplication(context);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		navigationItems = initNavigationDrawerItems();

		// Locate the DrawerLayout in the layout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer_shadow,
				GravityCompat.START);

		// Locate ListView in the layout
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer_listview);
		mMenuAdapter = new NavDrawerArrayAdapter(context, navigationItems);
		mDrawerList.setAdapter(mMenuAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(context, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_navigation_drawer_open,
				R.string.app_navigation_drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Create a list with all items in the NavigationDrawer (each row of the
	 * menu)
	 * 
	 * @return an ArrayList with all raws of the menu
	 */
	private ArrayList<String> initNavigationDrawerItems() {

		ArrayList<String> navigationItems = new ArrayList<String>();

		navigationItems.add(getString(R.string.navigation_drawer_home));
		navigationItems
				.add(getString(R.string.navigation_drawer_home_standard));
		navigationItems.add(getString(R.string.navigation_drawer_home_map));
		navigationItems.add(getString(R.string.navigation_drawer_home_cars));
		navigationItems.add(getString(R.string.navigation_drawer_cs));
		navigationItems.add(getString(R.string.navigation_drawer_history));
		navigationItems.add(getString(R.string.navigation_drawer_options));
		navigationItems.add(getString(R.string.navigation_drawer_info));
		navigationItems.add(getString(R.string.navigation_drawer_exit));

		return navigationItems;
	}

	/**
	 * Class responsible for registring user clicks over the navigation drawer
	 */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/**
	 * Define the user actions on navigation drawer item click
	 * 
	 * @param position
	 *            the position of the click
	 */
	private void selectItem(int position) {

		ProgressDialog progressDialog = new ProgressDialog(context);
		int userHomeScreen = NavDrawerHomeScreenPreferences
				.getUserHomeScreenChoice(context);

		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);

		switch (position) {
		case 0:
			break;
		case 1:
			if (isHomeScreenChanged(userHomeScreen, position)) {
				// TODO Set the action to this item
			}

			break;
		case 2:
			if (isHomeScreenChanged(userHomeScreen, position)) {
				startClosestStationsMap(progressDialog, true);
			}

			break;
		case 3:
			if (isHomeScreenChanged(userHomeScreen, position)) {
				// TODO Set the action to this item
			}

			break;
		case 4:
			startClosestStationsList(progressDialog);
			break;
		case 5:
			Intent historyIntent;
			if (globalContext.isPhoneDevice()) {
				historyIntent = new Intent(context, History.class);
			} else {
				historyIntent = new Intent(context, HistoryDialog.class);
			}
			startActivity(historyIntent);
			break;
		case 6:
			Intent preferencesIntent;
			if (globalContext.isPhoneDevice()) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					preferencesIntent = new Intent(context, Preferences.class);
				} else {
					preferencesIntent = new Intent(context,
							PreferencesPreHoneycomb.class);
				}
			} else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					preferencesIntent = new Intent(context,
							PreferencesDialog.class);
				} else {
					preferencesIntent = new Intent(context,
							PreferencesPreHoneycombDialog.class);
				}
			}
			startActivity(preferencesIntent);
			break;
		case 7:
			Intent aboutIntent;
			if (globalContext.isPhoneDevice()) {
				aboutIntent = new Intent(context, About.class);
			} else {
				aboutIntent = new Intent(context, AboutDialog.class);
			}
			startActivity(aboutIntent);
			break;
		case 8:
			ActivityUtils.closeApplication(context);
			break;
		}
	}

	/**
	 * Show a long toast about the changed home screen and set the change in the
	 * preference file
	 * 
	 * @param userHomeScreen
	 *            the current home screen
	 * @param userChoice
	 *            the user choice
	 * 
	 * @return if the home screen can be changed
	 */
	private boolean isHomeScreenChanged(int userHomeScreen, int userChoice) {

		boolean isHomeScreenChanged = true;
		String homeScreenName = navigationItems.get(userChoice);

		if (userChoice == 2 && !globalContext.areServicesAvailable()) {
			ActivityUtils.showLongToast(context, String.format(
					getString(R.string.navigation_drawer_home_screen_error),
					homeScreenName), 6000, 1000);

			isHomeScreenChanged = false;
		} else {
			if (userHomeScreen == userChoice - 1) {
				ActivityUtils
						.showLongToast(
								context,
								String.format(
										getString(R.string.navigation_drawer_home_screen_remains),
										homeScreenName), 5000, 1000);
			} else {
				NavDrawerHomeScreenPreferences.setUserChoice(context,
						userChoice - 1);
				ActivityUtils
						.showLongToast(
								context,
								String.format(
										getString(R.string.navigation_drawer_home_screen_changed),
										homeScreenName), 5500, 1000);
			}
		}

		return isHomeScreenChanged;
	}

	/**
	 * Start the ClosestStationsMap activity
	 * 
	 * @param progressDialog
	 *            the progress dialog
	 * @param isDirectStart
	 *            check if we should check for the current location or just
	 *            start the map activity
	 */
	private void startClosestStationsMap(ProgressDialog progressDialog,
			boolean isDirectStart) {

		if (!globalContext.areServicesAvailable()) {
			GooglePlayServicesErrorDialog googlePlayServicesErrorDialog = new GooglePlayServicesErrorDialog();
			googlePlayServicesErrorDialog.show(getSupportFragmentManager(),
					"GooglePlayServicesErrorDialog");
		} else {
			if (isDirectStart) {
				Intent closestStationsMapIntent = new Intent(context,
						ClosestStationsMap.class);
				context.startActivity(closestStationsMapIntent);
			} else {
				progressDialog.setMessage(context
						.getString(R.string.cs_list_loading_current_location));

				RetrieveCurrentLocation retrieveCurrentLocation = new RetrieveCurrentLocation(
						context, false, progressDialog);
				retrieveCurrentLocation.execute();
				RetrieveCurrentLocationTimeout retrieveCurrentLocationTimeout = new RetrieveCurrentLocationTimeout(
						retrieveCurrentLocation);
				(new Thread(retrieveCurrentLocationTimeout)).start();
			}
		}
	}

	/**
	 * Start the ClosestStationsList activity
	 * 
	 * @param progressDialog
	 *            the progress dialog
	 */
	private void startClosestStationsList(ProgressDialog progressDialog) {
		progressDialog.setMessage(String
				.format(getString(R.string.cs_list_loading_current_location)));

		RetrieveCurrentLocation retrieveCurrentLocation = new RetrieveCurrentLocation(
				context, true, progressDialog);
		retrieveCurrentLocation.execute();
		RetrieveCurrentLocationTimeout retrieveCurrentLocationTimeout = new RetrieveCurrentLocationTimeout(
				retrieveCurrentLocation);
		(new Thread(retrieveCurrentLocationTimeout)).start();
	}

	/**
	 * Class responsible for async creation of the databases
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	public class CreateDatabases extends AsyncTask<Void, Void, Void> {

		private Activity context;
		private ProgressBar sofbusLoading;

		public CreateDatabases(Activity context, ProgressBar sofbusLoading) {
			this.context = context;
			this.sofbusLoading = sofbusLoading;
		}

		@Override
		protected void onPreExecute() {
			actionsOnPreExecute(sofbusLoading);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Create the database by copying it from the assets folder to the
			// internal memory
			Sofbus24DatabaseUtils.createSofbus24Database(context);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			LoadStartingData loadStartingData = new LoadStartingData(context,
					sofbusLoading);
			loadStartingData.execute();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			CreateDatabases createDatabases = new CreateDatabases(context,
					sofbusLoading);
			createDatabases.execute();
		}
	}

	/**
	 * Class responsible for AsyncLoad of the DB information
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	public class LoadStartingData extends AsyncTask<Void, Void, Void> {

		private Activity context;
		private ProgressBar sofbusLoading;

		public LoadStartingData(Activity context, ProgressBar sofbusLoading) {
			this.context = context;
			this.sofbusLoading = sofbusLoading;
		}

		@Override
		protected void onPreExecute() {
			actionsOnPreExecute(sofbusLoading);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Load all vehicles from the Database, so use them lately
			ScheduleLoadVehicles.getInstance(context);

			// Load all metro stations from the Database, so use them lately
			MetroLoadStations.getInstance(context);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			startHomeScreen(null, sofbusLoading);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			LoadStartingData loadStartingData = new LoadStartingData(context,
					sofbusLoading);
			loadStartingData.execute();
		}
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onPreExecute method of the AsyncTask
	 * 
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 */
	private void actionsOnPreExecute(ProgressBar sofbusLoading) {
		sofbusLoading.setVisibility(View.VISIBLE);
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onPostExecute method of the AsyncTask
	 * 
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 */
	private void actionsOnPostExecute(ProgressBar sofbusLoading) {
		sofbusLoading.setVisibility(View.GONE);
	}

	/**
	 * Actions after the AsyncTask is finished
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 */
	private void startHomeScreen(Bundle savedInstanceState,
			ProgressBar sofbusLoading) {

		actionsOnPostExecute(sofbusLoading);
		int userHomeScreenChoice = NavDrawerHomeScreenPreferences
				.getUserHomeScreenChoice(context);
		switch (userHomeScreenChoice) {
		case 0:
			Sofbus24Fragment sofbus24Fragment;

			if (savedInstanceState == null) {
				sofbus24Fragment = new Sofbus24Fragment();
			} else {
				sofbus24Fragment = (Sofbus24Fragment) getSupportFragmentManager()
						.findFragmentByTag(
								TAG_SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT);
			}

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.sofbus24_home_screen_standard,
							sofbus24Fragment,
							TAG_SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT)
					.addToBackStack(null).commit();

			break;
		case 1:
			startClosestStationsMap(new ProgressDialog(context), true);
			break;
		case 2:
			// TODO: Start the DroidTrans
			break;
		}
	}
}
