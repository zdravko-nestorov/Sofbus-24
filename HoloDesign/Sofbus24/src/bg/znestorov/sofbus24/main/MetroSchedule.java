package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import bg.znestorov.sofbus24.favorites.FavouritesFragment;
import bg.znestorov.sofbus24.schedule.ScheduleFragment;

public class MetroSchedule extends FragmentActivity {

	private Activity context;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metro_schedule);

		context = MetroSchedule.this;

		// Fill the fragments list
		fillFragmentsList();

		// Set up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mViewPager = (ViewPager) findViewById(R.id.metro_schedule_pager);
		mViewPager.setOffscreenPageLimit(fragmentsList.size() - 1);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		// Add Favourites fragment
		fragmentsList.add(new ScheduleFragment());
		fragmentsList.add(new ScheduleFragment());
	}
}
