package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class About extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Setting activity title
		this.setTitle(getString(R.string.about_label));

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