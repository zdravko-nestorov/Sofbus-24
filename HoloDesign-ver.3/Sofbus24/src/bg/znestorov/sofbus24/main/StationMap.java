package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
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
	private LatLng stationLocation;

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
			} else if (stationBundle instanceof PublicTransportStation) {
				PublicTransportStation ptStation = (PublicTransportStation) stationBundle;
				processPTStationObject(ptStation);
			} else {
				VirtualBoardsStation vbTimeStation = (VirtualBoardsStation) stationBundle;
				processVBTimeStationObject(vbTimeStation);
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
	 *            the chosen metro station
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
	 * Process the VirtualBoardsTime station object
	 */
	private void processVBTimeStationObject(VirtualBoardsStation vbTimeStation) {
		MarkerOptions stationMarkerOptions = new MarkerOptions()
				.position(stationLocation)
				.title(String.format(vbTimeStation.getName() + " (%s)",
						vbTimeStation.getNumber()))
				.snippet(getPassingStationVehicles(vbTimeStation))
				.icon(BitmapDescriptorFactory
						.fromResource(getMarkerIcon(vbTimeStation.getType())));
		Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
		stationMap
				.setInfoWindowAdapter(new CustomMapMarker(getLayoutInflater()));

		stationMarker.showInfoWindow();
	}

	/**
	 * Get the passing vehicles through this station and format them appropriate
	 */
	private String getPassingStationVehicles(VirtualBoardsStation vbTimeStation) {
		ArrayList<Vehicle> stationVehiclesList = vbTimeStation
				.getVehiclesList();
		String currentTime = Utils.getValueAfterLast(
				vbTimeStation.getTime(context), ",").trim();

		StringBuilder stationVehicles = new StringBuilder();
		boolean flag_a = false;
		boolean flag_tl = false;
		boolean flag_tm = false;

		for (int i = 0; i < stationVehiclesList.size(); i++) {
			Vehicle stationVehicle = stationVehiclesList.get(i);
			String timeToUse = Utils.getTimeDifference(context, stationVehicle
					.getArrivalTimes().get(0), currentTime);

			switch (stationVehicle.getType()) {
			case BUS:
				if (flag_a) {
					stationVehicles.append(",");
				} else {
					flag_a = true;
					stationVehicles.append("<b>"
							+ getString(R.string.station_map_buses) + "</b>");
				}

				stationVehicles.append(" ¹").append(stationVehicle.getNumber())
						.append(" <b>(").append(timeToUse).append(")</b>");
				break;
			case TROLLEY:
				if (flag_tl) {
					stationVehicles.append(",");
				} else {
					flag_tl = true;
					if (flag_a) {
						stationVehicles.append("<br/>");
					}

					stationVehicles
							.append("<b>"
									+ getString(R.string.station_map_trolleys)
									+ "</b>");
				}

				stationVehicles.append(" ¹").append(stationVehicle.getNumber())
						.append(" <b>(").append(timeToUse).append(")</b>");
				break;
			default:
				if (flag_tm) {
					stationVehicles.append(",");
				} else {
					flag_tm = true;
					if (flag_a || flag_tl) {
						stationVehicles.append("<br/>");
					}

					stationVehicles.append("<b>"
							+ getString(R.string.station_map_trams) + "</b>");
				}

				stationVehicles.append(" ¹").append(stationVehicle.getNumber())
						.append(" <b>(").append(timeToUse).append(")</b>");
				break;
			}
		}

		return stationVehicles.toString();
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

	/**
	 * Class used to create custom map marker (to use new lines)
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	private class CustomMapMarker implements InfoWindowAdapter {

		LayoutInflater inflater = null;

		CustomMapMarker(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}

		@Override
		public View getInfoContents(Marker marker) {
			View customMapMarker = inflater.inflate(R.layout.map_marker_layout,
					null);

			TextView tv = (TextView) customMapMarker
					.findViewById(R.id.custom_marker_title);
			tv.setText(marker.getTitle());

			tv = (TextView) customMapMarker
					.findViewById(R.id.custom_marker_snippet);
			tv.setText(Html.fromHtml(marker.getSnippet()));

			return customMapMarker;
		}
	}
}
