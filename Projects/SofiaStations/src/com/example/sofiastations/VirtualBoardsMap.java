package com.example.sofiastations;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.example.info_station.MyItemizedOverlay;
import com.example.station_database.GPSStation;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

// Creating a MapActivity with the station position and info about the coming vehicle
public class VirtualBoardsMap extends MapActivity {
	MapView mapView;
	MapController mapController;
	GeoPoint geoPoint;
	GPSStation station;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_info_map);

		// Getting the information transfered from VirtualBoards activity
		try {
			station = (GPSStation) getIntent().getSerializableExtra(
					"GPSStation");
		} catch (Exception e) {
			station = null;
		}

		// Setting activity title
		this.setTitle(String.format(getString(R.string.st_inf_name), "\""
				+ station.getName() + "\""));

		// Creating MapView, setting StreetView and adding zoom controls
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setSatellite(false);
		mapView.setBuiltInZoomControls(true);

		// Add a location marker and a balloon above it
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.station);

		// Calculating the map coordinates
		mapController = mapView.getController();
		double lat = Double.parseDouble(station.getLat());
		double lng = Double.parseDouble(station.getLon());

		// Focus the map over an exact coordinates and set the zoom
		geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		mapController.animateTo(geoPoint);
		mapController.setZoom(18);

		// Getting the Station Info
		String stationInfo = station.getTime_stamp();

		// Add the marker
		MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawable,
				mapView);
		OverlayItem overlayItem = new OverlayItem(geoPoint, station.getName()
				+ " (" + station.getId() + ")", stationInfo);

		itemizedOverlay.addOverlay(overlayItem);

		// Showing the balloon information
		itemizedOverlay.onTap(geoPoint, mapView);

		mapOverlays.add(itemizedOverlay);

		// Switching between satellite and street view
		ImageView satellite = (ImageView) findViewById(R.id.satelite_img_button);
		satellite.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				if (mapView.isSatellite()) {
					mapView.setSatellite(false);
					mapView.setStreetView(true);
				} else {
					mapView.setStreetView(false);
					mapView.setSatellite(true);
				}
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}