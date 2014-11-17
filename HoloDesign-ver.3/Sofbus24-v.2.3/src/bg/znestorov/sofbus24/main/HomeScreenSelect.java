package bg.znestorov.sofbus24.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.databases.Sofbus24DatabaseUtils;
import bg.znestorov.sofbus24.entity.UpdateTypeEnum;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.navigation.NavDrawerHomeScreenPreferences;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HomeScreenSelect extends SherlockFragmentActivity {

	private FragmentActivity context;

	private int userChoice = -1;
	private static final String BUNDLE_USER_CHOICE = "USER CHOICE";

	public static final int REQUEST_CODE_HOME_SCREEN_SELECT = 0;
	public static final int RESULT_CODE_ACTIVITY_NEW = 1;
	public static final int RESULT_CODE_ACTIVITY_FINISH = 2;
	public static final int RESULT_CODE_ACTIVITY_RESTART = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_home_screen_select);

		// Get the application and the current context
		context = HomeScreenSelect.this;
		userChoice = savedInstanceState == null ? -1 : savedInstanceState
				.getInt(BUNDLE_USER_CHOICE);

		// Init the layout fields
		initLayoutFields(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQUEST_CODE_HOME_SCREEN_SELECT) {
			switch (resultCode) {
			case RESULT_CODE_ACTIVITY_NEW:
			case RESULT_CODE_ACTIVITY_RESTART:
				startHomeScreen();
				break;
			case RESULT_CODE_ACTIVITY_FINISH:
				finish();
				break;
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putInt(BUNDLE_USER_CHOICE, userChoice);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		ActivityUtils.closeApplication(context);
	}

	/**
	 * Initialize the layout fields and load the needed data
	 * 
	 * @param savedInstanceState
	 *            the state of the activity
	 */
	private void initLayoutFields(Bundle savedInstanceState) {

		View homeScreenView = findViewById(R.id.sofbus24_home_screen);
		View loadingView = findViewById(R.id.sofbus24_loading);

		boolean isHomeScreenSet = NavDrawerHomeScreenPreferences
				.isUserHomeScreenChoosen(context);

		if (isHomeScreenSet) {
			homeScreenView.setVisibility(View.GONE);
			loadingView.setVisibility(View.VISIBLE);

			processAppStartUp(savedInstanceState);
		} else {
			homeScreenView.setVisibility(View.VISIBLE);
			loadingView.setVisibility(View.GONE);

			processUserChoice(savedInstanceState);
		}
	}

	/**
	 * Process the user choice and load the needed information
	 * 
	 * @param savedInstanceState
	 *            the state of the activity
	 */
	private void processUserChoice(final Bundle savedInstanceState) {

		final RadioGroup homeScreenChoiceGroup = (RadioGroup) findViewById(R.id.sofbus24_home_screen_select);
		final ImageButton homeScreenChoiceBtn = (ImageButton) findViewById(R.id.sofbus24_home_screen_select_btn);

		// Check if there is any user choice
		if (userChoice >= 0) {
			homeScreenChoiceGroup.check(getCheckedViewId());
			homeScreenChoiceBtn.setVisibility(View.VISIBLE);
		}

		// Show the next button in case of the first item selected
		homeScreenChoiceGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						userChoice = checkedId;

						if (homeScreenChoiceBtn.getVisibility() == View.GONE) {
							homeScreenChoiceBtn.setVisibility(View.VISIBLE);
						}
					}
				});

		// Set on click listener over the next button
		homeScreenChoiceBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int userChoice = getCheckedViewNumber(homeScreenChoiceGroup
						.getCheckedRadioButtonId());
				NavDrawerHomeScreenPreferences.setUserChoice(context,
						userChoice);

				initLayoutFields(savedInstanceState);
			}
		});
	}

	/**
	 * Get the id of the selected radio button
	 * 
	 * @return the checked view id
	 */
	private int getCheckedViewId() {

		int checkedViewId;

		switch (userChoice) {
		case 1:
			checkedViewId = R.id.sofbus24_home_screen_standard;
			break;
		case 2:
			checkedViewId = R.id.sofbus24_home_screen_map;
			break;
		case 3:
			checkedViewId = R.id.sofbus24_home_screen_droidtrans;
			break;
		default:
			checkedViewId = -1;
			break;
		}

		return checkedViewId;
	}

	/**
	 * Check which radio button is checked by its id
	 * 
	 * @param viewId
	 *            the id of the checked radio button
	 * @return the consequent number of the radio button
	 */
	private int getCheckedViewNumber(int viewId) {

		int checkedViewNumber;

		switch (viewId) {
		case R.id.sofbus24_home_screen_standard:
			checkedViewNumber = 0;
			break;
		case R.id.sofbus24_home_screen_map:
			checkedViewNumber = 1;
			break;
		default:
			checkedViewNumber = 2;
			break;
		}

		return checkedViewNumber;

	}

	/**
	 * Load the information into the dabatase/objects and start the appropriate
	 * screens
	 * 
	 * @param savedInstanceState
	 *            the state of the activity
	 */
	private void processAppStartUp(Bundle savedInstanceState) {

		if (savedInstanceState == null) {

			// Creates the configuration file
			Configuration.createConfiguration(context);

			// Retrieve the information from the DB
			CreateDatabases createDatabases = new CreateDatabases(context);
			createDatabases.execute();
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

		private FragmentActivity context;

		public CreateDatabases(FragmentActivity context) {
			this.context = context;
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
			super.onPostExecute(result);
			new LoadStartingData(context).execute();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			new CreateDatabases(context).execute();
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

		private FragmentActivity context;

		public LoadStartingData(FragmentActivity context) {
			this.context = context;
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

			startHomeScreen();

			// Check for updates (only when the application is started for the
			// first time and everything is visualized)
			Utils.checkForUpdate(context, UpdateTypeEnum.DB);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			new LoadStartingData(context).execute();
		}
	}

	/**
	 * Start the standard home screen fragment
	 */
	private void startSofbus24() {
		Intent sofbus24Intent = new Intent(context, Sofbus24.class);
		startActivityForResult(sofbus24Intent, REQUEST_CODE_HOME_SCREEN_SELECT);
	}

	/**
	 * Start the DroidTrans activity
	 */
	private void startDroidTrans() {
		Bundle bundle = new Bundle();
		bundle.putBoolean(DroidTrans.BUNDLE_IS_DROID_TRANS_HOME_SCREEN, true);

		Intent droidTransIntent = new Intent(context, DroidTrans.class);
		droidTransIntent.putExtras(bundle);
		startActivityForResult(droidTransIntent,
				REQUEST_CODE_HOME_SCREEN_SELECT);
	}

	/**
	 * Actions after the AsyncTask is finished
	 */
	public void startHomeScreen() {

		int userHomeScreenChoice;
		if (NavDrawerHomeScreenPreferences.isUserHomeScreenChoosen(context)) {
			userHomeScreenChoice = NavDrawerHomeScreenPreferences
					.getUserHomeScreenChoice(context);
		} else {
			userHomeScreenChoice = 0;
			NavDrawerHomeScreenPreferences.setUserChoice(context,
					userHomeScreenChoice);
		}

		switch (userHomeScreenChoice) {
		case 0:
			startSofbus24();
			break;
		case 1:
			ActivityUtils.startClosestStationsMap(context,
					getSupportFragmentManager(), true);
			break;
		case 2:
			startDroidTrans();
			break;
		}
	}
}