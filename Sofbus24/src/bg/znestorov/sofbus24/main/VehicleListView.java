package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import bg.znestorov.sofbus24.schedule_stations.Direction;
import bg.znestorov.sofbus24.schedule_stations.DirectionTransfer;
import bg.znestorov.sofbus24.schedule_stations.HtmlRequestDirection;
import bg.znestorov.sofbus24.schedule_stations.HtmlResultDirection;
import bg.znestorov.sofbus24.schedule_vehicles.Vehicle;
import bg.znestorov.sofbus24.schedule_vehicles.VehicleAdapter;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.VehicleNames;

public class VehicleListView extends Activity {

	ListView listView;
	EditText editText;
	Context context;

	public static ArrayList<Vehicle> bus;
	public static ArrayList<Vehicle> trolley;
	public static ArrayList<Vehicle> tram;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_search);

		context = VehicleListView.this;

		// Setting activity title
		this.setTitle(getString(R.string.veh_ch_name));

		// Getting the information from VehicleTabView
		final String vehicleType = getIntent().getStringExtra(
				Constants.KEYWORD_VEHICLE_TYPE);

		// Getting the first row (TextView) text
		TextView separator = (TextView) findViewById(R.id.list_item_section_text);

		// Getting the ListView box from "activity_vehicle_search" layout
		listView = (ListView) findViewById(R.id.list_view_search);

		// Creating the arrays with vehicles names
		bus = VehicleNames.bus;
		trolley = VehicleNames.trolley;
		tram = VehicleNames.tram;

		if (vehicleType.equals(Constants.VEHICLE_BUS)) {
			listView.setAdapter(new VehicleAdapter(this, bus));
			separator.setText(getString(R.string.choose_vehicle_number)
					+ bus.get(0).getType().toLowerCase());
		} else if (vehicleType.equals(Constants.VEHICLE_TROLLEY)) {
			listView.setAdapter(new VehicleAdapter(this, trolley));
			separator.setText(getString(R.string.choose_vehicle_number)
					+ trolley.get(0).getType().toLowerCase());
		} else {
			listView.setAdapter(new VehicleAdapter(this, tram));
			separator.setText(getString(R.string.choose_vehicle_number)
					+ tram.get(0).getType().toLowerCase());
		}

		// Creating the search engine using the EditText box from
		// "activity_vehicle_search" layout
		editText = (EditText) findViewById(R.id.edit_box_search);
		editText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				VehicleListView obj = new VehicleListView();
				String searchText = editText.getText().toString();

				int textLength = editText.getText().length();

				bus = new ArrayList<Vehicle>();
				trolley = new ArrayList<Vehicle>();
				tram = new ArrayList<Vehicle>();

				if (vehicleType.equals(Constants.VEHICLE_BUS)) {
					bus = obj.onTextChanged(VehicleNames.bus, searchText,
							textLength);
					listView.setAdapter(new VehicleAdapter(
							VehicleListView.this, bus));
				} else if (vehicleType.equals(Constants.VEHICLE_TROLLEY)) {
					trolley = obj.onTextChanged(VehicleNames.trolley,
							searchText, textLength);
					listView.setAdapter(new VehicleAdapter(
							VehicleListView.this, trolley));
				} else {
					tram = obj.onTextChanged(VehicleNames.tram, searchText,
							textLength);
					listView.setAdapter(new VehicleAdapter(
							VehicleListView.this, tram));
				}
			}
		});

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					Utils.hideKeyboard(context, editText);
				}
			}
		});

		// Setting ListView listener (if an item is clicked)
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Vehicle vehicle = (Vehicle) listView.getAdapter().getItem(
						position);

				// Getting the information from the clicked item
				String choice = vehicle.getType() + "$" + vehicle.getNumber();

				// Getting the HtmlResult and showing a ProgressDialog
				Context context = VehicleListView.this;
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context
						.getString(R.string.loading_message_retrieve_schedule_info));
				LoadStationsAsyncTask loadStationsAsyncTask = new LoadStationsAsyncTask(
						context, progressDialog, choice);
				loadStationsAsyncTask.execute();
			}

		});
	}

	// Creating new list according to the input in the search box
	private ArrayList<Vehicle> onTextChanged(ArrayList<Vehicle> inputList,
			String searchText, int searchTextLength) {
		ArrayList<Vehicle> output = new ArrayList<Vehicle>();

		for (int i = 0; i < inputList.size(); i++) {
			if (searchTextLength <= inputList.get(i).getNumber().length()) {
				if (searchText.equalsIgnoreCase(inputList.get(i).getNumber()
						.substring(0, searchTextLength))) {
					output.add(inputList.get(i));
				}
			}
		}

		return output;
	}

	// AsyncTask capable of loading the HttpRequestDirection
	private class LoadStationsAsyncTask extends AsyncTask<Void, Void, String> {
		Context context;
		ProgressDialog progressDialog;
		String vehicleChoice;

		// Http Get parameter used to create/abort the HTTP connection
		HttpGet httpGet;

		public LoadStationsAsyncTask(Context context,
				ProgressDialog progressDialog, String vehicleChoice) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.vehicleChoice = vehicleChoice;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							cancel(true);
						}
					});
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String htmlResult = null;

			try {
				HtmlRequestDirection htmlRequestDirection = new HtmlRequestDirection(
						VehicleListView.this, vehicleChoice);
				httpGet = htmlRequestDirection.createDirectionRequest();
				htmlResult = htmlRequestDirection.getInformation(httpGet);
			} catch (Exception e) {
				htmlResult = null;
			}

			return htmlResult;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();

			// HtmlResult processing and creating an ArrayList
			HtmlResultDirection htmlResultDirection = new HtmlResultDirection(
					context, vehicleChoice, result);
			ArrayList<Direction> directionList = htmlResultDirection
					.showResult();
			ArrayList<Direction> resultList = directionList;

			// Creating AlertDialog with the directions, if HtmlResult !=
			// null, otherwise showing an error message
			Builder dialog = new AlertDialog.Builder(context);

			if (resultList != null && resultList.size() > 1) {
				String[] directions = new String[2];
				directions[0] = resultList.get(0).getDirection();
				directions[1] = resultList.get(1).getDirection();
				ArrayAdapter<CharSequence> itemsAdapter = new ArrayAdapter<CharSequence>(
						context, R.layout.activity_vehicle_direction_choice,
						directions);

				// Creating DirectionTransfer object, so transfer the
				// HtmlResult to the next activity
				final DirectionTransfer directionTransfer = new DirectionTransfer(
						resultList);

				dialog.setTitle(R.string.veh_ch_direction_choice)
						.setAdapter(itemsAdapter,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										directionTransfer.setChoice(i);
										Bundle bundle = new Bundle();
										Intent stationIntent = new Intent(
												context, StationTabView.class);
										bundle.putSerializable(
												Constants.KEYWORD_BUNDLE_DIRECTION_TRANSFER,
												directionTransfer);
										stationIntent.putExtras(bundle);
										editText.setText("");
										startActivityForResult(stationIntent, i);
									}
								}).show();
			} else {
				Intent intent = new Intent(context,
						VirtualBoardsStationChoice.class);

				intent.putExtra(Constants.KEYWORD_HTML_RESULT,
						Constants.SCHEDULE_NO_INFO);
				context.startActivity(intent);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			progressDialog.dismiss();
			httpGet.abort();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			Utils.showKeyboard(context, editText);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}