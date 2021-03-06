package bg.znestorov.sofbus24.main;

import static bg.znestorov.sofbus24.utils.StationCoordinates.getLocation;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.GPSStationAdapter;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.gps.HtmlResultSumc;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

public class VirtualBoards extends Activity {

	ListView listView;

	Activity context;
	Builder dialog;

	// ArrayList of GPSStations
	ArrayList<GPSStation> station_list = new ArrayList<GPSStation>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station);

		// Setting activity title
		this.setTitle(getString(R.string.gps_name));

		context = VirtualBoards.this;
		dialog = new AlertDialog.Builder(VirtualBoards.this);

		String stationCode = null;
		String htmlSrc = null;
		String lat, lon;

		String ss_transfer = getIntent().getStringExtra(
				Constants.KEYWORD_HTML_RESULT);

		if (ss_transfer != null && !"".equals(ss_transfer)
				&& !ss_transfer.contains(Constants.SUMC_HTML_ERROR_MESSAGE)
				&& !ss_transfer.contains(Constants.SUMC_CAPTCHA_ERROR_MESSAGE)) {

			String[] tempArray = ss_transfer
					.split(Constants.GLOBAL_PARAM_SEPARATOR);
			stationCode = tempArray[0];
			htmlSrc = tempArray[1];
			lat = tempArray[2];
			lon = tempArray[3];

			HtmlResultSumc result = new HtmlResultSumc(context, stationCode,
					htmlSrc);
			station_list = new ArrayList<GPSStation>();
			station_list = result.showResult();

			String time_stamp = station_list.get(0).getTime_stamp();

			if (!stationCode.equals(stationCode.replaceAll("\\D+", ""))
					&& !time_stamp.equals(Constants.SUMC_UNKNOWN_INFO)) {
				stationCode = Utils.getStationId(tempArray[1], tempArray[0]);
			}

			// Getting the coordinates from FAVOURITES
			if (lat != null && !lat.equals(Constants.GLOBAL_PARAM_EMPTY)
					&& lon != null && !lon.equals(Constants.GLOBAL_PARAM_EMPTY)) {
				station_list.get(0).setLat(lat);
				station_list.get(0).setLon(lon);
			} else {
				// Getting the coordinates from the XML file
				String[] coordinates = getLocation(context, stationCode);

				if (coordinates != null && !"".equals(coordinates)) {
					station_list.get(0).setLat(coordinates[0]);
					station_list.get(0).setLon(coordinates[1]);
				} else {
					station_list.get(0).setLat(Constants.GLOBAL_PARAM_EMPTY);
					station_list.get(0).setLon(Constants.GLOBAL_PARAM_EMPTY);
				}
			}

			station_list.get(0).setCodeO(tempArray[4]);

			// Getting the ListView box from "activity_station" layout
			listView = (ListView) findViewById(R.id.list_view_stations);
			listView.setAdapter(new GPSStationAdapter(context, station_list));

			// Setting the first TextView in the Activity
			TextView listName = (TextView) findViewById(R.id.vehicle_text_view);
			TextView listTime = (TextView) findViewById(R.id.direction_text_view);
			ImageView refreshButton = (ImageView) findViewById(R.id.refresh_button);

			listName.setText("\"" + station_list.get(0).getName() + " ("
					+ station_list.get(0).getId() + ")\"");
			listTime.setText(String.format(getString(R.string.st_inf_time),
					result.getInformationTime(htmlSrc)));

			// Refresh button functionality
			refreshButton.setVisibility(View.VISIBLE);
			refreshButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.refresh_button:
						new HtmlRequestSumc().getInformation(context,
								station_list.get(0).getId(), station_list
										.get(0).getCodeO(), null);
						break;
					}
				}
			});
		} else {
			// Error with Transferring data between activities or with the HTML
			// Request
			showErrorDialog();
		}

	}

	// Error dialog example with custom title and message
	public void showErrorDialog(String title, String msg) {
		dialog.setTitle(title)
				.setMessage(msg)
				.setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(context.getString(R.string.button_title_ok),
						new OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								finish();
							}
						}).show();
	}

	// Alert Dialog when UNKNOWN error happens
	public void showErrorDialog() {
		this.setTitle(getString(R.string.gps_error_unknown));
		showErrorDialog(getString(R.string.gps_err_dialog_title),
				getString(R.string.gps_map_station_choice_error_summary));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_gps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final GPSStation gpsStation = station_list.get(0);

		switch (item.getItemId()) {
		case R.id.menu_add_favourite:
			FavouritesDataSource datasource = new FavouritesDataSource(this);

			datasource.open();
			if (datasource.getStation(gpsStation) == null) {
				datasource.createStation(gpsStation);
				Toast.makeText(context, R.string.st_inf_fav_ok,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.st_inf_fav_err,
						Toast.LENGTH_SHORT).show();
			}
			datasource.close();

			break;

		case R.id.menu_see_map:
			if (!gpsStation.getLat().equals(Constants.GLOBAL_PARAM_EMPTY)
					&& !gpsStation.getLon()
							.equals(Constants.GLOBAL_PARAM_EMPTY)) {

				// Showing a ProgressDialog
				Bundle bundle = new Bundle();
				Context context = VirtualBoards.this;
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context
						.getString(R.string.loading_message_preview_gps_map));

				LoadMapAsyncTask loadMap = new LoadMapAsyncTask(bundle,
						context, progressDialog, gpsStation);
				loadMap.execute();
			} else {
				Intent intent = new Intent(context,
						VirtualBoardsStationChoice.class);

				intent.putExtra(Constants.KEYWORD_HTML_RESULT,
						Constants.VB_NO_COORDINATES);
				context.startActivity(intent);
			}

			break;
		}

		return true;
	}

	// Getting the needed information about the station and putting it into the
	// TIME_STAMP field
	private void setInfo() {
		StringBuilder stationInfo = new StringBuilder();
		boolean flag_a = false;
		boolean flag_tl = false;
		boolean flag_tm = false;

		for (int i = 0; i < station_list.size(); i++) {
			GPSStation vehicle = station_list.get(i);

			if (vehicle.getType().equals(context.getString(R.string.title_bus))) {
				if (flag_a) {
					stationInfo.append(", ").append(vehicle.getNumber());
				} else {
					flag_a = true;
					stationInfo.append(context.getString(R.string.title_buses))
							.append(vehicle.getNumber());
				}
			} else if (vehicle.getType().equals(
					context.getString(R.string.title_trolley))) {
				if (flag_tl) {
					stationInfo.append(", ").append(vehicle.getNumber());
				} else {
					flag_tl = true;
					if (flag_a) {
						stationInfo
								.append("\n"
										+ context
												.getString(R.string.title_trolleys))
								.append(vehicle.getNumber());
					} else {
						stationInfo.append(
								context.getString(R.string.title_trolleys))
								.append(vehicle.getNumber());
					}
				}
			} else {
				if (flag_tm) {
					stationInfo.append(", ").append(vehicle.getNumber());
				} else {
					flag_tm = true;
					if (flag_a || flag_tl) {
						stationInfo.append(
								"\n" + context.getString(R.string.title_trams))
								.append(vehicle.getNumber());
					} else {
						stationInfo.append(
								context.getString(R.string.title_trams))
								.append(vehicle.getNumber());
					}
				}
			}
		}

		station_list.get(0).setTime_stamp(stationInfo.toString());
	}

	// AsyncTask capable for loading the map
	private class LoadMapAsyncTask extends AsyncTask<Void, Void, Intent> {
		Bundle bundle;
		Context context;
		ProgressDialog progressDialog;
		GPSStation gpsStation;

		public LoadMapAsyncTask(Bundle bundle, Context context,
				ProgressDialog progressDialog, GPSStation gpsStation) {
			this.bundle = bundle;
			this.context = context;
			this.progressDialog = progressDialog;
			this.gpsStation = gpsStation;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Intent doInBackground(Void... params) {
			setInfo();
			Intent stationInfoIntent = new Intent(context,
					VirtualBoardsMap.class);
			bundle.putSerializable(Constants.KEYWORD_BUNDLE_GPS_STATION,
					gpsStation);
			stationInfoIntent.putExtras(bundle);

			return stationInfoIntent;
		}

		@Override
		protected void onPostExecute(Intent result) {
			progressDialog.dismiss();

			startActivity(result);
		}
	}

}