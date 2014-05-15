package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.publictransport.PublicTransportDirections;
import bg.znestorov.sofbus24.publictransport.PublicTransportFragment;
import bg.znestorov.sofbus24.utils.Constants;

public class PublicTransport extends FragmentActivity implements
		ActionBar.TabListener {

	private ActionBar actionBar;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private PublicTransportDirections ptDirections;
	private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_public_transport);

		initBundleInfo();
		initLayoutFields();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	/**
	 * Get the current location coordinates from the Bundle object
	 */
	private void initBundleInfo() {
		Bundle extras = getIntent().getExtras();
		ptDirections = (PublicTransportDirections) extras
				.get(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE);
	}

	/**
	 * Initialize the layout fields (ActionBar, ViewPager, SectionsPagerAdapter
	 * and SlidingMenu)
	 */
	private void initLayoutFields() {
		// Set up the ActionBar
		actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.pt_title));
		actionBar.setSubtitle(getSubtitle());
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the fragments list
		createFragmentsList();

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mViewPager = (ViewPager) findViewById(R.id.pt_pager);
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
					.setText(mSectionsPagerAdapter.getTabName(i))
					.setTabListener(this));
		}
	}

	/**
	 * Create the ActionBar subtitle using the vehicle type and number
	 * 
	 * @return the actionBar subtitle in format - [VehicleType] ¹[VehicleNumber]
	 */
	private String getSubtitle() {
		Vehicle vehicle = ptDirections.getVehicle();
		String subtitle;

		switch (vehicle.getType()) {
		case BUS:
			subtitle = String.format(getString(R.string.pt_bus),
					vehicle.getNumber());
			break;
		case TROLLEY:
			subtitle = String.format(getString(R.string.pt_trolley),
					vehicle.getNumber());
			break;
		case TRAM:
			subtitle = String.format(getString(R.string.pt_tram),
					vehicle.getNumber());
			break;
		default:
			subtitle = String.format(getString(R.string.pt_bus),
					vehicle.getNumber());
			break;
		}

		return subtitle;
	}

	/**
	 * Create the FragmentsList, where each element contains a separate
	 * direction
	 */
	private void createFragmentsList() {
		ptDirections.setActiveDirection(0);
		fragmentsList.add(PublicTransportFragment.newInstance(ptDirections));

		ptDirections.setActiveDirection(1);
		fragmentsList.add(PublicTransportFragment.newInstance(ptDirections));
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

		public String getTabName(int i) {
			String tabName;

			switch (i) {
			case 0:
				tabName = ptDirections.getDirectionsNames().get(0);
				break;
			case 1:
				tabName = ptDirections.getDirectionsNames().get(1);
				break;
			default:
				tabName = ptDirections.getDirectionsNames().get(0);
				break;
			}

			return tabName;
		}
	}
}
