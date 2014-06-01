package bg.znestorov.sofbus24.utils.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.Sofbus24;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
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
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * Init the UIL image loader
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void initImageLoader(Context context) {
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

		ImageLoader.getInstance().init(config);
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
	public static void showNoCoordinatesAlertDialog(Activity context) {
		ActivityUtils.showCustomAlertDialog(context,
				android.R.drawable.ic_menu_report_image,
				context.getString(R.string.app_dialog_title_error),
				context.getString(R.string.app_coordinates_error),
				context.getString(R.string.app_button_ok), null, null, null);
	}

	/**
	 * Show no Internet alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void showNoInternetAlertDialog(Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_menu_info_details)
				.setTitle(context.getString(R.string.app_dialog_title_error))
				.setMessage(context.getString(R.string.app_internet_error))
				.setNegativeButton(context.getString(R.string.app_button_ok),
						null).show();
	}

	/**
	 * Show no Info alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 * @param msg
	 *            message to be shown on the alert dialog
	 */
	public static void showNoInfoAlertDialog(Activity context, Spanned msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_menu_info_details)
				.setTitle(context.getString(R.string.app_dialog_title_error))
				.setMessage(msg)
				.setNegativeButton(context.getString(R.string.app_button_ok),
						null).show();
	}

	/**
	 * Show no Internet or Schedule alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void showNoInternetOrInfoAlertDialog(Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_menu_info_details)
				.setTitle(context.getString(R.string.app_dialog_title_error))
				.setMessage(
						context.getString(R.string.app_internet_or_info_error))
				.setNegativeButton(context.getString(R.string.app_button_ok),
						null).show();
	}

	/**
	 * Create custom AlertDialog with custom fields
	 * 
	 * @param context
	 *            the current Activity context
	 * @param icon
	 *            the icon of the AlertDialog
	 * @param title
	 *            the title of the AlertDialog
	 * @param message
	 *            the message content of the AlertDialog
	 * @param positiveButton
	 *            the positive button text of the AlertDialog
	 * @param positiveOnClickListener
	 *            the positive onClickListener of the AlertDialog
	 * @param negativeButton
	 *            the negative button text of the AlertDialog
	 * @param negativeOnClickListener
	 *            the negative onClickListener of the AlertDialog
	 */
	public static void showCustomAlertDialog(Activity context, int icon,
			CharSequence title, CharSequence message,
			CharSequence positiveButton,
			OnClickListener positiveOnClickListener,
			CharSequence negativeButton, OnClickListener negativeOnClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message);

		if (positiveButton != null) {
			builder.setPositiveButton(positiveButton, positiveOnClickListener);
		}

		if (negativeButton != null) {
			builder.setNegativeButton(negativeButton, negativeOnClickListener);
		}

		builder.show();
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
		// Declare that the home screen sections are changed
		Sofbus24.setFavouritesChanged(true);
		Sofbus24.setVBChanged(true);
		Sofbus24.setMetroChanged(isMetroStationChanged(station));

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
				Toast.LENGTH_SHORT).show();
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
		// Declare that the home screen sections are changed
		Sofbus24.setFavouritesChanged(true);
		Sofbus24.setVBChanged(true);
		Sofbus24.setMetroChanged(isMetroStationChanged(station));

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
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Check if the station changed is type METRO1 or METRO2
	 * 
	 * @param station
	 *            the current station
	 * @return if the station changed is metro one
	 */
	private static boolean isMetroStationChanged(Station station) {
		boolean isMetroStationChanged = false;

		if (station != null && station.getType() != null) {
			switch (station.getType()) {
			case METRO1:
			case METRO2:
				isMetroStationChanged = true;
				break;
			default:
				isMetroStationChanged = false;
				break;
			}
		}

		return isMetroStationChanged;
	}

}
