package bg.znestorov.sofbus24.gps_map;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

public class MyLocation {

	// LogCat TAG for console messages
	private final static String TAG = "MyLocation";

	// Location variables
	LocationManager lm;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	public boolean getLocation(Context context) {
		// Creating LocationManager
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// Exceptions will be thrown if provider is not enabled
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			// For testing purpose
			Log.d(TAG, "GPS-ът е изключен.");
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			// For testing purpose
			Log.d(TAG, "WiFi-ят е изключен.");
		}

		// Check if all available providers are disabled
		if (!gps_enabled && !network_enabled) {
			return false;
		} else {
			return true;
		}
	}
}