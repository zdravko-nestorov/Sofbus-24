package bg.znestorov.sofbus24.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps_map.MapRoute;
import bg.znestorov.sofbus24.gps_map.MapRoute.RouteListener;
import bg.znestorov.sofbus24.gps_map.MapRouteOverlay;
import bg.znestorov.sofbus24.gps_map.MyItemizedOverlay;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.station_database.StationsDataSource;
import bg.znestorov.sofbus24.utils.Constants;

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

	// Sliding status bar parameters
	private static Animation slideIn;
	private static Animation slideOut;
	private static TextView locationStatusView;
	private static TextView locationStatusViewBackground;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;

	// Route variables
	private MapRouteOverlay mapRouteOverlay;
	private int routePointsSize = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_info_map);

		// Sliding animations for the status bar
		slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		slideIn.setDuration(Constants.TIME_STATUS_BAR_SLIDE_IN);
		slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		slideOut.setDuration(Constants.TIME_STATUS_BAR_SLIDE_OUT);

		// Set the status bar slide out animation to start after the slide in
		slideOut.setStartOffset(Constants.TIME_STATUS_BAR_SLIDE_IN);

		// Set the status bar background invisible once it is gone
		slideOut.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				locationStatusViewBackground.setVisibility(View.INVISIBLE);
			}
		});

		// Show the status bar
		locationStatusView = (TextView) findViewById(R.id.message_location_status);
		locationStatusViewBackground = (TextView) findViewById(R.id.message_location_background);
		locationStatusView.startAnimation(slideIn);
		locationStatusView.setVisibility(View.VISIBLE);
		locationStatusViewBackground.setVisibility(View.VISIBLE);

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
		myLocationOverlay = new MyLocationOverlay(this, mapView);
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
				// Hide the status bar
				locationStatusView.startAnimation(slideOut);
				locationStatusView.setVisibility(View.INVISIBLE);

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
				// Hide the status bar
				locationStatusView.startAnimation(slideOut);
				locationStatusView.setVisibility(View.INVISIBLE);

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
			m_Location = new Location("");
			m_Location.setLatitude(Double.parseDouble("42.696492"));
			m_Location.setLongitude(Double.parseDouble("23.326011"));

			focusCurrentPosition(m_Location, false);

			Toast.makeText(this, R.string.map_gps_no_last_location,
					Toast.LENGTH_SHORT).show();
		}

		// Set default map view
		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// Get "exitAlert" value from the Shared Preferences
		boolean satellitePref = sharedPreferences
				.getBoolean("satellite", false);

		if (satellitePref) {
			mapView.setSatellite(true);
		} else {
			mapView.setSatellite(false);
		}

		// Get "compass" value from the Shared Preferences
		boolean compass = sharedPreferences.getBoolean("compass", false);

		if (compass) {
			myLocationOverlay.enableCompass();
		} else {
			myLocationOverlay.disableCompass();
		}

		// Switching between satellite and street view
		ImageView satellite = (ImageView) findViewById(R.id.satelite_img_button);

		// Get "mapView" value from the Shared Preferences
		boolean mapViewPref = sharedPreferences.getBoolean("mapView", true);

		if (mapViewPref) {
			satellite.setVisibility(View.VISIBLE);
		} else {
			satellite.setVisibility(View.INVISIBLE);
		}

		satellite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mapView.isSatellite()) {
					mapView.setSatellite(false);
				} else {
					mapView.setSatellite(true);
				}
			}
		});
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

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// Get "closestStations" value from the Shared Preferences
		String closestStations = sharedPreferences.getString("closestStations",
				"8");
		try {
			StationsDataSource.stations_count = Integer
					.parseInt(closestStations);
		} catch (NumberFormatException e) {
			StationsDataSource.stations_count = 8;
		}

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
		try {
			double lat = Double.parseDouble(gpsStation.getLat());
			double lng = Double.parseDouble(gpsStation.getLon());
			GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6),
					(int) (lng * 1E6));

			OverlayItem overlayItem = new OverlayItem(geoPoint,
					gpsStation.getName() + " (" + gpsStation.getId() + ")", "");
			fItemizedOverlay.addOverlay(overlayItem);
		} catch (Exception e) {
			Log.d(TAG, "No such station - " + gpsStation.getName() + " ("
					+ gpsStation.getId() + ")");
		}
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
			int check = 0;

			try {
				Location locCurr = new Location("");
				locCurr.setLatitude(menuGeoPoint.getLatitudeE6() / 1E6);
				locCurr.setLongitude(menuGeoPoint.getLongitudeE6() / 1E6);

				// Failed on current location
				check = 1;

				Location locTap = new Location("");
				locTap.setLatitude(tapGeoPoint.getLatitudeE6() / 1E6);
				locTap.setLongitude(tapGeoPoint.getLongitudeE6() / 1E6);

				Float distanceTo = locTap.distanceTo(locCurr);
				BigDecimal bd = new BigDecimal(distanceTo);
				BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				distanceTo = rounded.floatValue();

				Toast.makeText(
						this,
						String.format(getString(R.string.map_gps_distance_OK),
								distanceTo.toString()), Toast.LENGTH_LONG)
						.show();

				// Failed on drawing the route
				check = 2;

				// Make a RoutePath between current location and the chosen
				// station
				GeoPoint geoPoint1 = new GeoPoint(menuGeoPoint.getLatitudeE6(),
						menuGeoPoint.getLongitudeE6());
				GeoPoint geoPoint2 = new GeoPoint(tapGeoPoint.getLatitudeE6(),
						tapGeoPoint.getLongitudeE6());
				doDrawPath(geoPoint1, geoPoint2);

				if (Constants.ROUTE_NO_INTERNET) {
					throw new Exception();
				}
			} catch (Exception e) {
				if (check == 0) {
					Toast.makeText(this, R.string.map_gps_no_last_location,
							Toast.LENGTH_LONG).show();
				} else if (check == 1) {
					Toast.makeText(this, R.string.map_gps_distance_ERR,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, R.string.map_gps_route_error,
							Toast.LENGTH_LONG).show();
				}
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
			i.putExtra(Constants.HELP_ACTIVITY,
					getString(R.string.map_help_text));
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
		// myLocationOverlay.enableCompass();
	}

	@Override
	public void onPause() {
		super.onPause();

		// Remove listeners
		m_LocationManager.removeUpdates(m_NetworkLocationListener);
		m_LocationManager.removeUpdates(m_GPSLocationListener);

		// Disable MyLocation, Compass and DB
		myLocationOverlay.disableMyLocation();
		// myLocationOverlay.disableCompass();
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

	// Draw RoutePath between two points
	private void doDrawPath(GeoPoint gpSrc, GeoPoint gpDest) {
		MapRoute oRoute = new MapRoute(gpSrc, gpDest);

		oRoute.getPoints(new RouteListener() {
			public void onDetermined(ArrayList<GeoPoint> alPoint) {
				GeoPoint oPointA = null;
				GeoPoint oPointB = null;

				// Check if a route is already drawn
				// Special case - if Internet suddenly stop and come again
				if (routePointsSize != -1
						&& routePointsSize < mapView.getOverlays().size()) {
					Constants.ROUTE_NO_INTERNET = false;

					// If a route is drawn - clear it
					for (int i = 1; i < routePointsSize - 1; i++) {
						mapView.getOverlays().remove(0);
					}
				} else {
					Constants.ROUTE_NO_INTERNET = true;
				}

				// Create the route
				for (int i = 1; i < alPoint.size() - 1; i++) {
					// Get the number of points of the route
					routePointsSize = alPoint.size();

					oPointA = alPoint.get(i - 1);
					oPointB = alPoint.get(i);

					mapRouteOverlay = new MapRouteOverlay(oPointA, oPointB,
							Constants.ROUTE_MODE, Constants.ROUTE_COLOR);
					mapView.getOverlays().add(0, mapRouteOverlay);
				}

				mapView.invalidate();
			}

			public void onError() {
			}
		});
	}

}