package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import bg.znestorov.sofbus24.closest.stations.list.ClosestStationsListFragment;

public class ClosestStationsList extends FragmentActivity {

	private ActionBar actionBar;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_closest_stations_list);

		// Fill the fragments list
		fillFragmentsList();

		// Set up the action bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.cs_list_title));

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mViewPager = (ViewPager) findViewById(R.id.cs_list_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(
				R.menu.activity_closest_stations_list_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_cs_list_refresh:
			((ClosestStationsListFragment) fragmentsList.get(0)).update(
					ClosestStationsList.this, null);
			return true;
		case R.id.action_cs_list_map:
			// TODO: Set the event on clicking the button
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
	}

	/**
	 * Fill the Fragment map with all fragments in the TabHost
	 */
	private void fillFragmentsList() {
		// Add a ClosestStationsList Fragment
		Fragment closestStationsListFragment = new ClosestStationsListFragment();
		Bundle extras = getIntent().getExtras();
		closestStationsListFragment.setArguments(extras);
		fragmentsList.add(closestStationsListFragment);
	}
}
