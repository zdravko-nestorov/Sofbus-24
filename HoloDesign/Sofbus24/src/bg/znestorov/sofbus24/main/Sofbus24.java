package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import bg.znestorov.sofbus24.activity.ActivityUtils;
import bg.znestorov.sofbus24.databases.StationsDatabaseUtils;
import bg.znestorov.sofbus24.databases.VehiclesDatabaseUtils;
import bg.znestorov.sofbus24.favorites.FavouritesFragment;
import bg.znestorov.sofbus24.metro.MetroFragment;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.schedule.ScheduleFragment;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.Constants;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class Sofbus24 extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	/**
	 * Sliding menu (using external project)
	 */
	private SlidingMenu slidingMenu;

	/**
	 * The context to use. Usually your android.app.Application or
	 * android.app.Activity object
	 */
	private Activity context;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sofbus24);

		context = Sofbus24.this;

		// Creating and copying the DB to the SD card (used only for testing
		// purposes) - DO NOT UNCOMMENT
		// StationsDatabaseUtils.generateAndCopyStationsDB(context);
		// VehiclesDatabaseUtils.generateAndCopyStationsDB(context);

		// Create the database by copying it from the assets folder to the
		// internal memory
		StationsDatabaseUtils.createStationsDatabase(context);
		VehiclesDatabaseUtils.createVehiclesDatabase(context);

		// Load all vehicles from the Database, so use them lately
		ScheduleLoadVehicles.getInstance(context);

		// Load all favourites stations from the Database, so use them lately
		MetroLoadStations.getInstance(context);

		// Init the UIL image loader
		ActivityUtils.initImageLoader(context);

		// Fill the fragment map
		fillFragmentMap();

		// Set up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount() - 1);
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

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getPageIcon(i))
					.setTabListener(this));
		}

		// Create the sliding menu
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slide_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(R.layout.activity_sliding_menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sofbus24_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		int tabPosition = tab.getPosition();

		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tabPosition);

		// Show Toast with the Metro direction if the metro tab is selected
		Fragment fragment = fragmentsList.get(tabPosition);

		if (fragment instanceof MetroFragment) {
			((MetroFragment) fragment).showDirectionNameToast();
		}

		if (fragment instanceof FavouritesFragment) {
			((FavouritesFragment) fragment).update(context);
		}
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
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.toggle();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.slidingMenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

		@Override
		public int getCount() {
			return Constants.GLOBAL_TAB_COUNT;
		}

		public Integer getPageIcon(int position) {
			switch (position) {
			case 0:
				return R.drawable.ic_tab_favorites;
			case 1:
				return R.drawable.ic_tab_real_time;
			case 2:
				return R.drawable.ic_tab_schedule;
			case 3:
				return R.drawable.ic_tab_metro;
			}
			return null;
		}
	}

	/**
	 * Fill the Fragment map with all fragments in the TabHost
	 */
	private void fillFragmentMap() {
		Fragment fragment;
		Bundle bundle = new Bundle();

		// Add Favourites fragment
		fragmentsList.add(new FavouritesFragment());

		// Add Virtual Boards fragment
		fragment = new DummySectionFragment();
		bundle.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 1);
		fragment.setArguments(bundle);
		fragmentsList.add(fragment);

		// Add Schedule fragment
		fragmentsList.add(new ScheduleFragment());

		// Add Metro fragment
		fragmentsList.add(new MetroFragment());
	}

	/**
	 * A dummy fragment representing a section of the application, but that
	 * simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_sofbus24_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
