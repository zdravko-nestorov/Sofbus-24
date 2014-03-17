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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.metro.MetroScheduleFragment;
import bg.znestorov.sofbus24.utils.Constants;

public class MetroSchedule extends FragmentActivity {

	private ActionBar actionBar;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private MetroStation ms;
	private List<Fragment> fragmentsList = new ArrayList<Fragment>();
	private List<ArrayList<String>> fragmentsScheduleList = new ArrayList<ArrayList<String>>();

	private static int activePageHour;
	private int currentPagePosition = 0;
	private TextView metroScheduleTime;
	private ImageButton leftArrow;
	private ImageButton rightArrow;

	private TextView metroStationName;
	private TextView metroDirection;

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
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mViewPager = (ViewPager) findViewById(R.id.metro_schedule_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Get the time TextView and ImageButtons
		metroScheduleTime = (TextView) findViewById(R.id.metro_schedule_time);
		leftArrow = (ImageButton) findViewById(R.id.metro_schedule_img_left);
		rightArrow = (ImageButton) findViewById(R.id.metro_schedule_img_right);

		// Get the header TextView views and set them labels
		metroStationName = (TextView) findViewById(R.id.metro_schedule_station_name);
		metroDirection = (TextView) findViewById(R.id.metro_schedule_direction);
		actionsOverTextViews();

		// Set on page change listener
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
			}

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			public void onPageSelected(int position) {
				currentPagePosition = position;
				actionsOnPageSelected();
			}
		});

		// Set active tab
		setActiveTab();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.activity_metro_schedule_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_ms_reset:
			setActiveTab();
			return true;
		case R.id.action_ms_refresh:
			for (int i = 0; i < fragmentsList.size(); i++) {
				((MetroScheduleFragment) fragmentsList.get(i))
						.update(MetroSchedule.this);
			}

			String currentTime = DateFormat.format("dd.MM.yyy, kk:mm",
					new java.util.Date()).toString();
			actionBar.setSubtitle(currentTime);

			return true;
		case R.id.action_ms_map:
			// TODO: Open the map
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
				fragmentsScheduleList.add(metroSchedule);
			}
		}
	}

	/**
	 * Set the current active tab
	 */
	private void setActiveTab() {
		int currentHour = Integer.parseInt(DateFormat.format("kk",
				new java.util.Date()).toString());
		boolean isCurrentHourInRange = false;

		for (int i = 0; i < fragmentsScheduleList.size(); i++) {
			int firstTimeHour = Integer.parseInt(fragmentsScheduleList.get(i)
					.get(0).replaceAll(":.*", ""));
			if (firstTimeHour == currentHour) {
				// Check if current hour exists in the schedule list
				isCurrentHourInRange = true;

				// Set the active page hour to a variable used in
				// MetroScheduleFragments
				activePageHour = currentHour;

				// Set the position of the currently active fragment
				currentPagePosition = i;
				break;
			}
		}

		// Check if the current hour is present in the schedule. If not - set it
		// to the first fragment
		if (!isCurrentHourInRange) {
			activePageHour = Integer.parseInt(fragmentsScheduleList.get(0)
					.get(0).replaceAll(":.*", ""));
			currentPagePosition = 0;
		}

		actionsOnPageSelected();
		actionsOverImageButtons();
		mViewPager.setCurrentItem(currentPagePosition);
	}

	/**
	 * Set the Fragment schedule hour label and show the needed arrows
	 * 
	 * @param position
	 *            the position of the selected page
	 */
	private void actionsOnPageSelected() {
		// Set the MetroScheduleTime label
		String hourRange = fragmentsScheduleList.get(currentPagePosition)
				.get(0).replaceAll(":.*", ":00");
		metroScheduleTime.setText(hourRange);

		// Show needed arrows
		if (currentPagePosition == 0) {
			leftArrow.setVisibility(View.GONE);
		} else if (currentPagePosition == fragmentsList.size() - 1) {
			rightArrow.setVisibility(View.GONE);
		} else {
			leftArrow.setVisibility(View.VISIBLE);
			rightArrow.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Set onClickListeners over the ImageButtons
	 * 
	 * @param currentPagePosition
	 *            position of the currently selected tab
	 */
	private void actionsOverImageButtons() {
		// Set onClickListner over the left arrow
		leftArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(currentPagePosition - 1);
			}
		});

		// Set onClickListner over the right arrow
		rightArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(currentPagePosition + 1);
			}
		});
	}

	/**
	 * Set labels on the TextViews
	 */
	private void actionsOverTextViews() {
		String stationNumber = String.format(
				getString(R.string.metro_item_station_number_text_sign),
				ms.getNumber());
		String currentTime = DateFormat.format("dd.MM.yyy, kk:mm",
				new java.util.Date()).toString();

		actionBar.setTitle(stationNumber);
		actionBar.setSubtitle(currentTime);
		metroStationName.setText(ms.getName());
		metroDirection.setText(ms.getDirection());
	}

	public static int getActivePageHour() {
		return activePageHour;
	}
}
