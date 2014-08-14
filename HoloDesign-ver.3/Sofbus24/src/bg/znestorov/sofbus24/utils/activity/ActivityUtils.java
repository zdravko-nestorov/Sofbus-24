package bg.znestorov.sofbus24.utils.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.Sofbus24;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * The class contains only a static methods, helping with Activity interactions
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ActivityUtils {

	/**
	 * Request the focus and show a keyboard on EditText field
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param editText
	 *            the EditText field
	 */
	public static void showKeyboard(Context context, EditText editText) {
		// Focus the field
		editText.requestFocus();

		// Show soft keyboard for the user to enter the value
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * Request the focus and hide the keyboard on EditText field
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param editText
	 *            the EditText field
	 */
	public static void hideKeyboard(Context context, EditText editText) {
		// Hide soft keyboard
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (editText != null) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}

	/**
	 * Request the focus and hide the keyboard
	 * 
	 * @param context
	 *            Context of the current activity
	 */
	public static void hideKeyboard(Activity context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Activity.INPUT_METHOD_SERVICE);

		View view = context.getCurrentFocus();
		if (view != null) {
			inputMethodManager
					.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * Init the UIL image loader
	 * 
	 * @param context
	 *            the current activity context
	 * @return current ImageLoaderConfiguration
	 */
	public static ImageLoaderConfiguration initImageLoader(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
				.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 1)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.imageDownloader(new BaseImageDownloader(context))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.build();

		return config;
	}

	/**
	 * Create the display image options according via the Universal Image Loader
	 * options
	 * 
	 * @return the configured display image options
	 */
	public static DisplayImageOptions displayImageOptions() {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.delayBeforeLoading(100).cacheInMemory(true).cacheOnDisc(true)
				.build();

		return displayImageOptions;
	}

	/**
	 * Show no station coordinates alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void showNoCoordinatesToast(Activity context) {
		Toast.makeText(context,
				context.getString(R.string.app_coordinates_error),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Show no Internet alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void showNoInternetToast(Activity context) {
		Toast.makeText(context, context.getString(R.string.app_internet_error),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Show no Info alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 * @param msg
	 *            message to be shown on the alert dialog
	 */
	public static void showNoInfoAlertToast(Activity context, Spanned msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * Show no Internet or Schedule alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void showNoInternetOrInfoToast(Activity context) {
		Toast.makeText(context,
				context.getString(R.string.app_internet_or_info_error),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Close the application
	 * 
	 * @param context
	 *            the current Activity context
	 */
	public static void closeApplication(Activity context) {
		context.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * Restart the application (using PendingIntent to setup launching the
	 * activity in future and than close the application)
	 * 
	 * @param context
	 *            the current Activity context
	 */
	public static void restartApplication(Activity context) {
		// Set the application to be started again after 100 ms
		Intent mStartActivity = new Intent(context, Sofbus24.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(context,
				mPendingIntentId, mStartActivity,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
				mPendingIntent);

		// Close the application
		context.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * Check if the stations already exists in the favorites database and
	 * add/remove it to/from there. If a favorites imageView is given as a
	 * parameter, change it icon accordingly.
	 * 
	 * @param context
	 *            the current Activity context
	 * @param favouritesDatasource
	 *            the FavouritesDatasource
	 * @param station
	 *            the current station
	 * @param favouritesImageView
	 *            the imageView indicating the station status (if null - no
	 *            action is taken for it)
	 */
	public static void toggleFavouritesStation(Activity context,
			FavouritesDataSource favouritesDatasource, Station station,
			ImageView favouritesImageView) {
		// Check if the station is added to the favorites database
		favouritesDatasource.open();
		boolean isStationFavoruite = favouritesDatasource.getStation(station) != null;
		favouritesDatasource.close();

		if (!isStationFavoruite) {
			addToFavourites(context, favouritesDatasource, station);

			if (favouritesImageView != null) {
				favouritesImageView.setImageResource(R.drawable.ic_fav_full);
			}
		} else {
			removeFromFavourites(context, favouritesDatasource, station);

			if (favouritesImageView != null) {
				favouritesImageView.setImageResource(R.drawable.ic_fav_empty);
			}
		}
	}

	/**
	 * Add the station to the favorites database and indicates that the home
	 * screen favorites section is changed.
	 * 
	 * @param context
	 *            the current Activity context
	 * @param favouritesDatasource
	 *            the FavouritesDatasource
	 * @param station
	 *            the current station
	 */
	public static void addToFavourites(Activity context,
			FavouritesDataSource favouritesDatasource, Station station) {
		// Get the application context
		GlobalEntity globalContext = (GlobalEntity) context
				.getApplicationContext();

		// Declare that the home screen sections are changed
		globalContext.setFavouritesChanged(true);
		globalContext.setVbChanged(isVBStationChanged(station));

		// Add the station to the favorites section
		favouritesDatasource.open();
		favouritesDatasource.createStation(station);
		favouritesDatasource.close();

		// Show a toast message to inform the user that the station is added to
		// the favorites section
		Toast.makeText(
				context,
				Html.fromHtml(String.format(
						context.getString(R.string.app_toast_add_favourites),
						station.getName(), station.getNumber())),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Delete the station from the favorites database and indicates that the
	 * home screen favorites section is changed.
	 * 
	 * @param context
	 *            the current Activity context
	 * @param favouritesDatasource
	 *            the FavouritesDatasource
	 * @param station
	 *            the current station
	 */
	public static void removeFromFavourites(Activity context,
			FavouritesDataSource favouritesDatasource, Station station) {
		// Get the application context
		GlobalEntity globalContext = (GlobalEntity) context
				.getApplicationContext();

		// Declare that the home screen sections are changed
		globalContext.setFavouritesChanged(true);
		globalContext.setVbChanged(isVBStationChanged(station));

		// Delete the station from the favorites section
		favouritesDatasource.open();
		favouritesDatasource.deleteStation(station);
		favouritesDatasource.close();

		// Show a toast message to inform the user that the station is deleted
		// from the favorites section
		Toast.makeText(
				context,
				Html.fromHtml(String.format(
						context.getString(R.string.app_toast_remove_favourites),
						station.getName(), station.getNumber())),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Check if the station changed is not METRO
	 * 
	 * @param station
	 *            the current station
	 * @return if the station changed is not metro one
	 */
	private static boolean isVBStationChanged(Station station) {
		boolean isVBStationChanged = false;

		if (station != null && station.getType() != null) {
			switch (station.getType()) {
			case METRO1:
			case METRO2:
				isVBStationChanged = false;
				break;
			default:
				isVBStationChanged = true;
				break;
			}
		}

		return isVBStationChanged;
	}

	/**
	 * Create an input filter to limit characters in an EditText
	 * 
	 * @return an input filter
	 */
	public static InputFilter createInputFilter() {
		InputFilter inputFilter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {

				// InputFilters are a little complicated in Android versions
				// that display dictionary suggestions. You sometimes get a
				// SpannableStringBuilder, sometimes a plain String in the
				// source parameter
				if (source instanceof SpannableStringBuilder) {
					SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
					for (int i = end - 1; i >= start; i--) {
						char currentChar = source.charAt(i);
						if (!Character.isLetterOrDigit(currentChar)
								&& !Character.isSpaceChar(currentChar)) {
							sourceAsSpannableBuilder.delete(i, i + 1);
						}
					}
					return source;
				} else {
					StringBuilder filteredStringBuilder = new StringBuilder();
					for (int i = start; i < end; i++) {
						char currentChar = source.charAt(i);
						if (Character.isLetterOrDigit(currentChar)
								|| Character.isSpaceChar(currentChar)) {
							filteredStringBuilder.append(currentChar);
						}
					}
					return filteredStringBuilder.toString();
				}
			}
		};

		return inputFilter;
	}

	/**
	 * Lock the device in the current device orientation
	 * 
	 * @param context
	 *            the current Activity context
	 */
	public static void lockScreenOrientation(Activity context) {
		int currentOrientation = context.getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
	}

	/**
	 * Unlock the orientation of the device
	 * 
	 * @param context
	 *            the current Activity context
	 */
	public static void unlockScreenOrientation(Activity context) {
		context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	/**
	 * Check if there is an Internet connection or not
	 * 
	 * @param context
	 *            the current Activity context
	 * @return if there is an Internet connection
	 */
	public static boolean haveNetworkConnection(Activity context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		boolean haveConnected = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();

		for (NetworkInfo networkIngo : allNetworkInfo) {
			if ("WIFI".equalsIgnoreCase(networkIngo.getTypeName())) {
				if (networkIngo.isConnected()) {
					haveConnectedWifi = true;
				}
			}

			if ("MOBILE".equalsIgnoreCase(networkIngo.getTypeName())) {
				if (networkIngo.isConnected()) {
					haveConnectedMobile = true;
				}
			}
		}

		if (!haveConnectedWifi && !haveConnectedMobile) {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			haveConnected = networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected();
		}

		return haveConnectedWifi || haveConnectedMobile || haveConnected;
	}
}
