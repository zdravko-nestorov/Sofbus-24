package bg.znestorov.sofbus24.main;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfterLast;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;
import static bg.znestorov.sofbus24.utils.StationCoordinates.getLocation;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;

import android.R.drawable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.info_station.HtmlRequestStation;
import bg.znestorov.sofbus24.info_station.HtmlResultStation;
import bg.znestorov.sofbus24.schedule_stations.Direction;
import bg.znestorov.sofbus24.schedule_stations.DirectionTransfer;
import bg.znestorov.sofbus24.schedule_stations.Station;
import bg.znestorov.sofbus24.schedule_stations.StationAdapter;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.utils.Constants;

public class StationListView extends Activity {

	ListView listView;

	Context context;
	FavouritesDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station);

		// Setting activity title
		this.setTitle(getString(R.string.st_ch_name));

		context = StationListView.this;
		datasource = new FavouritesDataSource(context);

		DirectionTransfer directionTransfer;
		try {
			directionTransfer = (DirectionTransfer) getIntent().getSerializableExtra(Constants.KEYWORD_BUNDLE_DIRECTION_TRANSFER);
		} catch (Exception e) {
			directionTransfer = null;
		}

		// Getting the ListView box from "activity_station" layout
		listView = (ListView) findViewById(R.id.list_view_stations);

		ArrayList<Station> stations = new ArrayList<Station>();

		if (directionTransfer.getChoice() == 0) {
			Direction direction = directionTransfer.getDirection1();
			for (int i = 0; i < direction.getStations().size(); i++) {
				stations.add(new Station(direction, i));
			}

			listView.setAdapter(new StationAdapter(context, stations));
			registerForContextMenu(listView);
		} else {
			Direction direction = directionTransfer.getDirection2();
			for (int i = 0; i < direction.getStations().size(); i++) {
				stations.add(new Station(direction, i));
			}

			listView.setAdapter(new StationAdapter(context, stations));
			registerForContextMenu(listView);
		}

		// Setting the first TextView in the Activity
		TextView vehicleNumber = (TextView) findViewById(R.id.vehicle_text_view);
		TextView vehicleDirection = (TextView) findViewById(R.id.direction_text_view);

		String vehicleType = stations.get(0).getVehicleType();
		String vehicleNumberText = stations.get(0).getVehicleNumber();
		String vehicleDirectionText = stations.get(0).getDirection();

		if (vehicleType.equals(context.getString(R.string.title_bus))) {
			vehicleNumber.setText(vehicleType + " ¹ " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
		} else if (vehicleType.equals(context.getString(R.string.title_trolley))) {
			vehicleNumber.setText(vehicleType + " ¹ " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
		} else {
			vehicleNumber.setText(vehicleType + " ¹ " + vehicleNumberText);
			vehicleDirection.setText(vehicleDirectionText);
		}

		// Setting ListView listener (if an item is clicked)
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Station station = (Station) listView.getAdapter().getItem(position);
				String selectedValue = station.getStation();
				Toast.makeText(context, selectedValue, Toast.LENGTH_SHORT).show();

				// Getting the station number
				String stationCode = station.getStation();
				if (stationCode.contains("(") && stationCode.contains(")")) {
					stationCode = getValueAfterLast(stationCode, "(");
					stationCode = getValueBefore(stationCode, ")");
				}

				// Getting the HtmlResult and showing a ProgressDialog
				Context context = StationListView.this;
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context.getString(R.string.loading_message_retrieve_schedule_info));
				LoadStationAsyncTask loadStationAsyncTask = new LoadStationAsyncTask(context, progressDialog, station, stationCode);
				loadStationAsyncTask.execute();
			}
		});
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_station_list_context, menu);

		// Set menu title and header
		menu.setHeaderTitle(getString(R.string.st_list_cont_menu_header));
		menu.setHeaderIcon(drawable.ic_menu_info_details);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		Station station = (Station) listView.getAdapter().getItem((int) info.id);
		String selectedValue = station.getStation();

		// Getting the name and the number of the station
		String stationCode = selectedValue;
		if (stationCode.contains("(") && stationCode.contains(")")) {
			stationCode = stationCode.substring(stationCode.indexOf("(") + 1, stationCode.indexOf(")"));
		}

		switch (item.getItemId()) {
		case R.id.st_list_schedule:
			// Getting the HtmlResult and showing a ProgressDialog
			Context context = StationListView.this;
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(context.getString(R.string.loading_message_retrieve_schedule_info));
			LoadStationAsyncTask loadStationAsyncTask = new LoadStationAsyncTask(context, progressDialog, station, stationCode);
			loadStationAsyncTask.execute();

			Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
			break;
		case R.id.st_list_gps:
			new HtmlRequestSumc().getInformation(StationListView.this, stationCode, Constants.SCHEDULE_GPS_PARAM, null);

			Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
			break;
		}

		return true;
	}

	// AsyncTask capable of loading the HttpRequestTimeStamp
	private class LoadStationAsyncTask extends AsyncTask<Void, Void, String> {
		Context context;
		ProgressDialog progressDialog;
		Station station;
		String stationCode;

		// Http Get parameter used to create/abort the HTTP connection
		HttpGet httpGet;

		public LoadStationAsyncTask(Context context, ProgressDialog progressDialog, Station station, String stationCode) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.station = station;
			this.stationCode = stationCode;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String htmlResult = null;

			try {
				HtmlRequestStation htmlRequestStation = new HtmlRequestStation(station);
				httpGet = htmlRequestStation.createStationRequest();
				htmlResult = htmlRequestStation.getInformation(httpGet);
			} catch (Exception e) {
				htmlResult = null;
			}

			return htmlResult;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();

			// HtmlResult processing and creating an time_stamp
			String time_stamp = HtmlResultStation.showResult(context, result);

			// Getting the coordinates of the station
			String[] coordinates = getLocation(context, stationCode);

			// New activity parameters
			Bundle bundle = new Bundle();
			Intent stationInfoIntent = new Intent(context, StationInfoMap.class);
			bundle.putSerializable(Constants.KEYWORD_BUNDLE_STATION, station);
			stationInfoIntent.putExtras(bundle);

			if (time_stamp != null && !"".equals(time_stamp) && time_stamp.length() > 2 && coordinates != null && !"".equals(coordinates)
					&& coordinates.length > 0) {

				// Fixing the time_stamp
				time_stamp = time_stamp.substring(1, time_stamp.length() - 1);
				station.setTime_stamp(time_stamp);

				// Setting the map coordinates into the station object
				station.setCoordinates(coordinates);

				startActivityForResult(stationInfoIntent, 1);
			} else if (time_stamp != null && !"".equals(time_stamp) && time_stamp.length() > 2
					&& (coordinates == null || "".equals(coordinates) || coordinates.length == 0)) {

				// Fixing the time_stamp
				time_stamp = time_stamp.substring(1, time_stamp.length() - 1);
				station.setTime_stamp(time_stamp);

				startActivityForResult(stationInfoIntent, 1);
			} else {
				Intent intent = new Intent(context, VirtualBoardsStationChoice.class);

				intent.putExtra(Constants.KEYWORD_HTML_RESULT, Constants.SCHEDULE_NO_INFO);
				context.startActivity(intent);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			progressDialog.dismiss();
			httpGet.abort();
		}
	}
}