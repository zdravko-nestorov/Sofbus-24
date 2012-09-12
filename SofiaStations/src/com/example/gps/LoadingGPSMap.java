package com.example.gps;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.station_database.GPSStation;

// Showing a ProgressDialog once loading the list of stations for the chosen vehicle using an AsyncTask
public class LoadingGPSMap extends AsyncTask<Void, Void, ArrayList<GPSStation>> {
	ArrayList<GPSStation> station_list;
	ProgressDialog progressDialog;

	public LoadingGPSMap(ArrayList<GPSStation> station_list,
			ProgressDialog progressDialog) {
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

}