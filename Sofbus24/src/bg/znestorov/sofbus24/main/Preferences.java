package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Setting activity title
		this.setTitle(getString(R.string.pref_name));
	}
}
