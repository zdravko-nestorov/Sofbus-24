package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.history.HistoryEntity;
import bg.znestorov.sofbus24.history.HistoryFragment;
import bg.znestorov.sofbus24.history.HistoryOfSearches;
import bg.znestorov.sofbus24.utils.LanguageChange;

/**
 * History activity containing information about the searches
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class History extends FragmentActivity {

	private Activity context;
	private ActionBar actionBar;

	private static final String FRAGMENT_TAG_NAME = "HISTORY FRAGMENT";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_history);

		// Get the current context
		context = History.this;

		// Set the action bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.history_title));

		// Get the layout fields in the view
		final ProgressBar loadingHistory = (ProgressBar) findViewById(R.id.history_loading);
		final View historyFragment = findViewById(R.id.history_fragment);

		// Start an empty fragment
		startFragment(savedInstanceState, new ArrayList<HistoryEntity>());

		// Start an asynchrnic task to load the data from the preferences file
		new AsyncTask<Void, Void, ArrayList<HistoryEntity>>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				loadingHistory.setVisibility(View.VISIBLE);
				historyFragment.setVisibility(View.INVISIBLE);
			}

			@Override
			protected ArrayList<HistoryEntity> doInBackground(Void... params) {
				return HistoryOfSearches.getInstance(context)
						.getHistoryOfSearches(context);
			}

			@Override
			protected void onPostExecute(ArrayList<HistoryEntity> historyList) {
				super.onPostExecute(historyList);

				loadingHistory.setVisibility(View.INVISIBLE);
				historyFragment.setVisibility(View.VISIBLE);

				refreshFragment(historyList);
			}

		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_history_actions, menu);

		return super.onCreateOptionsMenu(menu);
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
	 * Create and start/restart a new HistoryFragment with all searches from the
	 * SharedPreferences file
	 * 
	 * @param savedInstanceState
	 *            the state of the current activity
	 * @param historyList
	 *            the list with the history searches from the SharedPreferences
	 *            file
	 */
	private void startFragment(Bundle savedInstanceState,
			ArrayList<HistoryEntity> historyList) {
		Fragment historyFragment;

		if (savedInstanceState == null) {
			historyFragment = HistoryFragment.newInstance(historyList);
		} else {
			historyFragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG_NAME);
		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.history_fragment, historyFragment,
						FRAGMENT_TAG_NAME).commit();
	}

	/**
	 * Refresh the existing HistoryFragment with all searches from the
	 * SharedPreferences file
	 * 
	 * @param historyList
	 *            the list with the history searches from the SharedPreferences
	 *            file
	 */
	private void refreshFragment(ArrayList<HistoryEntity> historyList) {
		HistoryFragment historyFragment = ((HistoryFragment) getSupportFragmentManager()
				.findFragmentByTag(FRAGMENT_TAG_NAME));
		if (historyFragment != null) {
			historyFragment.onFragmentRefresh(historyList, null);
		}
	}
}
