package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.MapUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class StationMap extends Activity {

	private Activity context;
	private ActionBar actionBar;

	private GoogleMap stationMap;
	private LatLng centerStationLocation;

	private boolean isCurrentLocationAnimated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_station);

		// Get the current activity context
		context = StationMap.this;

		// Set up the action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get the station map fragment
		stationMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.station_map)).getMap();

		// Check if the station map is found
		if (stationMap != null) {
			// Get the MetroStation object from the Bundle
			Bundle extras = getIntent().getExtras();
			final Station stationBundle = (Station) extras
					.get(Constants.BUNDLE_STATION_MAP);

			// Set ActionBar title and subtitle
			actionBar.setTitle(getActionBarTitle(stationBundle));
			actionBar.setSubtitle(stationBundle.getName());

			// Check if the station has coordinates in the DB
			try {
				centerStationLocation = new LatLng(
						Double.parseDouble(stationBundle.getLat()),
						Double.parseDouble(stationBundle.getLon()));
			} catch (Exception e) {
				centerStationLocation = new LatLng(
						Constants.GLOBAL_PARAM_SOFIA_CENTER_LATITUDE,
						Constants.GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE);
				stationBundle.setType(VehicleType.NOIMAGE);
			}

			// Activate my location, set a location button that center the map
			// over a point and start a LocationChangeListener
			stationMap.setMyLocationEnabled(true);
			stationMap.getUiSettings().setMyLocationButtonEnabled(true);
			stationMap
					.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
						@Override
						public void onMyLocationChange(Location currentLocation) {
							initGoogleMaps(stationBundle, currentLocation);
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
	 * @param stationBundle
	 *            the station received as an extra content from the previous
	 *            activity
	 * @param currentLocation
	 *            the current location
	 */
	private void initGoogleMaps(Station stationBundle,
			final Location currentLocation) {
		// Check the type of the bundle object
		if (stationBundle instanceof MetroStation) {
			MetroStation metroStation = (MetroStation) stationBundle;
			processMetroStationObject(currentLocation, metroStation);
		} else if (stationBundle instanceof PublicTransportStation) {
			PublicTransportStation ptStation = (PublicTransportStation) stationBundle;
			processPTStationObject(currentLocation, ptStation);
		} else {
			VirtualBoardsStation vbTimeStation = (VirtualBoardsStation) stationBundle;
			processVBTimeStationObject(currentLocation, vbTimeStation);
		}

		// Animate the map to the station position
		animateMapFocus(centerStationLocation);
		stationMap
				.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
					@Override
					public boolean onMyLocationButtonClick() {
						if (isCurrentLocationAnimated) {
							animateMapFocus(centerStationLocation);
							isCurrentLocationAnimated = false;
						} else {
							animateMapFocus(currentLocation);
							isCurrentLocationAnimated = true;
						}
						return true;
					}
				});

		// Remove the locationChangeListener
		stationMap.setOnMyLocationChangeListener(null);
	}

	/**
	 * Process the MetroStation object
	 * 
	 * @param currentLocation
	 *            the current user location
	 * @param metroStation
	 *            the chosen metro station
	 */
	private void processMetroStationObject(Location currentLocation,
			MetroStation metroStation) {
		MarkerOptions stationMarkerOptions = new MarkerOptions()
				.position(centerStationLocation)
				.title(String.format(metroStation.getName() + " (%s)",
						metroStation.getNumber()))
				.snippet(
						String.format(context.getString(R.string.app_distance),
								MapUtils.getMapDistance(context,
										currentLocation, metroStation)))
				.icon(BitmapDescriptorFactory
						.fromResource(getMarkerIcon(metroStation.getType())));
		Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
		stationMarker.showInfoWindow();
	}

	/**
	 * Process the PublicTranspStation object
	 * 
	 * @param currentLocation
	 *            the current user location
	 * @param ptStation
	 *            the chosen public transport station
	 */
	private void processPTStationObject(Location currentLocation,
			PublicTransportStation ptStation) {
		MarkerOptions stationMarkerOptions = new MarkerOptions()
				.position(centerStationLocation)
				.title(String.format(ptStation.getName() + " (%s)",
						ptStation.getNumber()))
				.snippet(
						String.format(context.getString(R.string.app_distance),
								MapUtils.getMapDistance(context,
										currentLocation, ptStation)))
				.icon(BitmapDescriptorFactory
						.fromResource(getMarkerIcon(ptStation.getType())));
		Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
		stationMarker.showInfoWindow();
	}

	/**
	 * Process the VirtualBoardsTime station object
	 * 
	 * @param currentLocation
	 *            the current user location
	 * @param vbTimeStation
	 *            the chosen virtual boards station
	 */
	private void processVBTimeStationObject(Location currentLocation,
			VirtualBoardsStation vbTimeStation) {
		MarkerOptions stationMarkerOptions = new MarkerOptions()
				.position(centerStationLocation)
				.title(String.format(vbTimeStation.getName() + " (%s)",
						vbTimeStation.getNumber()))
				.snippet(
						String.format(context.getString(R.string.app_distance),
								MapUtils.getMapDistance(context,
										currentLocation, vbTimeStation)))
				.icon(BitmapDescriptorFactory
						.fromResource(getMarkerIcon(vbTimeStation.getType())));
		Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
		stationMarker.showInfoWindow();
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
				.target(stationLocation).zoom(17).build();
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
						stationLocation.getLongitude())).zoom(17).build();
		stationMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/**
	 * Form the ActionBar title, according to the type of the vehicle and its
	 * number
	 * 
	 * @param station
	 *            the chosen station
	 * @return the action bar title
	 */
	private String getActionBarTitle(Station station) {
		String stationText;

		switch (station.getType()) {
		case METRO1:
		case METRO2:
			stationText = getString(R.string.metro_item_station_number_text_sign);
			break;
		default:
			stationText = getString(R.string.pt_item_station_number_text_sign);
			break;
		}

		String actionBarTitle = String.format(stationText, station.getNumber());

		return actionBarTitle;
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
		case BTT:
			markerIcon = R.drawable.ic_station_map_marker;
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
}
