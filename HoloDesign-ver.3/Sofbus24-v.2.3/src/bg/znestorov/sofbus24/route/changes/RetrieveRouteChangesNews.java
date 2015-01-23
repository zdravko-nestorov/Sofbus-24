package bg.znestorov.sofbus24.route.changes;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import bg.znestorov.sofbus24.entity.RouteChangesEntity;
import bg.znestorov.sofbus24.main.RouteChangesNews;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Retrieving the information about a route change news (starting the
 * RouteChangesRoute)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveRouteChangesNews extends AsyncTask<Void, Void, String> {

	private Activity context;
	private ProgressDialog progressDialog;
	private RouteChangesEntity routeChanges;

	private ResponseHandler<String> responseHandler;

	private static final String HTML_PRINT_ELEMENT_1 = "<a href=\"#\" onclick=\"window.print\\(\\);\" style=\"margin-right:5px;\" >отпечатай<\\/a>";
	private static final String HTML_PRINT_ELEMENT_2 = "<a href=\"#\" onclick=\"window.print\\(\\);\" style=\"margin-right:5px; display: none;\" >отпечатай<\\/a>";
	private static final String HTML_BACK_ELEMENT = "<a href=\"#\" onclick=\"window.history.back\\(\\);\">&lsaquo;&lsaquo;&nbsp;назад<\\/a>";
	private static final String HTML_IMAGE_ELEMENT_1 = "<\\/a><br.*?\\/><a href=\"..\\/uploaded_images\\/newsimg\\/.*?.jpg\" target=\"_blank\"><div align=\"center\">Вижте в по-голям размер<\\/div><\\/a>";
	private static final String HTML_IMAGE_ELEMENT_2 = "<br.*?\\/><a href=\"..\\/uploaded_images\\/newsimg\\/.*?.jpg\" target=\"_blank\">";
	private static final String HTML_IMAGE_ELEMENT_PATH_OLD = "\\.\\.\\/";
	private static final String HTML_IMAGE_ELEMENT_PATH_NEW = "http://forum.sofiatraffic.bg/";

	public RetrieveRouteChangesNews(Activity context,
			ProgressDialog progressDialog, RouteChangesEntity routeChanges) {

		this.context = context;
		this.progressDialog = progressDialog;
		this.routeChanges = routeChanges;

		this.responseHandler = ActivityUtils.getUtfResponseHandler();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();
	}

	@Override
	protected String doInBackground(Void... arg0) {

		String htmlResult;
		DefaultHttpClient routeChangesNewsHttpClient = new DefaultHttpClient();

		try {
			HttpGet routeChangesHttpRequest = createRouteChangesNewsRequest();
			htmlResult = routeChangesNewsHttpClient.execute(
					routeChangesHttpRequest, responseHandler);
		} catch (Exception e) {
			htmlResult = null;
		} finally {
			routeChangesNewsHttpClient.getConnectionManager().shutdown();
		}

		if (!progressDialog.isShowing()) {
			cancel(true);
		}

		return htmlResult;
	}

	@Override
	protected void onPostExecute(String htmlResult) {
		super.onPostExecute(htmlResult);

		if (htmlResult == null) {
			ActivityUtils.showNoInternetToast(context);
		} else {
			htmlResult = htmlResult.replaceAll(HTML_BACK_ELEMENT, "");
			htmlResult = htmlResult.replaceAll(HTML_PRINT_ELEMENT_1, "");
			htmlResult = htmlResult.replaceAll(HTML_PRINT_ELEMENT_2, "");
			htmlResult = htmlResult.replaceAll(HTML_IMAGE_ELEMENT_1, "");
			htmlResult = htmlResult.replaceAll(HTML_IMAGE_ELEMENT_2, "");
			htmlResult = htmlResult.replaceAll(HTML_IMAGE_ELEMENT_PATH_OLD,
					HTML_IMAGE_ELEMENT_PATH_NEW);

			// Assign the HTML response to the RouteChanges entity
			routeChanges.setHtmlResponse(htmlResult);

			// Start RouteChangesNews activity
			Bundle bundle = new Bundle();
			bundle.putSerializable(RouteChangesNews.BUNDLE_ROUTE_CHANGES_NEWS,
					routeChanges);

			Intent routeChangesNewsIntent = new Intent(context,
					RouteChangesNews.class);
			routeChangesNewsIntent.putExtras(bundle);
			context.startActivity(routeChangesNewsIntent);
		}

		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		dismissLoadingView();
	}

	/**
	 * Create HttpGet request to retrieve the route changes news
	 * 
	 * @return a HttpGet request for the route changes news
	 * @throws URISyntaxException
	 */
	private HttpGet createRouteChangesNewsRequest() throws URISyntaxException {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(routeChanges.getUrl()));

		return httpGet;
	}

	/**
	 * Create the loading view and lock the screen
	 */
	private void createLoadingView() {
		ActivityUtils.lockScreenOrientation(context);

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

	/**
	 * Dismiss the loading view and unlock the screen
	 */
	private void dismissLoadingView() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		ActivityUtils.unlockScreenOrientation(context);
	}

}