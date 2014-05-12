package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import bg.znestorov.sofbus24.edit.tabs.EditTabsFragment;
import bg.znestorov.sofbus24.entity.Config;

public class EditTabs extends FragmentActivity {

	private Activity context;
	private Bundle savedInstanceState;

	private static final String FRAGMENT_TAG_NAME = "EditTabs Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sofbus24_edit_tabs);

		// Get the current context and create a SavedInstanceState object
		context = EditTabs.this;
		this.savedInstanceState = savedInstanceState;

		startFragment();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Create and start a new EditTabsFragment with all needed information
	 */
	private void startFragment() {
		Fragment fragment;

		if (savedInstanceState == null) {
			fragment = EditTabsFragment.newInstance(new Config(context));
		} else {
			fragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG_NAME);
		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.metro_schedule_fragment, fragment,
						FRAGMENT_TAG_NAME).commit();
	}
}
