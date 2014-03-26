package bg.znestorov.sofbus24.closest.stations.list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.main.ClosestStationsList;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class responsible for AsyncLoad of the current location
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveCurrentPosition extends AsyncTask<Void, Void, Void> {

	private Activity context;
	private ProgressDialog progressDialog;

	// Default latitude and longitude
	private double latitude = 0.0;
	private double longitude = 0.0;

	// Location Managers responsible for the current location
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;

	// Database
	StationsDataSource datasource;

	// Available Location providers
	private boolean isNetworkProviderOn = true;
	private boolean isGpsProviderOn = true;

	public RetrieveCurrentPosition(Activity context,
			ProgressDialog progressDialog) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.datasource = new StationsDataSource(this.context);
	}

	@Override
	protected void onPreExecute() {
		myLocationListener = new MyLocationListener();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		try {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
		} catch (Exception e) {
			isNetworkProviderOn = false;
		}

		try {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
		} catch (Exception e) {
			isGpsProviderOn = false;
		}

		// Create progress dialog showing the loading message
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

	@Override
	protected Void doInBackground(Void... params) {
		if (isNetworkProviderOn || isGpsProviderOn) {
			while (this.latitude == 0.0 && this.longitude == 0.0) {
				// Repeat until the user obtain coordinates
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		progressDialog.dismiss();

		if (isNetworkProviderOn || isGpsProviderOn) {
			LatLng currentLocation = new LatLng(this.latitude, this.longitude);

			Intent closestStationsListIntent = new Intent(context,
					ClosestStationsList.class);
			closestStationsListIntent.putExtra(
					Constants.BUNDLE_CLOSEST_STATIONS_LIST, currentLocation);
			context.startActivity(closestStationsListIntent);
		} else {
			OnClickListener positiveOnClickListener = new OnClickListener() {
				public void onClick(DialogInterface dialog, int i) {
					Intent intent = new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					context.startActivity(intent);
				}

			};

			ActivityUtils.showCustomAlertDialog(context,
					android.R.drawable.ic_dialog_alert,
					context.getString(R.string.app_dialog_title_error),
					context.getString(R.string.app_location_error),
					context.getString(R.string.app_button_yes),
					positiveOnClickListener,
					context.getString(R.string.app_button_no), null);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		try {
			progressDialog.dismiss();
			locationManager.removeUpdates(myLocationListener);
		} catch (Exception e) {
			// Workaround used just in case when this activity is destroyed
			// before the dialog
		}
	}

	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			try {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			} catch (Exception e) {
			}
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
	}

}