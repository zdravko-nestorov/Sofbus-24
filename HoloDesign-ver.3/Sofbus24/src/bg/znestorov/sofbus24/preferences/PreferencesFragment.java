package bg.znestorov.sofbus24.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

public class PreferencesFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private Activity context;
	private GlobalEntity globalContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// Get the application and current activity context
		context = getActivity();
		globalContext = (GlobalEntity) context.getApplicationContext();

		// Remove the preferences category in case of tablets
		if (!globalContext.isPhoneDevice()) {
			PreferenceCategory preferencesCategory = (PreferenceCategory) findPreference(Constants.PREFERENCE_KEY_FAVOURITES_EXPANDED_CATEGORY);
			PreferenceScreen preferencesScreen = getPreferenceScreen();
			preferencesScreen.removePreference(preferencesCategory);
		}

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
			globalContext.setHasToRestart(true);
		}
	}
}
