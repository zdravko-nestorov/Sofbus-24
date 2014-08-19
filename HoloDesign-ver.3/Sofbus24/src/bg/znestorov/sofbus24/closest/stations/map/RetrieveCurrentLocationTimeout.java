package bg.znestorov.sofbus24.closest.stations.map;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;

/**
 * Runnable class used to cancel the AsyncTask after 10 seconds (if no location
 * is found)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class RetrieveCurrentLocationTimeout implements Runnable {

	private AsyncTask<Void, Void, Void> retrieveCurrentLocation;

	public RetrieveCurrentLocationTimeout(
			AsyncTask<Void, Void, Void> retrieveCurrentLocation) {
		this.retrieveCurrentLocation = retrieveCurrentLocation;
	}

	@Override
	public void run() {
		mHandler.postDelayed(runnable, 6000);
	}

	Handler mHandler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (retrieveCurrentLocation.getStatus() == Status.RUNNING
					|| retrieveCurrentLocation.getStatus() == Status.PENDING) {
				retrieveCurrentLocation.cancel(true);
			}
		}
	};
}