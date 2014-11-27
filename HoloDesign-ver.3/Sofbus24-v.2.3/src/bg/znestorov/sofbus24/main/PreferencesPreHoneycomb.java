package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.KeyEvent;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.preferences.ResetSettingsDialog;
import bg.znestorov.sofbus24.preferences.RestartApplicationDialog;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.analytics.GoogleAnalytics;

@SuppressWarnings("deprecation")
public class PreferencesPreHoneycomb extends SherlockPreferenceActivity
		implements OnSharedPreferenceChangeListener {

	private Activity context;
	private GlobalEntity globalContext;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		addPreferencesFromResource(R.xml.preferences);

		// Get the application and current activity context
		context = PreferencesPreHoneycomb.this;
		globalContext = (GlobalEntity) getApplicationContext();

		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.pref_title));
	}

	@Override
	public void onResume() {
		super.onStop();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getSupportMenuInflater().inflate(R.menu.activity_preferences_actions,
				menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (globalContext.isHasToRestart()) {
				restartApplication(false);
			} else {
				finish();
			}

			return true;
		case R.id.action_pref_reset:
			resetPreferences();
			return true;
		case R.id.action_pref_info_details:
			Intent aboutIntent;
			if (globalContext.isPhoneDevice()) {
				aboutIntent = new Intent(context, About.class);
			} else {
				aboutIntent = new Intent(context, AboutDialog.class);
			}
			startActivity(aboutIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && globalContext.isHasToRestart()) {
			restartApplication(false);

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(Constants.PREFERENCE_KEY_FAVOURITES_EXPANDED)) {
			globalContext.setFavouritesChanged(true);
		}

		if (key.equals(Constants.PREFERENCE_KEY_APP_LANGUAGE)) {
			globalContext.setHasToRestart(true);
		}

		if (key.equals(Constants.PREFERENCE_KEY_GOOGLE_ANALYTICS)) {
			GoogleAnalytics
					.getInstance(globalContext)
					.setAppOptOut(
							!sharedPreferences
									.getBoolean(
											key,
											Constants.PREFERENCE_DEFAULT_VALUE_GOOGLE_ANALYTICS));
		}
	}

	/**
	 * Restart the application after showing an AlertDialog
	 * 
	 * @param isResetted
	 *            indicates if the method is invoked in case of settings reset.
	 *            If so, and no restart is wanted at this point, leave at the
	 *            current screen and keep the information about application
	 *            restart
	 */
	public void restartApplication(boolean isResetted) {
		RestartApplicationDialog.showRestartApplicationDialog(context,
				isResetted).show();
	}

	/**
	 * Reset the preferences to default
	 */
	private void resetPreferences() {
		ResetSettingsDialog.resetPreferences(context, this).show();
	}
}
