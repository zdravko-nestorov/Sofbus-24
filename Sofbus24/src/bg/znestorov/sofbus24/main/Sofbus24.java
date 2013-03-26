package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.gps_map.MyLocation;
import bg.znestorov.sofbus24.gps_map.ObtainCurrentCordinates;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.DatabaseUtils;
import bg.znestorov.sofbus24.utils.TranslatorLatinToCyrillic;

public class Sofbus24 extends Activity implements OnClickListener {

	Context context = Sofbus24.this;
	SharedPreferences sharedPreferences;

	// Exit alert dialog
	boolean exitAlert = false;

	// Variable holding the written text in the EditField in VirtualBoards alert
	// dialog
	private static String vbInputText = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		// Setting activity title
		this.setTitle(getString(R.string.ss_name));
		// TextView actionBarLabel = (TextView)
		// findViewById(R.id.action_bar_label);
		// Utils.setActionBarLabel(actionBarLabel, getString(R.string.ss_name));

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// Get "exitAlert" value from the Shared Preferences
		exitAlert = sharedPreferences.getBoolean(
				Constants.PREFERENCE_KEY_EXIT_ALERT,
				Constants.PREFERENCE_DEFAULT_VALUE_EXIT_ALERT);

		// DatabaseUtils.copyDatabase(context);
		// DatabaseUtils.generateStationsXML(context);
		// DatabaseUtils.generateStationsTEXT(context);
		// DatabaseUtils.deleteFavouriteDatabase(context);

		// Copy station.db from the APK to the internal memory (if not exists)
		DatabaseUtils.createStationsDatabase(context);

		ImageView btn_gps = (ImageView) findViewById(R.id.btn_gps);
		btn_gps.setOnClickListener(this);
		ImageView btn_map = (ImageView) findViewById(R.id.btn_map);
		btn_map.setOnClickListener(this);
		ImageView btn_schedule = (ImageView) findViewById(R.id.btn_schedule);
		btn_schedule.setOnClickListener(this);
		ImageView btn_favourite = (ImageView) findViewById(R.id.btn_favourite);
		btn_favourite.setOnClickListener(this);
		ImageView btn_options = (ImageView) findViewById(R.id.btn_options);
		btn_options.setOnClickListener(this);
		ImageView btn_exit = (ImageView) findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_gps:
			startVirtualBoardsActivity("");
			break;
		case R.id.btn_map:
			startVirtualBoardsMapGPSActivity();
			break;
		case R.id.btn_schedule:
			Intent listVehicleChoice = new Intent(context, VehicleTabView.class);
			context.startActivity(listVehicleChoice);
			break;
		case R.id.btn_favourite:
			Intent favourites = new Intent(context, Favourites.class);
			context.startActivity(favourites);
			break;
		case R.id.btn_options:
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i, 0);
			break;
		case R.id.btn_exit:
			// if (exitAlert) {
			// onKeyDown(KeyEvent.KEYCODE_BACK, null);
			// } else {
			// finish();
			// }

			ObtainCurrentCordinates fetchCordinates = new ObtainCurrentCordinates(context);
			fetchCordinates.execute();

			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			finish();
			Intent homeScreenSelect = new Intent(context,
					HomeScreenSelect.class);
			startActivity(homeScreenSelect);
		}

		if (requestCode == 1) {
			startVirtualBoardsActivity(vbInputText);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (exitAlert) {
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.btn_exit)
						.setMessage(R.string.exit_msg)
						.setCancelable(true)
						.setPositiveButton(
								context.getString(R.string.button_title_yes),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int i) {
										finish();
									}

								})
						.setNegativeButton(
								context.getString(R.string.button_title_no),
								null).show();
			} else {
				finish();
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	// AsyncTask capable for loading the map
	private class LoadMapAsyncTask extends AsyncTask<Void, Void, Void> {
		Context context;
		ProgressDialog progressDialog;

		public LoadMapAsyncTask(Context context, ProgressDialog progressDialog) {
			this.context = context;
			this.progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Intent gps_location = new Intent(context, VirtualBoardsMapGPS.class);
			context.startActivity(gps_location);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		}
	}

	// Once button btn_gps is clicked
	private void startVirtualBoardsActivity(String inputText) {
		AlertDialog.Builder alert = new AlertDialog.Builder(Sofbus24.this);

		alert.setTitle(R.string.btn_gps);
		alert.setMessage(R.string.gps_msg);

		// Set an EditText view to get user input
		final EditText input = new EditText(Sofbus24.this);
		input.setText(inputText);
		input.setSelection(input.getText().length());
		alert.setView(input);

		alert.setPositiveButton(context.getString(R.string.button_title_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String stationID = input.getText().toString();
						stationID = TranslatorLatinToCyrillic
								.translate(stationID);
						new HtmlRequestSumc().getInformation(context,
								stationID, null, null);

					}
				});

		alert.setNegativeButton(
				context.getString(R.string.button_title_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.setNeutralButton(context.getString(R.string.button_title_help),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(Sofbus24.this,
								VirtualBoardsHelp.class);
						startActivityForResult(intent, 1);
					}
				});

		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);

		// Add Text listener on input field
		input.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String inputText = input.getText().toString();
				vbInputText = inputText;

				if ((inputText.length() == 0)
						|| (inputText.length() <= 2 && !inputText
								.equals(inputText.replaceAll("\\D+", "")))) {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}
		});
	}

	// Once button btn_map is clicked
	private void startVirtualBoardsMapGPSActivity() {
		MyLocation myLocation = new MyLocation();

		// Check to see if at least one provider is enabled
		if (myLocation.getLocation(context)) {
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Loading...");

			LoadMapAsyncTask loadMap = new LoadMapAsyncTask(context,
					progressDialog);
			loadMap.execute();
		} else {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.ss_gps_map_msg_title)
					.setMessage(R.string.ss_gps_map_msg_body)
					.setCancelable(false)
					.setPositiveButton(
							context.getString(R.string.button_title_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int i) {
									Intent intent = new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(intent);
								}

							})
					.setNegativeButton(
							context.getString(R.string.button_title_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int i) {
								}

							}).show();
		}
	}

}
