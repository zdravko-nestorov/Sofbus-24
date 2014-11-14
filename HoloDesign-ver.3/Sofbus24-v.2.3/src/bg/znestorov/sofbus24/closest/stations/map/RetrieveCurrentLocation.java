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
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.ClosestStationsList;
import bg.znestorov.sofbus24.main.ClosestStationsListDialog;
import bg.znestorov.sofbus24.main.ClosestStationsMap;
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
public class RetrieveCurrentLocation extends AsyncTask<Void, Void, Void> {

	private FragmentActivity context;
	private GlobalEntity globalContext;

	private boolean isClosestStationsList;
	private ProgressDialog progressDialog;

	// Default latitude and longitude
	private double latitude = 0.0;
	private double longitude = 0.0;

	// Location Managers responsible for the current location
	private LocationManager locationManager;
	private MyLocationListener myNetworkLocationListener;
	private MyLocationListener myGPSLocationListener;

	// Available Location providers
	private boolean isLocationServicesAvailable;
	private boolean isAnyProviderEabled;

	// Different location providers
	private static final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
	private static final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

	// The minimum distance and time to for the location updates
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 2;

	public RetrieveCurrentLocation(FragmentActivity context,
			boolean isClosestStationsList, ProgressDialog progressDialog) {
		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();

		this.progressDialog = progressDialog;
		this.isClosestStationsList = isClosestStationsList;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		String locationProviders = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		isLocationServicesAvailable = locationProviders
				.contains(LocationManager.NETWORK_PROVIDER)
				|| locationProviders.contains(LocationManager.GPS_PROVIDER);

		try {
			if (isLocationServicesAvailable) {
				locationManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				registerForLocationUpdates();
			}
		} catch (Exception e) {
			isAnyProviderEabled = false;
		}

		createLoadingView();
	}

	@Override
	protected Void doInBackground(Void... params) {

		while (latitude == 0.0 && longitude == 0.0) {

			// In case of all providers are disabled - dissmiss the progress
			// dialog (if any) and cancel the async task
			if (!isAnyProviderEabled) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				cancel(true);
			}

			// In case the progress dialog is dissmissed - cancel the async task
			if (progressDialog != null && !progressDialog.isShowing()) {
				cancel(true);
			}

			// If the async task is cancelled stop the loop
			if (isCancelled()) {
				break;
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		actionsOnLocationFound();
		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		Location lastKnownLocation = null;
		if (locationManager != null) {
			lastKnownLocation = locationManager
					.getLastKnownLocation(GPS_PROVIDER) == null ? locationManager
					.getLastKnownLocation(NETWORK_PROVIDER) == null ? null
					: locationManager.getLastKnownLocation(NETWORK_PROVIDER)
					: locationManager.getLastKnownLocation(GPS_PROVIDER);
		}

		// Check if there is any last known location
		if (lastKnownLocation == null) {
			// Check if the ClosestStationsList was refreshed
			if (isClosestStationsList && progressDialog == null) {
				((ClosestStationsList) context)
						.refreshClosestStationsListFragmentFailed();

				showLongToast(context
						.getString(R.string.app_location_modules_timeout_error));
			} else {
				if (!isAnyProviderEabled) {
					showLongToast(context
							.getString(R.string.app_location_modules_error));
				} else {
					if (progressDialog.isShowing()) {
						// Show different message in case of ClosestStationsList
						// and ClosestStationsMap
						if (isClosestStationsList) {
							showLongToast(context
									.getString(R.string.app_location_timeout_error));
						} else {
							showLongToast(context
									.getString(R.string.app_location_timeout_map_error));
						}
					}
				}

				// In case of ClosestStationsMap - start the map fragment and it
				// will take care for the rest (just inform the user)
				if (progressDialog.isShowing() && !isClosestStationsList) {
					Intent closestStationsMapIntent = new Intent(context,
							ClosestStationsMap.class);
					context.startActivity(closestStationsMapIntent);
				}
			}
		} else {
			actionsOnLocationFound();
		}

		dismissLoadingView();
	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			registerForLocationUpdates();
		}

		@Override
		public void onProviderEnabled(String provider) {
			registerForLocationUpdates();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			registerForLocationUpdates();
		}
	}

	/**
	 * Create the loading view and lock the screen
	 */
	private void createLoadingView() {
		ActivityUtils.lockScreenOrientation(context);

		if (progressDialog != null) {
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

		if (locationManager != null) {
			if (myNetworkLocationListener != null) {
				locationManager.removeUpdates(myNetworkLocationListener);
			}

			if (myGPSLocationListener != null) {
				locationManager.removeUpdates(myGPSLocationListener);
			}
		}

		ActivityUtils.unlockScreenOrientation(context);
	}

	/**
	 * Check if any of the providers is enabled
	 * 
	 * @return if any provider is ebanled
	 */
	private void registerForLocationUpdates() {

		if (locationManager != null) {
			// Getting the GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(GPS_PROVIDER);

			// Getting the network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(NETWORK_PROVIDER);

			// Check if any of the providers is enabled
			isAnyProviderEabled = isGPSEnabled || isNetworkEnabled;

			if (isAnyProviderEabled) {
				if (isNetworkEnabled) {
					if (myNetworkLocationListener == null) {
						myNetworkLocationListener = new MyLocationListener();

						locationManager.requestLocationUpdates(
								NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES,
								myNetworkLocationListener);
					}
				} else {
					if (myNetworkLocationListener != null) {
						locationManager
								.removeUpdates(myNetworkLocationListener);
						myNetworkLocationListener = null;
					}
				}

				if (isGPSEnabled) {
					if (myGPSLocationListener == null) {
						myGPSLocationListener = new MyLocationListener();

						locationManager.requestLocationUpdates(GPS_PROVIDER,
								MIN_TIME_BETWEEN_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES,
								myGPSLocationListener);
					}
				} else {
					if (myGPSLocationListener != null) {
						locationManager.removeUpdates(myGPSLocationListener);
						myGPSLocationListener = null;
					}
				}
			}
		}
	}

	/**
	 * Show a toast for a long period of time
	 * 
	 * @param message
	 *            the message of the toast
	 */
	private void showLongToast(String message) {
		ActivityUtils.showLongToast(context, message, 3000, 1000);
	}

	/**
	 * Actions when any location is found
	 */
	private void actionsOnLocationFound() {
		// Check if the location is available
		if (isLocationServicesAvailable) {

			// Check which activity called the async task
			if (isClosestStationsList) {

				// Check what have to be done - start new activity or update
				// fragment
				if (progressDialog != null) {
					LatLng currentLocation = new LatLng(this.latitude,
							this.longitude);

					Intent closestStationsListIntent;
					if (globalContext.isPhoneDevice()) {
						closestStationsListIntent = new Intent(context,
								ClosestStationsList.class);
					} else {
						closestStationsListIntent = new Intent(context,
								ClosestStationsListDialog.class);
					}
					closestStationsListIntent.putExtra(
							Constants.BUNDLE_CLOSEST_STATIONS_LIST,
							currentLocation);
					context.startActivity(closestStationsListIntent);
				} else {
					((ClosestStationsList) context)
							.refreshClosestStationsListFragment();
				}
			} else {
				Intent closestStationsMapIntent = new Intent(context,
						ClosestStationsMap.class);
				context.startActivity(closestStationsMapIntent);
			}
		} else {
			try {
				DialogFragment dialogFragment = new LocationSourceDialog();
				dialogFragment.show(context.getSupportFragmentManager(),
						"dialogFragment");
			} catch (Exception e) {
				/**
				 * Fixing a strange error that is happening sometimes when the
				 * dialog is created. I guess sometimes the activity gets
				 * destroyed before the dialog successfully be shown.
				 * 
				 * java.lang.IllegalStateException: Activity has been destroyed
				 */
			}
		}
	}

}