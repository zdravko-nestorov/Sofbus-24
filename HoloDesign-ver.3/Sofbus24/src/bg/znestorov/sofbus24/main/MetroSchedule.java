package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.ScheduleEntity;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.metro.MetroScheduleFragment;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class MetroSchedule extends FragmentActivity {

	private Activity context;
	private Bundle savedInstanceState;
	private FavouritesDataSource favouritesDatasource;

	private ActionBar actionBar;

	private ImageView addToFavourites;
	private ImageButton leftArrow;
	private ImageButton rightArrow;

	private TextView metroScheduleTime;
	private TextView metroStationName;
	private TextView metroDirection;

	private View metroScheduleFragment;
	private ProgressBar metroScheduleLoading;

	private MetroStation ms;
	private ArrayList<ArrayList<String>> scheduleHourList;
	private int currentScheduleHourIndex = 0;

	private static final String SAVED_STATE_KEY = "Current Schedule Hour Index";
	private static final String FRAGMENT_TAG_NAME = "Metro Schedule Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metro_schedule);

		// Get the current context and create a FavouritesDatasource and
		// a SavedInstanceState objects
		context = MetroSchedule.this;
		favouritesDatasource = new FavouritesDataSource(context);
		this.savedInstanceState = savedInstanceState;

		initBundleInfo();
		initLayoutFields();
		initFragmentContent();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(SAVED_STATE_KEY, currentScheduleHourIndex);
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
			// This is needed, because the fragment should be restarted
			savedInstanceState = null;

			initActiveFragmentContent();
			return true;
		case R.id.action_ms_refresh:
			initRefresh();
			return true;
		case R.id.action_ms_map:
			Intent metroMapIntent = new Intent(context, StationMap.class);
			metroMapIntent.putExtra(Constants.BUNDLE_STATION_MAP, ms);
			startActivity(metroMapIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the refresh by putting a 500 ms delay
	 */
	private void initRefresh() {
		// This is needed, because the fragment should be restarted
		savedInstanceState = null;

		// Show the loading ProgressBar
		metroScheduleFragment.setVisibility(View.GONE);
		metroScheduleLoading.setVisibility(View.VISIBLE);

		// Start a new thread - just to wait 500 ms
		Handler handler = new Handler();
		Runnable myrunnable = new Runnable() {
			public void run() {
				try {
					// Reset the time shown in the action bar
					actionBar.setSubtitle(DateFormat.format("dd.MM.yyy, kk:mm",
							new java.util.Date()).toString());

					// Initialize the fragment content
					initFragmentContent();
				} catch (Exception e) {
				}
			}
		};

		handler.postDelayed(myrunnable, 500);
	}

	/**
	 * Get and process the Bundle information
	 */
	private void initBundleInfo() {
		// Get the MetroStation object from Bundle
		Bundle extras = getIntent().getExtras();
		ms = (MetroStation) extras.get(Constants.BUNDLE_METRO_SCHEDULE);

		// Get an ArrayList of ArrayList with all active schedules
		scheduleHourList = getScheduleHourList(ms);

		// Get the active schedule (according to the current hour)
		if (savedInstanceState != null) {
			currentScheduleHourIndex = savedInstanceState
					.getInt(SAVED_STATE_KEY);
		} else {
			currentScheduleHourIndex = getActiveScheduleHourIndex(scheduleHourList);
		}
	}

	/**
	 * Initialize the layout fields (ActionBar, ImageViews and TextVies)
	 */
	private void initLayoutFields() {
		// Get the Action Bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get the Favorite ImageView and the Arrow ImageButtons
		addToFavourites = (ImageView) findViewById(R.id.metro_schedule_favourite);
		leftArrow = (ImageButton) findViewById(R.id.metro_schedule_img_left);
		rightArrow = (ImageButton) findViewById(R.id.metro_schedule_img_right);
		actionsOverImageButtons();

		// Get the header TextView views and set them labels
		metroScheduleTime = (TextView) findViewById(R.id.metro_schedule_time);
		metroStationName = (TextView) findViewById(R.id.metro_schedule_station_name);
		metroDirection = (TextView) findViewById(R.id.metro_schedule_direction);
		actionsOverTextViews();

		// Get the Fragment and the loading ProgressBar
		metroScheduleFragment = findViewById(R.id.metro_schedule_fragment);
		metroScheduleLoading = (ProgressBar) findViewById(R.id.metro_schedule_loading);
	}

	/**
	 * Set onClickListeners over the ImageButtons
	 */
	private void actionsOverImageButtons() {
		// Set onClickListner over the Favorites ImageView
		addToFavourites.setImageResource(getFavouriteImage(ms));
		addToFavourites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityUtils.toggleFavouritesStation(context,
						favouritesDatasource, ms, addToFavourites);
			}
		});

		// Set onClickListner over the left arrow
		leftArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savedInstanceState = null;
				currentScheduleHourIndex--;
				initFragmentContent();
			}
		});

		// Set onClickListner over the right arrow
		rightArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savedInstanceState = null;
				currentScheduleHourIndex++;
				initFragmentContent();
			}
		});
	}

	/**
	 * Get the favorites image according to this if exists in the Favorites
	 * Database
	 * 
	 * @param station
	 *            the station on the current row
	 * @return the station image id
	 */
	private Integer getFavouriteImage(Station station) {
		Integer favouriteImage;

		favouritesDatasource.open();
		if (favouritesDatasource.getStation(station) == null) {
			favouriteImage = R.drawable.ic_fav_empty;
		} else {
			favouriteImage = R.drawable.ic_fav_full;
		}
		favouritesDatasource.close();

		return favouriteImage;
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
		String stationName = ms.getName();
		String stationDirection = ms.getDirection();

		actionBar.setTitle(stationNumber);
		actionBar.setSubtitle(currentTime);
		metroStationName.setText(stationName);
		metroDirection.setText(stationDirection);
	}

	/**
	 * Check which fragment should be loaded
	 */
	private void initFragmentContent() {
		if (currentScheduleHourIndex == getActiveScheduleHourIndex(scheduleHourList)) {
			initActiveFragmentContent();
		} else {
			initCurrentFragmentContent();
		}
	}

	/**
	 * Initialize the active fragment
	 */
	private void initActiveFragmentContent() {
		// Get the active schedule (according to the current hour)
		currentScheduleHourIndex = getActiveScheduleHourIndex(scheduleHourList);

		// Format the schedule list
		ArrayList<String> formattedScheduleList = formatScheduleList(scheduleHourList
				.get(currentScheduleHourIndex));

		// Get the active hour of the schedule (according to the current time
		// for this hour)
		int activeScheduleIndex = getActiveScheduleIndex(formattedScheduleList);

		// Initialize the active fragment
		startFragment(formattedScheduleList, true, activeScheduleIndex);
	}

	/**
	 * Initialize the current fragment
	 */
	private void initCurrentFragmentContent() {
		// Format the schedule list
		ArrayList<String> formattedScheduleList = formatScheduleList(scheduleHourList
				.get(currentScheduleHourIndex));

		// Get the active hour of the schedule (according to the current time
		// for this hour)
		int activeScheduleIndex = getActiveScheduleIndex(formattedScheduleList);

		// Initialize the active fragment
		startFragment(formattedScheduleList, false, activeScheduleIndex);
	}

	/**
	 * Create a new MetroScheduleFragment with all needed information
	 * 
	 * @param formattedScheduleList
	 *            a formatted schedule list with current times of arrival and
	 *            remaining times
	 * @param isActive
	 *            if the fragment is active
	 * @param scheduleIndex
	 *            the active hour of the schedule (according to the current time
	 *            for this hour)
	 */
	private void startFragment(ArrayList<String> formattedScheduleList,
			boolean isActive, int scheduleIndex) {
		Fragment fragment;

		if (savedInstanceState == null) {
			ScheduleEntity metroScheduleEntity = new ScheduleEntity(
					formattedScheduleList, isActive, scheduleIndex);
			fragment = MetroScheduleFragment.newInstance(metroScheduleEntity);
		} else {
			fragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG_NAME);
		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.metro_schedule_fragment, fragment,
						FRAGMENT_TAG_NAME).commit();

		actionsOnFragmentChange();
	}

	/**
	 * Set the Fragment schedule hour label and show the needed arrows
	 */
	private void actionsOnFragmentChange() {
		// Set the MetroScheduleTime label
		String hourRange = getHour(scheduleHourList.get(
				currentScheduleHourIndex).get(0))
				+ ":00";
		metroScheduleTime.setText(hourRange);

		// Show needed arrows
		if (currentScheduleHourIndex == 0) {
			leftArrow.setVisibility(View.GONE);
		} else if (currentScheduleHourIndex == scheduleHourList.size() - 1) {
			rightArrow.setVisibility(View.GONE);
		} else {
			leftArrow.setVisibility(View.VISIBLE);
			rightArrow.setVisibility(View.VISIBLE);
		}

		metroScheduleFragment.setVisibility(View.VISIBLE);
		metroScheduleLoading.setVisibility(View.GONE);
	}

	/**
	 * Get a list with all schedule hours for this station (if for some hour
	 * there is no times of arrival - it is not added to the list)
	 * 
	 * @param ms
	 *            the MetroStation object retrieved from the Bundle
	 * @return a list with the schedule hours
	 */
	private ArrayList<ArrayList<String>> getScheduleHourList(MetroStation ms) {
		ArrayList<ArrayList<String>> scheduleHourList = new ArrayList<ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			ArrayList<String> metroSchedule = ms.getSchedule().get(i);

			if (metroSchedule != null && !metroSchedule.isEmpty()) {
				scheduleHourList.add(metroSchedule);
			}
		}

		return scheduleHourList;
	}

	/**
	 * Get the index of the current metro schedule in the scheduleHourList
	 * 
	 * @param scheduleHourList
	 *            a list containing all schedules for this station (that has
	 *            time of arrivals)
	 * @return the index of the current schedule hour
	 */
	private int getActiveScheduleHourIndex(
			ArrayList<ArrayList<String>> scheduleHourList) {
		int currentScheduleHourIndex = -1;

		int currentHour = Integer.parseInt(DateFormat.format("kk",
				new java.util.Date()).toString());
		boolean isCurrentHourInRange = false;

		for (int i = 0; i < scheduleHourList.size(); i++) {
			int scheduleHour = getHour(scheduleHourList.get(i).get(0));
			if (scheduleHour == currentHour) {
				// This rule is set in case the current hour is after the last
				// time schedule for the current fragment (if the current hour
				// is 15:59, and the last schedule is 15:50). In this case we
				// get the next hour as the current one
				if (isScheduleActive(scheduleHourList.get(i))) {
					isCurrentHourInRange = true;
					currentScheduleHourIndex = i;
				} else {
					// Check if this is the last hour that there is a schedule.
					// If so - do nothing
					if (i != scheduleHourList.size() - 1) {
						isCurrentHourInRange = true;
						currentScheduleHourIndex = i + 1;
					}
				}

				break;
			}
		}

		// Check if the current hour is present in the schedule. If not - set it
		// to the first fragment
		if (!isCurrentHourInRange) {
			currentScheduleHourIndex = 0;
		}

		return currentScheduleHourIndex;
	}

	/**
	 * Get the hour part of a time input in format HH:MM
	 * 
	 * @param time
	 *            the time input in format HH:MM
	 * @return the hour part of the time input
	 */
	private int getHour(String time) {
		int hour;

		if (time != null && time.contains(":")) {
			hour = Integer.parseInt(time.replaceAll(":.*", ""));
		} else {
			hour = -1;
		}

		return hour;
	}

	/**
	 * Check if the current fragment contains a schedule that is after the
	 * current hour. For example:<br/>
	 * <b>If the last schedule time is 15:50 and the current hour is 15:59 - the
	 * fragment is not Active</b>
	 * 
	 * @param scheduleList
	 *            the current fragment schedule time list
	 * @return if the fragment is active or not
	 */
	private boolean isScheduleActive(ArrayList<String> scheduleList) {
		boolean isScheduleActive = false;

		if (scheduleList != null && scheduleList.size() > 0) {
			String currentTime = DateFormat.format("kk:mm",
					new java.util.Date()).toString();
			String metroScheduleTime = scheduleList
					.get(scheduleList.size() - 1);
			String differenceTime = Utils.getTimeDifference(context,
					metroScheduleTime, currentTime);

			if (!"---".equals(differenceTime)) {
				isScheduleActive = true;
			}
		}

		return isScheduleActive;
	}

	/**
	 * Find the difference between the current time and the metro schedule time
	 * and create new list containing both
	 * 
	 * @param scheduleList
	 *            the current fragment schedule time list
	 * @return an ArrayList containing the current time and the time left
	 */
	private ArrayList<String> formatScheduleList(ArrayList<String> scheduleList) {
		ArrayList<String> formattedMetroScheduleList = new ArrayList<String>();
		String currentTime = DateFormat.format("kk:mm", new java.util.Date())
				.toString();

		for (int i = 0; i < scheduleList.size(); i++) {
			String metroScheduleTime = scheduleList.get(i);
			String differenceTime = Utils.getTimeDifference(context,
					metroScheduleTime, currentTime);

			if (!"---".equals(differenceTime)) {
				metroScheduleTime = String.format(metroScheduleTime + " (%s)",
						differenceTime);
			}

			formattedMetroScheduleList.add(metroScheduleTime);
		}

		return formattedMetroScheduleList;
	}

	/**
	 * Get the current active schedule time from the active schedule list
	 * 
	 * @param scheduleList
	 *            the current fragment schedule time list (must be formatted
	 *            list)
	 * @return the index of the current schedule
	 */
	private int getActiveScheduleIndex(ArrayList<String> scheduleList) {
		int currentScheduleIndex = -1;

		for (int i = 0; i < scheduleList.size(); i++) {
			if (scheduleList.get(i).contains("~")) {
				currentScheduleIndex = i;
				break;
			}
		}

		return currentScheduleIndex;
	}
}
