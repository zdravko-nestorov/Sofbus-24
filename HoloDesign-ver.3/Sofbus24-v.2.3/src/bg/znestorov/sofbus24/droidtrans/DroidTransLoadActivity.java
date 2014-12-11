package bg.znestorov.sofbus24.droidtrans;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.DroidTrans;
import bg.znestorov.sofbus24.main.DroidTransDialog;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * AsyncTask used for loading the DroidTrans activity
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class DroidTransLoadActivity extends AsyncTask<Void, Void, Void> {

	private Activity context;
	private GlobalEntity globalContext;
	private ProgressDialog progressDialog;

	public DroidTransLoadActivity(Activity context) {
		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();
		this.progressDialog = new ProgressDialog(context);
	}

	public void onPreExecute() {
		super.onPreExecute();

		createLoadingView();
	}

	public Void doInBackground(Void... params) {
		return null;
	}

	public void onPostExecute(Void result) {
		super.onPostExecute(result);

		startDroidTrans();
		dismissLoadingView();
	}

	/**
	 * Start the DroidTrans activity
	 */
	private void startDroidTrans() {
		Intent droidTransIntent;
		if (globalContext.isPhoneDevice()) {
			droidTransIntent = new Intent(context, DroidTrans.class);
		} else {
			droidTransIntent = new Intent(context, DroidTransDialog.class);
		}

		context.startActivity(droidTransIntent);
	}

	/**
	 * Create the loading view and lock the screen
	 */
	private void createLoadingView() {
		ActivityUtils.lockScreenOrientation(context);

		progressDialog.setMessage(context
				.getString(R.string.droid_trans_load_data));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/**
	 * Dismiss the loading view and unlock the screen
	 */
	private void dismissLoadingView() {
		if (progressDialog != null) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
			}
		}

		ActivityUtils.unlockScreenOrientation(context);
	}

}