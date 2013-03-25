package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class VirtualBoardsHelp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Removing title of the window
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_gps_station_help);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);

		// Tapped outside so we finish the activity
		if (!dialogBounds.contains((int) event.getX(), (int) event.getY())) {
			this.finish();
		}

		return super.dispatchTouchEvent(event);
	}
}