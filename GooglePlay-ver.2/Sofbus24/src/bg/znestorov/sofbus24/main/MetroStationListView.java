package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.metro_schedule_directions.MetroDirection;
import bg.znestorov.sofbus24.metro_schedule_directions.MetroDirectionTransfer;
import bg.znestorov.sofbus24.metro_schedule_stations.MetroStation;
import bg.znestorov.sofbus24.metro_schedule_stations.MetroStationAdapter;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

public class MetroStationListView extends Activity {

	private ListView listView;
	private EditText editText;

	private Activity context;
	private FavouritesDataSource datasource;
	private ArrayList<MetroStation> metroStations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metro_schedule);

		// Setting activity title
		this.setTitle(getString(R.string.metro_st_ch_name));

		context = MetroStationListView.this;
		datasource = new FavouritesDataSource(context);

		MetroDirectionTransfer mdt;
		try {
			mdt = (MetroDirectionTransfer) getIntent().getSerializableExtra(
					Constants.KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER);
		} catch (Exception e) {
			// It should never go here
			mdt = null;
		}

		MetroDirection md = mdt.getDirectionsList().get(mdt.getChoice());
		metroStations = md.getStationsAsList();

		// Getting the ListView box from "activity_station" layout
		listView = (ListView) findViewById(R.id.list_view_search);
		listView.setAdapter(new MetroStationAdapter(context, metroStations));
		registerForContextMenu(listView);

		// Setting the first TextView in the Activity
		TextView metroDirection = (TextView) findViewById(R.id.direction_text_view);
		String vehicleDirectionText = md.getName();
		metroDirection.setText(vehicleDirectionText);

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

				MetroStationListView mslv = new MetroStationListView();
				String searchText = editText.getText().toString();
				int textLength = editText.getText().length();

				ArrayList<MetroStation> metroStationsAfterSearch = mslv
						.onTextChanged(metroStations, searchText, textLength);
				listView.setAdapter(new MetroStationAdapter(context,
						metroStationsAfterSearch));
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
				MetroStation ms = (MetroStation) listView.getAdapter().getItem(
						position);
				String stationName = ms.getName();
				Toast.makeText(context, stationName, Toast.LENGTH_SHORT).show();

				// Getting the HtmlResult and showing a ProgressDialog
				Context context = MetroStationListView.this;
				ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context
						.getString(R.string.loading_message_retrieve_schedule_info));
			}
		});
	}

	/**
	 * Creating new list according to the input in the search box
	 * 
	 * @param inputList
	 *            inputList
	 * @param searchText
	 *            text in the search box
	 * @param searchTextLength
	 *            length of the search text
	 * @return processed input list
	 */
	private ArrayList<MetroStation> onTextChanged(
			ArrayList<MetroStation> inputList, String searchText,
			int searchTextLength) {
		ArrayList<MetroStation> outputList = new ArrayList<MetroStation>();

		for (int i = 0; i < inputList.size(); i++) {
			if (searchTextLength <= inputList.get(i).getNumber().length()) {
				if (searchText.equalsIgnoreCase(inputList.get(i).getNumber()
						.substring(0, searchTextLength))) {
					outputList.add(inputList.get(i));
				}
			}
		}

		return outputList;
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
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