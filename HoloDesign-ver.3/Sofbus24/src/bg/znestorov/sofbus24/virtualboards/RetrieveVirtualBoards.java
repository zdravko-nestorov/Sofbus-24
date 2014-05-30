package bg.znestorov.sofbus24.virtualboards;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
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
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.HtmlRequestCodes;
import bg.znestorov.sofbus24.entity.HtmlResultCodes;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.VirtualBoardsTime;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class RetrieveVirtualBoards {

	private Activity context;
	private Object callerInstance;

	private Station station;

	private HtmlRequestCodes htmlRequestCode;
	private HtmlResultCodes htmlResultCode;

	private DefaultHttpClient httpClient;
	private FavouritesDataSource favouriteDatasource;

	public RetrieveVirtualBoards(Activity context, Object callerInstance,
			Station station, HtmlRequestCodes htmlRequestCode) {
		// Set the current activity context and the object that created an
		// instance of this class
		this.context = context;
		this.callerInstance = callerInstance;

		// Set the selected station
		if (station.getCustomField() == null
				|| "".equals(station.getCustomField())) {
			station.setCustomField("1");
		}
		this.station = station;

		// Set the type of call to the class
		this.htmlRequestCode = htmlRequestCode;

		// Creating a HTTP Client
		this.httpClient = new DefaultHttpClient();

		// Create an instance of the favourite database
		this.favouriteDatasource = new FavouritesDataSource(context);
	}

	/**
	 * Retrieve the information for the selected station
	 */
	public void getSumcInformation() {
		// Load the cookies from the preferences (if exists)
		loadCookiesFromPreferences();

		// Create the appropriate progress dialog message (if searched by
		// HomeScreen - show only the searched string, otherwise - the station
		// caption)
		Spanned progressDialogMsg = getProgressDialogMsg(context
				.getString(R.string.vb_time_retrieve_info));

		// Making HttpRequest and showing a progress dialog if needed
		ProgressDialog progressDialog = createProgressDialog(progressDialogMsg);
		RetrieveSumcInformation retrieveSumcInformation = new RetrieveSumcInformation(
				progressDialog, null, null);
		retrieveSumcInformation.execute();
	}

	/**
	 * Getting the value for the CAPTCHA code and put it in a SharedPreferences
	 * file - containing the information in key=value format. It is saved on the
	 * internal memory of the device and contains all cookies from the request.
	 */
	private void saveCookiesToPreferences() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.VB_PREFERENCES_NAME_SUMC_COOKIES,
				Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.clear();

		int i = 0;
		for (Cookie cookie : httpClient.getCookieStore().getCookies()) {
			edit.putString(Constants.VB_PREFERENCES_COOKIE_NAME + i,
					cookie.getName());
			edit.putString(Constants.VB_PREFERENCES_COOKIE_VALUE + i,
					cookie.getValue());
			edit.putString(Constants.VB_PREFERENCES_COOKIE_DOMAIN + i,
					cookie.getDomain());
			edit.putString(Constants.VB_PREFERENCES_COOKIE_PATH + i,
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
	 */
	private void loadCookiesFromPreferences() {
		CookieStore cookieStore = httpClient.getCookieStore();
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.VB_PREFERENCES_NAME_SUMC_COOKIES,
				Context.MODE_PRIVATE);

		int i = 0;
		while (sharedPreferences.contains(Constants.VB_PREFERENCES_COOKIE_NAME
				+ i)) {
			final String name = sharedPreferences.getString(
					Constants.VB_PREFERENCES_COOKIE_NAME + i, null);
			final String value = sharedPreferences.getString(
					Constants.VB_PREFERENCES_COOKIE_VALUE + i, null);
			final BasicClientCookie result = new BasicClientCookie(name, value);

			result.setDomain(sharedPreferences.getString(
					Constants.VB_PREFERENCES_COOKIE_DOMAIN + i, null));
			result.setPath(sharedPreferences.getString(
					Constants.VB_PREFERENCES_COOKIE_PATH + i, null));
			cookieStore.addCookie(result);
			i++;
		}
	}

	/**
	 * Adding the User-Agent, the Referrer and the parameters to the HttpPost
	 * 
	 * @param vehicleTypeId
	 *            the searched vehicle type:
	 *            <ul>
	 *            <li>0 - stands for TRAMs</li>
	 *            <li>1 - stands for BUSES</li>
	 *            <li>2 - stands for TROLLEYS</li>
	 *            </ul>
	 * @param captchaText
	 *            The text that the user entered according to the CAPTCHA image
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @return an HTTP POST object, created with the needed params
	 */
	private HttpPost createSumcRequest(String vehicleTypeId,
			String captchaText, String captchaId) {
		final HttpPost result = new HttpPost(Constants.VB_URL);
		result.addHeader("User-Agent", Constants.VB_URL_USER_AGENT);
		result.addHeader("Referer", Constants.VB_URL_REFERER);

		try {
			final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					assignHttpPostParameters(vehicleTypeId, captchaText,
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
	 * @param vehicleTypeId
	 *            the searched vehicle type:
	 *            <ul>
	 *            <li>0 - stands for TRAMs</li>
	 *            <li>1 - stands for BUSES</li>
	 *            <li>2 - stands for TROLLEYS</li>
	 *            </ul>
	 * @param captchaText
	 *            The text that the user entered according to the CAPTCHA image
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @return a list with BasicNameValuePair parameters for the HTTP POST
	 *         request
	 */
	private List<BasicNameValuePair> assignHttpPostParameters(
			String vehicleTypeId, String captchaText, String captchaId) {
		List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>();
		result.addAll(Arrays.asList(
				new BasicNameValuePair(Constants.VB_URL_STOP_CODE, station
						.getNumber()),
				new BasicNameValuePair(Constants.VB_URL_GO, "1"),
				new BasicNameValuePair(Constants.VB_URL_SEC, "5"),
				new BasicNameValuePair(Constants.VB_URL_O, station
						.getCustomField())));

		if (vehicleTypeId != null) {
			result.add(new BasicNameValuePair(Constants.VB_URL_VEHICLE_TYPE_ID,
					vehicleTypeId));
		}

		if (captchaText != null && captchaId != null) {
			result.add(new BasicNameValuePair(Constants.VB_URL_CAPTCHA_ID,
					captchaId));
			result.add(new BasicNameValuePair(Constants.VB_URL_CAPTCHA_TEXT,
					captchaText));
		}

		return result;
	}

	/**
	 * Create a list with all unique station numbers, so check the way to
	 * proceed with the HTML result
	 * 
	 * @param htmlSourceCode
	 *            the HTML source returned by the standard HTML request (without
	 *            vehicleTypeId parameter)
	 * @return a list with all unique station numbers from the HTML source
	 */
	private LinkedHashSet<String> getStationNumbers(String htmlSourceCode) {
		LinkedHashSet<String> stationNumbers = new LinkedHashSet<String>();

		Pattern pattern = Pattern.compile(Constants.VB_REGEX_STATION_INFO);
		Matcher matcher = pattern.matcher(htmlSourceCode);
		while (matcher.find()) {
			try {
				stationNumbers.add(Utils.getOnlyDigits(matcher.group(2)));
			} catch (Exception e) {
			}
		}

		return stationNumbers;
	}

	/**
	 * Get the vehicles types of the cars passing through this station via the
	 * response HTML source code, as follows:<br/>
	 * <ul>
	 * <li>0 - for TRAMS</li>
	 * <li>1 - for BUSSES</li>
	 * <li>2 - for TROLLEYS</li>
	 * </ul>
	 * 
	 * @param htmlResult
	 *            the response HTML code
	 * @return an ArrayList with the different types of vehicles passing through
	 *         the station
	 */
	private ArrayList<String> getStationVehicleTypes(String htmlResult) {
		ArrayList<String> stationVehicleTypes = new ArrayList<String>();

		Pattern pattern = Pattern.compile(Constants.VB_REGEX_VEHICLE_TYPES);
		Matcher matcher = pattern.matcher(htmlResult);
		while (matcher.find()) {
			try {
				String vehicleName = matcher.group(2).trim().toUpperCase();
				String vehicleType;

				if (vehicleName.contains(Constants.VB_VEHICLE_TYPE_TROLLEY)) {
					vehicleType = "2";
				} else if (vehicleName.contains(Constants.VB_VEHICLE_TYPE_TRAM)) {
					vehicleType = "0";
				} else {
					vehicleType = "1";
				}

				stationVehicleTypes.add(vehicleType);
			} catch (Exception e) {
			}
		}

		return stationVehicleTypes;
	}

	/**
	 * Create the HTML source by combining only the needed information from each
	 * of the 3 requests (bus, trolley, tram)
	 * 
	 * @param htmlSourceCode
	 *            the global HTML source code
	 * @param tempHtmlSourceCode
	 *            the current HTML source code (bus, trolley or tram)
	 * @return the combined output between the global and current HTML results
	 */
	private String createHtmlSourceOutput(String htmlSourceCode,
			String tempHtmlSourceCode) {
		// Check if the global source code is empty or there is no
		// available information
		if (htmlSourceCode == null
				|| "".equals(htmlSourceCode)
				|| (htmlSourceCode != null && !"".equals(htmlSourceCode) && !htmlSourceCode
						.contains(Constants.VB_REGEX_SCHEDULE_START))) {
			htmlSourceCode = "";
		}

		// Check if the current source code is not empty
		if (tempHtmlSourceCode != null && !"".equals(tempHtmlSourceCode)) {
			// Check if the current source code contains schedule info
			if (tempHtmlSourceCode.contains(Constants.VB_REGEX_SCHEDULE_START)) {
				htmlSourceCode = appendArrivals(htmlSourceCode,
						tempHtmlSourceCode);
			} else if (htmlSourceCode == null
					|| "".equals(htmlSourceCode)
					|| tempHtmlSourceCode
							.contains(Constants.VB_CAPTCHA_REQUIRED)) {
				htmlSourceCode = tempHtmlSourceCode;
			}
		}

		return htmlSourceCode;
	}

	/**
	 * Append the arrivals from the global and current HTML source codes
	 * 
	 * @param htmlSourceCode
	 *            the global HTML source code
	 * @param tempHtmlSourceCode
	 *            the current HTML source code (bus, trolley or tram)
	 * @return the combined output between the global and current HTML results
	 *         (with replaced arrivarls section)
	 */
	private String appendArrivals(String htmlSourceCode,
			String tempHtmlSourceCode) {
		Pattern pattern = Pattern.compile(Constants.VB_REGEX_SCHEDULE_BODY);

		// Find arrivals (from the global source code)
		String arrivals = "";
		Matcher matcher = pattern.matcher(htmlSourceCode);
		try {
			if (matcher.find()) {
				arrivals = matcher.group(1).trim();
			}
		} catch (Exception e) {
		}

		// Find temp arrivals (from the current source code)
		String tempArrivals = "";
		Matcher tempMatcher = pattern.matcher(tempHtmlSourceCode);
		if (tempMatcher.find()) {
			tempArrivals = tempMatcher.group(1).trim();
		}

		tempHtmlSourceCode = tempHtmlSourceCode.replaceAll(
				Constants.VB_REGEX_SCHEDULE_BODY, "<div class=\"arrivals\">"
						+ arrivals + "\n" + tempArrivals + "\n</div>");

		return tempHtmlSourceCode;
	}

	/**
	 * Creating a request to the SUMC server to see if any CAPTCHA is required
	 * to be entered as a security measure
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	private class RetrieveSumcInformation extends AsyncTask<Void, Void, String> {

		private ProgressDialog progressDialog;
		private String captchaText;
		private String captchaId;

		// Http Post parameter used to create/abort the HTTP connection
		private HttpPost httpPost;

		public RetrieveSumcInformation(ProgressDialog progressDialog,
				String captchaText, String captchaId) {
			this.progressDialog = progressDialog;
			this.captchaText = captchaText;
			this.captchaId = captchaId;
		}

		@Override
		protected void onPreExecute() {
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
		protected String doInBackground(Void... params) {
			String htmlResult = null;

			try {
				// Create a standard HTML request (without vehicleTypeId param)
				httpPost = createSumcRequest(null, captchaText, captchaId);
				htmlResult = httpClient.execute(httpPost,
						new BasicResponseHandler());

				// Check if a capture is needed
				if (htmlResult.contains(Constants.VB_CAPTCHA_REQUIRED)) {
					htmlResultCode = HtmlResultCodes.CAPTCHA_NEEDED;
				} else {
					// Check how many unique station numbers are returned
					LinkedHashSet<String> stationNumbers = getStationNumbers(htmlResult);

					/**
					 * Proceed according to the unique station numbers in the
					 * request:<br/>
					 * - in case of empty list - no info available<br/>
					 * - in case of one station number - check the schedule for
					 * all type of vehciles<br/>
					 * - in case of more than one station number - show a list
					 * with all stations<br/>
					 */
					if (stationNumbers.isEmpty()) {
						htmlResultCode = HtmlResultCodes.NO_INFORMATION;
					} else if (stationNumbers.size() == 1) {
						ArrayList<String> stationVehicleTypes = getStationVehicleTypes(htmlResult);
						int vehicleTypesCount = stationVehicleTypes.size();

						/**
						 * Check what types of vehicles pass through the station<br/>
						 * - in case of 1 type - no more requests needed (the
						 * first requests take the time schedule)<br/>
						 * - in case of 2 types - make one more requests to get
						 * the times of arrival of other types of vehicles<br/>
						 * - in case of 3 types - make two more requests to get
						 * the times of arrival of other types of vehicles<br/>
						 */
						String tempHtmlSourceCode;
						if (vehicleTypesCount == 2) {
							httpPost = createSumcRequest(
									stationVehicleTypes.get(1), captchaText,
									captchaId);
							tempHtmlSourceCode = httpClient.execute(httpPost,
									new BasicResponseHandler());
							htmlResult = createHtmlSourceOutput(htmlResult,
									tempHtmlSourceCode);
						} else if (vehicleTypesCount == 3) {
							for (int i = 1; i < 3; i++) {
								httpPost = createSumcRequest(
										stationVehicleTypes.get(i),
										captchaText, captchaId);
								tempHtmlSourceCode = httpClient.execute(
										httpPost, new BasicResponseHandler());
								htmlResult = createHtmlSourceOutput(htmlResult,
										tempHtmlSourceCode);
							}
						}

						/**
						 * Check if the SKGT site returned all needed
						 * information
						 * <ul>
						 * <li>No result - INTERNER ERROR</li>
						 * <li>Result with information about captcha (in case
						 * there are more than one vehicle and some of the
						 * requests face captcha image) - CAPTCHA NEEDED</li>
						 * <li>No problem - SINGLE RESULT</li>
						 * </ul>
						 */
						if (htmlResult == null || "".equals(htmlResult)) {
							throw new Exception();
						} else if (htmlResult
								.contains(Constants.VB_CAPTCHA_REQUIRED)) {
							htmlResultCode = HtmlResultCodes.CAPTCHA_NEEDED;
						} else {
							htmlResultCode = HtmlResultCodes.SINGLE_RESULT;
						}
					} else {
						htmlResultCode = HtmlResultCodes.MULTIPLE_RESULTS;
					}
				}
			} catch (Exception e) {
				htmlResultCode = HtmlResultCodes.NO_INTERNET;
			}

			return htmlResult;
		}

		@Override
		protected void onPostExecute(String htmlResult) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}

			switch (htmlResultCode) {
			case CAPTCHA_NEEDED:
				checkCaptchaText(htmlResult);
				break;
			default:
				proccessHtmlResult(htmlResult);
				break;
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
	 * Checking if a captcha image has to be showed to the user
	 * 
	 * @param htmlResult
	 *            The response text, prepared from the HTTP request to the SUMC
	 *            server
	 */
	private void checkCaptchaText(String htmlResult) {
		// Get the captcha id (poleicngi) from the HTML source result
		String captchaId = getCaptchaId(htmlResult);

		// Making HttpRequest and showing a progress dialog if needed
		ProgressDialog progressDialog = createProgressDialog(Html
				.fromHtml(context.getString(R.string.vb_time_captcha_check)));
		RetrieveCaptchaInformation retrieveCaptchaInformation = new RetrieveCaptchaInformation(
				progressDialog, captchaId);
		retrieveCaptchaInformation.execute();
	}

	/**
	 * Get the captcha id (poleicngi) from the HTML source file
	 * 
	 * @param htmlResult
	 *            The response text, prepared from the HTTP request to the SUMC
	 *            server
	 * @return the captcha id from the source file
	 */
	private String getCaptchaId(String htmlResult) {
		String captchaId = null;

		Pattern pattern = Pattern.compile(Constants.VB_CAPTCHA_REGEX);
		Matcher matcher = pattern.matcher(htmlResult);

		if (matcher.find()) {
			captchaId = matcher.group(1);
		}

		return captchaId;
	}

	/**
	 * Create a captcha HTTP get request
	 * 
	 * @param captchaId
	 *            The id of the captcha image, token from the source file
	 * @return a HTTP get request to the SUMC server
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private HttpGet createCaptchaRequest(String captchaId) {
		final HttpGet request = new HttpGet(String.format(
				Constants.VB_CAPTCHA_URL, captchaId));

		request.addHeader("User-Agent", Constants.VB_URL_USER_AGENT);
		request.addHeader("Referer", Constants.VB_URL_REFERER);
		request.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, true);

		return request;
	}

	/**
	 * Getting the image as a Bitmap image from the source file
	 * 
	 * @param httpGet
	 *            the newly created HTTP get request
	 * @param captchaId
	 *            The Id of the captcha image, token from the source file
	 * @return a Bitmap captcha image
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private Bitmap getCaptchaImage(HttpGet httpGet, String captchaId)
			throws IOException {
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream in = httpEntity.getContent();

		int next;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((next = in.read()) != -1) {
			bos.write(next);
		}

		bos.flush();
		byte[] result = bos.toByteArray();
		bos.close();
		httpEntity.consumeContent();

		Bitmap captchaImage = BitmapFactory.decodeByteArray(result, 0,
				result.length);
		captchaImage = Bitmap.createScaledBitmap(captchaImage, 180, 60, false);

		return captchaImage;
	}

	/**
	 * Processing the CAPTCHA text, after clicking OK button
	 * 
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 */
	private void processCaptchaText(String captchaId, String captchaText) {
		// Translate the text to be always in latin
		captchaText = TranslatorCyrillicToLatin.translate(context, captchaText);

		// Making HttpRequest and showing a progress dialog
		ProgressDialog progressDialog = createProgressDialog(Html
				.fromHtml(String.format(
						context.getString(R.string.vb_time_retrieve_info),
						station.getNumber())));
		RetrieveSumcInformation retrieveSumcInformation = new RetrieveSumcInformation(
				progressDialog, captchaText, captchaId);
		retrieveSumcInformation.execute();
	}

	/**
	 * Creating an Alert Dialog with the captcha image and sets some settings on
	 * it (as enlarging the image, set the keyboard input type and so on)
	 * 
	 * @param captchaId
	 *            The Id of the CAPTCHA image, token from the source file
	 * @param captchaImage
	 *            The Bitmap CAPTCHA image
	 */
	private void getCaptchaText(final String captchaId, Bitmap captchaImage) {
		Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.vb_time_sumc_captcha);
		dialogBuilder.setMessage(R.string.vb_time_sumc_captcha_msg);

		LinearLayout panel = new LinearLayout(context);
		panel.setOrientation(LinearLayout.VERTICAL);

		ImageView image = new ImageView(context);
		image.setId(2);
		image.setImageBitmap(captchaImage);
		panel.addView(image);

		final EditText input = new EditText(context);
		input.setId(1);
		input.setSingleLine();
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_URI
				| InputType.TYPE_TEXT_VARIATION_PHONETIC);

		ScrollView view = new ScrollView(context);
		panel.addView(input);
		view.addView(panel);

		dialogBuilder
				.setCancelable(true)
				.setPositiveButton(R.string.app_button_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String captchaText = input.getText().toString();
								processCaptchaText(captchaId, captchaText);

								dialog.dismiss();
							}
						}).setView(view);

		dialogBuilder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				proccessHtmlResult(null);
			}
		});

		// Workaround to show a keyboard
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alertDialog.show();
	}

	/**
	 * Class responsible for loading the captcha image from the server
	 * 
	 * @author Zdravko Nestorov
	 * @version 1.0
	 * 
	 */
	private class RetrieveCaptchaInformation extends
			AsyncTask<Void, Void, Bitmap> {

		private ProgressDialog progressDialog;
		private String captchaId;

		// Http Get parameter used to create/abort the HTTP connection
		private HttpGet httpGet;

		public RetrieveCaptchaInformation(ProgressDialog progressDialog,
				String captchaId) {
			this.progressDialog = progressDialog;
			this.captchaId = captchaId;
		}

		@Override
		protected void onPreExecute() {
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
		protected Bitmap doInBackground(Void... params) {
			Bitmap captchaImage;
			try {
				httpGet = createCaptchaRequest(captchaId);
				captchaImage = getCaptchaImage(httpGet, captchaId);
			} catch (Exception e) {
				captchaImage = null;
			}

			return captchaImage;
		}

		@Override
		protected void onPostExecute(Bitmap captchaImage) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}

			if (captchaImage != null) {
				getCaptchaText(captchaId, captchaImage);
			} else {
				htmlResultCode = HtmlResultCodes.NO_INTERNET;
				proccessHtmlResult(null);
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
	 * Proccessing the html result and start new activity if needed (checks for
	 * all possible errors that can occur)
	 * 
	 * @param htmlResult
	 *            The response text, prepared from the HTTP request to the SUMC
	 *            server
	 */
	private void proccessHtmlResult(String htmlResult) {
		saveCookiesToPreferences();
		httpClient.getConnectionManager().shutdown();

		// Process the result accordingly
		ProcessVirtualBoards processVirtualBoards = new ProcessVirtualBoards(
				context, htmlResult);

		switch (htmlResultCode) {
		// In case of an error with the result (caprcha needed, no internet or
		// no information)
		case NO_INTERNET:
		case NO_INFORMATION:
		case CAPTCHA_NEEDED:
			switch (htmlRequestCode) {
			case REFRESH:
				((VirtualBoardsTime) callerInstance)
						.startVirtualBoardsTimeFragment(null, getErrorMsg());
				break;
			default:
				Spanned progressDialogMsg = getProgressDialogMsg(context
						.getString(R.string.app_info_error));
				ActivityUtils.showNoInfoAlertDialog(context, progressDialogMsg);
				break;
			}

			break;
		// In case of single result (only one station found)
		case SINGLE_RESULT:
			VirtualBoardsStation vbTimeStation;

			switch (htmlRequestCode) {
			case REFRESH:
				vbTimeStation = processVirtualBoards
						.getVBSingleStationFromHtml();
				((VirtualBoardsTime) callerInstance)
						.startVirtualBoardsTimeFragment(vbTimeStation, null);
				break;
			case MULTIPLE_RESULTS:
				ArrayList<Station> stationsList = new ArrayList<Station>(
						processVirtualBoards.getMultipleStationsFromHtml()
								.values());
				((VirtualBoardsFragment) callerInstance)
						.setListAdapterViaSearch(stationsList);
				// Important - no break here, because if only one station is
				// found - directly open the VirtualBoards
			default:
				vbTimeStation = processVirtualBoards
						.getVBSingleStationFromHtml();
				Intent vbTimeIntent = new Intent(context,
						VirtualBoardsTime.class);
				vbTimeIntent.putExtra(Constants.BUNDLE_VIRTUAL_BOARDS_TIME,
						vbTimeStation);
				context.startActivity(vbTimeIntent);
				break;
			}

			break;
		// In case of multiple result (more than one station found)
		default:
			HashMap<String, Station> stationsMap;

			switch (htmlRequestCode) {
			case REFRESH:
			case SINGLE_RESULT:
				stationsMap = processVirtualBoards
						.getMultipleStationsFromHtml();
				station = stationsMap.get(station.getNumber());
				getSumcInformation();

				break;
			case FAVOURITES:
				stationsMap = processVirtualBoards
						.getMultipleStationsFromHtml();
				station = stationsMap.get(station.getNumber());
				updateFavouritesStation(station);
				getSumcInformation();

				break;
			default:
				ArrayList<Station> stationsList = new ArrayList<Station>(
						processVirtualBoards.getMultipleStationsFromHtml()
								.values());
				((VirtualBoardsFragment) callerInstance)
						.setListAdapterViaSearch(stationsList);

				break;
			}

			break;
		}

	}

	/**
	 * Get the progress dialog message according to the htmlRequestCode
	 * 
	 * @param msg
	 *            the unformatted message from strings
	 * @return the formatted message
	 */
	private Spanned getProgressDialogMsg(String msg) {
		Spanned progressDialogMsg;

		switch (htmlRequestCode) {
		case MULTIPLE_RESULTS:
			progressDialogMsg = Html.fromHtml(String.format(msg,
					station.getNumber()));
			break;
		default:
			progressDialogMsg = Html.fromHtml(String.format(
					msg,
					String.format(station.getName() + " (%s)",
							station.getNumber())));
			break;
		}

		return progressDialogMsg;
	}

	/**
	 * Form the error message
	 * 
	 * @param msg
	 *            the unformatted message from strings
	 * @return the formatted message
	 */
	private String getErrorMsg() {
		String errorDialogMsg;

		switch (htmlResultCode) {
		case CAPTCHA_NEEDED:
			errorDialogMsg = context.getString(R.string.app_captcha_error);
			break;
		case NO_INFORMATION:
			switch (htmlRequestCode) {
			case MULTIPLE_RESULTS:
				errorDialogMsg = String.format(
						context.getString(R.string.app_info_error),
						station.getNumber());
				break;
			default:
				errorDialogMsg = String.format(
						context.getString(R.string.app_info_error),
						String.format(station.getName() + " (%s)",
								station.getNumber()));
				break;
			}

			break;
		default:
			errorDialogMsg = String.format(
					context.getString(R.string.app_internet_error),
					String.format(station.getName() + " (%s)",
							station.getNumber()));
			break;
		}

		return errorDialogMsg;
	}

	/**
	 * Create a progress dialog if needed (if the instance of this class is
	 * created by the REFRESH - no progress dialog needed)
	 * 
	 * @param msg
	 * @return
	 */
	private ProgressDialog createProgressDialog(Spanned msg) {
		ProgressDialog progressDialog;

		switch (htmlRequestCode) {
		case REFRESH:
			progressDialog = null;
			break;
		default:
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(msg);
			break;
		}

		return progressDialog;
	}

	/**
	 * Update the station in the favourites database
	 * 
	 * @param stationToUpdate
	 *            the new station (fullfilled with all information)
	 */
	private void updateFavouritesStation(Station stationToUpdate) {
		favouriteDatasource.open();
		favouriteDatasource.updateStation(stationToUpdate);
		favouriteDatasource.close();
	}
}
