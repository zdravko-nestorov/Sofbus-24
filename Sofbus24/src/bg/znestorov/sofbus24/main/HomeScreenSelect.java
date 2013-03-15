package bg.znestorov.sofbus24.main;

import bg.znestorov.sofbus24.utils.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class HomeScreenSelect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nome_screen_choice);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Get SharedPreferences from option menu
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Get "homeScreen" value from the Shared Preferences
		String homeScreenVersion = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_HOME_SCREEN,
				Constants.PREFERENCE_DEFAULT_VALUE_HOME_SCREEN);

		if ("version_1".equals(homeScreenVersion)) {
			finish();
			Intent mainIntent = new Intent(HomeScreenSelect.this,
					Sofbus24.class);
			HomeScreenSelect.this.startActivity(mainIntent);
		} else {
			finish();
			Intent mainIntent = new Intent(HomeScreenSelect.this,
					Sofbus24.class);
			HomeScreenSelect.this.startActivity(mainIntent);
		}
	}

}
