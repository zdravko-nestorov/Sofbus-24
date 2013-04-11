package bg.znestorov.sofbus24.main;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import bg.znestorov.sofbus24.info_station.MyItemizedOverlay;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

// Creating a MapActivity with the station position and info about the coming vehicle
public class StationInfoRouteMap extends MapActivity {
	
	MapView mapView;
	MapController mapController;
	GeoPoint geoPoint;

	SharedPreferences sharedPreferences;

	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// Translate the GeoPoint to screen pixels
			Point screenPts = new Point();
			mapView.getProjection().toPixels(geoPoint, screenPts);

			// Add the marker
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.bus_station);
			canvas.drawBitmap(bmp, screenPts.x - 16, screenPts.y - 37, null);
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);

		Context context = StationInfoRouteMap.this;

		// Getting the information transfered from StationTabView activity
		String extraInfo = getIntent().getStringExtra(Constants.KEYWORD_ROUTE_MAP);

		// Getting vehicleType, vehicleNumber and coordinates
		String vehicleType = getValueBefore(extraInfo, "$");
		String vehicleNumber = getValueAfter(vehicleType, ";");
		vehicleType = getValueBefore(vehicleType, ";");
		List<String> coordinates = Arrays.asList(getValueAfter(extraInfo, "$")
				.split(";"));

		// Setting activity title
		this.setTitle(context.getString(R.string.st_inf_route_title)
				+ vehicleType + " ¹ " + vehicleNumber + "\"");

		// Creating MapView, setting StreetView and adding zoom controls
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setSatellite(false);
		mapView.setBuiltInZoomControls(true);

		// Add a location marker and a balloon above it
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable;
		if (vehicleType.equals(context.getString(R.string.title_bus))) {
			drawable = getResources().getDrawable(R.drawable.bus_station);
		} else if (vehicleType
				.equals(context.getString(R.string.title_trolley))) {
			drawable = getResources().getDrawable(R.drawable.trolley_station);
		} else {
			drawable = getResources().getDrawable(R.drawable.tram_station);
		}

		// Calculating the map coordinates
		mapController = mapView.getController();

		for (int i = 0; i < coordinates.size(); i++) {
			String lt = getValueBefore(coordinates.get(i), ",");
			String lg = getValueAfter(coordinates.get(i), ",");
			String stationName = getValueAfter(lg, ",");
			lg = getValueBefore(lg, ",");
			double lat = Double.parseDouble(lt);
			double lng = Double.parseDouble(lg);

			geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

			if (i == coordinates.size() / 2) {
				// Focus the map over an exact coordinates and set the zoom
				mapController.animateTo(geoPoint);
				mapController.setZoom(13);
			}

			// Add the marker
			MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawable,
					mapView);
			OverlayItem overlayItem = new OverlayItem(geoPoint, vehicleType
					+ " ¹ " + vehicleNumber, stationName);

			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}

		// Set default map view
		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// Get "exitAlert" value from the Shared Preferences
		String satellitePref = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_SATELLITE,
				Constants.PREFERENCE_DEFAULT_VALUE_SATELLITE);

		if ("map_satellite".equals(satellitePref)) {
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