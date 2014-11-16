package bg.znestorov.sofbus24.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.databases.Sofbus24DatabaseUtils;
import bg.znestorov.sofbus24.entity.UpdateTypeEnum;
import bg.znestorov.sofbus24.metro.MetroLoadStations;
import bg.znestorov.sofbus24.navigation.NavDrawerHomeScreenPreferences;
import bg.znestorov.sofbus24.schedule.ScheduleLoadVehicles;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HomeScreenSelect extends SherlockFragmentActivity {

	private FragmentActivity context;
	private ActionBar actionBar;

	public static final int REQUEST_CODE_HOME_SCREEN_SELECT = 0;
	public static final int RESULT_CODE_ACTIVITY_NEW = 1;
	public static final int RESULT_CODE_ACTIVITY_FINISH = 2;
	public static final int RESULT_CODE_ACTIVITY_RESTART = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_home_screen_select);

		// Get the application and the current context;
		context = HomeScreenSelect.this;

		// Initialize the ActionBar
		initActionBar();

		// Get the fields in the layout
		ProgressBar sofbusLoading = (ProgressBar) findViewById(R.id.sofbus24_loading);

		if (savedInstanceState == null) {

			// Creates the configuration file
			Configuration.createConfiguration(context);

			// Retrieve the information from the DB and set up the layout fields
			CreateDatabases createDatabases = new CreateDatabases(context,
					sofbusLoading);
			createDatabases.execute();
		} else {
			actionsOnPostExecute(sofbusLoading);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQUEST_CODE_HOME_SCREEN_SELECT) {
			switch (resultCode) {
			case RESULT_CODE_ACTIVITY_NEW:
				startHomeScreen();
				break;
			case RESULT_CODE_ACTIVITY_FINISH:
				finish();
				break;
			case RESULT_CODE_ACTIVITY_RESTART:
				ActivityUtils.restartApplication(context);
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		ActivityUtils.closeApplication(context);
	}

	/**
	 * Initialize the action bar
	 */
	private void initActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setTitle(getString(R.string.app_sofbus24));
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
	 * Class responsible for async creation of the databases
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	public class CreateDatabases extends AsyncTask<Void, Void, Void> {

		private FragmentActivity context;
		private ProgressBar sofbusLoading;

		public CreateDatabases(FragmentActivity context,
				ProgressBar sofbusLoading) {
			this.context = context;
			this.sofbusLoading = sofbusLoading;
		}

		@Override
		protected void onPreExecute() {
			actionsOnPreExecute(sofbusLoading);
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

			LoadStartingData loadStartingData = new LoadStartingData(context,
					sofbusLoading);
			loadStartingData.execute();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			CreateDatabases createDatabases = new CreateDatabases(context,
					sofbusLoading);
			createDatabases.execute();
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
		private ProgressBar sofbusLoading;

		public LoadStartingData(FragmentActivity context,
				ProgressBar sofbusLoading) {
			this.context = context;
			this.sofbusLoading = sofbusLoading;
		}

		@Override
		protected void onPreExecute() {
			actionsOnPreExecute(sofbusLoading);
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

			actionsOnPostExecute(sofbusLoading);
			startHomeScreen();

			// Check for updates (only when the application is started for the
			// first time and everything is visualized)
			Utils.checkForUpdate(context, UpdateTypeEnum.DB);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			LoadStartingData loadStartingData = new LoadStartingData(context,
					sofbusLoading);
			loadStartingData.execute();
		}
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onPreExecute method of the AsyncTask
	 * 
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 */
	private void actionsOnPreExecute(ProgressBar sofbusLoading) {
		sofbusLoading.setVisibility(View.VISIBLE);
	}

	/**
	 * Actions over the LayoutFields (which to be shown and visible) - in
	 * onPostExecute method of the AsyncTask
	 * 
	 * @param sofbusLoading
	 *            the ProgressBar of the Layout
	 */
	private void actionsOnPostExecute(ProgressBar sofbusLoading) {
		sofbusLoading.setVisibility(View.GONE);
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