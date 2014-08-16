package bg.znestorov.sofbus24.history;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.HtmlRequestCodesEnum;
import bg.znestorov.sofbus24.entity.RefreshableListFragment;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.history.HistoryDeleteAllDialog.OnDeleteAllHistoryListener;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.virtualboards.RetrieveVirtualBoards;

/**
 * History ListFragment containing information about the searches
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class HistoryFragment extends ListFragment implements
		RefreshableListFragment, OnDeleteAllHistoryListener {

	private Activity context;

	private HistoryAdapter historyAdapter;
	private ArrayList<HistoryEntity> historyList = new ArrayList<HistoryEntity>();

	public static HistoryFragment newInstance(
			ArrayList<HistoryEntity> historyList) {
		HistoryFragment historyFragment = new HistoryFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_HISTORY_LIST, historyList);
		historyFragment.setArguments(bundle);

		return historyFragment;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(
				R.layout.activity_history_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the information from the History activity
		Bundle bundle = this.getArguments();
		historyList.clear();
		historyList.addAll((ArrayList<HistoryEntity>) bundle
				.getSerializable(Constants.BUNDLE_HISTORY_LIST));

		// Create and set the list adapter to the ListFragment
		historyAdapter = new HistoryAdapter(context, historyList);
		setListAdapter(historyAdapter);

		// Activate the option menu
		setHasOptionsMenu(true);

		return fragmentView;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onFragmentRefresh(Object newHistoryList, String emptyText) {
		ArrayAdapter<HistoryEntity> historyAdapter = (HistoryAdapter) getListAdapter();

		if (historyAdapter != null) {
			historyList.clear();
			if (newHistoryList != null) {
				historyList.addAll((ArrayList<HistoryEntity>) newHistoryList);
			}

			historyAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int searchesCount = historyList.size();

		switch (item.getItemId()) {
		case R.id.action_history_top:
			if (searchesCount > 0) {
				getListView().setSelectionFromTop(0, 0);
			}

			return true;
		case R.id.action_history_delete_all:
			// Check if there are any registred searches
			if (searchesCount > 0) {
				DialogFragment dialogFragment = HistoryDeleteAllDialog
						.newInstance();
				dialogFragment.setTargetFragment(this, 0);

				// TODO: Find a way to retain the DialogFragment on orientation
				// changes
				dialogFragment.show(getFragmentManager(), "dialog");
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
		StationEntity station = stationDatasource.getStation(stationNumber);
		stationDatasource.close();

		// Check if the station is existing in the database
		if (station == null) {
			station = new StationEntity();
			station.setNumber(stationNumber);
			station.setName(stationName);
		}

		// Check the type of station and retrieve the information accordingly
		switch (history.getHistoryType()) {
		case BTT:
			RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
					context, null, station, HtmlRequestCodesEnum.SINGLE_RESULT);
			retrieveVirtualBoards.getSumcInformation();
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

	@Override
	public void onDeleteAllHistoryClicked() {
		HistoryOfSearches.getInstance(context).clearHistoryOfSearches();
		historyList.clear();
		historyAdapter.notifyDataSetChanged();

		Toast.makeText(
				context,
				Html.fromHtml(getString(R.string.history_menu_remove_all_toast)),
				Toast.LENGTH_SHORT).show();
	}
}
