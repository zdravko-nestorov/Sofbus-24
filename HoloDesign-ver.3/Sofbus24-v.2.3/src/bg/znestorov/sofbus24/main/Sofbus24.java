package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.closest.stations.map.RetrieveCurrentLocation;
import bg.znestorov.sofbus24.closest.stations.map.RetrieveCurrentLocationTimeout;
import bg.znestorov.sofbus24.databases.Sofbus24DatabaseUtils;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HomeTabEntity;
import bg.znestorov.sofbus24.favorites.FavouritesStationFragment;
import bg.znestorov.sofbus24.metro.MetroFragment;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.navigation.NavDrawerArrayAdapter;
import bg.znestorov.sofbus24.navigation.NavDrawerHomeScreenPreferences;
import bg.znestorov.sofbus24.schedule.ScheduleFragment;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.GooglePlayServicesErrorDialog;
import bg.znestorov.sofbus24.virtualboards.VirtualBoardsFragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Sofbus24 extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	private FragmentActivity context;
	private GlobalEntity globalContext;
	private ActionBar actionBar;

	private ViewPager mViewPager;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerArrayAdapter mMenuAdapter;
	private CharSequence mTitle;
	private ArrayList<String> navigationItems;

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
		ViewPager sofbusViewPager = (ViewPager) findViewById(R.id.sofbus24_pager);
		ProgressBar sofbusLoading = (ProgressBar) findViewById(R.id.sofbus24_loading);
		ImageButton sofbusRetry = (ImageButton) findViewById(R.id.sofbus24_retry);

		if (savedInstanceState == null) {
			// Check for updates (only when the application is started for the
			// first time)
			Utils.checkForUpdate(context);

			// Creates the configuration file
			Configuration.createConfiguration(context);

			// Retrieve the information from the DB and set up the layout fields
			CreateDatabases createDatabases = new CreateDatabases(context,
					sofbusViewPager, sofbusLoading, sofbusRetry);
			createDatabases.execute();
		} else {
			actionsAfterAsyncFinish(sofbusViewPager, sofbusLoading, sofbusRetry);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		actionsOverHomeScreen(-1);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mViewPager != null) {
			int currentTab = mViewPager.getCurrentItem();
			Fragment currentFragment = fragmentsList.get(currentTab);

			MenuItem favouritesSort = menu
					.findItem(R.id.action_favourites_sort);
			MenuItem favouritesRemoveAll = menu
					.findItem(R.id.action_favourites_remove_all);
			MenuItem metroMapRoute = menu.findItem(R.id.action_metro_map_route);

			if (currentFragment instanceof FavouritesStationFragment) {
				favouritesSort.setVisible(true);
				favouritesRemoveAll.setVisible(true);
				metroMapRoute.setVisible(false);
			} else if (currentFragment instanceof MetroFragment) {
				favouritesSort.setVisible(false);
				favouritesRemoveAll.setVisible(false);
				metroMapRoute.setVisible(true);
			} else {
				favouritesSort.setVisible(false);
				favouritesRemoveAll.setVisible(false);
				metroMapRoute.setVisible(false);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getSupportMenuInflater()
				.inflate(R.menu.activity_sofbus24_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		actionsOverHomeScreen(tab.getPosition());
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onBackPressed() {
		ActivityUtils.closeApplication(context);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ProgressDialog progressDialog = new ProgressDialog(context);

		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}

			return true;
		case R.id.action_closest_stations_map:
			startClosestStationsMap(progressDialog, false);

			return true;
		case R.id.action_edit_tabs:
			Intent editTabsIntent;
			if (globalContext.isPhoneDevice()) {
				editTabsIntent = new Intent(context, EditTabs.class);
			} else {
				editTabsIntent = new Intent(context, EditTabsDialog.class);
			}

			startActivity(editTabsIntent);

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

		// Get the Title
		mTitle = getTitle();

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

	private ArrayList<String> initNavigationDrawerItems() {

		ArrayList<String> navigationItems = new ArrayList<String>();

		navigationItems.add(getString(R.string.navigation_drawer_home));
		navigationItems
				.add(getString(R.string.navigation_drawer_home_standard));
		navigationItems.add(getString(R.string.navigation_drawer_home_map));
		navigationItems.add(getString(R.string.navigation_drawer_home_cars));
		navigationItems.add(getString(R.string.navigation_drawer_history));
		navigationItems.add(getString(R.string.navigation_drawer_cs));
		navigationItems.add(getString(R.string.navigation_drawer_cs_map));
		navigationItems.add(getString(R.string.navigation_drawer_cs_list));
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
				mViewPager.setCurrentItem(0);
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
			Intent historyIntent;
			if (globalContext.isPhoneDevice()) {
				historyIntent = new Intent(context, History.class);
			} else {
				historyIntent = new Intent(context, HistoryDialog.class);
			}
			startActivity(historyIntent);
			break;
		case 5:
			// TODO Set an action
			break;
		case 6:
			startClosestStationsMap(progressDialog, false);
			break;
		case 7:
			startClosestStationsList(progressDialog);
			break;
		case 8:
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
		case 9:
			Intent aboutIntent;
			if (globalContext.isPhoneDevice()) {
				aboutIntent = new Intent(context, About.class);
			} else {
				aboutIntent = new Intent(context, AboutDialog.class);
			}
			startActivity(aboutIntent);
			break;
		case 10:
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

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * Initialize the layout fields (ActionBar, ViewPager and
	 * SectionsPagerAdapter)
	 */
	private void initLayoutFields() {

		// Set the tabs to the ActionBar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the fragments list
		createFragmentsList();

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mViewPager = (ViewPager) findViewById(R.id.sofbus24_pager);
		mViewPager
				.setOffscreenPageLimit(Constants.GLOBAL_PARAM_HOME_TABS_COUNT - 1);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar
		initTabs();
	}

	/**
	 * For each of the sections in the app, add a tab to the action bar
	 */
	private void initTabs() {
		if (actionBar.getTabCount() > 0) {
			actionBar.removeAllTabs();
		}

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getPageIcon(i))
					.setTabListener(this));
		}
	}

	/**
	 * Create or rearrange (if already created) the FragmentsList, using the
	 * current application config file
	 */
	private void createFragmentsList() {
		// Get the application cofig file
		ConfigEntity config = new ConfigEntity(context);

		// Emtpy the fragmentsList if contains any elements
		if (!fragmentsList.isEmpty()) {
			fragmentsList.clear();
		}

		// Create a new ordered list with fragments (according to the
		// configuration file)
		for (int i = 0; i < Constants.GLOBAL_PARAM_HOME_TABS_COUNT; i++) {
			HomeTabEntity homeTab = config.getTabByPosition(context, i);
			if (homeTab.isTabVisible()) {
				fragmentsList.add(getFragmentByTagName(homeTab));
			}
		}
	}

	/**
	 * Get the fragment according to the given HomeTab
	 * 
	 * @param homeTab
	 *            HomeTab object pointing which fragment to be choosen
	 * @return the fragment associated to the given HomeTab
	 */
	private Fragment getFragmentByTagName(HomeTabEntity homeTab) {
		Fragment fragment;

		String homeTabName = homeTab.getTabName();
		if (homeTabName.equals(getString(R.string.edit_tabs_favourites))) {
			fragment = new FavouritesStationFragment();
		} else if (homeTabName.equals(getString(R.string.edit_tabs_search))) {
			fragment = new VirtualBoardsFragment();
		} else if (homeTabName.equals(getString(R.string.edit_tabs_schedule))) {
			fragment = new ScheduleFragment();
		} else {
			fragment = new MetroFragment();
		}

		return fragment;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentsList.get(position);
		}

		/**
		 * Purpose of this method is to check whether an item in the adapter
		 * still exists in the dataset and where it should show. For each entry
		 * in dataset, request its Fragment.
		 * 
		 * If the Fragment is found, return its (new) position. There's no need
		 * to return POSITION_UNCHANGED; ViewPager handles it.
		 * 
		 * If the Fragment passed to this method is not found, remove all
		 * references and let the ViewPager remove it from display by by
		 * returning POSITION_NONE;
		 */
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

		public Integer getPageIcon(int position) {
			return getPageIconByTagName(fragmentsList.get(position));
		}

		/**
		 * Get the current item page icon according to the fragment type
		 * 
		 * @param fragment
		 *            the fragment set on this tab
		 * @return the icon associated to the given fragment
		 */
		private int getPageIconByTagName(Fragment fragment) {
			int pageIcon;

			if (fragment instanceof FavouritesStationFragment) {
				pageIcon = R.drawable.ic_tab_favorites;
			} else if (fragment instanceof VirtualBoardsFragment) {
				pageIcon = R.drawable.ic_tab_real_time;
			} else if (fragment instanceof ScheduleFragment) {
				pageIcon = R.drawable.ic_tab_schedule;
			} else {
				pageIcon = R.drawable.ic_tab_metro;
			}

			return pageIcon;
		}
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
		private ViewPager sofbusViewPager;
		private ProgressBar sofbusLoading;
		private ImageButton sofbusRetry;

		public CreateDatabases(Activity context, ViewPager sofbusViewPager,
				ProgressBar sofbusLoading, ImageButton sofbusRetry) {
			this.context = context;
			this.sofbusViewPager = sofbusViewPager;
			this.sofbusLoading = sofbusLoading;
			this.sofbusRetry = sofbusRetry;
		}

		@Override
		protected void onPreExecute() {
			actionsOnPreExecute(sofbusViewPager, sofbusLoading, sofbusRetry);
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
			startHomeScreen();

			LoadStartingData loadStartingData = new LoadStartingData(context,
					sofbusViewPager, sofbusLoading, sofbusRetry);
			loadStartingData.execute();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			// Actions over the LayoutFields (which to be shown and visible)
			actionsOnCancelled(sofbusViewPager, sofbusLoading, sofbusRetry);

			// Set onClickListener to the retry button
			sofbusRetry.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CreateDatabases createDatabases = new CreateDatabases(
							context, sofbusViewPager, sofbusLoading,
							sofbusRetry);
					createDatabases.execute();
				}
			});
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
		private ViewPager sofbusViewPager;
		private ProgressBar sofbusLoading;
		private ImageButton sofbusRetry;

		public LoadStartingData(Activity context, ViewPager sofbusViewPager,
				ProgressBar sofbusLoading, ImageButton sofbusRetry) {
			this.context = context;
			this.sofbusViewPager = sofbusViewPager;
			this.sofbusLoading = sofbusLoading;
			this.sofbusRetry = sofbusRetry;
		}

		@Override
		protected void onPreExecute() {
			actionsOnPreExecute(sofbusViewPager, sofbusLoading, sofbusRetry);
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
			actionsAfterAsyncFinish(sofbusViewPager, sofbusLoading, sofbusRetry);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			// Actions over the LayoutFields (which to be shown and visible)
			actionsOnCancelled(sofbusViewPager, sofbusLoading, sofbusRetry);

			// Set onClickListener to the retry button
			sofbusRetry.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LoadStartingData loadStartingData = new LoadStartingData(
							context, sofbusViewPager, sofbusLoading,
							sofbusRetry);
					loadStartingData.execute();
				}
			});
		}
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onPreExecute method of the AsyncTask
	 * 
	 * @param sofbusViewPager
	 *            the ViewPager of the Layout
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 * @param sofbusRetry
	 *            the ImageButton of the Layout
	 */
	private void actionsOnPreExecute(ViewPager sofbusViewPager,
			ProgressBar sofbusLoading, ImageButton sofbusRetry) {
		sofbusViewPager.setVisibility(View.GONE);
		sofbusLoading.setVisibility(View.VISIBLE);
		sofbusRetry.setVisibility(View.GONE);
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onPostExecute method of the AsyncTask
	 * 
	 * @param sofbusViewPager
	 *            the ViewPager of the Layout
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 * @param sofbusRetry
	 *            the ImageButton of the Layout
	 */
	private void actionsOnPostExecute(ViewPager sofbusViewPager,
			ProgressBar sofbusLoading, ImageButton sofbusRetry) {
		sofbusViewPager.setVisibility(View.VISIBLE);
		sofbusLoading.setVisibility(View.GONE);
		sofbusRetry.setVisibility(View.GONE);
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onCancelled method of the AsyncTask
	 * 
	 * @param sofbusViewPager
	 *            the ViewPager of the Layout
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 * @param sofbusRetry
	 *            the ImageButton of the Layout
	 */
	private void actionsOnCancelled(ViewPager sofbusViewPager,
			ProgressBar sofbusLoading, ImageButton sofbusRetry) {
		sofbusViewPager.setVisibility(View.GONE);
		sofbusLoading.setVisibility(View.GONE);
		sofbusRetry.setVisibility(View.VISIBLE);
	}

	private void startHomeScreen() {

		int userHomeScreenChoice = NavDrawerHomeScreenPreferences
				.getUserHomeScreenChoice(context);
		switch (userHomeScreenChoice) {
		case 1:
			startClosestStationsMap(new ProgressDialog(context), true);
			break;
		case 2:
			// TODO: Start the DroidTrans
			break;
		}
	}

	/**
	 * Actions after the AsyncTask is finished
	 * 
	 * @param sofbusViewPager
	 *            the ViewPager of the Layout
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 * @param sofbusRetry
	 *            the ImageButton of the Layout
	 */
	private void actionsAfterAsyncFinish(ViewPager sofbusViewPager,
			ProgressBar sofbusLoading, ImageButton sofbusRetry) {
		actionsOnPostExecute(sofbusViewPager, sofbusLoading, sofbusRetry);
		initLayoutFields();
	}

	/**
	 * Proceed with updating the HomeScreen if needed
	 * 
	 * @param tabPosition
	 *            the tabPosition that is pressed or "-1" in case of onResume
	 */
	private void actionsOverHomeScreen(int tabPosition) {

		// Check if this is called from "onResume(...)", so take the current
		// active tab or from "onTabSelected(...)", so set the according menu
		// items
		if (tabPosition == -1) {
			// Check if the activity has to be restarted
			if (globalContext.isHasToRestart()) {
				ActivityUtils.restartApplication(context);

				return;
			}

			// Check if the ordering and visibility of the tabs should be
			// changed
			if (globalContext.isHomeScreenChanged()) {
				// Rearrange the fragmentsList
				createFragmentsList();

				// Notify the adapter for the changes in the
				// fragmentsList
				mSectionsPagerAdapter.notifyDataSetChanged();

				// For each of the sections in the application, add a
				// tab to the ActionBar
				initTabs();

				// Show a message that the home screen is changed
				Toast.makeText(context, getString(R.string.edit_tabs_toast),
						Toast.LENGTH_SHORT).show();

				// Reset to default
				globalContext.setHomeScreenChanged(false);

				return;
			}

			// Check if the view pager is already created (if the application
			// has just started)
			if (mViewPager != null) {
				tabPosition = mViewPager.getCurrentItem();
			}
		} else {
			// Declare that the options menu has changed, so should be recreated
			// (make the system calls the method onPrepareOptionsMenu)
			supportInvalidateOptionsMenu();

			// When the given tab is selected, switch to the corresponding page
			// in the ViewPager.
			mViewPager.setCurrentItem(tabPosition);
		}

		// Get the Fragment from the fragmentsList (used to check what type is
		// the current fragment. It doesn't store the real fragment - it will be
		// taken from the FragmentManager)
		if (mViewPager != null) {
			Fragment fakeFragment = fragmentsList.get(tabPosition);
			if (fakeFragment instanceof FavouritesStationFragment) {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_favourites),
						getString(R.string.edit_tabs_favourites_pre_honeycomb));
			} else if (fakeFragment instanceof VirtualBoardsFragment) {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_search),
						getString(R.string.edit_tabs_search_pre_honeycomb));
			} else if (fakeFragment instanceof ScheduleFragment) {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_schedule),
						getString(R.string.edit_tabs_schedule_pre_honeycomb));
			} else {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_metro),
						getString(R.string.edit_tabs_metro_pre_honeycomb));
			}

			// Check if the FragmentManager is created and proceed with actions
			// for each fragment (updates)
			if (getSupportFragmentManager().getFragments() != null) {
				List<Fragment> fmFragmentsList = getSupportFragmentManager()
						.getFragments();

				if (fakeFragment instanceof FavouritesStationFragment
						&& globalContext.isFavouritesChanged()) {

					// Match the fake fragment from the fragmentsList with the
					// one from the FragmentManager
					for (Fragment fragment : fmFragmentsList) {
						if (fragment instanceof FavouritesStationFragment) {
							((FavouritesStationFragment) fragment)
									.onResumeFragment(context);
							globalContext.setFavouritesChanged(false);
						}
					}
				}

				if (fakeFragment instanceof VirtualBoardsFragment
						&& globalContext.isVbChanged()) {

					// Match the fake fragment from the fragmentsList with the
					// one from the FragmentManager
					for (Fragment fragment : fmFragmentsList) {
						if (fragment instanceof VirtualBoardsFragment) {
							((VirtualBoardsFragment) fragment)
									.onResumeFragment(context);
							globalContext.setVbChanged(false);
						}
					}
				}
			}
		}
	}
}
