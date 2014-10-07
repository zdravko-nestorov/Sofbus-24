package bg.znestorov.sofbus24.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.main.R;

/**
 * Dialog alerting the user about application restart
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RestartApplicationDialog extends DialogFragment {

	private Activity context;
	private int icon;
	private String title;
	private Spanned message;
	private String negativeBtn;
	private String positiveBtn;
	private OnClickListener negativeOnClickListener;
	private OnClickListener positiveOnClickListener;

	private boolean isResetted;

	public static final String BUNDLE_IS_RESETTED = "IS RESETTED";

	public static RestartApplicationDialog newInstance(boolean isResetted) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(BUNDLE_IS_RESETTED, isResetted);

		RestartApplicationDialog restartApplicationDialog = new RestartApplicationDialog();
		restartApplicationDialog.setArguments(bundle);

		return restartApplicationDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		icon = android.R.drawable.ic_menu_info_details;
		title = getString(R.string.app_dialog_title_important);
		message = Html.fromHtml(getString(R.string.pref_restart_app));
		negativeBtn = getString(R.string.app_button_no);
		positiveBtn = getString(R.string.app_button_yes);
		isResetted = getArguments().getBoolean(BUNDLE_IS_RESETTED);

		positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.finish();
			}
		};

		negativeOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!isResetted) {
					((GlobalEntity) context.getApplicationContext())
							.setHasToRestart(false);
					context.finish();
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message)
				.setNegativeButton(negativeBtn, negativeOnClickListener)
				.setPositiveButton(positiveBtn, positiveOnClickListener);

		return builder.create();
	}
}