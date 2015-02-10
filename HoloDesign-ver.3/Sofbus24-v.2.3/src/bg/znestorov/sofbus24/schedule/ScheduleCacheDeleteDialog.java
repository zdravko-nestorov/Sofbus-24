package bg.znestorov.sofbus24.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import bg.znestorov.sofbus24.databases.ScheduleDataSource;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Dialog alerting the user that the schedule cache will be deleted
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleCacheDeleteDialog extends DialogFragment {

	private Activity context;
	private ScheduleDataSource scheduleDatasource;

	private int icon;
	private String title;
	private Spanned message;
	private String negativeBtn;
	private String positiveBtn;
	private OnClickListener positiveOnClickListener;

	private VehicleTypeEnum scheduleCacheType;
	private String scheduleCacheNumber;

	private static final String BUNDLE_SCHEDULE_CACHE_TYPE = "SCHEDULE CACHE TYPE";
	private static final String BUNDLE_SCHEDULE_CACHE_NUMBER = "SCHEDULE CACHE NUMBER";

	public static ScheduleCacheDeleteDialog newInstance(
			VehicleTypeEnum scheduleCacheType, String scheduleCacheNumber) {

		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_SCHEDULE_CACHE_TYPE,
				String.valueOf(scheduleCacheType));
		bundle.putString(BUNDLE_SCHEDULE_CACHE_NUMBER, scheduleCacheNumber);

		ScheduleCacheDeleteDialog scheduleCacheDeleteDialog = new ScheduleCacheDeleteDialog();
		scheduleCacheDeleteDialog.setArguments(bundle);

		return scheduleCacheDeleteDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		context = getActivity();
		scheduleDatasource = new ScheduleDataSource(context);
		icon = R.drawable.ic_menu_delete;
		title = getString(R.string.app_dialog_title_important);
		negativeBtn = getString(R.string.app_button_no);
		positiveBtn = getString(R.string.app_button_yes);

		scheduleCacheType = VehicleTypeEnum.valueOf(getArguments().getString(
				BUNDLE_SCHEDULE_CACHE_TYPE));
		scheduleCacheNumber = getArguments().getString(
				BUNDLE_SCHEDULE_CACHE_NUMBER);

		switch (scheduleCacheType) {
		case BTT:
		case METRO:
			message = Html
					.fromHtml(getString(R.string.pt_menu_clear_all_schedule_cache));
			break;
		default:
			message = Html
					.fromHtml(getString(R.string.pt_menu_clear_schedule_cache,
							scheduleCacheNumber));
			break;
		}

		positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String deleteMessage;

				// Open the schedule cache database
				scheduleDatasource.open();

				// Delete the appropriate schedule from the DB
				switch (scheduleCacheType) {
				case BTT:
					scheduleDatasource.deleteAllScheduleCache();
					deleteMessage = getString(R.string.pt_menu_clear_all_schedule_cache_toast);

					break;
				default:
					scheduleDatasource
							.deleteScheduleCache(ScheduleCachePreferences
									.getNumberOfDays(context));
					deleteMessage = getString(R.string.pt_menu_clear_schedule_cache_toast);

					break;
				}

				// Close the schedule cache database
				scheduleDatasource.close();

				// Show a long toast with the result of the deletion process
				ActivityUtils.showLongToast(context, deleteMessage, 3000, 1000);
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message)
				.setNegativeButton(negativeBtn, null)
				.setPositiveButton(positiveBtn, positiveOnClickListener);

		return builder.create();
	}
}