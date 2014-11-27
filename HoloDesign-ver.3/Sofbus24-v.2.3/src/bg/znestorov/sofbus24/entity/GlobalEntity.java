package bg.znestorov.sofbus24.entity;

import java.util.HashMap;

import android.app.Application;
import android.content.pm.PackageManager;
import bg.znestorov.sofbus24.main.R;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Global class that extends Application and save state across several
 * Activities and all parts of your application. Each Activity is also a
 * Context, which is information about its execution environment in the broadest
 * sense. Your application also has a context, and Android guarantees that it
 * will exist as a single instance across your application.
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class GlobalEntity extends Application {

	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 * 
	 * A single tracker is usually enough for most purposes. In case you do need
	 * multiple trackers, storing them all in Application object helps ensure
	 * that they are created only once per application instance.
	 */
	public enum TrackerName {
		APP_TRACKER;
	}

	private boolean isPhoneDevice;
	private boolean areServicesAvailable;
	private boolean isGoogleStreetViewAvailable;

	// Indicates if the standard home screen has changed tabs
	private boolean hasToRestart = false;
	private boolean isFavouritesChanged = false;
	private boolean isVbChanged = false;
	private boolean isHomeScreenChanged = false;

	// Indicates if the home activity is changed
	private boolean isHomeActivityChanged = false;

	// Google Analytics
	private HashMap<TrackerName, Tracker> mTrackers;;

	@Override
	public void onCreate() {
		super.onCreate();
		initialize();
	}

	public boolean isPhoneDevice() {
		return isPhoneDevice;
	}

	public void setPhoneDevice(boolean isPhoneDevice) {
		this.isPhoneDevice = isPhoneDevice;
	}

	public boolean isHasToRestart() {
		return hasToRestart;
	}

	public void setHasToRestart(boolean hasToRestart) {
		this.hasToRestart = hasToRestart;
	}

	public boolean isFavouritesChanged() {
		return isFavouritesChanged;
	}

	public void setFavouritesChanged(boolean isFavouritesChanged) {
		this.isFavouritesChanged = isFavouritesChanged;
	}

	public boolean isVbChanged() {
		return isVbChanged;
	}

	public void setVbChanged(boolean isVbChanged) {
		this.isVbChanged = isVbChanged;
	}

	public boolean isHomeScreenChanged() {
		return isHomeScreenChanged;
	}

	public void setHomeScreenChanged(boolean isHomeScreenChanged) {
		this.isHomeScreenChanged = isHomeScreenChanged;
	}

	public boolean areServicesAvailable() {
		return areServicesAvailable;
	}

	public void setServicesAvailable(boolean areServicesAvailable) {
		this.areServicesAvailable = areServicesAvailable;
	}

	public boolean isGoogleStreetViewAvailable() {
		return isGoogleStreetViewAvailable;
	}

	public void setGoogleStreetViewAvailable(boolean isGoogleStreetViewAvailable) {
		this.isGoogleStreetViewAvailable = isGoogleStreetViewAvailable;
	}

	public boolean isHomeActivityChanged() {
		return isHomeActivityChanged;
	}

	public void setHomeActivityChanged(boolean isHomeActivityChanged) {
		this.isHomeActivityChanged = isHomeActivityChanged;
	}

	public synchronized Tracker getTracker(TrackerName trackerId) {

		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

			// Check what tracker to be created. In case we need multiple
			// trackers, create different ones using the appropriate
			// configuration file
			switch (trackerId) {
			default:
				Tracker tracker = analytics.newTracker(R.xml.app_tracker);
				mTrackers.put(trackerId, tracker);
				break;
			}
		}

		return mTrackers.get(trackerId);
	}

	private void initialize() {
		isPhoneDevice = getResources().getBoolean(R.bool.isPhone);

		try {
			getPackageManager().getApplicationInfo("com.google.android.gms", 0);
			areServicesAvailable = true;
		} catch (PackageManager.NameNotFoundException e) {
			areServicesAvailable = false;
		}

		try {
			getPackageManager().getApplicationInfo("com.google.android.street",
					0);
			isGoogleStreetViewAvailable = true;
		} catch (PackageManager.NameNotFoundException e) {
			isGoogleStreetViewAvailable = false;
		}

		mTrackers = new HashMap<TrackerName, Tracker>();
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tisPhoneDevice: " + isPhoneDevice
				+ "\n\tareServicesAvailable: " + areServicesAvailable
				+ "\n\tisGoogleStreetViewAvailable: "
				+ isGoogleStreetViewAvailable + "\n\thasToRestart: "
				+ hasToRestart + "\n\tisFavouritesChanged: "
				+ isFavouritesChanged + "\n\tisVbChanged: " + isVbChanged
				+ "\n\tisHomeScreenChanged: " + isHomeScreenChanged
				+ "\n\tisHomeActivityChanged: " + isHomeActivityChanged + "\n}";
	}

}