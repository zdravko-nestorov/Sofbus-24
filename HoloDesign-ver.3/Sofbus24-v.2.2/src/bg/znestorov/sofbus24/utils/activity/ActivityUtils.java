package bg.znestorov.sofbus24.utils.activity;

import java.io.File;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.LanguageChange;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
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
@SuppressLint("InlinedApi")
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
	@SuppressLint("NewApi")
	public static ImageLoaderConfiguration initImageLoader(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);

		ImageLoaderConfiguration config;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			config = new ImageLoaderConfiguration.Builder(context)
					.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
					.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
					.threadPoolSize(3)
					.threadPriority(Thread.NORM_PRIORITY - 1)
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(
							new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					.memoryCacheSize(2 * 1024 * 1024)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.discCacheSize(50 * 1024 * 1024)
					.discCacheFileCount(100)
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
					.imageDownloader(new BaseImageDownloader(context))
					.defaultDisplayImageOptions(
							DisplayImageOptions.createSimple()).build();
		} else {
			config = new ImageLoaderConfiguration.Builder(context)
					.threadPoolSize(3)
					.threadPriority(Thread.NORM_PRIORITY - 1)
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(
							new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					.memoryCacheSize(2 * 1024 * 1024)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.discCacheSize(50 * 1024 * 1024)
					.discCacheFileCount(100)
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
					.imageDownloader(new BaseImageDownloader(context))
					.defaultDisplayImageOptions(
							DisplayImageOptions.createSimple()).build();
		}

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
		try {
			// Check if the context is given
			if (context != null) {
				// Fetch the packagemanager so we can get the default launch
				// activity (we can replace this intent with any other activity
				// if you want)
				PackageManager pm = context.getPackageManager();

				// Check if we got the PackageManager
				if (pm != null) {

					// Create the intent with the default start activity for
					// your application
					Intent mStartActivity = pm
							.getLaunchIntentForPackage(context.getPackageName());
					if (mStartActivity != null) {
						mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// Create a pending intent so the application is
						// restarted after System.exit(0) was called. We use an
						// AlarmManager to call this intent in 100ms
						int mPendingIntentId = 223344;
						PendingIntent mPendingIntent = PendingIntent
								.getActivity(context, mPendingIntentId,
										mStartActivity,
										PendingIntent.FLAG_CANCEL_CURRENT);
						AlarmManager mgr = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
						mgr.set(AlarmManager.RTC,
								System.currentTimeMillis() + 100,
								mPendingIntent);

						// Kill the application
						closeApplication(context);
					}
				}
			}
		} catch (Exception ex) {
		}
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
			FavouritesDataSource favouritesDatasource, StationEntity station,
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
			FavouritesDataSource favouritesDatasource, StationEntity station) {
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
			FavouritesDataSource favouritesDatasource, StationEntity station) {
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
	 * Update the station info in the favourites DB
	 * 
	 * @param context
	 *            the current Activity context
	 * @param station
	 *            the current station
	 */
	public static void updateFavouritesStationInfo(Activity context,
			StationEntity station) {
		FavouritesDataSource favouritesDatasource = new FavouritesDataSource(
				context);
		favouritesDatasource.open();
		favouritesDatasource.updateStationInfo(station);
		favouritesDatasource.close();
	}

	/**
	 * Check if the station changed is not METRO
	 * 
	 * @param station
	 *            the current station
	 * @return if the station changed is not metro one
	 */
	private static boolean isVBStationChanged(StationEntity station) {
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

	/**
	 * Convert dp to pixels
	 * 
	 * @param context
	 *            the current activity context
	 * @param dp
	 *            the dip size
	 * @return the pixel size
	 */
	public static int spToPx(Activity context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	/**
	 * Show activity as a Dialog window (to show activity as dialog and dim the
	 * background, you need to declare <b>android:theme="@style/PopupTheme"</b>
	 * on for the chosen activity on the manifest)
	 * 
	 * @param activity
	 *            the activity
	 * @param isInvisible
	 *            mark if the activity will be invisible or not
	 */
	public static void showAsPopup(Activity activity, boolean isInvisible) {
		activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		activity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		int pixelsHeight = metrics.heightPixels;
		int pixelsWidth = metrics.widthPixels;
		if (!isInvisible) {
			if (pixelsHeight > pixelsWidth) {
				// Portrait orientation
				pixelsWidth = (int) (pixelsHeight * 0.58);
				pixelsHeight = (int) (pixelsHeight * 0.86);
			} else {
				// Landscape orientation
				pixelsWidth = (int) (pixelsWidth * 0.58);
				pixelsHeight = (int) (pixelsHeight * 0.86);
			}
		} else {
			pixelsWidth = 0;
			pixelsHeight = 0;
		}

		LayoutParams params = activity.getWindow().getAttributes();
		params.height = pixelsHeight;
		params.width = pixelsWidth;
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;

		activity.getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) params);
	}

	/**
	 * Force the tabs not to be embaded in the ActionBar
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void forceTabs(SherlockFragmentActivity context) {
		try {
			final ActionBar actionBar = context.getSupportActionBar();
			final Method setHasEmbeddedTabsMethod = actionBar.getClass()
					.getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
			setHasEmbeddedTabsMethod.setAccessible(true);
			setHasEmbeddedTabsMethod.invoke(actionBar, false);
		} catch (final Exception e) {
			// This error is safe to ignore, standard tabs will appear.
		}
	}

	/**
	 * Show a GooglePlayErrorDialog
	 * 
	 * @param fragment
	 *            the current fragment
	 */
	public static void showGooglePlayServicesErrorDialog(Fragment fragment) {
		GooglePlayServicesErrorDialog googlePlayServicesErrorDialog = new GooglePlayServicesErrorDialog();
		googlePlayServicesErrorDialog.show(fragment.getFragmentManager(),
				"GooglePlayServicesErrorDialog");
	}

	/**
	 * Show a GooglePlayErrorDialog
	 * 
	 * @param fragmentActivity
	 *            the current activity
	 */
	public static void showGooglePlayServicesErrorDialog(
			FragmentActivity fragmentActivity) {
		GooglePlayServicesErrorDialog googlePlayServicesErrorDialog = new GooglePlayServicesErrorDialog();
		googlePlayServicesErrorDialog.show(
				fragmentActivity.getSupportFragmentManager(),
				"GooglePlayServicesErrorDialog");
	}

	/**
	 * Show a GoogleStreetViewErrorDialog
	 * 
	 * @param fragment
	 *            the current fragment
	 */
	public static void showGoogleStreetViewErrorDialog(Fragment fragment) {
		GoogleStreetViewErrorDialog googleStreetViewErrorDialog = new GoogleStreetViewErrorDialog();
		googleStreetViewErrorDialog.show(fragment.getFragmentManager(),
				"GoogleStreetViewErrorDialog");
	}

	/**
	 * Show a GoogleStreetViewErrorDialog
	 * 
	 * @param fragmentActivity
	 *            the current activity
	 */
	public static void showGoogleStreetViewErrorDialog(
			FragmentActivity fragmentActivity) {
		GoogleStreetViewErrorDialog googleStreetViewErrorDialog = new GoogleStreetViewErrorDialog();
		googleStreetViewErrorDialog.show(
				fragmentActivity.getSupportFragmentManager(),
				"GooglePlayServicesErrorDialog");
	}

	/**
	 * In case of API level 8-14, there is a problem with the background of the
	 * AlertDialogs. If we want a LIGHT, we should inverse the current one. The
	 * {@value isInverseBackgroundForced} is used to determine which theme
	 * should be used:
	 * <ul>
	 * <li>TRUE - light background</li>
	 * <li>FALSE - dark background</li>
	 * </ul>
	 * 
	 * @param context
	 *            the current activirty context
	 * @param dialogBuilder
	 *            the dialog builder
	 */
	public static void setInverseBackgroundForced(Activity context,
			AlertDialog.Builder dialogBuilder) {
		int sdkVersion = Build.VERSION.SDK_INT;
		boolean isInverseBackgroundForced = true;

		if (sdkVersion < Build.VERSION_CODES.HONEYCOMB) {
			dialogBuilder.setInverseBackgroundForced(isInverseBackgroundForced);
		}
	}

	/**
	 * Workaround to set the subtitle of the first started tab (in android
	 * version lower than HONEYCOMB its in different language)
	 * 
	 * @param context
	 *            the current activity context
	 * @param actionBar
	 *            the action bar
	 */
	public static void setHomeScreenActionBarSubtitle(Activity context,
			ActionBar actionBar, String subtitle, String subtitlePreHoneycomb) {
		int sdkVersion = Build.VERSION.SDK_INT;

		if (sdkVersion < Build.VERSION_CODES.HONEYCOMB
				&& "bg".equals(LanguageChange.getUserLocale(context))) {
			actionBar.setSubtitle(subtitlePreHoneycomb);
		} else {
			actionBar.setSubtitle(subtitle);
		}
	}
}