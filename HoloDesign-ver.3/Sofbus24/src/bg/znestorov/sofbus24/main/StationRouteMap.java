package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.publictransport.RetrievePublicTransportStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.MapUtils;
import bg.znestorov.sofbus24.utils.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class StationRouteMap extends Activity {

	private Activity context;
	private ActionBar actionBar;

	private DirectionsEntity directionsEntity;

	private GoogleMap stationMap;
	private boolean isCurrentLocationFocused = false;
	private LatLng centerStationLocation = new LatLng(
			Constants.GLOBAL_PARAM_SOFIA_CENTER_LATITUDE,
			Constants.GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE);

	private LatLng currentMarkerLatLng;

	/**
	 * When the user clicks outside a marker its snippet is hidden
	 */
	private final OnMapClickListener onMapClickListener = new OnMapClickListener() {
		@Override
		public void onMapClick(LatLng arg0) {
			currentMarkerLatLng = null;
		}
	};

	/**
	 * Check if selecting the current marker shows or hides the snippet
	 */
	private final OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			if (!marker.isInfoWindowShown()) {
				Station station = getMarkerStation(marker);
				currentMarkerLatLng = new LatLng(Double.parseDouble(station
						.getLat()), Double.parseDouble(station.getLon()));
			} else {
				currentMarkerLatLng = null;
			}

			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_station_route);

		// Get the current context
		context = StationRouteMap.this;

		// Set up the action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get the station map fragment
		stationMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.station_route_map)).getMap();

		// Check if the station map is found
		if (stationMap != null) {
			// Get the list of MetroStation objects from the Bundle
			Bundle extras = getIntent().getExtras();
			directionsEntity = (DirectionsEntity) extras
					.get(Constants.BUNDLE_STATION_ROUTE_MAP);

			// Get the needed fields from the bundle object to form the action
			// bar title and subtitle
			Vehicle vehicle = directionsEntity.getVehicle();
			final VehicleType stationType = vehicle.getType();
			int ptActiveDirection = directionsEntity.getActiveDirection();
			String directionName = directionsEntity.getDirectionsNames().get(
					ptActiveDirection);

			// Set ActionBar title and subtitle
			actionBar.setTitle(getLineName(vehicle));
			actionBar.setSubtitle(directionName);

			// Set a click listener over the map (when the map is clicked the
			// snippet becames hidden)
			stationMap.setOnMapClickListener(onMapClickListener);

			// Set a click listener over the markers
			stationMap.setOnMarkerClickListener(onMarkerClickListener);

			// Activate my location, set a location button that center the map
			// over a point and start a LocationChangeListener
			stationMap.setMyLocationEnabled(true);
			stationMap.getUiSettings().setMyLocationButtonEnabled(true);
			stationMap
					.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
						@Override
						public void onMyLocationChange(Location currentLocation) {
							initGoogleMaps(stationType, currentLocation);
						}
					});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.activity_map_station_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_sm_google_street_view:
			if (currentMarkerLatLng != null) {
				Uri streetViewUri = Uri.parse("google.streetview:cbll="
						+ currentMarkerLatLng.latitude + ","
						+ currentMarkerLatLng.longitude
						+ "&cbp=1,90,,0,1.0&mz=20");
				Intent streetViewIntent = new Intent(Intent.ACTION_VIEW,
						streetViewUri);
				startActivity(streetViewIntent);
			} else {
				Toast.makeText(context,
						getString(R.string.app_no_station_selected_error),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_sm_map_mode_normal:
			stationMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			return true;
		case R.id.action_sm_map_mode_terrain:
			stationMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			return true;
		case R.id.action_sm_map_mode_satellite:
			stationMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			return true;
		case R.id.action_sm_map_mode_hybrid:
			stationMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the GoogleMaps and all of its objects
	 * 
	 * @param stationType
	 *            the station type
	 * @param currentLocation
	 *            the current location
	 */
	private void initGoogleMaps(VehicleType stationType,
			final Location currentLocation) {
		// Check the type of the bundle object
		if (VehicleType.METRO1.equals(stationType)
				|| VehicleType.METRO2.equals(stationType)) {
			processListOfMetroStationObjects(currentLocation);
		} else {
			processListOfPTStationObjects(currentLocation);
		}

		// Animate the map to the station position
		animateMapFocus(centerStationLocation);
		stationMap
				.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
					@Override
					public boolean onMyLocationButtonClick() {
						if (isCurrentLocationFocused) {
							animateMapFocus(centerStationLocation);
							isCurrentLocationFocused = false;
						} else {
							animateMapFocus(currentLocation);
							isCurrentLocationFocused = true;
						}
						return true;
					}
				});

		// Remove the locationChangeListener
		stationMap.setOnMyLocationChangeListener(null);
	}

	/**
	 * Process the list of MetroStation objects
	 * 
	 * @param currentLocation
	 *            the current location
	 */
	private void processListOfMetroStationObjects(Location currentLocation) {
		// Get the active direction parameters
		int metroActiveDirection = directionsEntity.getActiveDirection();
		String metroDirectionName = directionsEntity.getDirectionsNames().get(
				metroActiveDirection);
		ArrayList<Station> metroDirectionStations = directionsEntity
				.getDirectionsList().get(metroActiveDirection);

		// Create an object consisted of a set of all points of the route
		PolylineOptions metroRouteOptionsM1 = new PolylineOptions().width(4)
				.color(Color.RED);
		PolylineOptions metroRouteOptionsM2 = new PolylineOptions().width(4)
				.color(Color.BLUE);

		// Process all stations of the metro route
		for (int i = 0; i < metroDirectionStations.size(); i++) {
			Station station = metroDirectionStations.get(i);
			MetroStation ms = new MetroStation(station, metroDirectionName);

			// Create the marker over the map
			if (ms.hasCoordinates()) {
				LatLng msLocation = new LatLng(Double.parseDouble(ms.getLat()),
						Double.parseDouble(ms.getLon()));

				// Add the msLocation to the appropriate route options object
				int stationNumber = Integer.parseInt(station.getNumber());
				if (stationNumber < 2999) {
					metroRouteOptionsM2.add(msLocation);
				} else if (stationNumber > 3000) {
					metroRouteOptionsM1.add(msLocation);
				} else {
					metroRouteOptionsM1.add(msLocation);
					metroRouteOptionsM2.add(msLocation);
				}

				// Create a marker on the msLocation and set some options
				MarkerOptions stationMarkerOptions = new MarkerOptions()
						.position(msLocation)
						.title(String.format(ms.getName() + " (%s)",
								ms.getNumber()))
						.snippet(
								String.format(context
										.getString(R.string.app_distance),
										MapUtils.getMapDistance(context,
												currentLocation, station)))
						.icon(BitmapDescriptorFactory
								.fromResource(getMarkerIcon(ms.getType())));
				stationMap.addMarker(stationMarkerOptions);
			}
		}

		// Set a click listener over the markers' snippets
		stationMap
				.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {
						// Get the station associated with this marker
						Station station = getMarkerStation(marker);

						// Getting the Metro schedule from the station URL
						// address
						ProgressDialog progressDialog = new ProgressDialog(
								context);
						progressDialog.setMessage(Html.fromHtml(String.format(
								getString(R.string.metro_loading_schedule),
								station.getName(), station.getNumber())));
						RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
								context, progressDialog, station);
						retrieveMetroSchedule.execute();
					}
				});

		// Draw a line between all the markers
		stationMap.addPolyline(metroRouteOptionsM1);
		stationMap.addPolyline(metroRouteOptionsM2);
	}

	/**
	 * Process the list of Public Transport (busses, trolleys and trams) objects
	 * 
	 * @param currentLocation
	 *            the current location
	 */
	private void processListOfPTStationObjects(Location currentLocation) {
		// Get the active direction parameters
		int ptActiveDirection = directionsEntity.getActiveDirection();
		String ptDirectionName = directionsEntity.getDirectionsNames().get(
				ptActiveDirection);
		ArrayList<Station> ptDirectionStations = directionsEntity
				.getDirectionsList().get(ptActiveDirection);

		// Create a HashMap to associate each marker with a station
		final HashMap<String, PublicTransportStation> markersAndStations = new HashMap<String, PublicTransportStation>();

		// Create an object consisted of a set of all points of the route
		PolylineOptions ptRouteOptions = new PolylineOptions().width(3).color(
				Color.parseColor("#008000"));

		// Process all stations of the public transport route
		for (int i = 0; i < ptDirectionStations.size(); i++) {
			Station station = ptDirectionStations.get(i);

			// Create the marker over the map
			if (station.hasCoordinates()) {
				LatLng ptLocation = new LatLng(Double.parseDouble(station
						.getLat()), Double.parseDouble(station.getLon()));

				// Add the msLocation to the appropriate route options object
				ptRouteOptions.add(ptLocation);

				// Center the map over the central station of the route
				if (i == ptDirectionStations.size() / 2) {
					centerStationLocation = ptLocation;
				}

				// Create a marker on the msLocation and set some options
				MarkerOptions stationMarkerOptions = new MarkerOptions()
						.position(ptLocation)
						.title(String.format(station.getName() + " (%s)",
								station.getNumber()))
						.snippet(
								String.format(context
										.getString(R.string.app_distance),
										MapUtils.getMapDistance(context,
												currentLocation, station)))
						.icon(BitmapDescriptorFactory
								.fromResource(getMarkerIcon(station.getType())));
				Marker marker = stationMap.addMarker(stationMarkerOptions);

				// Associate the marker and the station
				PublicTransportStation ptStation = new PublicTransportStation(
						station);
				ptStation.setDirection(ptDirectionName);
				markersAndStations.put(marker.getId(),
						new PublicTransportStation(ptStation));
			}
		}

		// Draw a line between all the markers
		stationMap.addPolyline(ptRouteOptions);

		// Set a click listener over the markers' snippets
		stationMap
				.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {
						// Get the station associated to this marker
						PublicTransportStation ptStation = markersAndStations
								.get(marker.getId());

						// Getting the PublicTransport schedule from the station
						// URL address
						ProgressDialog progressDialog = new ProgressDialog(
								context);
						progressDialog.setMessage(Html.fromHtml(String.format(
								getString(R.string.pt_item_loading_schedule),
								String.format(ptStation.getName() + " (%s)",
										ptStation.getNumber()))));
						RetrievePublicTransportStation retrievePublicTransportStation = new RetrievePublicTransportStation(
								context, progressDialog, ptStation,
								directionsEntity);
						retrievePublicTransportStation.execute();
					}
				});
	}

	/**
	 * Process the vehicle type and format the action bar title (for PT (public
	 * transport) - [line name] ¹[line number], and for METRO - [line number])
	 * 
	 * @param vehicle
	 *            the chosen vehicle
	 * @return the direction name in format<br>
	 *         for PT (public transport) - [line name] ¹[line number], and for
	 *         METRO - [line number]
	 */
	private String getLineName(Vehicle vehicle) {
		String lineName;

		switch (vehicle.getType()) {
		case BUS:
			lineName = String.format(getString(R.string.pt_bus),
					vehicle.getNumber());
			break;
		case TROLLEY:
			lineName = String.format(getString(R.string.pt_trolley),
					vehicle.getNumber());
			break;
		case TRAM:
			lineName = String.format(getString(R.string.pt_tram),
					vehicle.getNumber());
			break;
		case METRO1:
			lineName = getString(R.string.metro_search_tab_direction1);
			break;
		case METRO2:
			lineName = getString(R.string.metro_search_tab_direction2);
			break;
		default:
			lineName = String.format(getString(R.string.pt_bus),
					vehicle.getNumber());
			break;
		}

		return lineName;
	}

	/**
	 * Construct a CameraPosition focusing on Mountain View and animate the
	 * camera to that position
	 * 
	 * @param stationLocation
	 *            the location of the station over the map (using LatLng object)
	 */
	private void animateMapFocus(LatLng stationLocation) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(stationLocation).zoom(11.8f).build();
		stationMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/**
	 * Construct a CameraPosition focusing on Mountain View and animate the
	 * camera to that position
	 * 
	 * @param stationLocation
	 *            the location of the station over the map (using Location
	 *            object)
	 */
	private void animateMapFocus(Location stationLocation) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(stationLocation.getLatitude(),
						stationLocation.getLongitude())).zoom(11.8f).build();
		stationMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/**
	 * Get the appropriate marker icon according to the station type
	 * 
	 * @param stationType
	 *            the type of the station
	 * @return the marker icon from the resources
	 */
	private int getMarkerIcon(VehicleType stationType) {
		int markerIcon;

		switch (stationType) {
		case BUS:
			markerIcon = R.drawable.ic_bus_map_marker;
			break;
		case TROLLEY:
			markerIcon = R.drawable.ic_trolley_map_marker;
			break;
		case TRAM:
			markerIcon = R.drawable.ic_tram_map_marker;
			break;
		case METRO1:
		case METRO2:
			markerIcon = R.drawable.ic_metro_map_marker;
			break;
		default:
			markerIcon = R.drawable.ic_none_map_marker;
			break;
		}

		return markerIcon;
	}

	/**
	 * Get the station associated with this marker
	 * 
	 * @param marker
	 *            the current marker
	 * @return the station associated with this marker
	 */
	private Station getMarkerStation(Marker marker) {
		// Get the station number
		String markerTitle = marker.getTitle();
		String stationNumber = Utils.getValueBeforeLast(
				Utils.getValueAfterLast(markerTitle, "("), ")");

		// Get the station from the DB
		StationsDataSource stationDatasource = new StationsDataSource(context);
		stationDatasource.open();
		Station station = stationDatasource.getStation(stationNumber);
		stationDatasource.close();

		return station;
	}
}
