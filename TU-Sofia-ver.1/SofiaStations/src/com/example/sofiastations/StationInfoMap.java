package com.example.sofiastations;

import static com.example.utils.Utils.getValueAfter;
import static com.example.utils.Utils.getValueBefore;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.info_station.MyItemizedOverlay;
import com.example.schedule_stations.Station;
import com.example.station_database.FavouritesDataSource;
import com.example.station_database.GPSStation;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

// Creating a MapActivity with the station position and info about the coming vehicle
public class StationInfoMap extends MapActivity {
	MapView mapView;
	MapController mapController;
	GeoPoint geoPoint;
	Station station;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_info_map);

		// Getting the information transfered from StationListView activity
		try {
			station = (Station) getIntent().getSerializableExtra("Station");
		} catch (Exception e) {
			station = null;
		}

		// Setting activity title
		this.setTitle(String.format(getString(R.string.st_inf_name), "\""
				+ getValueBefore(station.getStation(), "(").trim() + "\""));

		// Creating MapView, setting StreetView and adding zoom controls
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setSatellite(false);
		mapView.setBuiltInZoomControls(true);

		// Add a location marker and a balloon above it
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable;
		if (station.getVehicleType().equals("�������")) {
			drawable = getResources().getDrawable(R.drawable.bus_station);
		} else if (station.getVehicleType().equals("������")) {
			drawable = getResources().getDrawable(R.drawable.trolley_station);
		} else {
			drawable = getResources().getDrawable(R.drawable.tram_station);
		}

		// Calculating the map coordinates
		mapController = mapView.getController();
		double lat = Double.parseDouble(station.getCoordinates()[0]);
		double lng = Double.parseDouble(station.getCoordinates()[1]);

		// Focus the map over an exact coordinates and set the zoom
		geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		mapController.animateTo(geoPoint);
		mapController.setZoom(18);

		// Add the marker
		MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawable,
				mapView);
		OverlayItem overlayItem = new OverlayItem(geoPoint,
				station.getVehicleType() + " � " + station.getVehicleNumber(),
				station.getDirection() + "\n" + station.getStation() + "\n"
						+ station.getTime_stamp());

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_station_info_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		GPSStation gpsStation = new GPSStation();
		String stationCode = getValueAfter(station.getStation(), "(");
		stationCode = getValueBefore(stationCode, ")");

		gpsStation.setId(stationCode);
		gpsStation.setName(getValueBefore(station.getStation(), "(").trim());
		gpsStation.setLat(station.getCoordinates()[0]);
		gpsStation.setLon(station.getCoordinates()[1]);

		switch (item.getItemId()) {
		case R.id.menu_add_favourite:
			FavouritesDataSource datasource = new FavouritesDataSource(this);

			datasource.open();
			if (datasource.getStation(gpsStation) == null) {
				datasource.createStation(gpsStation);
				Toast.makeText(this, R.string.st_inf_fav_ok, Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, R.string.st_inf_fav_err,
						Toast.LENGTH_SHORT).show();
				;
			}
			datasource.close();
			break;
		case R.id.menu_focus:
			mapController.animateTo(geoPoint);
			break;
		}

		return true;
	}
}