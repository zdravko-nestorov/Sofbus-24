package bg.znestorov.sofbus24.closest.stations.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
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
	private Criteria criteria;
	private String bestProvider;

	// Available Location providers
	private boolean isMyLocationAvailable;
	private boolean isCurrentLocationAvailable = false;
	private MyLocationListener myLocationListener;

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
		isMyLocationAvailable = locationProviders
				.contains(LocationManager.NETWORK_PROVIDER)
				|| locationProviders.contains(LocationManager.GPS_PROVIDER);

		if (isMyLocationAvailable) {
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

			/*
			 * Criteria object will select best service based on: accuracy,
			 * power consumption, response, bearing and monetary cost
			 * 
			 * Set false to use best service otherwise it will select the
			 * default Sim network and give the location based on sim network
			 * now it will first check satellite than Internet than Sim network
			 * location
			 */
			criteria = new Criteria();

			// Get the best provider
			bestProvider = locationManager.getBestProvider(criteria, false);

			// Get location
			Location location = locationManager
					.getLastKnownLocation(bestProvider);

			if (location != null) {
				isCurrentLocationAvailable = true;
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			} else {
				myLocationListener = new MyLocationListener();
				if (locationManager.isProviderEnabled(bestProvider)) {
					isCurrentLocationAvailable = true;
					locationManager.requestLocationUpdates(bestProvider, 0, 0,
							myLocationListener);
				}
			}
		}

		createLoadingView();
	}

	@Override
	protected Void doInBackground(Void... params) {
		while (latitude == 0.0 && longitude == 0.0) {
			if (!isCurrentLocationAvailable
					|| (progressDialog != null && !progressDialog.isShowing())) {
				cancel(true);
			}

			if (isCancelled()) {
				break;
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		// Check if the location is available
		if (isMyLocationAvailable) {

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

		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		// Show a message when there is a timeout
		if (progressDialog.isShowing()) {
			final Toast registrationToast = Toast.makeText(context,
					context.getString(R.string.app_location_timeout),
					Toast.LENGTH_SHORT);
			registrationToast.show();

			new CountDownTimer(3000, 1000) {
				public void onTick(long millisUntilFinished) {
					registrationToast.show();
				}

				public void onFinish() {
					registrationToast.show();
				}
			}.start();
		}

		// In case of refresh and timeout
		if (isClosestStationsList && progressDialog == null) {
			((ClosestStationsList) context)
					.refreshClosestStationsListFragmentFailed();
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
			findBestProvider();
		}

		@Override
		public void onProviderEnabled(String provider) {
			findBestProvider();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			findBestProvider();
		}

		private void findBestProvider() {
			if (locationManager != null) {
				locationManager.removeUpdates(this);
				bestProvider = locationManager.getBestProvider(criteria, false);

				if (locationManager.isProviderEnabled(bestProvider)) {
					isCurrentLocationAvailable = true;
					locationManager.requestLocationUpdates(bestProvider, 0, 0,
							this);
				} else {
					isCurrentLocationAvailable = false;
				}
			} else {
				isCurrentLocationAvailable = false;
			}
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

		if (locationManager != null && myLocationListener != null) {
			locationManager.removeUpdates(myLocationListener);
		}

		ActivityUtils.unlockScreenOrientation(context);
	}
}