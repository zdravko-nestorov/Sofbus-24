package bg.znestorov.sofbus24.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.R;

public class ResetSettingsDialog extends DialogFragment {

	public interface OnResetSettingsListener {
		public void onResetSettingsClicked();
	}

	private Activity context;
	private int icon;
	private String title;
	private String message;
	private String negativeBtn;
	private String positiveBtn;
	private OnClickListener positiveOnClickListener;

	private OnResetSettingsListener onResetSettingsListener;

	public static ResetSettingsDialog newInstance() {
		return new ResetSettingsDialog();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onResetSettingsListener = (OnResetSettingsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnResetSettingsListener interface...");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		icon = android.R.drawable.ic_menu_info_details;
		title = getString(R.string.app_dialog_title_important);
		message = getString(R.string.pref_reset);
		negativeBtn = getString(R.string.app_button_no);
		positiveBtn = getString(R.string.app_button_yes);

		positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = preferences.edit();
				editor.clear();
				editor.commit();

				// Check if the user wants to restart the application
				((GlobalEntity) context.getApplicationContext())
						.setHasToRestart(true);
				onResetSettingsListener.onResetSettingsClicked();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message)
				.setNegativeButton(negativeBtn, null)
				.setPositiveButton(positiveBtn, positiveOnClickListener);

		return builder.create();
	}
}