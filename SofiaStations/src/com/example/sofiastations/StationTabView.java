package com.example.sofiastations;

import static com.example.utils.StationCoordinates.getRoute;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.schedule_stations.Direction;
import com.example.schedule_stations.DirectionTransfer;

public class StationTabView extends TabActivity {

	// Variable used for transferring information from VehicleListView
	public static final String keyVehicleChoice = "VEHICLE";

	// Extra info for the ListViews
	private static final String direction1 = "DIRECTION-1";
	private static final String direction2 = "DIRECTION-2";

	DirectionTransfer directionTransfer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = new Bundle();

		try {
			directionTransfer = (DirectionTransfer) getIntent()
					.getSerializableExtra("DirectionTransfer");
		} catch (Exception e) {
			directionTransfer = null;
		}

		int direction = directionTransfer.getChoice();

		// TabHost which contain the tabs
		TabHost tabHost = getTabHost();

		// Direction 1 tab
		TabSpec tabDirection1 = tabHost.newTabSpec(direction1);
		tabDirection1.setIndicator("",
				getResources().getDrawable(R.drawable.left));
		// Transferring the HtmlResult to ListViewStationChoice - DIRECTION-1
		Intent tabDirection1Intent = new Intent(this, StationListView.class);
		directionTransfer.setChoice(0);
		bundle.putSerializable("DirectionTransfer", directionTransfer);
		tabDirection1Intent.putExtras(bundle);
		tabDirection1.setContent(tabDirection1Intent);
		tabHost.addTab(tabDirection1);

		// Direction 2 tab
		TabSpec tabDirection2 = tabHost.newTabSpec(direction2);
		tabDirection2.setIndicator("",
				getResources().getDrawable(R.drawable.right));
		// Transferring the HtmlResult to ListViewStationChoice - DIRECTION-1
		Intent tabDirection2Intent = new Intent(this, StationListView.class);
		directionTransfer.setChoice(1);
		bundle.putSerializable("DirectionTransfer", directionTransfer);
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
		Direction direction;
		if (directionTransfer.getChoice() == 0) {
			direction = directionTransfer.getDirection1();
		} else {
			direction = directionTransfer.getDirection2();
		}

		switch (item.getItemId()) {
		case R.id.menu_route:
			// Getting the coordinates of the whole route
			final String coordinates = getRoute(StationTabView.this,
					direction.getStations());

			// Checking if even one station has coordinates in the DB
			if (coordinates != null) {

				// Showing a ProgressDialog
				Context context = StationTabView.this;
				final ProgressDialog progressDialog = new ProgressDialog(
						context);
				progressDialog.setMessage("Loading...");
				progressDialog.show();

				final Direction th_direction = direction;
				new Thread(new Runnable() {
					public void run() {
						Intent intent = new Intent(StationTabView.this,
								StationInfoRouteMap.class);
						intent.putExtra(StationInfoRouteMap.ROUTE_MAP,
								th_direction.getVehicleType() + ";"
										+ th_direction.getVehicleNumber() + "$"
										+ coordinates);
						startActivity(intent);
						progressDialog.dismiss();
					}
				}).start();
			} else {
				new AlertDialog.Builder(this)
						.setTitle(R.string.st_ch_menu_err_title)
						.setMessage(R.string.st_ch_menu_err_msg)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("OK", new OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						}).show();
			}
			break;
		}
		return true;
	}

}
