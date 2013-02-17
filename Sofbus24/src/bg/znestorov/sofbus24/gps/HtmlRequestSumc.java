package bg.znestorov.sofbus24.gps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.VirtualBoards;
import bg.znestorov.sofbus24.main.VirtualBoardsStationChoice;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

public class HtmlRequestSumc {

	// LogCat TAG
	private static final String TAG = "SUMC";

	// CAPTCHA position in the source file
	private static final String CAPTCHA_START = "<img src=\"/captcha/";
	private static final String CAPTCHA_END = "\"";
	private static final String REQUIRES_CAPTCHA = "Въведете символите от изображението";
	// CAPTCHA URL link
	private static final String CAPTCHA_IMAGE = "http://m.sofiatraffic.bg/captcha/%s";

	// SUMC - URL and variables
	private static final String URL = "http://m.sofiatraffic.bg/vt";
	// q=000000 in order to find only by ID (we expect that there is no label
	// 000000)
	private static final String QUERY_BUS_STOP_ID = "q";
	private static final String QUERY_O = "o";
	private static final String QUERY_GO = "go";
	private static final String QUERY_CAPTCHA_TEXT = "sc";
	private static final String QUERY_CAPTCHA_ID = "poleicngi";

	// User Agent and Referrer
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1017.2 Safari/535.19";
	private static final String REFERER = "http://m.sofiatraffic.bg/vt/";

	// SUMC cookies
	private static final String SHARED_PREFERENCES_NAME_SUMC_COOKIES = "sumc_cookies";
	private static final String PREFERENCES_COOKIE_NAME = "name";
	private static final String PREFERENCES_COOKIE_DOMAIN = "domain";
	private static final String PREFERENCES_COOKIE_PATH = "path";
	private static final String PREFERENCES_COOKIE_VALUE = "value";

	// Getting the CAPTCHA text from the dialog result
	private static String dialogResult = null;

	// Coordinates of the station
	private static String[] coordinates = new String[2];

	// Getting the source file of the HTTP request and opening a new Activity
	public void getInformation(Context context, String stationCode,
			String stationCodeO, String[] transferCoordinates) {
		// Assigning the transfer coordinates to a global variable
		if (transferCoordinates != null) {
			coordinates = transferCoordinates;
		} else {
			coordinates[0] = "EMPTY";
			coordinates[1] = "EMPTY";
		}

		// Check if stationCodeO is null or not
		if (stationCodeO == null) {
			stationCodeO = "-1";
		}

		// Setting timeout parameters
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		// Creating ThreadSafeClientConnManager
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory
				.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(
				httpParameters, schemeRegistry);

		// HTTP Client - created once and using cookies
		DefaultHttpClient client = new DefaultHttpClient(cm, httpParameters);

		Log.d(TAG, stationCode);

		loadCookiesFromPreferences(context, client);

		// Making HttpRequest and showing a progress dialog
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Loading...");
		LoadingSumc loadingSumc = new LoadingSumc(context, progressDialog,
				client, stationCode, stationCodeO, null, null);
		loadingSumc.execute();
	}

	// Saving cookies
	private void saveCookiesToPreferences(Context context,
			DefaultHttpClient client) {
		final SharedPreferences sharedPreferences = context
				.getSharedPreferences(SHARED_PREFERENCES_NAME_SUMC_COOKIES,
						Context.MODE_PRIVATE);
		final Editor edit = sharedPreferences.edit();
		edit.clear();

		int i = 0;
		for (Cookie cookie : client.getCookieStore().getCookies()) {
			edit.putString(PREFERENCES_COOKIE_NAME + i, cookie.getName());
			edit.putString(PREFERENCES_COOKIE_VALUE + i, cookie.getValue());
			edit.putString(PREFERENCES_COOKIE_DOMAIN + i, cookie.getDomain());
			edit.putString(PREFERENCES_COOKIE_PATH + i, cookie.getPath());
			i++;
		}
		edit.commit();
	}

	// Loading cookies
	private void loadCookiesFromPreferences(Context context,
			DefaultHttpClient client) {
		final CookieStore cookieStore = client.getCookieStore();
		final SharedPreferences sharedPreferences = context
				.getSharedPreferences(SHARED_PREFERENCES_NAME_SUMC_COOKIES,
						Context.MODE_PRIVATE);

		int i = 0;
		while (sharedPreferences.contains(PREFERENCES_COOKIE_NAME + i)) {
			final String name = sharedPreferences.getString(
					PREFERENCES_COOKIE_NAME + i, null);
			final String value = sharedPreferences.getString(
					PREFERENCES_COOKIE_VALUE + i, null);
			final BasicClientCookie result = new BasicClientCookie(name, value);

			result.setDomain(sharedPreferences.getString(
					PREFERENCES_COOKIE_DOMAIN + i, null));
			result.setPath(sharedPreferences.getString(PREFERENCES_COOKIE_PATH
					+ i, null));
			cookieStore.addCookie(result);
			i++;
		}
	}

	// Adding the User-Agent, the Referrer and the parameters to the HttpPost
	private static HttpPost createRequest(String stationCode,
			String stationCodeO, String captchaText, String captchaId) {
		final HttpPost result = new HttpPost(URL);
		result.addHeader("User-Agent", USER_AGENT);
		result.addHeader("Referer", REFERER);

		try {
			final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					parameters(stationCode, stationCodeO, captchaText,
							captchaId), "UTF-8");
			result.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Not supported default encoding?",
					e);
		}

		return result;
	}

	// Adding parameters to a list (used in the HttpPost)
	private static List<BasicNameValuePair> parameters(String stationCode,
			String stationCodeO, String captchaText, String captchaId) {
		// Ensure that the search will return results
		if ("-1".equals(stationCodeO)
				|| Constants.SCHEDULE_GPS_PARAM.equals(stationCodeO)) {
			stationCodeO = "1";
		}

		final List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>(
				5);
		result.addAll(Arrays.asList(new BasicNameValuePair(QUERY_BUS_STOP_ID,
				stationCode), new BasicNameValuePair(QUERY_GO, "1"),
				new BasicNameValuePair(QUERY_O, stationCodeO)));

		if (captchaText != null && captchaId != null) {
			result.add(new BasicNameValuePair(QUERY_CAPTCHA_ID, captchaId));
			result.add(new BasicNameValuePair(QUERY_CAPTCHA_TEXT, captchaText));
		}

		return result;
	}

	// Check if CAPTCHA is required
	private void checkCaptchaText(DefaultHttpClient client, Context context,
			String stationCode, String stationCodeO, String src) {
		try {
			if (src.contains(REQUIRES_CAPTCHA)) {
				String captchaId = getCaptchaId(src);
				Log.d(TAG, "Captcha ID: " + captchaId);

				if (captchaId != null) {
					// Making HttpRequest and showing a progress dialog
					ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog.setMessage("Loading...");
					LoadingCaptcha loadingCaptcha = new LoadingCaptcha(context,
							progressDialog, client, stationCode, stationCodeO,
							captchaId);
					loadingCaptcha.execute();
				} else {
					startNewActivity(client, context, stationCode,
							stationCodeO, src);
				}
			} else {
				startNewActivity(client, context, stationCode, stationCodeO,
						src);
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not get data for parameters for station: "
					+ stationCode, e);
			startNewActivity(client, context, stationCode, stationCodeO,
					VirtualBoards.captchaErrorMessage);
		}
	}

	// Get the CAPTCHA ID from the HTML source file
	private static String getCaptchaId(String src) {
		final int captchaStart = src.indexOf(CAPTCHA_START);
		if (captchaStart == -1) {
			return null;
		}
		final int captchaEnd = src.indexOf(CAPTCHA_END, captchaStart
				+ CAPTCHA_START.length());
		if (captchaEnd == -1) {
			return null;
		}

		return src.substring(captchaStart + CAPTCHA_START.length(), captchaEnd);
	}

	// Get the image from URL
	private static Bitmap getCaptchaImage(HttpClient client, String captchaId)
			throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(String.format(CAPTCHA_IMAGE,
				captchaId));
		request.addHeader("User-Agent", USER_AGENT);
		request.addHeader("Referer", REFERER);
		request.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, true);

		final HttpResponse response = client.execute(request);

		final HttpEntity entity = response.getEntity();
		final InputStream in = entity.getContent();

		int next;
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((next = in.read()) != -1) {
			bos.write(next);
		}
		bos.flush();
		byte[] result = bos.toByteArray();

		bos.close();

		entity.consumeContent();

		Bitmap sumcBitmap = BitmapFactory.decodeByteArray(result, 0,
				result.length);
		return Bitmap.createScaledBitmap(sumcBitmap, 180, 60, false);
	}

	// Showing an AlertDialog to enter the CAPTCHA text
	private void getCaptchaText(final DefaultHttpClient client,
			final Context context, final String stationCode,
			final String stationCodeO, final String captchaId,
			Bitmap captchaImage) {
		final Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.sumc_captcha);
		dialogBuilder.setMessage(R.string.sumc_captcha_msg);
		final LinearLayout panel = new LinearLayout(context);
		panel.setOrientation(LinearLayout.VERTICAL);
		final TextView label = new TextView(context);
		label.setId(1);
		label.setText(R.string.sumc_captcha);
		panel.addView(label);

		final ImageView image = new ImageView(context);
		image.setId(3);
		image.setImageBitmap(captchaImage);
		panel.addView(image);

		final EditText input = new EditText(context);
		input.setId(2);
		input.setSingleLine();
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_URI
				| InputType.TYPE_TEXT_VARIATION_PHONETIC);
		final ScrollView view = new ScrollView(context);
		panel.addView(input);
		view.addView(panel);

		dialogBuilder
				.setCancelable(true)
				.setPositiveButton(R.string.sumc_button_label,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialogResult = input.getText().toString();

								processCaptchaText(client, context,
										stationCode, stationCodeO, captchaId);
								dialog.dismiss();
							}
						}).setView(view);

		dialogBuilder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				dialogResult = null;
			}
		});
		dialogBuilder.create().show();

	}

	// Processing the CAPTCHA text, after clicking OK button
	private void processCaptchaText(DefaultHttpClient client, Context context,
			String stationCode, String stationCodeO, String captchaId) {
		String captchaText = dialogResult;
		dialogResult = null;

		Log.d(TAG, "RESULT: " + captchaText);

		// Making HttpRequest and showing a progress dialog
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Loading...");
		LoadingSumc loadingSumc = new LoadingSumc(context, progressDialog,
				client, stationCode, stationCodeO, captchaText, captchaId);
		loadingSumc.execute();
	}

	// Starting new activity
	private void startNewActivity(DefaultHttpClient client, Context context,
			String stationCode, String stationCodeO, String src) {
		String text = stationCode + "SEPARATOR" + src + "SEPARATOR"
				+ coordinates[0] + "SEPARATOR" + coordinates[1] + "SEPARATOR"
				+ stationCodeO;

		saveCookiesToPreferences(context, client);
		client.getConnectionManager().shutdown();

		Intent intent = new Intent();

		// Check which activity calls this class and in case of "VirtualBoards"
		// finish the previous activity
		if (context.getClass().toString()
				.equals("class bg.znestorov.sofbus24.main.VirtualBoards")) {
			intent = new Intent(context, VirtualBoards.class);

			intent.putExtra(VirtualBoards.keyHtmlResult, text);
			context.startActivity(intent);
			((VirtualBoards) context).finish();

			return;
		}

		// Check if the result is returning multiple results
		if (src.toUpperCase().contains(Constants.SEARCH_TYPE_COUNT_RESULTS_1)
				&& src.toUpperCase().contains(
						Constants.SEARCH_TYPE_COUNT_RESULTS_2)
				&& "-1".equals(stationCodeO)) {
			intent = new Intent(context, VirtualBoardsStationChoice.class);

			intent.putExtra(VirtualBoards.keyHtmlResult, text);
			context.startActivity(intent);

			return;
		}

		// Check in case the user is requesting information from Schedule
		// section
		if (src.toUpperCase().contains(Constants.SEARCH_TYPE_COUNT_RESULTS_1)
				&& src.toUpperCase().contains(
						Constants.SEARCH_TYPE_COUNT_RESULTS_2)
				&& Constants.SCHEDULE_GPS_PARAM.equals(stationCodeO)) {
			stationCodeO = Utils.getCodeO(src, stationCode);
			new HtmlRequestSumc().getInformation(context, stationCode,
					stationCodeO, null);

			return;
		}

		// Check in case the user is requesting information from Favorites
		// section
		if (src.toUpperCase().contains(Constants.SEARCH_TYPE_COUNT_RESULTS_1)
				&& src.toUpperCase().contains(
						Constants.SEARCH_TYPE_COUNT_RESULTS_2)
				&& Constants.FAVORITES_GPS_PARAM.equals(stationCodeO)) {
			stationCodeO = Utils.getCodeO(src, stationCode);

			// Updating the Favorites DB with the CodeO
			FavouritesDataSource datasource = new FavouritesDataSource(context);
			datasource.open();
			datasource.updateStation(stationCode, stationCodeO);
			datasource.close();

			new HtmlRequestSumc().getInformation(context, stationCode,
					stationCodeO, null);

			return;
		}

		// Start VirtualBoards
		intent = new Intent(context, VirtualBoards.class);

		intent.putExtra(VirtualBoards.keyHtmlResult, text);
		context.startActivity(intent);
	}

	private class LoadingSumc extends AsyncTask<Void, Void, String> {
		Context context;
		ProgressDialog progressDialog;
		DefaultHttpClient client;
		String stationCode;
		String stationCodeO;
		String captchaText;
		String captchaId;

		public LoadingSumc(Context context, ProgressDialog progressDialog,
				DefaultHttpClient client, String stationCode,
				String stationCodeO, String captchaText, String captchaId) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.client = client;
			this.stationCode = stationCode;
			this.stationCodeO = stationCodeO;
			this.captchaText = captchaText;
			this.captchaId = captchaId;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String htmlSourceCode = null;

			try {
				final HttpPost request = createRequest(stationCode,
						stationCodeO, captchaText, captchaId);
				htmlSourceCode = client.execute(request,
						new BasicResponseHandler());
			} catch (Exception e) {
				Log.e(TAG, "Could not get data for station " + stationCode, e);
				htmlSourceCode = "EXCEPTION";
			}

			return htmlSourceCode;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();

			if (result.equals("EXCEPTION")) {
				startNewActivity(client, context, stationCode, stationCodeO,
						VirtualBoards.htmlErrorMessage);
			} else {
				checkCaptchaText(client, context, stationCode, stationCodeO,
						result);
			}
		}
	}

	private class LoadingCaptcha extends AsyncTask<Void, Void, Bitmap> {
		Context context;
		ProgressDialog progressDialog;
		DefaultHttpClient client;
		String stationCode;
		String stationCodeO;
		String captchaId;

		public LoadingCaptcha(Context context, ProgressDialog progressDialog,
				DefaultHttpClient client, String stationCode,
				String stationCodeO, String captchaId) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.client = client;
			this.stationCode = stationCode;
			this.stationCodeO = stationCodeO;
			this.captchaId = captchaId;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap captchaImage;
			try {
				captchaImage = getCaptchaImage(client, captchaId);
			} catch (Exception e) {
				captchaImage = null;
			}

			return captchaImage;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			progressDialog.dismiss();

			if (result != null) {
				getCaptchaText(client, context, stationCode, stationCodeO,
						captchaId, result);
			} else {
				startNewActivity(client, context, stationCode, stationCodeO,
						VirtualBoards.htmlErrorMessage);
			}
		}
	}
}