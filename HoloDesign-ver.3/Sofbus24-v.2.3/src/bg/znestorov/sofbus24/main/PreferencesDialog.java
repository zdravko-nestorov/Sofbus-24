package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.view.MotionEvent;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class PreferencesDialog extends Preferences {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtils.showAsPopup(this, false, true);
		super.onCreate(savedInstanceState);
		this.setFinishOnTouchOutside(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// If we've received a touch notification that the user has touched
		// outside the app, finish the activity.
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			super.onBackPressed();
			return true;
		}

		// Delegate everything else to Activity.
		return super.onTouchEvent(event);
	}

}