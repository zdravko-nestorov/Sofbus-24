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
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.metro.MetroScheduleFragment;
import bg.znestorov.sofbus24.utils.Constants;

public class MetroSchedule extends FragmentActivity {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private MetroStation ms;
	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metro_schedule);

		// Get the MetroStation object from the Bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ms = (MetroStation) extras.get(Constants.BUNDLE_METRO_SCHEDULE);
		}

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
		mViewPager.setAdapter(mSectionsPagerAdapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the header TextView views and set them labels
		String stationNumber = String.format(
				getString(R.string.metro_item_station_number_text_sign),
				ms.getNumber());
		String currentTime = String.format(
				getString(R.string.metro_schedule_time_info), DateFormat
						.format("dd.MM.yyy, kk:mm", new java.util.Date())
						.toString());
		String hourRange = DateFormat.format("kk", new java.util.Date())
				.toString() + ":00";

		TextView metroStationName = (TextView) findViewById(R.id.metro_schedule_station_name);
		TextView metroDirection = (TextView) findViewById(R.id.metro_schedule_direction);
		TextView metroScheduleTime = (TextView) findViewById(R.id.metro_schedule_time);

		actionBar.setTitle(stationNumber);
		actionBar.setSubtitle(currentTime);
		metroStationName.setText(ms.getName());
		metroDirection.setText(ms.getDirection());
		metroScheduleTime.setText(hourRange);
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
		Fragment fragment;

		for (int i = 4; i <= 24; i++) {
			ArrayList<String> metroSchedule = ms.getSchedule().get(i);

			if (metroSchedule != null && !metroSchedule.isEmpty()) {
				fragment = new MetroScheduleFragment();

				Bundle bundle = new Bundle();
				bundle.putSerializable(Constants.BUNDLE_METRO_SCHEDULE,
						metroSchedule);

				fragment.setArguments(bundle);
				fragmentsList.add(fragment);
			}
		}
	}
}
