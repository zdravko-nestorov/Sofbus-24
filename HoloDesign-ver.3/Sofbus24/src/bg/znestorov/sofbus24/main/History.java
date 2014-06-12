package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.HtmlRequestCodes;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.history.HistoryAdapter;
import bg.znestorov.sofbus24.history.HistoryEntity;
import bg.znestorov.sofbus24.history.HistoryOfSearches;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.virtualboards.RetrieveVirtualBoards;

/**
 * History ListActivity containing information about the searches
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class History extends ListActivity {

	private Activity context;
	private ActionBar actionBar;

	private ArrayList<HistoryEntity> historyList = new ArrayList<HistoryEntity>();
	private ArrayAdapter<HistoryEntity> historyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		// Get the current context
		context = History.this;

		// Set the action bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.history_title));

		// Get the list with the history of searches from the shared preferences
		historyList = HistoryOfSearches.getInstance(context)
				.getHistoryOfSearches();

		// Create the History ListAdapter
		historyAdapter = new HistoryAdapter(context, historyList);

		// Set the ListAdapter
		setListAdapter(historyAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Refresh the history list view
		if (historyAdapter != null) {
			historyList.clear();
			historyList.addAll(HistoryOfSearches.getInstance(context)
					.getHistoryOfSearches());
			historyAdapter.notifyDataSetChanged();
		}
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
		case R.id.action_history_delete_all:
			int searchesCount = historyList.size();

			// Check if there are any registred searches
			if (searchesCount > 0) {
				OnClickListener positiveOnClickListener = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						HistoryOfSearches.getInstance(context)
								.clearHistoryOfSearches();
						historyList.clear();
						historyAdapter.notifyDataSetChanged();

						Toast.makeText(
								context,
								Html.fromHtml(getString(R.string.history_menu_remove_all_toast)),
								Toast.LENGTH_SHORT).show();
					}
				};

				ActivityUtils
						.showCustomAlertDialog(
								context,
								android.R.drawable.ic_menu_delete,
								getString(R.string.app_dialog_title_important),
								Html.fromHtml(getString(R.string.history_menu_remove_all_confirmation)),
								getString(R.string.app_button_yes),
								positiveOnClickListener,
								getString(R.string.app_button_no), null);
			} else {
				Toast.makeText(
						context,
						Html.fromHtml(getString(R.string.history_menu_remove_all_empty_toast)),
						Toast.LENGTH_SHORT).show();
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		HistoryEntity history = (HistoryEntity) getListAdapter().getItem(
				position);

		// Get the station number and station name of the search
		String stationNumber = Utils.getValueBetween(history.getHistoryValue(),
				"(", ")");
		String stationName = Utils.getValueBefore(history.getHistoryValue(),
				"(");

		// Get the corresponding station to the station number via the stations
		// database
		StationsDataSource stationDatasource = new StationsDataSource(context);
		stationDatasource.open();
		Station station = stationDatasource.getStation(stationNumber);
		stationDatasource.close();

		// Check if the station is existing in the database
		if (station == null) {
			station = new Station();
			station.setNumber(stationNumber);
			station.setName(stationName);
		}

		// Check the type of station and retrieve the information accordingly
		switch (history.getHistoryType()) {
		case BTT:
			RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
					context, null, station, HtmlRequestCodes.SINGLE_RESULT);
			retrieveVirtualBoards.getSumcInformation();
			Toast.makeText(context, history.getHistoryValue(),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			// Set the metro station URL address
			station.setCustomField(String.format(Constants.METRO_STATION_URL,
					station.getNumber()));

			// Getting the Metro schedule from the station URL address
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(Html.fromHtml(String.format(
					getString(R.string.metro_loading_schedule),
					station.getName(), station.getNumber())));
			RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
					context, progressDialog, station);
			retrieveMetroSchedule.execute();
			break;
		}
	}
}
