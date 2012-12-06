package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.gps.HtmlResultSumcChoice;
import bg.znestorov.sofbus24.station_database.GPSStation;

public class VirtualBoardsStationChoice extends ListActivity {

	private Context context;
	List<GPSStation> values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_station_choice);

		context = VirtualBoardsStationChoice.this;

		String ss_transfer = getIntent().getStringExtra(
				VirtualBoards.keyHtmlResult);

		String stationCode = null;
		String htmlSrc = null;

		if (ss_transfer != null && !"".equals(ss_transfer)
				&& !ss_transfer.contains(VirtualBoards.htmlErrorMessage)
				&& !ss_transfer.contains(VirtualBoards.captchaErrorMessage)) {

			String[] tempArray = ss_transfer.split("SEPARATOR");
			stationCode = tempArray[0];
			htmlSrc = tempArray[1];

			HtmlResultSumcChoice result = new HtmlResultSumcChoice(stationCode,
					htmlSrc);
			values = new ArrayList<GPSStation>();
			values = result.showResult();

			// Use the SimpleCursorAdapter to show the
			// elements in a ListView
			ArrayAdapter<GPSStation> adapter = new ArrayAdapter<GPSStation>(
					this, android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
		} else {
			this.setTitle(getString(R.string.gps_station_choice_error_internet));
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		GPSStation station = (GPSStation) getListAdapter().getItem(position);
		String selectedRow = station.getName();
		Toast.makeText(this, selectedRow, Toast.LENGTH_SHORT).show();

		String[] coordinates = { station.getLat(), station.getLon() };
		HtmlRequestSumc sumc = new HtmlRequestSumc();

		sumc.getInformation(context, station.getId(), coordinates);
	}

}