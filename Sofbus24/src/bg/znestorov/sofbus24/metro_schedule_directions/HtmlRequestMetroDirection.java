package bg.znestorov.sofbus24.metro_schedule_directions;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
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
import bg.znestorov.sofbus24.main.MetroStationTabView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.VirtualBoardsStationChoice;
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
	 *            Activity context of the current activity
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
	 * Create a MetroDirectionTransfer object by parsing the HTML response XML
	 * 
	 * @param context
	 *            Activity context of the current activity
	 * @param scheduleXml
	 *            the HTML response of the request (XML file)
	 */
	private void processHtmlResult(Activity context, String scheduleXml) {
		MetroDirectionTransfer mdt = HtmlResultMetroDirection
				.getMetroDirections(scheduleXml);

		startNewActivity(context, mdt);
	}

	/**
	 * Creating an AlertDialog, indicating the directions of the Metro. After
	 * choosing one of them redirects to a new activity, showing all the
	 * stations in this direction
	 * 
	 * @param context
	 *            Activity context of the current activity
	 * @param mdt
	 *            the MetroDirection transfer object, created from the XML file
	 */
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
										MetroStationTabView.class);
								bundle.putSerializable(
										Constants.KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER,
										mdt);
								stationIntent.putExtras(bundle);
								context.startActivityForResult(stationIntent, i);
							}
						}).show();
	}

	/**
	 * Starting an error activity, informing that there is a problem with the
	 * Internet connection
	 * 
	 * @param context
	 *            Activity context of the current activity
	 * @param errorText
	 *            the Internet error
	 */
	private void startErrorActivity(Activity context, String errorText) {
		Intent intent = new Intent(context, VirtualBoardsStationChoice.class);
		intent.putExtra(Constants.KEYWORD_HTML_RESULT, errorText);
		context.startActivity(intent);
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
			HttpResponse htmlResponse = null;
			String scheduleXml = null;

			try {
				htmlResponse = client.execute(new HttpGet(
						Constants.METRO_SCHEDULE_URL));
				StatusLine statusLine = htmlResponse.getStatusLine();

				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					htmlResponse.getEntity().writeTo(out);
					out.close();
					scheduleXml = out.toString();
				} else {
					htmlResponse.getEntity().getContent().close();
					scheduleXml = Constants.METRO_INTERNET_PROBLEM;
				}
			} catch (Exception e) {
				scheduleXml = Constants.METRO_INTERNET_PROBLEM;
			} finally {
				client.getConnectionManager().shutdown();
			}

			return scheduleXml;
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
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}
		}
	}
}
