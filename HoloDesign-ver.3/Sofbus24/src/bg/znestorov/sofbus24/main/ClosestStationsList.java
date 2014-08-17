package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.closest.stations.list.ClosestStationsListFragment;
import bg.znestorov.sofbus24.closest.stations.map.LocationSourceDialog;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ClosestStationsList extends FragmentActivity {

	private Activity context;
	private GlobalEntity globalContext;
	private Bundle savedInstanceState;

	private ActionBar actionBar;
	private View csListFragment;
	private ProgressBar csListLoading;

	private ImageView streetView;
	private ProgressBar streetViewLoading;
	private ImageButton streetViewButton;

	private LatLng currentLocation;

	private static final String FRAGMENT_TAG_NAME = "Closest Stations List Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_closest_stations_list);

		// Get the current context and create a SavedInstanceState objects
		context = ClosestStationsList.this;
		globalContext = (GlobalEntity) getApplicationContext();
		this.savedInstanceState = savedInstanceState;

		initBundleInfo();
		initLayoutFields();
		startClosestStationsListFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(
				R.menu.activity_closest_stations_list_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_cs_list_refresh:
			initRefresh();
			return true;
		case R.id.action_cs_list_map:
			Intent closestStationsMapIntent = new Intent(context,
					ClosestStationsMap.class);
			startActivity(closestStationsMapIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the refresh by putting a 500 ms delay
	 */
	private void initRefresh() {
		// Show the loading ProgressBar
		csListFragment.setVisibility(View.GONE);
		csListLoading.setVisibility(View.VISIBLE);

		// Retrieve the current position
		RetrieveCurrentPosition retrieveCurrentPosition = new RetrieveCurrentPosition(
				context, null);
		retrieveCurrentPosition.execute();
	}

	/**
	 * Get the current location coordinates from the Bundle object
	 */
	private void initBundleInfo() {
		Bundle extras = getIntent().getExtras();
		currentLocation = (LatLng) extras
				.get(Constants.BUNDLE_CLOSEST_STATIONS_LIST);
	}

	/**
	 * Initialize the layout fields (ActionBar, FragmentManager and ProgressBar)
	 */
	private void initLayoutFields() {
		// Get the Action Bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.cs_list_title));

		// Get the Fragment and the loading ProgressBar
		csListFragment = findViewById(R.id.cs_list_fragment);
		csListLoading = (ProgressBar) findViewById(R.id.cs_list_loading);

		// Get StreetView ImageView and ProgressBar
		streetView = (ImageView) findViewById(R.id.cs_list_street_view_image);
		streetViewLoading = (ProgressBar) findViewById(R.id.cs_list_street_view_progress);
		streetViewButton = (ImageButton) findViewById(R.id.cs_list_street_view_button);
		actionsOverStreetViewFileds();
	}

	/**
	 * Set the onClickListener over the GoogleStreetView button
	 */
	private void actionsOverStreetViewFileds() {
		streetViewButton
				.setOnClickListener(new android.view.View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Uri streetViewUri = Uri.parse("google.streetview:cbll="
								+ currentLocation.latitude + ","
								+ currentLocation.longitude
								+ "&cbp=1,90,,0,1.0&mz=20");
						Intent streetViewIntent = new Intent(
								Intent.ACTION_VIEW, streetViewUri);
						startActivity(streetViewIntent);
					}
				});
	}

	/**
	 * Used to refresh the content of the ClosestStationsListFragment according
	 * to the newly retrieved location
	 */
	private void refreshClosestStationsListFragment() {
		// Refresh the fragment
		ClosestStationsListFragment csListFragment = ((ClosestStationsListFragment) getSupportFragmentManager()
				.findFragmentByTag(FRAGMENT_TAG_NAME));
		if (csListFragment != null) {
			csListFragment.onFragmentRefresh(currentLocation, null);
		}

		// Proccess the layout fields
		actionsOnFragmentStart();
	}

	/**
	 * Create a new ClosestStationsListFragment with all needed information
	 */
	private void startClosestStationsListFragment() {
		Fragment fragment;

		if (savedInstanceState == null) {
			fragment = ClosestStationsListFragment.newInstance(currentLocation);
		} else {
			fragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG_NAME);
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.cs_list_fragment, fragment, FRAGMENT_TAG_NAME)
				.commit();

		actionsOnFragmentStart();
	}

	/**
	 * Set the Fragment schedule hour label and show the needed arrows
	 */
	private void actionsOnFragmentStart() {
		loadLocationStreetView();

		csListFragment.setVisibility(View.VISIBLE);
		csListLoading.setVisibility(View.GONE);
	}

	/**
	 * Load the current location StreetView
	 * 
	 * @param imageView
	 *            the ImageView of the StreetView from the layout
	 * @param progressBar
	 *            the ProgressBar shown when the image is loading
	 */
	private void loadLocationStreetView() {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ActivityUtils.initImageLoader(context));

		DisplayImageOptions displayImageOptions = ActivityUtils
				.displayImageOptions();

		String imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
				currentLocation.latitude + "", currentLocation.longitude + "");
		imageLoader.displayImage(imageUrl, streetView, displayImageOptions,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						streetViewLoading.setVisibility(View.VISIBLE);
						streetViewButton.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						streetViewLoading.setVisibility(View.GONE);
						streetViewButton.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						streetViewLoading.setVisibility(View.GONE);
						streetViewButton.setVisibility(View.VISIBLE);
					}
				});
	}

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

		// Available Location providers
		private boolean isMyLocationAvailable = false;
		private boolean isNetworkProviderOn = true;
		private boolean isGpsProviderOn = true;

		public RetrieveCurrentPosition(Activity context,
				ProgressDialog progressDialog) {
			this.context = context;
			this.progressDialog = progressDialog;
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
							LocationManager.GPS_PROVIDER, 0, 0,
							myLocationListener);
				} catch (Exception e) {
					isGpsProviderOn = false;
				}
			}

			createLoadingView();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (isMyLocationAvailable
					&& (isNetworkProviderOn || isGpsProviderOn)) {
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

			if (isMyLocationAvailable
					&& (isNetworkProviderOn || isGpsProviderOn)) {
				LatLng currentLocation = new LatLng(this.latitude,
						this.longitude);

				// Check what have to be done - start new activity or update
				// fragment
				if (progressDialog != null) {
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
					refreshClosestStationsListFragment();
				}
			} else {
				DialogFragment dialogFragment = new LocationSourceDialog();
				dialogFragment.show(getSupportFragmentManager(),
						"dialogFragment");
			}

			dismissLoadingView();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
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
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		}

		/**
		 * Create the loading view and lock the screen
		 */
		private void createLoadingView() {
			ActivityUtils.lockScreenOrientation(context);

			// Create progress dialog showing the loading message (if needed)
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
				locationManager.removeUpdates(myLocationListener);
			}

			ActivityUtils.unlockScreenOrientation(context);
		}
	}
}