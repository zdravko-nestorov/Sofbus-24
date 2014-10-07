package bg.znestorov.sofbus24.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.Toast;
import bg.znestorov.sofbus24.closest.stations.map.GoogleMapsRoute;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HtmlRequestCodesEnum;
import bg.znestorov.sofbus24.entity.SortTypeEnum;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.virtualboards.RetrieveVirtualBoards;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ClosestStationsMap extends SherlockFragmentActivity implements
		LocationListener {

	private Activity context;
	private GlobalEntity globalContext;

	private StationsDataSource stationsDatasource;
	private FavouritesDataSource favouritesDatasource;

	private ActionBar actionBar;

	private boolean positionFocus;
	private BigDecimal stationsRadius;
	private SharedPreferences sharedPreferences;

	private GoogleMap googleMap;
	private List<Polyline> routePoylineList = new ArrayList<Polyline>();

	/**
	 * Indicates if my location has been already focused
	 */
	private boolean isMyLocationAlreadyFocused = false;

	/**
	 * Indicates which marker is selected on the map (in case no selection -
	 * null)
	 */
	private LatLng selectedMarkerLatLng;

	/**
	 * Connect each point over the map with the appropriate station
	 */
	private HashMap<String, StationEntity> markersAndStations = new HashMap<String, StationEntity>();

	/**
	 * Listener used to check when the map is pressed
	 */
	private final OnMapClickListener onMapClickListener = new OnMapClickListener() {
		@Override
		public void onMapClick(LatLng point) {
			onMapTouchEvent(point);
		}
	};

	/**
	 * Listener used to check when the map is longed pressed
	 */
	private final OnMapLongClickListener onMapLongClickListener = new OnMapLongClickListener() {
		@Override
		public void onMapLongClick(LatLng point) {
			onMapTouchEvent(point);
		}
	};

	/**
	 * Listener used to check which marker is clicked
	 */
	private final OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			if (!marker.isInfoWindowShown()) {
				StationEntity station = markersAndStations.get(marker.getId());
				selectedMarkerLatLng = new LatLng(Double.parseDouble(station
						.getLat()), Double.parseDouble(station.getLon()));
			} else {
				selectedMarkerLatLng = null;
			}

			return false;
		}
	};

	/**
	 * Listener used to check which marker snippet is pressed
	 */
	private final OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
		@Override
		public void onInfoWindowClick(Marker marker) {
			// Get the station associated to this marker
			StationEntity station = markersAndStations.get(marker.getId());
			proccessWithStationResult(station);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_closest_stations_map);

		// Get the current activity context and get an instance of the
		// StationsDatasource
		context = ClosestStationsMap.this;
		globalContext = (GlobalEntity) getApplicationContext();
		stationsDatasource = new StationsDataSource(context);
		favouritesDatasource = new FavouritesDataSource(context);

		getSharedPreferencesFields();
		initActionBar();
		initGoogleMaps();
	}

	/**
	 * Get the values from the SharedPreferences file (positionFocus and
	 * stationsRadius)
	 */
	private void getSharedPreferencesFields() {
		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		// Get "positionFocus" value from the SharedPreferences file
		positionFocus = sharedPreferences.getBoolean(
				Constants.PREFERENCE_KEY_POSITION_FOCUS,
				Constants.PREFERENCE_DEFAULT_VALUE_POSITION_FOCUS);

		// Get "stationsFocus" value from the SharedPreferences file
		stationsRadius = new BigDecimal(sharedPreferences.getString(
				Constants.PREFERENCE_KEY_STATIONS_RADIUS,
				Constants.PREFERENCE_DEFAULT_VALUE_STATIONS_RADIUS));
	}

	/**
	 * Set up the action bar
	 */
	private void initActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.cs_map_title));
	}

	/**
	 * Set the ActionBar subtitle - it depends on the stations nearby
	 * 
	 * @param closestStationsCount
	 *            the count of the nearby stations
	 */
	private void setActionBarSubTitle(int closestStationsCount) {
		actionBar.setSubtitle(getString(R.string.cs_map_subtitle,
				stationsRadius, closestStationsCount));
	}

	/**
	 * Initialize the GoogleMaps and show the current location to the user
	 */
	private void initGoogleMaps() {
		// Verify that the Google Play services APK is available and up-to-date
		// on this device
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);

		// Check if Google Play Services are available or not
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		} else {
			// Getting reference to the SupportMapFragment of activity layout
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.closest_stations_map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map and the corresponding
			// buttons
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(true);

			// Enable the GoogleMap listeners
			googleMap.setOnMapClickListener(onMapClickListener);
			googleMap.setOnMapLongClickListener(onMapLongClickListener);

			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location
			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				animateMapFocus(location);
			}

			// Add a LocationUpdate listener in case of location change
			locationManager.requestLocationUpdates(provider, 20000, 0, this);

			// Visualize the favorites stations on the map
			new LoadStationsFromDb(context, null, null).execute();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// Showing the current location and zoom it in GoogleMaps
		animateMapFocus(location, false);

		// Visualize the closest stations to the new location
		new LoadStationsFromDb(context, location, null).execute();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getSupportMenuInflater().inflate(
				R.menu.activity_closest_stations_maps_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_gm_map_clear:
			googleMap.clear();
			isMyLocationAlreadyFocused = false;
			selectedMarkerLatLng = null;

			initGoogleMaps();
			Toast.makeText(context, getString(R.string.cs_map_clear_info),
					Toast.LENGTH_SHORT).show();

			return true;
		case R.id.action_gm_map_route:
			deleteRoute();

			if (googleMap.getMyLocation() != null) {
				if (selectedMarkerLatLng != null) {
					GoogleMapsRoute googleMapsRoute = new GoogleMapsRoute(
							context, this, googleMap.getMyLocation(),
							selectedMarkerLatLng);
					googleMapsRoute.execute();
				} else {
					Toast.makeText(context,
							getString(R.string.cs_map_route_error),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context,
						getString(R.string.cs_map_location_error),
						Toast.LENGTH_SHORT).show();
			}

			return true;
		case R.id.action_gm_map_gsv:
			if (globalContext.isGoogleStreetViewAvailable()) {
				if (selectedMarkerLatLng != null) {
					Uri streetViewUri = Uri.parse("google.streetview:cbll="
							+ selectedMarkerLatLng.latitude + ","
							+ selectedMarkerLatLng.longitude
							+ "&cbp=1,90,,0,1.0&mz=20");
					Intent streetViewIntent = new Intent(Intent.ACTION_VIEW,
							streetViewUri);
					startActivity(streetViewIntent);
				} else {
					Toast.makeText(context,
							getString(R.string.app_no_station_selected_error),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				ActivityUtils
						.showGoogleStreetViewErrorDialog(ClosestStationsMap.this);
			}

			return true;
		case R.id.action_gm_map_traffic:
			if (googleMap.isTrafficEnabled()) {
				googleMap.setTrafficEnabled(false);
				Toast.makeText(context,
						getString(R.string.cs_map_traffic_off_info),
						Toast.LENGTH_SHORT).show();
			} else {
				googleMap.setTrafficEnabled(true);
				Toast.makeText(context,
						getString(R.string.cs_map_traffic_on_info),
						Toast.LENGTH_SHORT).show();
			}

			return true;
		case R.id.action_gm_map_mode_normal:
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			Toast.makeText(context,
					Html.fromHtml(getString(R.string.cs_map_normal)),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_gm_map_mode_terrain:
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			Toast.makeText(context,
					Html.fromHtml(getString(R.string.cs_map_terrain)),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_gm_map_mode_satellite:
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			Toast.makeText(context,
					Html.fromHtml(getString(R.string.cs_map_satellite)),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_gm_map_mode_hybrid:
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			Toast.makeText(context,
					Html.fromHtml(getString(R.string.cs_map_hybrid)),
					Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Trigger the events that happens on onMapClick and onMapLongClick
	 * 
	 * @param point
	 *            the tapped point
	 */
	private void onMapTouchEvent(LatLng point) {
		// Indicates that no marker is selected
		selectedMarkerLatLng = null;

		// Create a location object according to the tapped position
		Location tapLocation = new Location("");
		tapLocation.setLatitude(point.latitude);
		tapLocation.setLongitude(point.longitude);

		// Showing the current location and zoom it in GoogleMaps
		animateMapFocus(tapLocation, true);

		// Visualize the closest station to the new location
		new LoadStationsFromDb(context, tapLocation,
				getString(R.string.cs_map_closest_stations)).execute();
	}

	/**
	 * Construct a CameraPosition focusing on Mountain View and animate the
	 * camera to that position
	 * 
	 * @param location
	 *            the location of the station over the map (using Location
	 *            object)
	 * @param isPressed
	 *            indicates if the location is retrieved by tap over the map
	 */
	private void animateMapFocus(Location location, boolean isPressed) {
		// Check if the map should be focused over the new location
		if (positionFocus || isPressed || !isMyLocationAlreadyFocused) {

			// Check if the current location was already focused
			if (!isMyLocationAlreadyFocused) {
				isMyLocationAlreadyFocused = true;
			}

			// Focus the map over the new location
			CameraPosition cameraPosition;

			// If the this action is triggered because of Map click - do not
			// zoom to this location
			if (!isPressed) {
				cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location
								.getLongitude())).zoom(16).build();
			} else {
				cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location
								.getLongitude()))
						.zoom(googleMap.getCameraPosition().zoom).build();
			}

			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}
	}

	/**
	 * Construct a CameraPosition focusing on Mountain View and animate the
	 * camera to that position (used when the camera is focused on the
	 * LastKnownLocation, because if a new one is found to be focused over it)
	 * 
	 * @param location
	 *            the location of the station over the map (using Location
	 *            object)
	 */
	private void animateMapFocus(Location location) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(), location
						.getLongitude())).zoom(16).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/**
	 * Get the closest stations to the location according to the radius from
	 * SharedPreferences file
	 * 
	 * @param location
	 *            the location of the station over the map (using Location
	 *            object)
	 * @return an ArrayList containing all stations in the stations radius
	 */
	private ArrayList<StationEntity> getClosestStations(Location location) {
		ArrayList<StationEntity> closestStations = new ArrayList<StationEntity>();

		stationsDatasource.open();
		closestStations.addAll(stationsDatasource.getClosestStations(context,
				new LatLng(location.getLatitude(), location.getLongitude()),
				stationsRadius));
		stationsDatasource.close();

		return closestStations;
	}

	/**
	 * Get a list with all stations marked as favorites
	 * 
	 * @return an ArrayList containing all favorites stations
	 */
	private ArrayList<StationEntity> getFavouritesStations() {
		ArrayList<StationEntity> favouritesStations = new ArrayList<StationEntity>();

		favouritesDatasource.open();
		favouritesStations.addAll(favouritesDatasource
				.getAllStationsSorted(SortTypeEnum.CUSTOM));
		favouritesDatasource.close();

		return favouritesStations;
	}

	/**
	 * Visualize the closest station over a map location on the GoogleMaps
	 * 
	 * @param location
	 *            the location of the station over the map (using Location
	 *            object)
	 * @param closestStations
	 *            the closest stations to map location
	 */
	private void visualizeClosestStations(Location location,
			List<StationEntity> closestStations) {
		// Process all stations of the public transport route
		for (int i = 0; i < closestStations.size(); i++) {
			StationEntity station = closestStations.get(i);

			// Create the marker over the map only if the station has
			// coordinates in the database
			if (station.hasCoordinates()) {
				LatLng stationLocation = new LatLng(Double.parseDouble(station
						.getLat()), Double.parseDouble(station.getLon()));

				// Create a marker on the station location and set some options
				MarkerOptions stationMarkerOptions = new MarkerOptions()
						.position(stationLocation)
						.title(String.format(station.getName() + " (%s)",
								station.getNumber()))
						.snippet(getStationTypeText(station));
				Marker marker = googleMap.addMarker(stationMarkerOptions);

				// Associate the marker and the station
				markersAndStations.put(marker.getId(), station);
			}
		}

		// Set a listeners over the markers
		googleMap.setOnMarkerClickListener(onMarkerClickListener);
		googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);

		// Set the ActionBar subtitle with the number of stations nearby
		setActionBarSubTitle(closestStations.size());
	}

	/**
	 * Visualize the closest station over a map location on the GoogleMaps
	 * 
	 * @param favouritesStations
	 *            the favourites stations
	 */
	private void visualizeFavouritesStations(
			List<StationEntity> favouritesStations) {
		// Process all stations of the public transport route
		for (int i = 0; i < favouritesStations.size(); i++) {
			StationEntity station = favouritesStations.get(i);

			// Create the marker over the map only if the station has
			// coordinates in the database
			if (station.hasCoordinates()) {
				LatLng stationLocation = new LatLng(Double.parseDouble(station
						.getLat()), Double.parseDouble(station.getLon()));

				// Create a marker on the station location and set some options
				MarkerOptions stationMarkerOptions = new MarkerOptions()
						.position(stationLocation)
						.title(String.format(station.getName() + " (%s)",
								station.getNumber()))
						.snippet(getStationTypeText(station))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_fav_full));
				Marker marker = googleMap.addMarker(stationMarkerOptions);

				// Associate the marker and the station
				markersAndStations.put(marker.getId(), station);
			}
		}

		// Set a listeners over the markers
		googleMap.setOnMarkerClickListener(onMarkerClickListener);
		googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
	}

	/**
	 * Class responsible for asynchronous load of the stations from the database
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	public class LoadStationsFromDb extends
			AsyncTask<Void, Void, List<StationEntity>> {

		private Activity context;
		private Location location;
		private String progressDialogMsg;

		private ProgressDialog progressDialog;

		public LoadStationsFromDb(Activity context, Location location,
				String progressDialogMsg) {
			this.context = context;
			this.location = location;
			this.progressDialogMsg = progressDialogMsg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			createLoadingView();
		}

		@Override
		protected List<StationEntity> doInBackground(Void... params) {
			if (location != null) {
				return getClosestStations(location);
			} else {
				return getFavouritesStations();
			}
		}

		@Override
		protected void onPostExecute(List<StationEntity> stationsList) {
			super.onPostExecute(stationsList);

			if (location != null) {
				visualizeClosestStations(location, stationsList);
			} else {
				visualizeFavouritesStations(stationsList);
			}

			dismissLoadingView();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissLoadingView();
		}

		/**
		 * Create the loading view and lock the screen
		 */
		private void createLoadingView() {
			ActivityUtils.lockScreenOrientation(context);

			if (progressDialogMsg != null && !"".equals(progressDialogMsg)) {
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(progressDialogMsg);
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(true);
				progressDialog
						.setOnCancelListener(new DialogInterface.OnCancelListener() {
							public void onCancel(DialogInterface dialog) {
								cancel(true);
							}
						});
				progressDialog.show();
			}
		}

		/**
		 * Dismiss the loading view and unlock the screen
		 */
		private void dismissLoadingView() {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			ActivityUtils.unlockScreenOrientation(context);
		}
	}

	/**
	 * Visualize the route between a map location and selected station
	 */
	public void visualizeRoute(String result) {
		try {
			// Tranform the string into a json object
			JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes
					.getJSONObject("overview_polyline");

			// Decode the points from the JSON object
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> routePointsList = decodePoly(encodedString);

			// Clear the route list
			routePoylineList.clear();

			// Iterate over the route points and visualize them on the map
			for (int z = 0; z < routePointsList.size() - 1; z++) {
				LatLng src = routePointsList.get(z);
				LatLng dest = routePointsList.get(z + 1);

				// Add the point to the map
				Polyline polyline = googleMap.addPolyline(new PolylineOptions()
						.add(new LatLng(src.latitude, src.longitude),
								new LatLng(dest.latitude, dest.longitude))
						.width(2).color(Color.BLUE).geodesic(true));

				// Add the point to the route list
				routePoylineList.add(polyline);
			}
		} catch (JSONException e) {
			Toast.makeText(context,
					getString(R.string.cs_map_fetch_route_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Clear the drawn route over the map
	 */
	private void deleteRoute() {
		for (Polyline polyline : routePoylineList) {
			polyline.remove();
		}

		routePoylineList.clear();
	}

	/**
	 * Create a list with the route map points using the JSON object
	 * 
	 * @param jsonRoutePoints
	 *            the points from the JSON object
	 * @return a list with the route map points
	 */
	private List<LatLng> decodePoly(String jsonRoutePoints) {
		List<LatLng> routePoints = new ArrayList<LatLng>();
		int index = 0, len = jsonRoutePoints.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = jsonRoutePoints.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = jsonRoutePoints.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			routePoints.add(p);
		}

		return routePoints;
	}

	/**
	 * Get the type of the the station associated with the selected marker
	 * 
	 * @param station
	 *            the station associated with the selected marker
	 * @return the type of the station
	 */
	private String getStationTypeText(StationEntity station) {
		String historyType;

		switch (station.getType()) {
		case METRO1:
		case METRO2:
			historyType = context.getString(R.string.cs_map_type_metro);
			break;
		default:
			historyType = context.getString(R.string.cs_map_type_btt);
			break;
		}

		return historyType;
	}

	/**
	 * Get the schedule for the station associated with the selected marker
	 * 
	 * @param station
	 *            the station associated with the selected marker
	 */
	private void proccessWithStationResult(StationEntity station) {
		// Getting the time of arrival of the vehicles
		String stationCustomField = station.getCustomField();
		String metroCustomField = String.format(Constants.METRO_STATION_URL,
				station.getNumber());

		// Check if the type of the station - BTT or METRO
		if (!stationCustomField.equals(metroCustomField)) {
			RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
					context, null, station, HtmlRequestCodesEnum.SINGLE_RESULT);
			retrieveVirtualBoards.getSumcInformation();
		} else {
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(Html.fromHtml(String.format(
					context.getString(R.string.metro_loading_schedule),
					station.getName(), station.getNumber())));
			RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
					context, progressDialog, station);
			retrieveMetroSchedule.execute();
		}
	}

}