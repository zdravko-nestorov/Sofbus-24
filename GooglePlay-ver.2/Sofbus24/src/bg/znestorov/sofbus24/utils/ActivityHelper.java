package bg.znestorov.sofbus24.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import bg.znestorov.sofbus24.main.R;

public class ActivityHelper {

	/**
	 * Dialog alerting that no location provider is enabled
	 * 
	 * @param context
	 *            Current activity context
	 */
	public static void createNoLocationAlert(final Context context) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.ss_gps_map_msg_title)
				.setMessage(R.string.ss_gps_map_msg_body)
				.setCancelable(false)
				.setPositiveButton(context.getString(R.string.button_title_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int i) {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								context.startActivity(intent);
							}

						})
				.setNegativeButton(
						context.getString(R.string.button_title_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int i) {
							}

						}).show();
	}

	/**
	 * Check if the OK button on the VirtualBoards alert dialog should be active
	 * or not
	 * 
	 * @param inputText
	 *            the text in the input field
	 * @param dialog
	 *            the alert dialog
	 */
	public static void setOkButtonActivity(String inputText, AlertDialog dialog) {
		if ((inputText.length() == 0)
				|| (inputText.length() <= 2 && !inputText.equals(inputText
						.replaceAll("\\D+", "")))) {
			dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
		} else {
			dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
		}
	}
}
