package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import bg.znestorov.sofbus24.edit.tabs.EditTabsFragment;

public class EditTabs extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sofbus24_edit_tabs);

		EditTabsFragment fragment = EditTabsFragment.newInstance(null);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.metro_schedule_fragment, fragment).commit();
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
}
