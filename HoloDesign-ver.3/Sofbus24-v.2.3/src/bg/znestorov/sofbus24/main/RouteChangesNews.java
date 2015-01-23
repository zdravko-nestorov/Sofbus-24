package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import bg.znestorov.sofbus24.entity.RouteChangesEntity;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.ThemeChange;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity that shows a single route change news
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RouteChangesNews extends SherlockFragmentActivity {

	private Activity context;
	private ActionBar actionBar;

	private WebView routeChangesWebView;

	private RouteChangesEntity routeChanges;
	public static final String BUNDLE_ROUTE_CHANGES_NEWS = "ROUTE CHANGES NEWS";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		ThemeChange.selectTheme(this);
		super.onCreate(savedInstanceState);

		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_route_changes_news);

		// Get the activity context
		context = RouteChangesNews.this;

		// Check the bundle state of the activity
		routeChanges = (RouteChangesEntity) getIntent().getExtras()
				.getSerializable(BUNDLE_ROUTE_CHANGES_NEWS);

		// Initialize the ActionBar and the Layout fields
		initActionBar();
		initLayoutFields();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(
				R.menu.activity_route_changes_news_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {

		// Select the current locale - we need to put this here, as this
		// activity won't recreate on orientation change
		LanguageChange.selectLocale(this);

		// Get the URL address and the device Android version
		String urlAddress = routeChanges.getUrl();
		int deviceSDK = android.os.Build.VERSION.SDK_INT;

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_route_changes_copy_link:

			if (deviceSDK < android.os.Build.VERSION_CODES.HONEYCOMB) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(urlAddress);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData
						.newPlainText(urlAddress, urlAddress);
				clipboard.setPrimaryClip(clip);
			}

			Toast.makeText(context,
					getString(R.string.route_changes_news_copy_link),
					Toast.LENGTH_SHORT).show();

			return true;
		case R.id.action_route_changes_open_browser:

			if (!urlAddress.startsWith("http://")
					&& !urlAddress.startsWith("https://")) {
				urlAddress = "http://" + urlAddress;
			}

			Intent browserIntent;
			if (deviceSDK < android.os.Build.VERSION_CODES.HONEYCOMB) {
				browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri
						.parse(urlAddress));
			} else {
				browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(urlAddress));
			}

			startActivity(Intent.createChooser(browserIntent,
					getString(R.string.route_changes_news_choose_browser)));

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
		actionBar.setTitle(getString(R.string.route_changes_title));
	}

	/**
	 * Initialize the Layout fields
	 */
	private void initLayoutFields() {
		routeChangesWebView = (WebView) findViewById(R.id.route_changes_news);
		routeChangesWebView.getSettings().setJavaScriptEnabled(true);
		routeChangesWebView.getSettings().setSupportZoom(true);
		routeChangesWebView.getSettings().setBuiltInZoomControls(true);

		// Load the data into the web view container
		routeChangesWebView.loadData(routeChanges.getHtmlResponse(),
				"text/html; charset=utf-8", "UTF-8");

		// TODO: Check on a tablet
		if (android.os.Build.VERSION.SDK_INT < 16) {
			routeChangesWebView.setBackgroundColor(0x00000000);
		} else {
			routeChangesWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));
		}
	}

}
