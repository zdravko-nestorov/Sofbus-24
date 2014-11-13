package bg.znestorov.sofbus24.navigation;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.closest.stations.map.RetrieveCurrentLocation;
import bg.znestorov.sofbus24.closest.stations.map.RetrieveCurrentLocationTimeout;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.About;
import bg.znestorov.sofbus24.main.AboutDialog;
import bg.znestorov.sofbus24.main.History;
import bg.znestorov.sofbus24.main.HistoryDialog;
import bg.znestorov.sofbus24.main.Preferences;
import bg.znestorov.sofbus24.main.PreferencesDialog;
import bg.znestorov.sofbus24.main.PreferencesPreHoneycomb;
import bg.znestorov.sofbus24.main.PreferencesPreHoneycombDialog;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.Sofbus24;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class NavDrawerHelper {

	private FragmentActivity context;
	private GlobalEntity globalContext;

	private Bundle savedInstanceState;
	private ProgressBar sofbusLoading;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ArrayList<String> navigationItems;

	private DrawerItemClickListener drawerItemClickListener;

	/**
	 * Class responsible for registring user clicks over the navigation drawer
	 */
	public class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectNavigationDrawerItem(position);
		}
	}

	public NavDrawerHelper(FragmentActivity context, Bundle savedInstanceState,
			ProgressBar sofbusLoading, DrawerLayout mDrawerLayout,
			ListView mDrawerList, ArrayList<String> navigationItems) {

		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();

		this.savedInstanceState = savedInstanceState;
		this.sofbusLoading = sofbusLoading;

		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.navigationItems = navigationItems;

		this.drawerItemClickListener = new DrawerItemClickListener();
	}

	/**
	 * Define the user actions on navigation drawer item click
	 * 
	 * @param position
	 *            the position of the click
	 */
	private void selectNavigationDrawerItem(int position) {

		ProgressDialog progressDialog = new ProgressDialog(context);
		int userHomeScreen = NavDrawerHomeScreenPreferences
				.getUserHomeScreenChoice(context);

		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);

		switch (position) {
		case 0:
			break;
		case 1:
		case 2:
		case 3:
			if (isHomeScreenChanged(userHomeScreen, position)) {
				NavDrawerHomeScreenPreferences.setUserChoice(context,
						position - 1);

				if (context instanceof Sofbus24) {
					((Sofbus24) context).startHomeScreen(savedInstanceState,
							sofbusLoading);
				} else {
					context.setResult(Sofbus24.RESULT_CODE_ACTIVITY_NEW);
					context.finish();
				}
			}

			break;
		case 4:
			startClosestStationsList(progressDialog);
			break;
		case 5:
			Intent historyIntent;
			if (globalContext.isPhoneDevice()) {
				historyIntent = new Intent(context, History.class);
			} else {
				historyIntent = new Intent(context, HistoryDialog.class);
			}
			context.startActivity(historyIntent);
			break;
		case 6:
			Intent preferencesIntent;
			if (globalContext.isPhoneDevice()) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					preferencesIntent = new Intent(context, Preferences.class);
				} else {
					preferencesIntent = new Intent(context,
							PreferencesPreHoneycomb.class);
				}
			} else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					preferencesIntent = new Intent(context,
							PreferencesDialog.class);
				} else {
					preferencesIntent = new Intent(context,
							PreferencesPreHoneycombDialog.class);
				}
			}
			context.startActivity(preferencesIntent);
			break;
		case 7:
			Intent aboutIntent;
			if (globalContext.isPhoneDevice()) {
				aboutIntent = new Intent(context, About.class);
			} else {
				aboutIntent = new Intent(context, AboutDialog.class);
			}
			context.startActivity(aboutIntent);
			break;
		case 8:
			context.setResult(Sofbus24.RESULT_CODE_ACTIVITY_FINISH);
			context.finish();
			break;
		}
	}

	/**
	 * Show a long toast about the changed home screen and set the change in the
	 * preference file
	 * 
	 * @param userHomeScreen
	 *            the current home screen
	 * @param userChoice
	 *            the user choice
	 * 
	 * @return if the home screen can be changed
	 */
	private boolean isHomeScreenChanged(int userHomeScreen, int userChoice) {

		boolean isHomeScreenChanged = true;
		String homeScreenName = navigationItems.get(userChoice);

		if (userChoice == 2 && !globalContext.areServicesAvailable()) {
			ActivityUtils.showLongToast(context, String.format(context
					.getString(R.string.navigation_drawer_home_screen_error),
					homeScreenName), 6000, 1000);

			isHomeScreenChanged = false;
		} else {
			if (userHomeScreen == userChoice - 1) {
				ActivityUtils
						.showLongToast(
								context,
								String.format(
										context.getString(R.string.navigation_drawer_home_screen_remains),
										homeScreenName), 5000, 1000);

				isHomeScreenChanged = false;
			} else {
				ActivityUtils
						.showLongToast(
								context,
								String.format(
										context.getString(R.string.navigation_drawer_home_screen_changed),
										homeScreenName), 5500, 1000);
			}
		}

		return isHomeScreenChanged;
	}

	/**
	 * Start the ClosestStationsList activity
	 * 
	 * @param progressDialog
	 *            the progress dialog
	 */
	private void startClosestStationsList(ProgressDialog progressDialog) {
		progressDialog.setMessage(String.format(context
				.getString(R.string.cs_list_loading_current_location)));

		RetrieveCurrentLocation retrieveCurrentLocation = new RetrieveCurrentLocation(
				context, true, progressDialog);
		retrieveCurrentLocation.execute();
		RetrieveCurrentLocationTimeout retrieveCurrentLocationTimeout = new RetrieveCurrentLocationTimeout(
				retrieveCurrentLocation);
		(new Thread(retrieveCurrentLocationTimeout)).start();
	}

	public DrawerItemClickListener getDrawerItemClickListener() {
		return drawerItemClickListener;
	}

}