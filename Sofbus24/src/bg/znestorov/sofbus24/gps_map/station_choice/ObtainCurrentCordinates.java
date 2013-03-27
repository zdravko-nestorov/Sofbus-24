package bg.znestorov.sofbus24.gps_map.station_choice;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.main.VirtualBoardsMapStationChoice;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.station_database.StationsDataSource;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.maps.GeoPoint;

public class ObtainCurrentCordinates extends AsyncTask<String, Integer, String> {

	private Context context;
	private ProgressDialog progressDailog;

	// Default lattitude and longitude
	private double latitude = 0.0;
	private double longitude = 0.0;

	// Location Managers responsible fot the current location
	private LocationManager mLocationManager;
	private VeggsterLocationListener mVeggsterLocationListener;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;

	// Database
	StationsDataSource datasource;

	public ObtainCurrentCordinates(Context context) {
		this.context = context;

		// Get Database access
		datasource = new StationsDataSource(this.context);

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);
	}

	@Override
	protected void onPreExecute() {
		mVeggsterLocationListener = new VeggsterLocationListener();
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0,
				mVeggsterLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, mVeggsterLocationListener);

		// Create progress dialog showing the loading message
		progressDailog = new ProgressDialog(context);
		progressDailog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				ObtainCurrentCordinates.this.cancel(true);
			}
		});
		progressDailog.setMessage("Loading...");
		progressDailog.setIndeterminate(true);
		progressDailog.setCancelable(true);
		progressDailog.show();

	}

	@Override
	protected void onCancelled() {
		progressDailog.dismiss();
		mLocationManager.removeUpdates(mVeggsterLocationListener);
	}

	@Override
	protected void onPostExecute(String result) {
		progressDailog.dismiss();

		// Getting a list with the closest stations
		List<GPSStation> station_list = getClosestStations();

		// Transforming the list to an array, so can be sent to another activity
		GPSStation[] station_array = station_list
				.toArray(new GPSStation[station_list.size()]);

		// Transfer the GPSStations array to VirtualBoardsMapStationChoice
		// activity
		Bundle bundle = new Bundle();
		Intent stationInfoIntent = new Intent(context,
				VirtualBoardsMapStationChoice.class);
		bundle.putSerializable("Array Of GPSStations", station_array);
		stationInfoIntent.putExtras(bundle);
		context.startActivity(stationInfoIntent);
	}

	@Override
	protected String doInBackground(String... params) {
		while (this.latitude == 0.0 && this.longitude == 0.0) {
			// Repeat until the user obtain coordinates
		}

		return null;
	}

	public class VeggsterLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			try {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			} catch (Exception e) {
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	}

	// Adding station marker according to GeoPoint coordinates
	private List<GPSStation> getClosestStations() {
		List<GPSStation> station_list = new ArrayList<GPSStation>();

		// Open database connection
		datasource.open();

		// Create geoPoint using the current coordinates
		GeoPoint geoPoint = new GeoPoint((int) (this.latitude * 1E6),
				(int) (this.longitude * 1E6));

		// Get "closestStations" value from the Shared Preferences
		String closestStations = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_CLOSEST_STATIONS,
				Constants.PREFERENCE_DEFAULT_VALUE_CLOSEST_STATIONS);
		try {
			StationsDataSource.stations_count = Integer
					.parseInt(closestStations);
		} catch (NumberFormatException e) {
			StationsDataSource.stations_count = 8;
		}

		// Get a list with the closest stations using the current location
		station_list = datasource.getClosestStations(geoPoint);

		// Close database connection
		datasource.close();

		return station_list;
	}

}