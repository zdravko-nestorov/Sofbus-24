package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class About extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Setting activity title
		String appVersion;
		try {
			appVersion = getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			appVersion = null;
		}
		this.setTitle(String
				.format(getString(R.string.about_label), appVersion));

		// Set up click listeners for all the buttons
		View aboutOKButton = findViewById(R.id.about_ok_button);
		aboutOKButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_ok_button:
			finish();
			break;
		}
	}
}