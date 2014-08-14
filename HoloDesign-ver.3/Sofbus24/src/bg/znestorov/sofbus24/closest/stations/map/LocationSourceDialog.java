package bg.znestorov.sofbus24.closest.stations.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import bg.znestorov.sofbus24.main.R;

public class LocationSourceDialog extends DialogFragment {

	private Activity context;
	private int icon;
	private String title;
	private String message;
	private String negativeBtn;
	private String positiveBtn;
	private OnClickListener positiveOnClickListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		icon = android.R.drawable.ic_menu_mylocation;
		title = getString(R.string.app_dialog_title_error);
		message = getString(R.string.app_location_error);
		negativeBtn = getString(R.string.app_button_no);
		positiveBtn = getString(R.string.app_button_yes);

		positiveOnClickListener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				Intent intent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message)
				.setNegativeButton(negativeBtn, null)
				.setPositiveButton(positiveBtn, positiveOnClickListener);

		return builder.create();
	}
}