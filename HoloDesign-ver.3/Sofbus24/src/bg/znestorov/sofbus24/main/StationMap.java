package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class StationMap extends Activity {

	private ActionBar actionBar;

	private GoogleMap stationMap;
	private LatLng stationLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_station);

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
			Station stationBundle = (Station) extras
					.get(Constants.BUNDLE_STATION_MAP);

			// Set ActionBar title and subtitle
			actionBar.setTitle(getActionBarTitle(stationBundle));
			actionBar.setSubtitle(stationBundle.getName());

			// Check if the station has coordinates in the DB
			try {
				stationLocation = new LatLng(Double.parseDouble(stationBundle
						.getLat()), Double.parseDouble(stationBundle.getLon()));
			} catch (Exception e) {
				stationLocation = new LatLng(
						Constants.GLOBAL_PARAM_SOFIA_CENTER_LATITUDE,
						Constants.GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE);
				stationBundle.setType(VehicleType.NOIMAGE);
			}

			// Animate the map to the station position
			animateMapFocus(stationLocation);

			// Check the type of the bundle object
			if (stationBundle instanceof MetroStation) {
				MetroStation metroStation = (MetroStation) stationBundle;
				processMetroStationObject(metroStation);
			} else {
				PublicTransportStation ptStation = (PublicTransportStation) stationBundle;
				processPTStationObject(ptStation);
			}
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
		case R.id.action_sm_focus:
			animateMapFocus(stationLocation);
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
	 * Process the MetroStation object
	 * 
	 * @param metroStation
	 *            the choosen metro station
	 */
	private void processMetroStationObject(MetroStation metroStation) {
		MarkerOptions stationMarkerOptions = new MarkerOptions()
				.position(stationLocation)
				.title(String.format(metroStation.getName() + " (%s)",
						metroStation.getNumber()))
				.snippet(metroStation.getDirection().replaceAll("-.*-", "-"))
				.icon(BitmapDescriptorFactory
						.fromResource(getMarkerIcon(metroStation.getType())));
		Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
		stationMarker.showInfoWindow();
	}

	/**
	 * Process the PublicTranspStation object
	 */
	private void processPTStationObject(PublicTransportStation ptStation) {
		MarkerOptions stationMarkerOptions = new MarkerOptions()
				.position(stationLocation)
				.title(String.format(ptStation.getName() + " (%s)",
						ptStation.getNumber()))
				.snippet(ptStation.getDirection().replaceAll("-.*-", "-"))
				.icon(BitmapDescriptorFactory
						.fromResource(getMarkerIcon(ptStation.getType())));
		Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
		stationMarker.showInfoWindow();
	}

	/**
	 * Construct a CameraPosition focusing on Mountain View and animate the
	 * camera to that position
	 * 
	 * @param stationLocation
	 *            the location of the station over the map
	 */
	private void animateMapFocus(LatLng stationLocation) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(stationLocation).zoom(17).build();
		stationMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/**
	 * Form the ActionBar title, according to the type of the vehicle and its
	 * number
	 * 
	 * @param station
	 *            the choosed station
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
