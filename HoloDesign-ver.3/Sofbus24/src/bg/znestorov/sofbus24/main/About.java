package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import bg.znestorov.sofbus24.about.RetrieveAppConfiguration;
import bg.znestorov.sofbus24.utils.LanguageChange;

public class About extends Activity {

	private Activity context;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_about);

		// Get the current context
		context = About.this;

		// Find the TextViews over the layout
		TextView aboutInformation = (TextView) findViewById(R.id.about_information);

		// Set the TextViews a formatted text
		String appVersion;
		try {
			appVersion = getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			appVersion = null;
		}
		aboutInformation.setText(Html.fromHtml(String.format(
				getString(R.string.about_information),
				getString(R.string.app_name), appVersion)));

		// Set up the action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setSubtitle(String.format(getString(R.string.about_subtitle),
				appVersion));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.activity_about_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		RetrieveAppConfiguration retrieveAppConfiguration;
		ProgressDialog progressDialog = new ProgressDialog(context);

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_about_update_db:
			progressDialog.setMessage(getString(R.string.about_update_db));
			retrieveAppConfiguration = new RetrieveAppConfiguration(context,
					progressDialog, false);
			retrieveAppConfiguration.execute();
			return true;
		case R.id.action_about_update_app:
			progressDialog.setMessage(getString(R.string.about_update_app));
			retrieveAppConfiguration = new RetrieveAppConfiguration(context,
					progressDialog, true);
			retrieveAppConfiguration.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
