package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.databases.Sofbus24DatabaseUtils;
import bg.znestorov.sofbus24.home.screen.Sofbus24Fragment;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.navigation.NavDrawerArrayAdapter;
import bg.znestorov.sofbus24.navigation.NavDrawerHelper;
import bg.znestorov.sofbus24.navigation.NavDrawerHomeScreenPreferences;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class Sofbus24 extends SherlockFragmentActivity {

	private FragmentActivity context;
	private ActionBar actionBar;
	private Bundle savedInstanceState;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerArrayAdapter mMenuAdapter;
	private ArrayList<String> navigationItems;

	public static final int REQUEST_CODE_SOFBUS_24 = 0;
	public static final int RESULT_CODE_ACTIVITY_NEW = 1;
	public static final int RESULT_CODE_ACTIVITY_FINISH = 2;
	public static final int RESULT_CODE_STANDARD_HOME_FRAGMENT = 3;

	private static final String TAG_SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT = "SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_sofbus24);

		// Get the application and curren context;
		context = Sofbus24.this;
		this.savedInstanceState = savedInstanceState;

		// Get the fields in the layout
		ProgressBar sofbusLoading = (ProgressBar) findViewById(R.id.sofbus24_loading);

		// Initialize the ActionBar and the NavigationDrawer
		initNavigationDrawer(sofbusLoading);

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
			actionsOnPostExecute(sofbusLoading);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		ProgressBar sofbusLoading = (ProgressBar) findViewById(R.id.sofbus24_loading);

		if (requestCode == REQUEST_CODE_SOFBUS_24) {
			switch (resultCode) {
			case RESULT_CODE_ACTIVITY_NEW:
				startHomeScreen(savedInstanceState, sofbusLoading);
				break;
			case RESULT_CODE_ACTIVITY_FINISH:
				finish();
				break;
			}
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
	 * 
	 * @param sofbusLoading
	 *            the loading of the Sofbus24 startup
	 */
	private void initNavigationDrawer(ProgressBar sofbusLoading) {

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
				savedInstanceState, sofbusLoading, mDrawerLayout, mDrawerList,
				navigationItems).getDrawerItemClickListener());

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
	 * Start the standard home screen fragment
	 */
	private void startStandardHomeScreen() {
		Sofbus24Fragment sofbus24Fragment;

		if (savedInstanceState == null) {
			sofbus24Fragment = new Sofbus24Fragment();
		} else {
			sofbus24Fragment = (Sofbus24Fragment) getSupportFragmentManager()
					.findFragmentByTag(
							TAG_SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT);

			if (sofbus24Fragment == null) {
				sofbus24Fragment = new Sofbus24Fragment();
			}
		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.sofbus24_home_screen_standard, sofbus24Fragment,
						TAG_SOFBUS_24_STANDARD_HOME_SCREEN_FRAGMENT)
				.addToBackStack(null).commit();
	}

	/**
	 * Start the DroidTrans activity
	 */
	private void startDroidTrans() {
		Bundle bundle = new Bundle();
		bundle.putBoolean(DroidTrans.BUNDLE_IS_DROID_TRANS_HOME_SCREEN, true);

		Intent droidTransIntent = new Intent(context, DroidTrans.class);
		droidTransIntent.putExtras(bundle);
		context.startActivityForResult(droidTransIntent, REQUEST_CODE_SOFBUS_24);
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
	public void startHomeScreen(Bundle savedInstanceState,
			ProgressBar sofbusLoading) {

		actionsOnPostExecute(sofbusLoading);

		int userHomeScreenChoice = NavDrawerHomeScreenPreferences
				.getUserHomeScreenChoice(context);

		switch (userHomeScreenChoice) {
		case 0:
			startStandardHomeScreen();

			break;
		case 1:
			ActivityUtils.startClosestStationsMap(context,
					getSupportFragmentManager(), true);
			break;
		case 2:
			startDroidTrans();
			break;
		}
	}
}