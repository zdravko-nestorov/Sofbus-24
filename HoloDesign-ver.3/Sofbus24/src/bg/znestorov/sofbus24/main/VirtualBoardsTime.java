package bg.znestorov.sofbus24.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.virtualboards.VirtualBoardsTimeFragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class VirtualBoardsTime extends FragmentActivity {

	private Activity context;
	private Bundle savedInstanceState;

	private ActionBar actionBar;
	private View vbTimeFragment;
	private ProgressBar vbTimeLoading;

	private ImageView vbTimeStreetView;
	private ProgressBar vbTimeStreetViewLoading;
	private View vbTimeBar;
	private TextView vbTimeStationCaption;
	private TextView vbTimeCurrentTime;

	private VirtualBoardsStation vbTimeStation;

	private static final String FRAGMENT_TAG_NAME = "Virtual Boards Time Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_virtual_boards_time);

		// Get the current context and create a SavedInstanceState objects
		context = VirtualBoardsTime.this;
		this.savedInstanceState = savedInstanceState;

		initBundleInfo();
		initLayoutFields();
		startVirtualBoardsTimeFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.activity_virtual_boards_time_actions,
				menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_vb_time_refresh:
			initRefresh();
			return true;
		case R.id.action_vb_time_map:
			if (vbTimeStation.hasCoordinates()) {
				Intent metroMapIntent = new Intent(context, StationMap.class);
				metroMapIntent.putExtra(Constants.BUNDLE_STATION_MAP,
						vbTimeStation);
				startActivity(metroMapIntent);
			} else {
				ActivityUtils.showNoCoordinatesAlertDialog(context);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the refresh by loading the information from SKGY site
	 */
	private void initRefresh() {
		// This is needed, because the fragment should be restarted
		savedInstanceState = null;

		// Show the loading ProgressBar
		vbTimeFragment.setVisibility(View.GONE);
		vbTimeLoading.setVisibility(View.VISIBLE);

		// TODO: Retrieve the refreshed information from SKGT site
	}

	/**
	 * Get the vehicles list and the station from the Bundle object
	 */
	private void initBundleInfo() {
		Bundle extras = getIntent().getExtras();
		vbTimeStation = (VirtualBoardsStation) extras
				.get(Constants.BUNDLE_VIRTUAL_BOARDS_TIME);
	}

	/**
	 * Initialize the layout fields (ActionBar, FragmentManager and ProgressBar)
	 */
	private void initLayoutFields() {
		// Get the Action Bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.vb_time_title));

		// Get the Fragment and the loading ProgressBar
		vbTimeFragment = findViewById(R.id.vb_time_fragment);
		vbTimeLoading = (ProgressBar) findViewById(R.id.vb_time_loading);

		// Get StreetView views
		vbTimeStreetView = (ImageView) findViewById(R.id.vb_time_street_view_image);
		vbTimeStreetViewLoading = (ProgressBar) findViewById(R.id.vb_time_street_view_progress);
		vbTimeBar = (View) findViewById(R.id.vb_time_bar);
		vbTimeStationCaption = (TextView) findViewById(R.id.vb_time_station_caption);
		vbTimeCurrentTime = (TextView) findViewById(R.id.vb_time_current_time);
		actionsOverStreetViewFileds();
	}

	/**
	 * Set the station caption ([station name] ([station number])) and time of
	 * retrieval of the information
	 */
	private void actionsOverStreetViewFileds() {
		vbTimeStationCaption.setText(getStationCaption());
		vbTimeCurrentTime.setText(String.format(
				getString(R.string.vb_time_current_time),
				vbTimeStation.getTime(context)));
	}

	/**
	 * Create the caption of the station in format [station name] ([station
	 * number])
	 * 
	 * @return the caption of the station in format [station name] ([station
	 *         number])
	 */
	private String getStationCaption() {
		return String.format(vbTimeStation.getName() + " (%s)",
				vbTimeStation.getNumber());
	}

	/**
	 * Create a new VirtualBoardsTimeFragment with all needed information
	 */
	private void startVirtualBoardsTimeFragment() {
		Fragment fragment;

		if (savedInstanceState == null) {
			fragment = VirtualBoardsTimeFragment.newInstance(vbTimeStation);
		} else {
			fragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG_NAME);
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.vb_time_fragment, fragment, FRAGMENT_TAG_NAME)
				.commit();

		actionsOnFragmentStart();
	}

	/**
	 * Set the Fragment schedule hour label and show the needed arrows
	 */
	private void actionsOnFragmentStart() {
		loadLocationStreetView();

		vbTimeFragment.setVisibility(View.VISIBLE);
		vbTimeLoading.setVisibility(View.GONE);
	}

	/**
	 * Load the street view of the station (if the station has coordinates in
	 * the database), otherwise shows the default location (the Sofia center)
	 */
	private void loadLocationStreetView() {
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions displayImageOptions = ActivityUtils
				.displayImageOptions();

		// Get the coordinates of the station (if exist in the database), or the
		// default one (if no coordinates exists in the database)
		String stationLat = vbTimeStation.getLat() != null ? vbTimeStation
				.getLat() : Constants.GLOBAL_PARAM_SOFIA_CENTER_LATITUDE + "";
		String stationLon = vbTimeStation.getLon() != null ? vbTimeStation
				.getLon() : Constants.GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE + "";

		// Create the station street view url address
		String imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
				stationLat, stationLon);

		// Loading the image and process the fields over
		imageLoader.displayImage(imageUrl, vbTimeStreetView,
				displayImageOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						vbTimeStreetViewLoading.setVisibility(View.VISIBLE);
						vbTimeBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						vbTimeStreetViewLoading.setVisibility(View.GONE);
						vbTimeBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						vbTimeStreetViewLoading.setVisibility(View.GONE);
						vbTimeBar.setVisibility(View.VISIBLE);
					}
				});
	}
}