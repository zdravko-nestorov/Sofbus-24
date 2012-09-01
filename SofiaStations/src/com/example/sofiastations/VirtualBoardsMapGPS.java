package com.example.sofiastations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gps_map.CurrentLocationOverlay;
import com.example.gps_map.MyItemizedOverlay;
import com.example.station_database.FavouritesDataSource;
import com.example.station_database.GPSStation;
import com.example.station_database.StationsDataSource;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class VirtualBoardsMapGPS extends MapActivity {

	// LogCat TAG for console messages
	private final static String TAG = "VirtualBoardsMapGPS";

	// Map variables
	MapView mapView;
	MapController mapController;
	List<Overlay> mapOverlays;
	Context context;
	GeoPoint menuGeoPoint;

	// GPS/WiFi variables
	String m_BestProvider;
	LocationManager m_LocationManager;
	LocationListener m_GPSLocationListener = null;
	LocationListener m_NetworkLocationListener = null;
	Location m_Location = null;

	// Closest stations variables
	MyLocationOverlay myLocationOverlay;
	MyItemizedOverlay itemizedOverlay;
	MyItemizedOverlay fItemizedOverlay;
	List<String> list = new ArrayList<String>();

	// Favorite stations
	List<GPSStation> fStations;

	// Database
	StationsDataSource datasource;

	// On tap variable
	public static GeoPoint tapGeoPoint;
	public static Location tapLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_info_map);

		tapGeoPoint = null;

		context = VirtualBoardsMapGPS.this;
		datasource = new StationsDataSource(context);

		// Getting the best provider on creating the activity
		getBestProvider();

		// Creating MapView, setting StreetView and adding zoom controls
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setSatellite(false);
		mapView.setBuiltInZoomControls(true);

		// Get the MapOverlays
		mapOverlays = mapView.getOverlays();

		// Creating an Overlay with current location
		myLocationOverlay = new CurrentLocationOverlay(this, mapView);
		myLocationOverlay.enableMyLocation();
		mapOverlays.add(myLocationOverlay);

		// Creating an Overlay with favorite stations
		Drawable fDrawable = getResources().getDrawable(R.drawable.favourite);
		fItemizedOverlay = new MyItemizedOverlay(fDrawable, mapView, context);
		setFavouriteStations();

		// Creating an Overlay with closest stations
		Drawable drawable = getResources().getDrawable(R.drawable.station);
		itemizedOverlay = new MyItemizedOverlay(drawable, mapView, context);

		// Creating TapOverlay, so show stations around the tap point
		TapOverlayMap tapMapOverlay = new TapOverlayMap();
		mapOverlays.add(tapMapOverlay);

		// Define a network listener that responds to location updates
		m_NetworkLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				getPosition(location, false);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				// Checking if the enabled provider is better
				getBestProvider();
			}

			public void onProviderDisabled(String provider) {
				// Check to see if any provider is left
				getBestProvider();
			}
		};

		// Define a GPS listener that responds to location updates
		m_GPSLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				getPosition(location, false);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				// Check to see if any provider is left
				getBestProvider();
			}

			public void onProviderDisabled(String provider) {
				// Check to see if any provider is left
				getBestProvider();
			}
		};

		Log.d(TAG, m_BestProvider);
		requestUpdates();

		m_Location = m_LocationManager.getLastKnownLocation(m_BestProvider);

		// Focus the map over an exact coordinates and set the zoom (using the
		// LastKnownLocation coordinates)
		try {
			m_Location.getLatitude();
			m_Location.getLongitude();

			getPosition(m_Location, false);

		} catch (Exception e) {
			Log.d(TAG, "Ќ€ма предишни координати.");
		}

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

		mapView.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// Choosing the best provider
	private void getBestProvider() {
		m_LocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(true);
		c.setPowerRequirement(Criteria.POWER_HIGH);

		m_BestProvider = m_LocationManager.getBestProvider(c, true);
	}

	// Focus the map over an exact coordinates and set the zoom
	private void focusCurrentPosition(Location loc, boolean tap) {
		GeoPoint geoPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
				(int) (loc.getLongitude() * 1E6));

		mapController = mapView.getController();
		mapController.animateTo(geoPoint);
		if (!tap) {
			mapController.setZoom(17);
		}
	}

	// Finding closest stations
	private List<GPSStation> findClosestStations(Location location) {
		List<GPSStation> stations = new ArrayList<GPSStation>();

		datasource.open();
		stations = datasource.getClosestStations(location);

		return stations;
	}

	// Adding station marker according to GeoPoint coordinates
	private void getClosestStations(GPSStation gpsStation) {
		for (int i = 0; i < fStations.size(); i++) {
			if (fStations.get(i).getId().equals(gpsStation.getId())) {
				return;
			}
		}

		if (list.isEmpty()) {
			list.add(gpsStation.getId());
		} else {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(gpsStation.getId())) {
					return;
				}
			}
			list.add(gpsStation.getId());
		}

		// Creating GeoPoint
		double lat = Double.parseDouble(gpsStation.getLat());
		double lng = Double.parseDouble(gpsStation.getLon());
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		OverlayItem overlayItem = new OverlayItem(geoPoint,
				gpsStation.getName() + " (" + gpsStation.getId() + ")", "");
		itemizedOverlay.addOverlay(overlayItem);
	}

	// Get the BestProvider, focus the map on the current position and set the
	// stations on the map
	private void getPosition(Location location, boolean tap) {
		// Focus the map over an exact coordinates and set the zoom
		focusCurrentPosition(location, tap);

		mapOverlays.remove(itemizedOverlay);

		// Get closes stations according to the current location
		List<GPSStation> stations = findClosestStations(location);
		for (int i = 0; i < stations.size(); i++) {
			getClosestStations(stations.get(i));
		}

		// Drawing the markers on the Overlay
		itemizedOverlay.populateNow();

		// Adding the itemized overlay to the map
		mapOverlays.add(itemizedOverlay);

		if (!tap) {
			// Add the coordinates of the current position (this way they
			// can be used in the menu)
			menuGeoPoint = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));
		}

		// Check for better provider
		getBestProvider();
	}

	// Finding favorite stations
	private List<GPSStation> findFavouriteStations() {
		// Open favorite DB
		FavouritesDataSource favouriteDatasource = new FavouritesDataSource(
				context);
		favouriteDatasource.open();

		// Get all stations
		List<GPSStation> stations = favouriteDatasource.getAllStations();

		// Close the favorite DB
		favouriteDatasource.close();

		return stations;
	}

	// Adding station marker according to GeoPoint coordinates
	private void getFavouriteStations(GPSStation gpsStation) {
		// Creating GeoPoint
		double lat = Double.parseDouble(gpsStation.getLat());
		double lng = Double.parseDouble(gpsStation.getLon());
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		OverlayItem overlayItem = new OverlayItem(geoPoint,
				gpsStation.getName() + " (" + gpsStation.getId() + ")", "");
		fItemizedOverlay.addOverlay(overlayItem);
	}

	// Set the favorite stations on the map
	private void setFavouriteStations() {

		mapOverlays.remove(fItemizedOverlay);

		// Get closes stations according to the current location
		fStations = findFavouriteStations();
		for (int i = 0; i < fStations.size(); i++) {
			getFavouriteStations(fStations.get(i));
		}

		// Drawing the markers on the Overlay
		fItemizedOverlay.populateNow();

		// Adding the itemized overlay to the map
		mapOverlays.add(fItemizedOverlay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_gps_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_gps_distance:
			try {
				Location locTap = new Location("");
				locTap.setLatitude(tapGeoPoint.getLatitudeE6() / 1E6);
				locTap.setLongitude(tapGeoPoint.getLongitudeE6() / 1E6);

				Location locCurr = new Location("");
				locCurr.setLatitude(menuGeoPoint.getLatitudeE6() / 1E6);
				locCurr.setLongitude(menuGeoPoint.getLongitudeE6() / 1E6);

				Float distanceTo = locTap.distanceTo(locCurr);
				BigDecimal bd = new BigDecimal(distanceTo);
				BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				distanceTo = rounded.floatValue();

				Toast.makeText(
						this,
						String.format(getString(R.string.map_gps_distance_OK),
								distanceTo.toString()), Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e) {
				Toast.makeText(this, R.string.map_gps_distance_ERR,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.menu_gps_focus:
			try {
				mapController.animateTo(menuGeoPoint);
				mapController.setZoom(17);
			} catch (Exception e) {
				Toast.makeText(this, R.string.map_gps_no_last_location,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.menu_gps_clean:
			Intent recreateIntent = getIntent();
			finish();
			startActivity(recreateIntent);
			break;
		case R.id.menu_gps_help:
			Intent i = new Intent(this, Help.class);
			startActivity(i);
			break;
		}

		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Request updates using LocationMenager
		requestUpdates();

		// Enable MyLocation and Compass
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}

	@Override
	public void onPause() {
		super.onPause();

		// Remove listeners
		m_LocationManager.removeUpdates(m_NetworkLocationListener);
		m_LocationManager.removeUpdates(m_GPSLocationListener);

		// Disable MyLocation, Compass and DB
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
		datasource.close();
	}

	// Request updates
	public void requestUpdates() {
		// Set the listener to the current LocationManager
		try {
			m_LocationManager.requestLocationUpdates("network", 20000, 10f,
					m_NetworkLocationListener);
		} catch (Exception e) {
			Log.d(TAG, "Network problem.");
		}

		try {
			m_LocationManager.requestLocationUpdates("gps", 20000, 10f,
					m_GPSLocationListener);
		} catch (Exception e) {
			Log.d(TAG, "GPS problem.");
		}
	}

	private class TapOverlayMap extends Overlay {
		@Override
		public boolean onTap(GeoPoint point, MapView mapView) {
			tapLocation = new Location("");
			tapLocation.setLatitude(point.getLatitudeE6() / 1E6);
			tapLocation.setLongitude(point.getLongitudeE6() / 1E6);

			setFavouriteStations();
			getPosition(tapLocation, true);

			return true;
		}
	}

}