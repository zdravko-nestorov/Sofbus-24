package bg.znestorov.sofbus24.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.main.R;

public class UpdateDatabaseDialog extends DialogFragment {

	private FragmentActivity context;
	private int icon;
	private String title;
	private String message;
	private String negativeBtn;
	private String positiveBtn;
	private OnClickListener positiveOnClickListener;

	private String stationsDatabaseUrl;
	private String vehiclesDatabaseUrl;
	private ConfigEntity newConfig;

	public static final String BUNDLE_STATIONS_DB_URL = "STATIONS DB URL";
	public static final String BUNDLE_VEHICLES_DB_URL = "VEHICLES DB URL";
	public static final String BUNDLE_NEW_APPLICATION_CONFIG = "NEW APPLICATION CONFIG";

	public static UpdateDatabaseDialog newInstance(String stationsDatabaseUrl,
			String vehiclesDatabaseUrl, ConfigEntity newConfig) {
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_STATIONS_DB_URL, stationsDatabaseUrl);
		bundle.putString(BUNDLE_VEHICLES_DB_URL, vehiclesDatabaseUrl);
		bundle.putSerializable(BUNDLE_NEW_APPLICATION_CONFIG, newConfig);

		UpdateDatabaseDialog updateDatabaseDialog = new UpdateDatabaseDialog();
		updateDatabaseDialog.setArguments(bundle);

		return updateDatabaseDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		icon = android.R.drawable.ic_menu_info_details;
		title = getString(R.string.app_dialog_title_important);
		message = getString(R.string.about_update_db_new);
		negativeBtn = getString(R.string.app_button_later);
		positiveBtn = getString(R.string.app_button_update);

		Bundle bundle = getArguments();
		stationsDatabaseUrl = bundle.getString(BUNDLE_STATIONS_DB_URL);
		vehiclesDatabaseUrl = bundle.getString(BUNDLE_VEHICLES_DB_URL);
		newConfig = (ConfigEntity) bundle
				.getSerializable(BUNDLE_NEW_APPLICATION_CONFIG);

		positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(Html.fromHtml(context
						.getString(R.string.about_update_db_copy)));
				RetrieveDatabases retrieveDatabases = new RetrieveDatabases(
						context, progressDialog, stationsDatabaseUrl,
						vehiclesDatabaseUrl, newConfig);
				retrieveDatabases.execute();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message)
				.setNegativeButton(negativeBtn, null)
				.setPositiveButton(positiveBtn, positiveOnClickListener);

		return builder.create();
	}
}