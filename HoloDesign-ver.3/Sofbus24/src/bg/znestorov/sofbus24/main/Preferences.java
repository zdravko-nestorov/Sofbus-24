package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.preferences.PreferencesFragment;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class Preferences extends Activity {

	private Activity context;
	private GlobalEntity globalContext;
	private ActionBar actionBar;

	private PreferencesFragment preferencesFragment = new PreferencesFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);

		// Get the application and current activity context
		context = Preferences.this;
		globalContext = (GlobalEntity) getApplicationContext();

		// Set up the action bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.pref_title));

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, preferencesFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.activity_preferences_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (globalContext.isHasToRestart()) {
				restartApplication();
			} else {
				finish();
			}

			return true;
		case R.id.action_pref_reset:
			resetPreferences();
			return true;
		case R.id.action_pref_info_details:
			Intent aboutIntent = new Intent(context, About.class);
			startActivity(aboutIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && globalContext.isHasToRestart()) {
			restartApplication();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Restart the application after showing an AlertDialog
	 */
	private void restartApplication() {
		OnClickListener positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		};

		OnClickListener negativeOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				globalContext.setHasToRestart(false);
				finish();
			}
		};

		ActivityUtils.showCustomAlertDialog(context,
				android.R.drawable.ic_menu_info_details,
				context.getString(R.string.app_dialog_title_important),
				Html.fromHtml(context.getString(R.string.pref_restart_app)),
				context.getString(R.string.app_button_yes),
				positiveOnClickListener,
				context.getString(R.string.app_button_no),
				negativeOnClickListener);
	}

	/**
	 * Reset the preferences to default
	 */
	private void resetPreferences() {
		OnClickListener positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = preferences.edit();
				editor.clear();
				editor.commit();

				// Check if the user wants to restart the application
				restartApplication();
			}
		};

		ActivityUtils.showCustomAlertDialog(context,
				android.R.drawable.ic_menu_info_details,
				context.getString(R.string.app_dialog_title_important),
				context.getString(R.string.pref_reset),
				context.getString(R.string.app_button_yes),
				positiveOnClickListener,
				context.getString(R.string.app_button_no), null);
	}
}
