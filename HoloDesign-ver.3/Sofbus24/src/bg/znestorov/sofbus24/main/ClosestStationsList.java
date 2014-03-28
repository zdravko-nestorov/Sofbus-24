package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import bg.znestorov.sofbus24.closest.stations.list.ClosestStationsListFragment;

public class ClosestStationsList extends Activity {

	private ActionBar actionBar;

	private ClosestStationsListFragment closestStationsListFragment = new ClosestStationsListFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the action bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.cs_list_title));

		// Get from Bundle
		Bundle extras = getIntent().getExtras();
		closestStationsListFragment.setArguments(extras);

		// Set the ClosestStationsListFragment
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, closestStationsListFragment)
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(
				R.menu.activity_closest_stations_list_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_cs_list_refresh:
			closestStationsListFragment.update(ClosestStationsList.this, null);
			return true;
		case R.id.action_cs_list_map:
			// TODO: Set the event on clicking the button
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
