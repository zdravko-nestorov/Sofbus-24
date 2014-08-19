package bg.znestorov.sofbus24.closest.stations.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import bg.znestorov.sofbus24.main.ClosestStationsMap;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Class responsible for AsyncLoad of the current location
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveCurrentLocation extends AsyncTask<Void, Void, Void> {

	private FragmentActivity context;
	private ProgressDialog progressDialog;

	// Default latitude and longitude
	private double latitude = 0.0;
	private double longitude = 0.0;

	// Location Managers responsible for the current location
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;

	// Available Location providers
	private boolean isMyLocationAvailable = false;
	private boolean isNetworkProviderOn = true;
	private boolean isGpsProviderOn = true;

	public RetrieveCurrentLocation(FragmentActivity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		String locationProviders = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		// TODO: Make check for "Location & Google search" acceptance
		isMyLocationAvailable = locationProviders
				.contains(LocationManager.NETWORK_PROVIDER)
				|| locationProviders.contains(LocationManager.GPS_PROVIDER);

		if (isMyLocationAvailable) {
			myLocationListener = new MyLocationListener();
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

			try {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						myLocationListener);
			} catch (Exception e) {
				isNetworkProviderOn = false;
			}

			try {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
			} catch (Exception e) {
				isGpsProviderOn = false;
			}
		}

		createLoadingView();
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (isMyLocationAvailable && (isNetworkProviderOn || isGpsProviderOn)) {
			while (this.latitude == 0.0 && this.longitude == 0.0) {
				if (isCancelled()) {
					break;
				}
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (isMyLocationAvailable && (isNetworkProviderOn || isGpsProviderOn)) {
			Intent closestStationsMapIntent = new Intent(context,
					ClosestStationsMap.class);
			context.startActivity(closestStationsMapIntent);
		} else {
			DialogFragment dialogFragment = new LocationSourceDialog();
			dialogFragment.show(context.getSupportFragmentManager(),
					"dialogFragment");
		}

		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		if (progressDialog.isShowing()) {
			Intent closestStationsMapIntent = new Intent(context,
					ClosestStationsMap.class);
			context.startActivity(closestStationsMapIntent);
		}

		dismissLoadingView();
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
			if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
				isNetworkProviderOn = false;
			}

			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				isGpsProviderOn = false;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
				isNetworkProviderOn = true;
			}

			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				isGpsProviderOn = true;
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * Create the loading view and lock the screen
	 */
	private void createLoadingView() {
		ActivityUtils.lockScreenOrientation(context);

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.cs_list_loading_current_location));
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

	/**
	 * Dismiss the loading view and unlock the screen
	 */
	private void dismissLoadingView() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		if (locationManager != null) {
			locationManager.removeUpdates(myLocationListener);
		}

		ActivityUtils.unlockScreenOrientation(context);
	}
}