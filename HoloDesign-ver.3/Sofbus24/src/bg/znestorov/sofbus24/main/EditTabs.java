package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.edit.tabs.EditTabsFragment;
import bg.znestorov.sofbus24.entity.Config;

public class EditTabs extends FragmentActivity {

	private Activity context;
	private Bundle savedInstanceState;

	private ActionBar actionBar;

	private Button cancelChanges;
	private Button saveChanges;

	private Fragment editTabsFragment;

	private static final String FRAGMENT_TAG_NAME = "EditTabs Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sofbus24_edit_tabs);

		// Get the current context and create a SavedInstanceState object
		context = EditTabs.this;
		this.savedInstanceState = savedInstanceState;

		initLayoutFields();
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
	 * Initialize the layout fields (ActionBar, ImageViews and TextVies)
	 */
	private void initLayoutFields() {
		// Get the Action Bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get the Cancel and Save Buttons
		cancelChanges = (Button) findViewById(R.id.sofbus_24_edit_tabs_cancel);
		saveChanges = (Button) findViewById(R.id.sofbus_24_edit_tabs_save);
		actionsOverButtons();
	}

	/**
	 * Set onClickListeners over the ImageButtons
	 */
	private void actionsOverButtons() {
		// Set onClickListner over the Cancel Button
		cancelChanges.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		// Set onClickListner over the Cancel Button
		saveChanges.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Config newConfig = ((EditTabsFragment) editTabsFragment)
						.getNewConfig();
				Configuration.editTabConfigurationFileds(context, newConfig);

				Sofbus24.setHomeScreenChanged(true);
				finish();
			}
		});
	}

	/**
	 * Create and start a new EditTabsFragment with all needed information
	 */
	private void startFragment() {
		if (savedInstanceState == null) {
			editTabsFragment = EditTabsFragment
					.newInstance(new Config(context));
		} else {
			editTabsFragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG_NAME);
		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.metro_schedule_fragment, editTabsFragment,
						FRAGMENT_TAG_NAME).commit();
	}
}
