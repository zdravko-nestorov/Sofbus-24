package com.example.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;

// Showing a ProgressDialog once loading the list of stations for the chosen vehicle using an AsyncTask
public class LoadingDialog extends AsyncTask<Void, Void, Void> {
	ProgressDialog progressDialog;

	public LoadingDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		progressDialog.dismiss();
	}

}