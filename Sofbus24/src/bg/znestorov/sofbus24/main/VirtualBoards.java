package bg.znestorov.sofbus24.main;

import static bg.znestorov.sofbus24.utils.StationCoordinates.getLocation;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
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
import bg.znestorov.sofbus24.gps.GPSStationAdapter;
import bg.znestorov.sofbus24.gps.HtmlResultSumc;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.station_database.GPSStation;

public class VirtualBoards extends ListActivity {

	Context context;
	Builder dialog;

	// Key word used to transfer data between activities (from HtmlResult)
	public static final String keyHtmlResult = "HTML_Result";

	// Possible errors after extracting the information from SUMC
	public static final String htmlErrorMessage = "HTML_ERROR";
	public static final String captchaErrorMessage = "CAPTCHA_ERROR";

	// Time_Stamp message
	private static final String unknown = "INCORRECT";
	private static final String noInfo = "В момента няма информация за спирка";
	private static final String noBus = "не съществува.";

	// ArrayList of GPSStations
	ArrayList<GPSStation> station_list = new ArrayList<GPSStation>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = VirtualBoards.this;

		dialog = new AlertDialog.Builder(VirtualBoards.this);

		String stationCode = null;
		String htmlSrc = null;
		String lat, lon;

		String ss_transfer = getIntent().getStringExtra(keyHtmlResult);

		if (ss_transfer != null && !"".equals(ss_transfer)
				&& !ss_transfer.contains(htmlErrorMessage)
				&& !ss_transfer.contains(captchaErrorMessage)) {

			String[] tempArray = ss_transfer.split("SEPARATOR");
			stationCode = tempArray[0];
			htmlSrc = tempArray[1];
			lat = tempArray[2];
			lon = tempArray[3];

			HtmlResultSumc result = new HtmlResultSumc(context, stationCode,
					htmlSrc);
			station_list = new ArrayList<GPSStation>();
			station_list = result.showResult();
			String time_stamp = station_list.get(0).getTime_stamp();

			// Error with the HTML source code (unknown)
			if (time_stamp.contains(unknown)) {
				showErrorDialog();
				// No such station
			} else if (time_stamp.contains(noBus)) {
				// If the station code is not empty
				if (!"".equals(stationCode)) {
					showNoBusDialog(stationCode);
				} else {
					showNoBusEmptyDialog();
				}
				// No results for the selected station
			} else if (time_stamp.contains(noInfo)) {
				showNoInfoDialog(station_list.get(0).getName());
			} else {
				// Getting the coordinates from FAVOURITES
				if (lat != null && !lat.equals("EMPTY") && lon != null
						&& !lon.equals("EMPTY")) {
					station_list.get(0).setLat(lat);
					station_list.get(0).setLon(lon);
				} else {
					// Getting the coordinates from the XML file
					String[] coordinates = getLocation(context, stationCode);

					if (coordinates != null && !"".equals(coordinates)) {
						station_list.get(0).setLat(coordinates[0]);
						station_list.get(0).setLon(coordinates[1]);
					} else {
						station_list.get(0).setLat("EMPTY");
						station_list.get(0).setLon("EMPTY");
					}
				}

				GPSStation gpsStation = new GPSStation();

				gpsStation.setName("\"" + station_list.get(0).getName() + "\"");
				gpsStation.setTime_stamp(String.format(
						getString(R.string.st_inf_time),
						result.getInformationTime(htmlSrc)));
				gpsStation.setId(stationCode);
				gpsStation.setCodeO(tempArray[4]);
				station_list.add(0, gpsStation);

				setListAdapter(new GPSStationAdapter(context, station_list));
			}

		} else {
			// Error with Transferring data between activities or with the HTML
			// Request
			showErrorDialog();
		}

	}

	// Error dialog example with custom title and message
	public void showErrorDialog(String title, String msg) {
		dialog.setTitle(title).setMessage(msg).setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK", new OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						finish();
					}
				}).show();
	}

	// Info dialog example with custom title and message
	public void showInfoDialog(String title, String msg) {
		dialog.setTitle(title).setMessage(msg).setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK", new OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {

					}
				}).show();
	}

	// Alert Dialog when UNKNOWN error happens
	public void showErrorDialog() {
		this.setTitle(getString(R.string.gps_error_unknown));
		showErrorDialog(getString(R.string.gps_err_dialog_title),
				getString(R.string.gps_err_dialog_msg));
	}

	// Alert Dialog when NO STATION error happens
	public void showNoBusDialog(String stationName) {
		this.setTitle(String.format(getString(R.string.gps_error_noBus),
				stationName));
		showErrorDialog(HtmlResultSumc.error_noBusStop, String.format(
				HtmlResultSumc.error_retrieve_noBusStop, stationName));
	}

	// Alert Dialog when NO STATION error happens (empty input)
	public void showNoBusEmptyDialog() {
		this.setTitle(getString(R.string.gps_error_noBusEmpty));
		showErrorDialog(HtmlResultSumc.error_noBusStop,
				getString(R.string.gps_error_noBusEmpty) + ".");
	}

	// Alert Dialog when NO BUS error happens
	public void showNoInfoDialog(String stationName) {
		this.setTitle(String.format(getString(R.string.gps_error_noInfo),
				stationName));
		showErrorDialog(HtmlResultSumc.error_noInfo, String.format(
				HtmlResultSumc.error_retrieve_noInfo, stationName));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_gps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final GPSStation gpsStation = station_list.get(1);

		switch (item.getItemId()) {
		case R.id.menu_add_favourite:
			FavouritesDataSource datasource = new FavouritesDataSource(this);

			datasource.open();
			if (datasource.getStation(gpsStation) == null) {
				datasource.createStation(gpsStation);
				showInfoDialog(getString(R.string.gps_fav_dialog_title),
						getString(R.string.st_inf_fav_ok));
			} else {
				showInfoDialog(getString(R.string.gps_err_dialog_title),
						getString(R.string.st_inf_fav_err));
			}
			datasource.close();

			break;

		case R.id.menu_see_map:
			if (!gpsStation.getLat().equals("EMPTY")
					&& !gpsStation.getLon().equals("EMPTY")) {

				// Showing a ProgressDialog
				Bundle bundle = new Bundle();
				Context context = VirtualBoards.this;
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setMessage("Loading...");

				LoadMapAsyncTask loadMap = new LoadMapAsyncTask(bundle,
						context, progressDialog, gpsStation);
				loadMap.execute();
			} else {
				showInfoDialog(getString(R.string.gps_err_dialog_title),
						getString(R.string.gps_error_noCoordinates));
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

		for (int i = 1; i < station_list.size(); i++) {
			GPSStation vehicle = station_list.get(i);

			if (vehicle.getType().equals("Автобус")) {
				if (flag_a) {
					stationInfo.append(", ").append(vehicle.getNumber());
				} else {
					flag_a = true;
					stationInfo.append("Автобуси: ")
							.append(vehicle.getNumber());
				}
			} else if (vehicle.getType().equals("Тролей")) {
				if (flag_tl) {
					stationInfo.append(", ").append(vehicle.getNumber());
				} else {
					flag_tl = true;
					if (flag_a) {
						stationInfo.append("\nТролеи: ").append(
								vehicle.getNumber());
					} else {
						stationInfo.append("Тролеи: ").append(
								vehicle.getNumber());
					}
				}
			} else {
				if (flag_tm) {
					stationInfo.append(", ").append(vehicle.getNumber());
				} else {
					flag_tm = true;
					if (flag_a || flag_tl) {
						stationInfo.append("\nТрамаи: ").append(
								vehicle.getNumber());
					} else {
						stationInfo.append("Трамаи: ").append(
								vehicle.getNumber());
					}
				}
			}
		}

		station_list.get(1).setTime_stamp(stationInfo.toString());
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
			bundle.putSerializable("GPSStation", gpsStation);
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