package bg.znestorov.sofbus24.gps;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.station_database.GPSStation;

// Showing a ProgressDialog once loading the list of stations for the chosen vehicle using an AsyncTask
public class LoadingGPSMap extends AsyncTask<Void, Void, ArrayList<GPSStation>> {

	Context context;
	ArrayList<GPSStation> station_list;
	ProgressDialog progressDialog;

	public LoadingGPSMap(Context context, ArrayList<GPSStation> station_list,
			ProgressDialog progressDialog) {
		this.context = context;
		this.station_list = station_list;
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	@Override
	protected ArrayList<GPSStation> doInBackground(Void... params) {
		setInfo();

		return station_list;
	}

	@Override
	protected void onPostExecute(ArrayList<GPSStation> result) {
		progressDialog.dismiss();
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
								.append("\n")
								.append(context
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
						stationInfo
								.append("\n")
								.append(context.getString(R.string.title_trams))
								.append(vehicle.getNumber());
					} else {
						stationInfo.append(
								context.getString(R.string.title_trams))
								.append(vehicle.getNumber());
					}
				}
			}
		}

		station_list.get(1).setTime_stamp(stationInfo.toString());
	}

}