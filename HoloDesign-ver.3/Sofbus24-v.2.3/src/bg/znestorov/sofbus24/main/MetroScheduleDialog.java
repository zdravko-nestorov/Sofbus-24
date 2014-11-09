package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class MetroScheduleDialog extends MetroSchedule {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtils.showAsPopup(this, false);
		super.onCreate(savedInstanceState);

		ActivityUtils.forceTabs(MetroScheduleDialog.this);
	}

}