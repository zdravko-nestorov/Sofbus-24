package bg.znestorov.sofbus24.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.ThemeChange;
import bg.znestorov.sofbus24.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
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

	private View webPageError;
	private TextView webPageErrorText;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getSupportMenuInflater()
				.inflate(R.menu.activity_web_page_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			return true;
		case R.id.action_web_page_home:
			loadWebPage(createStationUrlAddress());

			return true;
		case R.id.action_web_page_refresh:
			loadWebPage(webPage.getUrl());

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
	@SuppressLint("SetJavaScriptEnabled")
	private void initLayoutFields() {
		webPage = (WebView) findViewById(R.id.web_page);
		webPageLoading = (ProgressBar) findViewById(R.id.web_page_loading);
		webPageError = findViewById(R.id.web_page_error);
		webPageErrorText = (TextView) findViewById(R.id.web_page_error_text);

		initWebView();
		loadWebPage(createStationUrlAddress());
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
	public class WebViewSumcClient extends WebViewClient {

		private ProgressBar progressBar;
		private View webPageError;

		private boolean hasError;

		public WebViewSumcClient(ProgressBar progressBar, View webPageError) {
			this.progressBar = progressBar;
			this.webPageError = webPageError;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			if (webPageError.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.GONE);
				webPageError.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			hasError = true;
			initErrorView();

			view.setVisibility(View.GONE);
			webPageError.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			if (!hasError) {
				editSumcSiteCSS(view);
				view.setVisibility(View.VISIBLE);
				webPageError.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
			}

			hasError = false;
		}

		/**
		 * Initialize the error text into the TextView
		 */
		private void initErrorView() {

			webPageErrorText.setText(Html.fromHtml(getString(
					R.string.web_page_error, webPage.getUrl())));
		}
	}

	/**
	 * Initialize the web view and set the appropriate options
	 */
	private void initWebView() {
		webPage.setWebViewClient(new WebViewSumcClient(webPageLoading,
				webPageError));
		webPage.getSettings().setJavaScriptEnabled(true);
		webPage.getSettings().setBuiltInZoomControls(true);
		webPage.getSettings().setSupportZoom(true);
		webPage.getSettings().setRenderPriority(RenderPriority.HIGH);
		webPage.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
	}

	/**
	 * Load the web page - it is recomended to clear the cache and clear the
	 * history of the webview. This way the WebView forced to load the
	 * javascript after the page is loaded:
	 * http://stackoverflow.com/questions/6861640/android-webview-javascript-
	 * only-loads-sometimes
	 * 
	 * Also a workaround is to wait some milliseconds (in our case 50ms) before
	 * loading the content:
	 * http://stackoverflow.com/questions/18112715/webview-must
	 * -be-loaded-twice-to-load-correctly
	 * 
	 * @param urlAddress
	 *            the url address to load
	 */
	private void loadWebPage(final String urlAddress) {

		webPage.clearCache(true);
		webPage.clearHistory();

		// Load the page after 50ms, so ensure that there is no problem with
		// the threads
		webPage.postDelayed(new Runnable() {
			@Override
			public void run() {
				webPage.loadUrl(urlAddress.substring(0, 7));
			}
		}, 50);
	}

	/**
	 * Fix the css of the sumc site page (there are a lot of erros with
	 * scrolling and not needed images)
	 * 
	 * @param view
	 *            the web view container
	 */
	private void editSumcSiteCSS(WebView view) {
		view.loadUrl("javascript:document.getElementById(\"wrapper\").setAttribute(\"class\", \"sofbus\");");
		view.loadUrl("javascript:document.getElementById(\"wrapper\").setAttribute(\"id\", \"sofbus\");");
		view.loadUrl("javascript:document.getElementById(\"sofbus\").setAttribute(\"style\", \"text-align:left;margin:0 auto;margin-top:5px;padding:0 2px;\");");
		view.loadUrl("javascript:document.getElementsByClassName(\"tooltip\")[1].setAttribute(\"style\", \"display:none;\");");
		view.loadUrl("javascript:document.getElementsByClassName(\"footer\")[0].setAttribute(\"style\", \"display:none;\");");

		for (int i = 0; i < 10; i++) {
			view.loadUrl("javascript:document.getElementsByClassName(\"tooltip preview\")["
					+ i + "].setAttribute(\"style\", \"display:none;\");");
		}
	}
}