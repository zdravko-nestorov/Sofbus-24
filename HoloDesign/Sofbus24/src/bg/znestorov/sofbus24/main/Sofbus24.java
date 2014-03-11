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

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class Sofbus24 extends FragmentActivity implements ActionBar.TabListener {

	private Activity context;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private SlidingMenu slidingMenu;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	private static boolean isFavouritesChanged = false;
	private static boolean isMetroChanged = false;

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

		// Load all metro stations from the Database, so use them lately
		MetroLoadStations.getInstance(context);

		// Initialize the UIL image loader
		ActivityUtils.initImageLoader(context);

		// Fill the fragments list
		fillFragmentsList();

		// Set up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
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

		// For each of the sections in the app, add a tab to the action bar
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
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
		// Inflate the menu; this adds items to the action bar if it is present
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

		// Actions over each fragment
		Fragment fragment = fragmentsList.get(tabPosition);

		if (fragment instanceof FavouritesFragment && isFavouritesChanged) {
			((FavouritesFragment) fragment).update(context);
			isFavouritesChanged = false;
		}

		if (fragment instanceof MetroFragment) {
			((MetroFragment) fragment).showDirectionNameToast();

			if (isMetroChanged) {
				((MetroFragment) fragment).update();
				isMetroChanged = false;
			}
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
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentsList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

		public Integer getPageIcon(int position) {
			switch (position) {
			case 0:
				return R.drawable.ic_tab_favorites;
			case 1:
				return R.drawable.ic_tab_real_time;
			case 2:
				return R.drawable.ic_tab_schedule;
			default:
				return R.drawable.ic_tab_metro;
			}
		}
	}

	/**
	 * Fill the Fragment map with all fragments in the TabHost
	 */
	private void fillFragmentsList() {
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

	public static void setFavouritesChanged(boolean isFavouritesChanged) {
		Sofbus24.isFavouritesChanged = isFavouritesChanged;
	}

	public static void setMetroChanged(boolean isMetroChanged) {
		Sofbus24.isMetroChanged = isMetroChanged;
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
