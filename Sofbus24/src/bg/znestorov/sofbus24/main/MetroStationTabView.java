package bg.znestorov.sofbus24.main;

import static bg.znestorov.sofbus24.utils.StationCoordinates.getRoute;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import bg.znestorov.sofbus24.metro.MetroDirection;
import bg.znestorov.sofbus24.metro.MetroDirectionTransfer;
import bg.znestorov.sofbus24.schedule_stations.Direction;
import bg.znestorov.sofbus24.utils.Constants;

public class MetroStationTabView extends TabActivity {

	MetroDirectionTransfer mdt;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setting activity title
		this.setTitle(getString(R.string.metro_st_ch_name));

		Bundle bundle = new Bundle();
		context = MetroStationTabView.this;

		try {
			mdt = (MetroDirectionTransfer) getIntent().getSerializableExtra(
					Constants.KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER);
		} catch (Exception e) {
			mdt = null;
		}

		int direction = mdt.getChoice();

		// TabHost which contain the tabs
		TabHost tabHost = getTabHost();

		// Direction 1 tab
		TabSpec tabDirection1 = tabHost.newTabSpec(Constants.DIRECTION_1);
		tabDirection1.setIndicator("",
				getResources().getDrawable(R.drawable.left));
		// Transferring the HtmlResult to ListViewStationChoice - DIRECTION-1
		Intent tabDirection1Intent = new Intent(this, StationListView.class);
		mdt.setChoice(0);
		bundle.putSerializable(
				Constants.KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER, mdt);
		tabDirection1Intent.putExtras(bundle);
		tabDirection1.setContent(tabDirection1Intent);
		tabHost.addTab(tabDirection1);

		// Direction 2 tab
		TabSpec tabDirection2 = tabHost.newTabSpec(Constants.DIRECTION_2);
		tabDirection2.setIndicator("",
				getResources().getDrawable(R.drawable.right));
		// Transferring the HtmlResult to ListViewStationChoice - DIRECTION-1
		Intent tabDirection2Intent = new Intent(this, StationListView.class);
		mdt.setChoice(1);
		bundle.putSerializable(
				Constants.KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER, mdt);
		tabDirection2Intent.putExtras(bundle);
		tabDirection2.setContent(tabDirection2Intent);
		tabHost.addTab(tabDirection2);

		if (direction == 0) {
			tabHost.setCurrentTab(0);
		} else {
			tabHost.setCurrentTab(1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_route, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MetroDirection direction = mdt.getDirectionsList().get(mdt.getChoice());

		switch (item.getItemId()) {
		case R.id.menu_route:
			// Getting the coordinates of the whole route
			String coordinates = getRoute(MetroStationTabView.this,
					direction.getStations());

			// Checking if even one station has coordinates in the DB
			if (coordinates != null) {

				// Showing a ProgressDialog
				Context context = MetroStationTabView.this;
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog
						.setMessage(context
								.getString(R.string.loading_message_preview_info_map_route));

				LoadMapAsyncTask loadMap = new LoadMapAsyncTask(context,
						progressDialog, direction, coordinates);
				loadMap.execute();

			} else {
				new AlertDialog.Builder(this)
						.setTitle(R.string.st_ch_menu_err_title)
						.setMessage(R.string.st_ch_menu_err_msg)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(
								context.getString(R.string.button_title_ok),
								new OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
									}
								}).show();
			}
			break;

		case R.id.menu_help:
			Intent intent = new Intent(this, Help.class);
			intent.putExtra(Constants.KEYWORD_HELP,
					getString(R.string.st_ch_help_text));
			startActivity(intent);
			break;
		}
		return true;
	}

	// AsyncTask capable for loading the map
	private class LoadMapAsyncTask extends AsyncTask<Void, Void, Intent> {
		Context context;
		ProgressDialog progressDialog;
		Direction direction;
		String coordinates;

		public LoadMapAsyncTask(Context context, ProgressDialog progressDialog,
				Direction direction, String coordinates) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.direction = direction;
			this.coordinates = coordinates;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Intent doInBackground(Void... params) {
			Intent intent = new Intent(context, StationInfoRouteMap.class);
			intent.putExtra(
					Constants.KEYWORD_ROUTE_MAP,
					direction.getVehicleType() + ";"
							+ direction.getVehicleNumber() + "$" + coordinates);

			return intent;
		}

		@Override
		protected void onPostExecute(Intent result) {
			progressDialog.dismiss();

			startActivity(result);
		}
	}

}
