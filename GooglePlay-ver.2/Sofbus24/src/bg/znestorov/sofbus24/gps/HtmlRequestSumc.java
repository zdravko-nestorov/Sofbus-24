package bg.znestorov.sofbus24.gps;

import static bg.znestorov.sofbus24.utils.Utils.formatNumberOfDigits;
import static bg.znestorov.sofbus24.utils.Utils.isTimeInRange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

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
import android.view.WindowManager;
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
	private static final String TAG = "HTML Request SUMC";

	// Getting the CAPTCHA text from the dialog result
	private static String dialogResult = null;

	// Coordinates of the station
	private static String[] coordinates = new String[2];

	// Consecutive requests in case of an error
	private static int requestsCount = 0;

	/**
	 * Getting the source file of the HTTP request and opening a new Activity
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param transferCoordinates
	 *            array with length 2 - containg the longitude and latitude of
	 *            the station
	 */
	public void getInformation(Context context, String stationCode,
			String stationCodeO, String[] transferCoordinates) {
		// Assigning the transfer coordinates to a global variable
		if (transferCoordinates != null) {
			coordinates = transferCoordinates;
		} else {
			coordinates[0] = Constants.GLOBAL_PARAM_EMPTY;
			coordinates[1] = Constants.GLOBAL_PARAM_EMPTY;
		}

		// Check if stationCodeO is null or not
		if (stationCodeO == null) {
			stationCodeO = "-1";
		}

		// HTTP Client - created once and using cookies
		final DefaultHttpClient client = new DefaultHttpClient();

		loadCookiesFromPreferences(context, client);

		// Making HttpRequest and showing a progress dialog
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.loading_message_retrieve_sumc_info));
		LoadingSumc loadingSumc = new LoadingSumc(context, progressDialog,
				client, stationCode, stationCodeO, null, null);
		loadingSumc.execute();
	}

	/**
	 * Getting the value for the CAPTCHA code and put it in a SharedPreferences
	 * file - containing the information in key=value format. It is saved on the
	 * internal memory of the device and contains all cookies from the request.
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 */
	private void saveCookiesToPreferences(Context context,
			DefaultHttpClient client) {
		final SharedPreferences sharedPreferences = context
				.getSharedPreferences(
						Constants.SHARED_PREFERENCES_NAME_SUMC_COOKIES,
						Context.MODE_PRIVATE);
		final Editor edit = sharedPreferences.edit();
		edit.clear();

		int i = 0;
		for (Cookie cookie : client.getCookieStore().getCookies()) {
			edit.putString(Constants.PREFERENCES_COOKIE_NAME + i,
					cookie.getName());
			edit.putString(Constants.PREFERENCES_COOKIE_VALUE + i,
					cookie.getValue());
			edit.putString(Constants.PREFERENCES_COOKIE_DOMAIN + i,
					cookie.getDomain());
			edit.putString(Constants.PREFERENCES_COOKIE_PATH + i,
					cookie.getPath());
			i++;
		}
		edit.commit();
	}

	/**
	 * Getting the value for the CAPTCHA code from a SharedPreferences file that
	 * contains the information in key=value format. It is saved on the internal
	 * memory of the device, containing the cookies needed for sending a new
	 * request
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 */
	private void loadCookiesFromPreferences(Context context,
			DefaultHttpClient client) {
		final CookieStore cookieStore = client.getCookieStore();
		final SharedPreferences sharedPreferences = context
				.getSharedPreferences(
						Constants.SHARED_PREFERENCES_NAME_SUMC_COOKIES,
						Context.MODE_PRIVATE);

		int i = 0;
		while (sharedPreferences
				.contains(Constants.PREFERENCES_COOKIE_NAME + i)) {
			final String name = sharedPreferences.getString(
					Constants.PREFERENCES_COOKIE_NAME + i, null);
			final String value = sharedPreferences.getString(
					Constants.PREFERENCES_COOKIE_VALUE + i, null);
			final BasicClientCookie result = new BasicClientCookie(name, value);

			result.setDomain(sharedPreferences.getString(
					Constants.PREFERENCES_COOKIE_DOMAIN + i, null));
			result.setPath(sharedPreferences.getString(
					Constants.PREFERENCES_COOKIE_PATH + i, null));
			cookieStore.addCookie(result);
			i++;
		}
	}

	/**
	 * Adding the User-Agent, the Referrer and the parameters to the HttpPost
	 * 
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param captchaText
	 *            The text that the user entered according to the CAPTCHA image
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @return an HTTP POST object, created with the needed params
	 */
	private static HttpPost createSumcRequest(String stationCode,
			String stationCodeO, String captchaText, String captchaId) {
		final HttpPost result = new HttpPost(Constants.VB_URL);
		result.addHeader("User-Agent", Constants.USER_AGENT);
		result.addHeader("Referer", Constants.REFERER);

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

	/**
	 * Creating a list with BasicNameValuePair params, used for preparing the
	 * HTTP POST request
	 * 
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param captchaText
	 *            The text that the user entered according to the CAPTCHA image
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @return a list with BasicNameValuePair parameters for the HTTP POST
	 *         request
	 */
	private static List<BasicNameValuePair> parameters(String stationCode,
			String stationCodeO, String captchaText, String captchaId) {
		// Ensure that the search will return results
		if ("-1".equals(stationCodeO)
				|| Constants.SCHEDULE_GPS_PARAM.equals(stationCodeO)) {
			stationCodeO = "1";
		}

		final List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>(
				5);
		result.addAll(Arrays.asList(new BasicNameValuePair(
				Constants.QUERY_BUS_STOP_ID, stationCode),
				new BasicNameValuePair(Constants.QUERY_GO, "1"),
				new BasicNameValuePair(Constants.QUERY_O, stationCodeO)));

		if (captchaText != null && captchaId != null) {
			result.add(new BasicNameValuePair(Constants.QUERY_CAPTCHA_ID,
					captchaId));
			result.add(new BasicNameValuePair(Constants.QUERY_CAPTCHA_TEXT,
					captchaText));
		}

		return result;
	}

	/**
	 * Checking if a CAPTCHA image has to be showed to the user
	 * 
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param src
	 *            The response text, prepared from the HTTP request to the SUMC
	 *            server
	 */
	private void checkCaptchaText(DefaultHttpClient client, Context context,
			String stationCode, String stationCodeO, String src) {
		try {
			if (src.contains(Constants.REQUIRES_CAPTCHA)) {
				String captchaId = getCaptchaId(src);

				if (captchaId != null) {
					// Making HttpRequest and showing a progress dialog
					ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog
							.setMessage(context
									.getString(R.string.loading_message_retrieve_sumc_info));
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
					Constants.SUMC_CAPTCHA_ERROR_MESSAGE);
		}
	}

	/**
	 * Get the CAPTCHA ID from the HTML source file
	 * 
	 * @param src
	 *            The response text, prepared from the HTTP request to the SUMC
	 *            server
	 * @return the CAPTCHA Id from the source file
	 */
	private static String getCaptchaId(String src) {
		final int captchaStart = src.indexOf(Constants.CAPTCHA_START);
		if (captchaStart == -1) {
			return null;
		}
		final int captchaEnd = src.indexOf(Constants.CAPTCHA_END, captchaStart
				+ Constants.CAPTCHA_START.length());
		if (captchaEnd == -1) {
			return null;
		}

		return src.substring(captchaStart + Constants.CAPTCHA_START.length(),
				captchaEnd);
	}

	/**
	 * Getting the image file as a Bitmap image from the source file
	 * 
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 * @param request
	 *            HTTP GET request
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @return a Bitmap CAPTCHA image
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static Bitmap getCaptchaImage(HttpClient client, HttpGet request,
			String captchaId) throws ClientProtocolException, IOException {
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

	/**
	 * Create a CAPTCHA HTTP GET request
	 * 
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @return a HTTP GET request to the SUMC server
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpGet createCaptchaRequest(String captchaId)
			throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(String.format(
				Constants.CAPTCHA_IMAGE, captchaId));
		request.addHeader("User-Agent", Constants.USER_AGENT);
		request.addHeader("Referer", Constants.REFERER);
		request.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, true);

		return request;
	}

	/**
	 * Creating an Alert Dialog with the CAPTCHA image and sets some settings on
	 * it (as enlarging the image, set the keyboard input type and so on)
	 * 
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @param captchaImage
	 *            The Bitmap CAPTCHA image
	 */
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

		// Workaround to show a keyboard
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alertDialog.show();
	}

	/**
	 * Processing the CAPTCHA text, after clicking OK button
	 * 
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 */
	private void processCaptchaText(DefaultHttpClient client, Context context,
			String stationCode, String stationCodeO, String captchaId) {
		String captchaText = dialogResult;
		dialogResult = null;

		// Making HttpRequest and showing a progress dialog
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.loading_message_retrieve_sumc_catcha));
		LoadingSumc loadingSumc = new LoadingSumc(context, progressDialog,
				client, stationCode, stationCodeO, captchaText, captchaId);
		loadingSumc.execute();
	}

	/**
	 * Starting a new activity and checking for all possible errors that can
	 * occur. It is also checking the source that is creating the new activity
	 * and set a special condition before this
	 * 
	 * @param client
	 *            The DefaultHttpClient, which is created only once and used for
	 *            HTTP requests
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param src
	 *            The response text, prepared from the HTTP request to the SUMC
	 *            server
	 */
	private void startNewActivity(DefaultHttpClient client, Context context,
			String stationCode, String stationCodeO, String src) {
		saveCookiesToPreferences(context, client);
		client.getConnectionManager().shutdown();

		Intent intent = new Intent();

		// Check if codeO is present
		if ("-1".equals(stationCodeO)
				|| Constants.SCHEDULE_GPS_PARAM.equals(stationCodeO)
				|| Constants.GPS_TIMES_GPS_PARAM.equals(stationCodeO)
				|| Constants.FAVORITES_GPS_PARAM.equals(stationCodeO)
				|| Constants.MULTIPLE_RESULTS_GPS_PARAM.equals(stationCodeO)) {
			VirtualBoardsStationChoice.checkCodeO = false;
		} else {
			VirtualBoardsStationChoice.checkCodeO = true;
			stationCode = Utils.getStationId(src, stationCode, stationCodeO);
		}

		String text = stationCode + Constants.GLOBAL_PARAM_SEPARATOR + src
				+ Constants.GLOBAL_PARAM_SEPARATOR + coordinates[0]
				+ Constants.GLOBAL_PARAM_SEPARATOR + coordinates[1]
				+ Constants.GLOBAL_PARAM_SEPARATOR + stationCodeO;

		// Check which activity calls this class and in case of "VirtualBoards"
		// finish the previous activity
		if (context.getClass().toString()
				.equals("class bg.znestorov.sofbus24.main.VirtualBoards")) {

			// In case of multiple REFRESH, the SUMC site is not returning
			// information and falling in an error -
			// Constants.ERROR_NO_INFO_STATION
			if (src.indexOf(Constants.BODY_START) == -1
					|| src.indexOf(Constants.BODY_END) == -1) {
				text = text.replaceAll("<head>",
						Constants.SEARCH_ERROR_WITH_REFRESH);
				startAnErrorActivity(context, stationCode, stationCodeO, text);

				return;
			}

			// Null the request counter
			requestsCount = 0;

			intent = new Intent(context, VirtualBoards.class);
			intent.putExtra(Constants.KEYWORD_HTML_RESULT, text);
			context.startActivity(intent);
			((VirtualBoards) context).finish();

			return;
		}

		// Check if there is an error while retrieving/processing the
		// information
		if (src == null || "".equals(src)
				|| !src.contains(Constants.ERORR_NONE)
				|| src.contains(Constants.ERROR_NO_INFO_STATION)
				|| src.contains(Constants.ERROR_NO_INFO_NOW)
				|| src.contains(Constants.ERROR_NO_INFO)) {

			// Check if the request is from SCHEDULE/GPS GoogleMaps/FAVORITES
			if (!Constants.SCHEDULE_GPS_PARAM.equals(stationCodeO)
					&& !Constants.GPS_TIMES_GPS_PARAM.equals(stationCodeO)
					&& !Constants.FAVORITES_GPS_PARAM.equals(stationCodeO)
					&& !Constants.MULTIPLE_RESULTS_GPS_PARAM
							.equals(stationCodeO)) {

				// Check if the HTML source contains BODY_START and BODY_END ==>
				// in this case there is 100% a result (example:
				// "Централа автогара (2665)") and no multiple results are found
				if ((src.indexOf(Constants.BODY_START) == -1 || src
						.indexOf(Constants.BODY_END) == -1)
						&& !(src.toUpperCase().contains(
								Constants.SEARCH_TYPE_COUNT_RESULTS_1) && src
								.toUpperCase().contains(
										Constants.SEARCH_TYPE_COUNT_RESULTS_2))) {
					startAnErrorActivity(context, stationCode, stationCodeO,
							text);

					return;
				}
			}
		}

		// Check if the result is returning multiple results (after entering the
		// station code in the input dialog after selecting Virtual Boards
		// option on the home screen)
		if (src.toUpperCase().contains(Constants.SEARCH_TYPE_COUNT_RESULTS_1)
				&& src.toUpperCase().contains(
						Constants.SEARCH_TYPE_COUNT_RESULTS_2)
				&& "-1".equals(stationCodeO)) {
			requestsCount = 0;

			intent = new Intent(context, VirtualBoardsStationChoice.class);
			intent.putExtra(Constants.KEYWORD_HTML_RESULT, text);
			context.startActivity(intent);

			return;
		}

		// Check in case the user is requesting information from "Schedule",
		// "GPS Times GoogleMaps" or "Multiple Results " sections
		if (src.toUpperCase().contains(Constants.SEARCH_TYPE_COUNT_RESULTS_1)
				&& src.toUpperCase().contains(
						Constants.SEARCH_TYPE_COUNT_RESULTS_2)
				&& (Constants.SCHEDULE_GPS_PARAM.equals(stationCodeO)
						|| Constants.GPS_TIMES_GPS_PARAM.equals(stationCodeO) || Constants.MULTIPLE_RESULTS_GPS_PARAM
							.equals(stationCodeO))) {
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

		try {
			new HtmlResultSumc(context, stationCode, src).showResult();
			requestsCount = 0;

			// Start VirtualBoards
			intent = new Intent(context, VirtualBoards.class);
			intent.putExtra(Constants.KEYWORD_HTML_RESULT, text);
			context.startActivity(intent);

			// Case that is reached when the method is called from
			// SCHEDULE/GPS GoogleMaps/FAVORITES and some error occur
		} catch (Exception e) {
			// Start VirtualBoardsStationChoice
			startAnErrorActivity(context, stationCode, stationCodeO, text);
		}
	}

	/**
	 * Creating a request to the SUMC server to see if any CAPTCHA is required
	 * to be entered as a security measure
	 * 
	 * @author znestorov
	 * 
	 */
	private class LoadingSumc extends AsyncTask<Void, Void, String> {

		Context context;
		ProgressDialog progressDialog;
		DefaultHttpClient client;
		String stationCode;
		String stationCodeO;
		String captchaText;
		String captchaId;

		// Http Post parameter used to create/abort the HTTP connection
		HttpPost httpPost;

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

		@Override
		protected String doInBackground(Void... params) {
			String htmlSourceCode = null;

			try {
				httpPost = createSumcRequest(stationCode, stationCodeO,
						captchaText, captchaId);
				htmlSourceCode = client.execute(httpPost,
						new BasicResponseHandler());
			} catch (Exception e) {
				Log.e(TAG, "Could not get data for station " + stationCode, e);
				htmlSourceCode = Constants.EXCEPTION;
			}

			return htmlSourceCode;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}

			if (result.equals(Constants.EXCEPTION)) {
				startNewActivity(client, context, stationCode, stationCodeO,
						Constants.SUMC_HTML_ERROR_MESSAGE);
			} else {
				checkCaptchaText(client, context, stationCode, stationCodeO,
						result);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			try {
				progressDialog.dismiss();
				httpPost.abort();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}
		}
	}

	/**
	 * Class responsible for loading the CAPTCHA image from the server
	 * 
	 * @author znestorov
	 * 
	 */
	private class LoadingCaptcha extends AsyncTask<Void, Void, Bitmap> {

		Context context;
		ProgressDialog progressDialog;
		DefaultHttpClient client;
		String stationCode;
		String stationCodeO;
		String captchaId;

		// Http Get parameter used to create/abort the HTTP connection
		HttpGet httpGet;

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

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap captchaImage;
			try {
				httpGet = createCaptchaRequest(captchaId);
				captchaImage = getCaptchaImage(client, httpGet, captchaId);
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
						Constants.SUMC_HTML_ERROR_MESSAGE);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			try {
				progressDialog.dismiss();
				httpGet.abort();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}
		}
	}

	/**
	 * If an error occur once trying to retrieve an information from the mobile
	 * version of the site, a few checks are executed to determine if the
	 * problem is with the site, or just no information is available in this
	 * moment:
	 * <ul>
	 * <li>Make two more requests if the current time is between 05:00 and 24:00
	 * </li>
	 * <li>Make one more request if the current time is between 00:00 and 01:00</li>
	 * <li>Otherwise - show an error dialog</li>
	 * </ul>
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param stationCode
	 *            The code of the station
	 * @param stationCodeO
	 *            The position of the station code in case of multiple results:
	 *            <ul>
	 *            <li>null - when search for a first time for a result and
	 *            multiple are found</li>
	 *            <li>any number - the position of the station in the list with
	 *            multiple results</li>
	 *            <li>schedule - in case the method is called from SCHEDULE
	 *            section</li>
	 *            <li>gpsTimes - in case the method is called from GPS TIMES
	 *            section</li>
	 *            <li>favorites - in case the method is called from FAVORITES
	 *            section</li>
	 *            </ul>
	 * @param transferredText
	 *            the text that will be transferred to the error dialog
	 */
	private void startAnErrorActivity(Context context, String stationCode,
			String stationCodeO, String transferredText) {
		requestsCount++;

		TimeZone timeZone = TimeZone.getTimeZone(Constants.TIME_ZONE);
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(timeZone);

		String currentTime = calendar.get(Calendar.HOUR_OF_DAY)
				+ formatNumberOfDigits("" + calendar.get(Calendar.MINUTE), 2);

		// In case of an error with the mobile version of the site, make another
		// 2 requests (if the current time is between [05:00 and 24:00] and
		// [00:00 and 01:00])
		if (requestsCount <= Constants.MAX_CONSECUTIVE_REQUESTS_1
				&& !isTimeInRange(currentTime,
						Constants.CONSECUTIVE_REQUESTS_START_HOUR_1,
						Constants.CONSECUTIVE_REQUESTS_END_HOUR_1)) {
			new HtmlRequestSumc().getInformation(context, stationCode,
					stationCodeO, null);
			return;
		}

		// In case of an error with the mobile version of the site, make another
		// 1 request (if the current time is between [04:00 and 05:00])
		if (requestsCount <= Constants.MAX_CONSECUTIVE_REQUESTS_2
				&& isTimeInRange(currentTime,
						Constants.CONSECUTIVE_REQUESTS_START_HOUR_2,
						Constants.CONSECUTIVE_REQUESTS_END_HOUR_2)) {
			new HtmlRequestSumc().getInformation(context, stationCode,
					stationCodeO, null);
			return;
		}

		requestsCount = 0;

		Intent intent = new Intent(context, VirtualBoardsStationChoice.class);
		intent.putExtra(Constants.KEYWORD_HTML_RESULT, transferredText);
		context.startActivity(intent);
	}
}