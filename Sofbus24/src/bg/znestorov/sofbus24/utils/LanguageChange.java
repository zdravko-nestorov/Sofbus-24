package bg.znestorov.sofbus24.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class LanguageChange {

	private LanguageChange() {
	}

	private static String getUserLocale(Context context) {
		// Get SharedPreferences from option menu
		final SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		// Get "language" value from the Shared Preferences
		final String language = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_LANGUAGE,
				Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);

		return language;
	}

	public static void selectLocale(Context context) {
		Configuration config = new Configuration(context.getResources()
				.getConfiguration());
		String language = getUserLocale(context);

		if ("en".equals(language)) {
			config.locale = Locale.CANADA;
		} else {
			config.locale = Locale.getDefault();
		}

		context.getResources().updateConfiguration(config,
				context.getResources().getDisplayMetrics());
	}
}
