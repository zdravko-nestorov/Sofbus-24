package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.ThemeChange;
import bg.znestorov.sofbus24.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * WebView activity used to show some information from the real site
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class WebPage extends SherlockActivity {

	private ActionBar actionBar;

	private WebView webPage;
	private ProgressBar webPageLoading;

	private VehicleEntity vehicle;
	public static final String BUNDLE_VEHICLE = "VEHICLE";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		ThemeChange.selectTheme(this);
		super.onCreate(savedInstanceState);

		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_web_page);

		// Get the values from the Bundle
		vehicle = (VehicleEntity) getIntent().getExtras().getSerializable(
				BUNDLE_VEHICLE);

		// Initialize the ActionBar and the Layout fields
		initActionBar();
		initLayoutFields();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the ActionBar
	 */
	private void initActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.web_page_title));
		actionBar.setSubtitle(Utils.getCurrentDateTime());
	}

	/**
	 * Initialize the Layout fields
	 */
	private void initLayoutFields() {
		webPage = (WebView) findViewById(R.id.web_page);
		webPageLoading = (ProgressBar) findViewById(R.id.web_page_loading);

		webPage.getSettings().setJavaScriptEnabled(true);
		webPage.setWebViewClient(new AppWebViewClients(webPageLoading));
		webPage.loadUrl(createStationUrlAddress());
	}

	/**
	 * Create the public transport site URL address
	 * 
	 * @return the URL address of the selected station
	 */
	private String createStationUrlAddress() {

		String returnURL = String.format(
				Constants.SUMC_SITE_SCHEDULE_URL_ADDRESS, getVehicleType(),
				getVehicleNumber());

		return returnURL;
	}

	/**
	 * Get the vehicle type in text format
	 * 
	 * @return the vehicle type in text format
	 */
	private String getVehicleType() {

		String vehicleType;
		switch (vehicle.getType()) {
		case BUS:
			vehicleType = Constants.SUMC_SITE_SCHECULE_AUTOBUS;
			break;
		case TROLLEY:
			vehicleType = Constants.SUMC_SITE_SCHECULE_TROLLEYBUS;
			break;
		case TRAM:
			vehicleType = Constants.SUMC_SITE_SCHECULE_TRAMWAY;
			break;
		default:
			vehicleType = Constants.SUMC_SITE_SCHECULE_METRO;
			break;
		}

		return vehicleType;
	}

	/**
	 * Get the vehicle number according to its type
	 * 
	 * @return the vehicle number
	 */
	private String getVehicleNumber() {

		String vehicleNumber;
		switch (vehicle.getType()) {
		case BUS:
		case TROLLEY:
		case TRAM:
			vehicleNumber = vehicle.getNumber();
			break;
		default:
			vehicleNumber = "1";
			break;
		}

		return vehicleNumber;
	}

	/**
	 * Class used to manage the WebView states
	 * 
	 * @author Zdravko Nestorov
	 * 
	 */
	public class AppWebViewClients extends WebViewClient {

		private ProgressBar progressBar;

		public AppWebViewClients(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
		}
	}
}