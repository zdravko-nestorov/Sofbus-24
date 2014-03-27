package bg.znestorov.sofbus24.main;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import bg.znestorov.sofbus24.info_station.MyItemizedOverlay;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;

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
	SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);

		// Getting the information transfered from VirtualBoards activity
		try {
			station = (GPSStation) getIntent().getSerializableExtra(
					Constants.KEYWORD_BUNDLE_GPS_STATION);
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

		// Set default map view
		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// Get "exitAlert" value from the Shared Preferences
		String mapType = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_MAP_TYPE,
				Constants.PREFERENCE_DEFAULT_VALUE_MAP_TYPE);

		if ("map_satellite".equals(mapType)) {
			mapView.setSatellite(true);
		} else {
			mapView.setSatellite(false);
		}

		// Switching between satellite and street view
		ImageView satellite = (ImageView) findViewById(R.id.satelite_img_button);

		// Get "mapView" value from the Shared Preferences
		boolean mapViewPref = sharedPreferences.getBoolean(
				Constants.PREFERENCE_KEY_MAP_VIEW,
				Constants.PREFERENCE_DEFAULT_VALUE_MAP_VIEW);

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
}