package bg.znestorov.sofbus24.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.Preferences;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;

public class PreferencesFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private Activity context;
	private GlobalEntity globalContext;

	public PreferencesFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// Get the application and current activity context
		context = getActivity();
		globalContext = (GlobalEntity) context.getApplicationContext();

		// Registers a callback to be invoked when a change happens to a
		// preference
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
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

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Constants.PREFERENCE_KEY_FAVOURITES_EXPANDED)) {
			globalContext.setFavouritesChanged(true);
		}

		if (key.equals(Constants.PREFERENCE_KEY_APP_LANGUAGE)) {
			LanguageChange.selectLocale(context);
			Preferences.hasToRestart = true;
		}
	}
}
