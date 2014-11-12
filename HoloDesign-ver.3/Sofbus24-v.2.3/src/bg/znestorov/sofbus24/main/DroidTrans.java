package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;
import bg.znestorov.sofbus24.navigation.NavDrawerArrayAdapter;
import bg.znestorov.sofbus24.navigation.NavDrawerHelper;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DroidTrans extends SherlockFragmentActivity {

	private FragmentActivity context;
	private ActionBar actionBar;

	private boolean isDroidTransHomeScreen;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerArrayAdapter mMenuAdapter;
	private ArrayList<String> navigationItems;

	public static final String BUNDLE_IS_DROID_TRANS_HOME_SCREEN = "IS DROID TRANS HOME SCREEN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_droidtrans);

		// Get the current activity context and check if this activity is the
		// home screen
		context = DroidTrans.this;
		isDroidTransHomeScreen = getIntent().getExtras() != null ? getIntent()
				.getExtras().getBoolean(BUNDLE_IS_DROID_TRANS_HOME_SCREEN,
						false) : false;

		initActionBar();

		if (isDroidTransHomeScreen) {
			initNavigationDrawer();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(Sofbus24.RESULT_CODE_ACTIVITY_FINISH, new Intent());
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_droidtrans_actions,
				menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			if (isDroidTransHomeScreen) {
				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			} else {
				finish();
			}

			return true;
		case R.id.action_closest_stations_map:
			ActivityUtils.startClosestStationsMap(context,
					getSupportFragmentManager(), false);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Pass any configuration change to the drawer toggles
		if (mDrawerToggle != null) {
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	/**
	 * Set up the action bar
	 */
	private void initActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.droid_trans_title));
	}

	/**
	 * Initialize the navigation drawer
	 */
	private void initNavigationDrawer() {

		actionBar = getSupportActionBar();
		actionBar.setTitle(getString(R.string.app_sofbus24));

		// Enable ActionBar app icon to behave as action to toggle nav
		// drawerActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// Generate the titles of each row
		navigationItems = Utils.initNavigationDrawerItems(context);

		// Locate the DrawerLayout in the layout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer_shadow,
				GravityCompat.START);

		// Locate ListView in the layout
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer_listview);
		mMenuAdapter = new NavDrawerArrayAdapter(context, navigationItems);
		mDrawerList.setAdapter(mMenuAdapter);
		mDrawerList.setOnItemClickListener(new NavDrawerHelper(context, null,
				null, mDrawerLayout, mDrawerList, navigationItems)
				.getDrawerItemClickListener());

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(context, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_navigation_drawer_open,
				R.string.app_navigation_drawer_close) {

			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				mMenuAdapter.notifyDataSetChanged();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

}