package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.databases.StationsDatabaseUtils;
import bg.znestorov.sofbus24.databases.VehiclesDatabaseUtils;
import bg.znestorov.sofbus24.entity.Config;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HomeTab;
import bg.znestorov.sofbus24.favorites.FavouritesStationFragment;
import bg.znestorov.sofbus24.metro.MetroFragment;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.schedule.ScheduleFragment;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.virtualboards.VirtualBoardsFragment;

public class Sofbus24 extends FragmentActivity implements ActionBar.TabListener {

	private Activity context;
	private GlobalEntity globalContext;
	private ActionBar actionBar;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();
	private FavouritesStationFragment favouritesFragment;
	private VirtualBoardsFragment virtualBoardsFragment;
	private ScheduleFragment scheduleFragment;
	private MetroFragment metroFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sofbus24);

		// Get the application and curren context;
		globalContext = (GlobalEntity) getApplicationContext();
		context = Sofbus24.this;

		// Get the fields in the layout
		ViewPager sofbusViewPager = (ViewPager) findViewById(R.id.sofbus24_pager);
		ProgressBar sofbusLoading = (ProgressBar) findViewById(R.id.sofbus24_loading);
		ImageButton sofbusRetry = (ImageButton) findViewById(R.id.sofbus24_retry);

		if (savedInstanceState == null) {
			// Retrieve the information from the DB and set up the layout fields
			CreateDatabases createDatabases = new CreateDatabases(context,
					sofbusViewPager, sofbusLoading, sofbusRetry);
			createDatabases.execute();
		} else {
			actionsOnPostExecute(sofbusViewPager, sofbusLoading, sofbusRetry);
			initLayoutFields();
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

			MenuItem favouritesRemoveAll = menu
					.findItem(R.id.action_favourites_remove_all);
			MenuItem metroMapRoute = menu.findItem(R.id.action_metro_map_route);

			if (currentFragment instanceof FavouritesStationFragment) {
				favouritesRemoveAll.setVisible(true);
				metroMapRoute.setVisible(false);
			} else if (currentFragment instanceof MetroFragment) {
				favouritesRemoveAll.setVisible(false);
				metroMapRoute.setVisible(true);
			} else {
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
		getMenuInflater().inflate(R.menu.activity_sofbus24_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		actionsOverHomeScreen(tab.getPosition());
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
		switch (item.getItemId()) {
		case R.id.action_recent_history:
			Intent historyIntent = new Intent(context, History.class);
			startActivity(historyIntent);
			return true;
		case R.id.action_closest_stations_map:
			// TODO: Set the event on clicking the button
			return true;
		case R.id.action_closest_stations_list:
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog
					.setMessage(String
							.format(getString(R.string.cs_list_loading_current_location)));

			ClosestStationsList closestStationsList = new ClosestStationsList();
			ClosestStationsList.RetrieveCurrentPosition retrieveCurrentPosition = closestStationsList.new RetrieveCurrentPosition(
					context, progressDialog);
			retrieveCurrentPosition.execute();
			return true;
		case R.id.action_settings:
			Intent preferencesIntent = new Intent(context, Preferences.class);
			startActivity(preferencesIntent);
			return true;
		case R.id.action_edit_tabs:
			Intent editTabsIntent = new Intent(context, EditTabs.class);
			startActivity(editTabsIntent);
			return true;
		case R.id.action_about:
			Intent aboutIntent = new Intent(context, About.class);
			startActivity(aboutIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the layout fields (ActionBar, ViewPager and
	 * SectionsPagerAdapter)
	 */
	private void initLayoutFields() {
		// Creates the configuration file
		Configuration.createConfiguration(context);

		// Initialize the UIL image loader
		ActivityUtils.initImageLoader(context);

		// Set up the ActionBar
		actionBar = getActionBar();
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
		Config config = new Config(context);

		// Create and assaign each fragment to a variable, so be used once the
		// Tabs ordering and visibility is changed
		createSofbus24Fragments();

		// Emtpy the fragmentsList if contains any elements
		if (!fragmentsList.isEmpty()) {
			fragmentsList.clear();
		}

		// Create a new ordered list with fragments (according to the
		// configuration file)
		for (int i = 0; i < Constants.GLOBAL_PARAM_HOME_TABS_COUNT; i++) {
			HomeTab homeTab = config.getTabByPosition(context, i);
			if (homeTab.isTabVisible()) {
				fragmentsList.add(getFragmentByTagName(homeTab));
			}
		}
	}

	/**
	 * Create (if not created already) and assaign each fragment to a variable,
	 * so be used once the Tabs ordering and visibility is changed
	 */
	private void createSofbus24Fragments() {
		if (favouritesFragment == null) {
			favouritesFragment = new FavouritesStationFragment();
		}

		if (virtualBoardsFragment == null) {
			virtualBoardsFragment = new VirtualBoardsFragment();
		}

		if (scheduleFragment == null) {
			scheduleFragment = new ScheduleFragment();
		}

		if (metroFragment == null) {
			metroFragment = new MetroFragment();
		}
	}

	/**
	 * Get the fragment according to the given HomeTab
	 * 
	 * @param homeTab
	 *            HomeTab object pointing which fragment to be choosen
	 * @return the fragment associated to the given HomeTab
	 */
	private Fragment getFragmentByTagName(HomeTab homeTab) {
		Fragment fragment;

		String homeTabName = homeTab.getTabName();
		if (homeTabName.equals(getString(R.string.edit_tabs_favourites))) {
			fragment = favouritesFragment;
		} else if (homeTabName.equals(getString(R.string.edit_tabs_search))) {
			fragment = virtualBoardsFragment;
		} else if (homeTabName.equals(getString(R.string.edit_tabs_schedule))) {
			fragment = scheduleFragment;
		} else {
			fragment = metroFragment;
		}

		return fragment;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
			// Creating and copying the DB to the SD card (used only for testing
			// purposes) - DO NOT UNCOMMENT
			// StationsDatabaseUtils.generateAndCopyStationsDB(context);
			// VehiclesDatabaseUtils.generateAndCopyStationsDB(context);

			// Create the database by copying it from the assets folder to the
			// internal memory
			StationsDatabaseUtils.createStationsDatabase(context);
			VehiclesDatabaseUtils.createVehiclesDatabase(context);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
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
			actionsOnPostExecute(sofbusViewPager, sofbusLoading, sofbusRetry);
			initLayoutFields();
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

	/**
	 * Proceed with updating the HomeScreen if needed
	 * 
	 * @param tabPosition
	 *            the tabPosition that is pressed or "-1" in case of onResume
	 */
	private void actionsOverHomeScreen(int tabPosition) {
		// Check if this is called from "onResume" or from "onTabSelected"
		if (tabPosition == -1) {
			if (globalContext.isHasToRestart()) {
				ActivityUtils.restartApplication(context);
			} else {
				if (mViewPager != null) {
					Fragment fragment = fragmentsList.get(mViewPager
							.getCurrentItem());

					// Check the fragment type and proceed accordingly
					if (fragment instanceof FavouritesStationFragment
							&& globalContext.isFavouritesChanged()) {
						((FavouritesStationFragment) fragment)
								.onResumeFragment(context);
						globalContext.setFavouritesChanged(false);
					}

					if (fragment instanceof VirtualBoardsFragment
							&& globalContext.isVbChanged()) {
						((VirtualBoardsFragment) fragment)
								.onResumeFragment(context);
						globalContext.setVbChanged(false);
					}

					// Update the ordering and visibility of the tabs
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
						Toast.makeText(context,
								getString(R.string.edit_tabs_toast),
								Toast.LENGTH_SHORT).show();

						// Reset to default
						globalContext.setHomeScreenChanged(false);
					}
				}
			}
		} else {
			// Declare that the options menu has changed, so should be recreated
			// (make the system calls the method onPrepareOptionsMenu)
			supportInvalidateOptionsMenu();

			// When the given tab is selected, switch to the corresponding page
			// in the ViewPager.
			mViewPager.setCurrentItem(tabPosition);

			// Actions over each fragment
			Fragment fragment = fragmentsList.get(tabPosition);

			if (fragment instanceof FavouritesStationFragment) {
				actionBar.setSubtitle(getString(R.string.edit_tabs_favourites));

				if (globalContext.isFavouritesChanged()) {
					((FavouritesStationFragment) fragment)
							.onResumeFragment(context);
					globalContext.setFavouritesChanged(false);
				}
			}

			if (fragment instanceof VirtualBoardsFragment) {
				actionBar.setSubtitle(getString(R.string.edit_tabs_search));

				if (globalContext.isVbChanged()) {
					((VirtualBoardsFragment) fragment)
							.onResumeFragment(context);
					globalContext.setVbChanged(false);
				}
			}

			if (fragment instanceof ScheduleFragment) {
				actionBar.setSubtitle(getString(R.string.edit_tabs_schedule));
			}

			if (fragment instanceof MetroFragment) {
				actionBar.setSubtitle(getString(R.string.edit_tabs_metro));
			}
		}
	}
}
