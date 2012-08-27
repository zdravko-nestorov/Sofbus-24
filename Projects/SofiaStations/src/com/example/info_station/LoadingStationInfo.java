package com.example.info_station;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.schedule_stations.Station;

// Showing a ProgressDialog once loading the list of stations for the chosen vehicle using an AsyncTask
public class LoadingStationInfo extends AsyncTask<Void, Void, String> {
	Station station;
	ProgressDialog progressDialog;

	public LoadingStationInfo(Station station, ProgressDialog progressDialog) {
		this.station = station;
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		progressDialog.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		HtmlRequestStation htmlStation = new HtmlRequestStation(station);
		String result = HtmlResultStation.showResult(htmlStation.getInformation());

		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		progressDialog.dismiss();
	}

}