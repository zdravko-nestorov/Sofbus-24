package bg.znestorov.sofbus24.main;

import java.util.List;

import android.R.drawable;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;

public class Favourites extends ListActivity {

	private FavouritesDataSource datasource;
	private Context context;
	List<GPSStation> values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourites);

		// Setting activity title
		this.setTitle(getString(R.string.fav_info));

		datasource = new FavouritesDataSource(this);
		datasource.open();

		context = Favourites.this;

		values = datasource.getAllStations();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<GPSStation> adapter = new ArrayAdapter<GPSStation>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		GPSStation station = (GPSStation) getListAdapter().getItem(position);
		String selectedRow = station.getName();
		Toast.makeText(this, selectedRow, Toast.LENGTH_SHORT).show();

		String[] coordinates = { station.getLat(), station.getLon() };
		HtmlRequestSumc sumc = new HtmlRequestSumc();

		if ("-1".equals(station.getCodeO())) {
			sumc.getInformation(context, station.getId(),
					Constants.FAVORITES_GPS_PARAM, coordinates);
		} else {
			sumc.getInformation(context, station.getId(), station.getCodeO(),
					coordinates);
		}

		datasource.close();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_favourites, menu);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ArrayAdapter<GPSStation> adapter = (ArrayAdapter<GPSStation>) getListAdapter();

		switch (item.getItemId()) {
		case R.id.menu_delete:
			if (getListAdapter().getCount() > 0) {
				GPSStation station = new GPSStation();
				station = (GPSStation) getListAdapter().getItem(0);
				datasource.deleteStation(station);
				adapter.remove(station);
			}
			break;
		case R.id.menu_delete_all:
			GPSStation station = new GPSStation();
			int list_size = getListAdapter().getCount();

			for (int i = 0; i < list_size; i++) {
				station = (GPSStation) getListAdapter().getItem(0);
				datasource.deleteStation(station);
				adapter.remove(station);
				adapter.notifyDataSetChanged();
			}
			break;
		}

		adapter.notifyDataSetChanged();

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_favourites_context, menu);

		// Set menu title and header
		menu.setHeaderTitle(getString(R.string.st_list_cont_menu_header));
		menu.setHeaderIcon(drawable.ic_menu_info_details);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final ArrayAdapter<GPSStation> adapter = (ArrayAdapter<GPSStation>) getListAdapter();

		switch (item.getItemId()) {
		case R.id.fav_menu_rename:

			// Set an EditText view to get user input
			AlertDialog.Builder alert = new AlertDialog.Builder(Favourites.this);

			alert.setTitle(R.string.fav_menu_rename_title);
			alert.setMessage(R.string.fav_menu_rename_msg);

			// Set an EditText view to get user input
			final EditText input = new EditText(Favourites.this);
			alert.setView(input);

			alert.setPositiveButton(
					context.getString(R.string.button_title_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Get the selected station
							GPSStation station = (GPSStation) getListAdapter()
									.getItem((int) info.id);

							// Delete the station from favorites
							datasource.deleteStation(station);
							adapter.remove(station);

							// Get the user input
							String favName = input.getText().toString();

							// Change the name and add it to the database
							station.setName(favName);

							datasource.createStation(station);
							adapter.add(station);
						}
					});

			alert.setNegativeButton(
					context.getString(R.string.button_title_cancel),
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

					if (inputText.length() == 0) {
						dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(
								false);
					} else {
						dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(
								true);
					}
				}
			});

			break;
		case R.id.fav_menu_delete:
			GPSStation station = (GPSStation) getListAdapter().getItem(
					(int) info.id);
			datasource.deleteStation(station);
			adapter.remove(station);
			break;
		}

		adapter.notifyDataSetChanged();

		return true;
	}
}