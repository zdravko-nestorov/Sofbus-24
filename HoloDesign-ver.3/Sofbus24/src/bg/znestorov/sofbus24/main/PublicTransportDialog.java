package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class PublicTransportDialog extends PublicTransport {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtils.showAsPopup(this, false);
		super.onCreate(savedInstanceState);

		ActivityUtils.forceTabs(PublicTransportDialog.this);
	}

}