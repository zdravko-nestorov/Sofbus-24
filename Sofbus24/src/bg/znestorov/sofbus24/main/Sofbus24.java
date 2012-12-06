package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.gps_map.MyLocation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.DatabaseUtils;

public class Sofbus24 extends Activity implements OnClickListener {

	Context context = Sofbus24.this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		// Setting activity title
		this.setTitle(getString(R.string.ss_name));

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
		ImageView btn_about = (ImageView) findViewById(R.id.btn_about);
		btn_about.setOnClickListener(this);
		ImageView btn_exit = (ImageView) findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_gps:
			AlertDialog.Builder alert = new AlertDialog.Builder(Sofbus24.this);

			alert.setTitle(R.string.btn_gps);
			alert.setMessage(R.string.gps_msg);

			// Set an EditText view to get user input
			final EditText input = new EditText(Sofbus24.this);
			// input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);

			alert.setPositiveButton("ОК",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String stationID = input.getText().toString();

							try {
								Integer.parseInt(stationID);

								new HtmlRequestSumc(
										Constants.SEARCH_TYPE_NUMBER)
										.getInformation(context, stationID,
												null);
							} catch (NumberFormatException e) {
								new HtmlRequestSumc(Constants.SEARCH_TYPE_NAME)
										.getInformation(context, stationID,
												null);
							}

						}
					});

			alert.setNegativeButton("Отказ",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			final AlertDialog dialog = alert.create();
			dialog.show();
			dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);

			// Add Text listener on input field
			input.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String inputText = input.getText().toString();

					if ((inputText.length() == 0)
							|| (inputText.length() <= 2 && !inputText
									.equals(inputText.replaceAll("\\D+", "")))) {
						dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(
								false);
					} else {
						dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(
								true);
					}
				}
			});

			break;
		case R.id.btn_map:
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
						.setPositiveButton("ОК",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int i) {
										Intent intent = new Intent(
												android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
										startActivity(intent);
									}

								})
						.setNegativeButton("Отказ",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int i) {
									}

								}).show();
			}

			break;
		case R.id.btn_schedule:
			Intent listVehicleChoice = new Intent(context, VehicleTabView.class);
			context.startActivity(listVehicleChoice);
			break;
		case R.id.btn_favourite:
			Intent favourites = new Intent(context, Favourites.class);
			context.startActivity(favourites);
			break;
		case R.id.btn_about:
			Intent i = new Intent(this, About.class);
			startActivity(i);
			break;
		case R.id.btn_exit:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.btn_exit).setMessage(R.string.exit_msg)
				.setCancelable(true)
				.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int i) {
						finish();
					}

				}).setNegativeButton("Не", null).show();
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

}
