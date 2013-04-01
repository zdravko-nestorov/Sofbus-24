package bg.znestorov.sofbus24.main;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.VehicleNames;

public class VehicleTabView extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_view);

		// Setting activity title
		this.setTitle(getString(R.string.veh_ch_name));

		// Filling the vehicles' ArrayLists and showing a ProgressDialog
		// (via AsyncTask)
		Context context = VehicleTabView.this;
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.loading_message_retrieve_vehicles_numbers));
		LoadStationsAsyncTask loadStations = new LoadStationsAsyncTask(context,
				progressDialog);
		loadStations.execute();
	}

	// AsyncTask capable for loading the vehicles
	private class LoadStationsAsyncTask extends AsyncTask<Void, Void, Void> {
		Context context;
		ProgressDialog progressDialog;

		public LoadStationsAsyncTask(Context context,
				ProgressDialog progressDialog) {
			this.context = context;
			this.progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			new VehicleNames(context);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			// TabHost which contain the tabs
			TabHost tabHost = getTabHost();

			// Bus tab
			TabSpec busTab = tabHost.newTabSpec(Constants.VEHICLE_BUS);
			busTab.setIndicator("",
					getResources().getDrawable(R.drawable.bus_tab));
			Intent busIntent = new Intent(context, VehicleListView.class);
			busIntent.putExtra(Constants.KEYWORD_VEHICLE_TYPE,
					Constants.VEHICLE_BUS);
			busTab.setContent(busIntent);

			// Trolley tab
			TabSpec trolleyTab = tabHost.newTabSpec(Constants.VEHICLE_TROLLEY);
			trolleyTab.setIndicator("",
					getResources().getDrawable(R.drawable.trolley_tab));
			Intent trolleyIntent = new Intent(context, VehicleListView.class);
			trolleyIntent.putExtra(Constants.KEYWORD_VEHICLE_TYPE,
					Constants.VEHICLE_TROLLEY);
			trolleyTab.setContent(trolleyIntent);

			// Tram tab
			TabSpec tramTab = tabHost.newTabSpec(Constants.VEHICLE_TRAM);
			tramTab.setIndicator("",
					getResources().getDrawable(R.drawable.tram_tab));
			Intent tramIntent = new Intent(context, VehicleListView.class);
			tramIntent.putExtra(Constants.KEYWORD_VEHICLE_TYPE,
					Constants.VEHICLE_TRAM);
			tramTab.setContent(tramIntent);

			// Adding tabs to the TabHost
			tabHost.addTab(busTab);
			tabHost.addTab(trolleyTab);
			tabHost.addTab(tramTab);
		}
	}

}
