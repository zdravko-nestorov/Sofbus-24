package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashScreen extends Activity {

	// Set the display time
	private final int SPLASH_DISPLAY_LENGTH = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Obtain the sharedPreference, default to false if not available
		boolean spashScreen = sp.getBoolean("spashScreen", false);

		if (spashScreen) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					// Finish the "splash activity" so it can't be returned to
					SplashScreen.this.finish();

					// Create an Intent that will start the main activity
					Intent sofbus24 = new Intent(SplashScreen.this,
							Sofbus24.class);
					SplashScreen.this.startActivity(sofbus24);
				}
			}, SPLASH_DISPLAY_LENGTH);
		} else {
			// if the splash is not enabled, then finish the activity
			// immediately and go to main
			finish();
			Intent mainIntent = new Intent(SplashScreen.this, Sofbus24.class);
			SplashScreen.this.startActivity(mainIntent);
		}
	}

}
