package com.example.sofiastations;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.utils.VehicleNames;

public class VehicleTabView extends TabActivity {

	// Extra info for the ListViews
	private static final String bus = "BUS";
	private static final String trolley = "TROLLEY";
	private static final String tram = "TRAM";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_view);

		// Filling the vehicles' ArrayLists and showing a ProgressDialog
		Context context = VehicleTabView.this;
		// TODO Make loading screen
		new VehicleNames(context);

		// TabHost which contain the tabs
		TabHost tabHost = getTabHost();

		// Bus tab
		TabSpec busTab = tabHost.newTabSpec(bus);
		busTab.setIndicator("", getResources().getDrawable(R.drawable.bus_tab));
		Intent busIntent = new Intent(context, VehicleListView.class);
		busIntent.putExtra(VehicleListView.keyVehicleType, bus);
		busTab.setContent(busIntent);

		// Trolley tab
		TabSpec trolleyTab = tabHost.newTabSpec(trolley);
		trolleyTab.setIndicator("",
				getResources().getDrawable(R.drawable.trolley_tab));
		Intent trolleyIntent = new Intent(context, VehicleListView.class);
		trolleyIntent.putExtra(VehicleListView.keyVehicleType, trolley);
		trolleyTab.setContent(trolleyIntent);

		// Tram tab
		TabSpec tramTab = tabHost.newTabSpec(tram);
		tramTab.setIndicator("", getResources()
				.getDrawable(R.drawable.tram_tab));
		Intent tramIntent = new Intent(context, VehicleListView.class);
		tramIntent.putExtra(VehicleListView.keyVehicleType, tram);
		tramTab.setContent(tramIntent);

		// Adding tabs to the TabHost
		tabHost.addTab(busTab);
		tabHost.addTab(trolleyTab);
		tabHost.addTab(tramTab);
	}
}
