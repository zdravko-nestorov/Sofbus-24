package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.closest.stations.list.ClosestStationsListFragment;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ClosestStationsList extends FragmentActivity {

	private Activity context;
	private Bundle savedInstanceState;

	private ActionBar actionBar;
	private View csListFragment;
	private ProgressBar csListLoading;

	private ImageView streetView;
	private ProgressBar streetViewLoading;

	private LatLng currentLocation;

	private static final String FRAGMENT_TAG_NAME = "Closest Stations List Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_closest_stations_list);

		// Get the current context and create a SavedInstanceState objects
		context = ClosestStationsList.this;
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
			// TODO: Set the event on clicking the button
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the refresh by putting a 500 ms delay
	 */
	private void initRefresh() {
		// This is needed, because the fragment should be restarted
		savedInstanceState = null;

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

		// Get the Fragment and the loading ProgressBar
		csListFragment = findViewById(R.id.cs_list_fragment);
		csListLoading = (ProgressBar) findViewById(R.id.cs_list_loading);

		// Get StreetView ImageView and ProgressBar
		streetView = (ImageView) findViewById(R.id.cs_list_street_view_image);
		streetViewLoading = (ProgressBar) findViewById(R.id.cs_list_street_view_progress);
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
		DisplayImageOptions displayImageOptions = ActivityUtils
				.displayImageOptions();

		String imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
				currentLocation.latitude + "", currentLocation.longitude + "");
		imageLoader.displayImage(imageUrl, streetView, displayImageOptions,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						streetViewLoading.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						streetViewLoading.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						streetViewLoading.setVisibility(View.GONE);
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
		private boolean isNetworkProviderOn = true;
		private boolean isGpsProviderOn = true;

		public RetrieveCurrentPosition(Activity context,
				ProgressDialog progressDialog) {
			this.context = context;
			this.progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
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
			try {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}

				if (isNetworkProviderOn || isGpsProviderOn) {
					LatLng currentLocation = new LatLng(this.latitude,
							this.longitude);

					// Check what have to be done - start new activity or update
					// fragment
					if (progressDialog != null) {
						Intent closestStationsListIntent = new Intent(context,
								ClosestStationsList.class);
						closestStationsListIntent.putExtra(
								Constants.BUNDLE_CLOSEST_STATIONS_LIST,
								currentLocation);
						context.startActivity(closestStationsListIntent);
					} else {
						startClosestStationsListFragment();
					}
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
			} catch (Exception e) {
				// Workaround used just in case the orientation is changed once
				// retrieving info
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
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		}

	}
}
