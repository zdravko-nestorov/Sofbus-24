package bg.znestorov.sofbus24.metro;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.StationTabView;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Creating a HTTP GET request to the URL address -
 * "http://schedules.sofiatraffic.bg/metro/1", to retrieve information about
 * each direction of the Metro.
 * 
 * @author zanio
 * 
 */
public class HtmlRequestMetroDirection {

	/**
	 * Getting the source file of the HTTP request and opening a new Activity
	 * 
	 * @param context
	 *            Activity of the current activity
	 */
	public void getInformation(Activity context) {
		// HTTP Client - created once and using cookies
		final DefaultHttpClient client = new DefaultHttpClient();

		// Making HttpRequest and showing a progress dialog
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.loading_message_metro_schedule));
		LoadingMetroDirections loadingMetroDirections = new LoadingMetroDirections(
				context, progressDialog, client);
		loadingMetroDirections.execute();
	}

	/**
	 * Creating a HTTP GET request by creating the URL address and adding the
	 * User-Agent
	 * 
	 * @return a HTTP GET method with set parameters
	 */
	private HttpGet createMetroDirectionRequest() {
		final HttpGet result = new HttpGet(Constants.METRO_SCHEDULE_URL);
		result.addHeader("User-Agent", Constants.METRO_USER_AGENT);

		return result;
	}

	private void processHtmlResult(Activity context, String htmlSrc) {
		MetroDirectionTransfer mdt = HtmlResultMetroDirection
				.getMetroDirections(htmlSrc);

		if (mdt == null) {
			startErrorActivity(context, Constants.METRO_PARSING_PROBLEM);
		} else {
			startNewActivity(context, mdt);
		}

	}

	private void startNewActivity(final Activity context,
			final MetroDirectionTransfer mdt) {
		Builder dialog = new AlertDialog.Builder(context);
		String[] metroDirections = new String[mdt.getDirectionsListSize()];
		mdt.getDirectionsListNames().toArray(metroDirections);

		ArrayAdapter<CharSequence> itemsAdapter = new ArrayAdapter<CharSequence>(
				context, R.layout.activity_vehicle_direction_choice,
				metroDirections);

		dialog.setTitle(R.string.metro_st_ch_choice)
				.setAdapter(itemsAdapter,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								mdt.setChoice(i);
								Bundle bundle = new Bundle();
								Intent stationIntent = new Intent(context,
										StationTabView.class);
								bundle.putSerializable(
										Constants.KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER,
										mdt);
								stationIntent.putExtras(bundle);
								context.startActivityForResult(stationIntent, i);
							}
						}).show();
	}

	private void startErrorActivity(Activity context, String errorText) {

	}

	/**
	 * Asynchronous Class responsible for executing the HTTP GET request and
	 * retrieving the information from the response
	 * 
	 * @author zanio
	 * 
	 */
	private class LoadingMetroDirections extends AsyncTask<Void, Void, String> {

		private Activity context;
		private ProgressDialog progressDialog;
		private DefaultHttpClient client;

		private HttpGet httpGet;

		public LoadingMetroDirections(Activity context,
				ProgressDialog progressDialog, DefaultHttpClient client) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.client = client;
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
			String htmlResponse = null;

			try {
				httpGet = createMetroDirectionRequest();
				htmlResponse = client.execute(httpGet,
						new BasicResponseHandler());
				htmlResponse = new String(htmlResponse.getBytes("ISO-8859-1"),
						"UTF-8");
			} catch (Exception e) {
				htmlResponse = Constants.METRO_INTERNET_PROBLEM;
			} finally {
				client.getConnectionManager().shutdown();
			}

			return htmlResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}

			if (Constants.METRO_INTERNET_PROBLEM.equals(result)) {
				startErrorActivity(context, result);
			} else {
				processHtmlResult(context, result);
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
}
