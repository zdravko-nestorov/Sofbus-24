package bg.znestorov.sofbus24.favorites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.activity.ActivityUtils;
import bg.znestorov.sofbus24.activity.DrawableClickListener;
import bg.znestorov.sofbus24.activity.SearchEditText;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.FavouritesDatabaseUtils;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.main.R;

/**
 * Favourites fragment responsible for visualizing the items from Favourites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;

	private FavouritesDataSource favouritesDatasource;
	private List<Station> favouritesStations;

	public FavouritesFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_favourites_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Load the Favourites Datasource
		favouritesDatasource = new FavouritesDataSource(context);
		favouritesDatasource.open();

		// Fill the list view with the stations from the DB
		favouritesStations = favouritesDatasource.getAllStations();

		// Searching over the Favourites
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.favourites_search);
		actionsOverSearchEditText(searchEditText);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new FavouritesStationAdapter(context,
				favouritesStations);
		setListAdapter(adapter);

		// Activate the option menu
		setHasOptionsMenu(true);

		// Set the message if the list is empty
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.favourites_list_empty_text);
		emptyList.setText(Html
				.fromHtml(getString(R.string.fav_item_empty_list)));

		return myFragmentView;
	}

	@Override
	public void update() {
	}

	@Override
	public void update(Activity context) {
		favouritesDatasource = new FavouritesDataSource(context);
		favouritesDatasource.open();
		favouritesStations = favouritesDatasource.getAllStations();
		favouritesDatasource.close();
		setListAdapter(new FavouritesStationAdapter(context, favouritesStations));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		// TODO: Retrieve information about the station

		Toast.makeText(getActivity(), station.getName(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onResume() {
		favouritesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		favouritesDatasource.close();
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_favourites_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ArrayAdapter<Station> adapter = (ArrayAdapter<Station>) getListAdapter();

		switch (item.getItemId()) {
		case R.id.favourites_menu_remove_all:
			int favouritesCount = favouritesStations.size();

			// Check if the Favourites database is empty or not
			if (favouritesCount > 0) {
				// Create an Alert Dialog to ensure that the user wants to clear
				// the list
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							setListAdapter(new FavouritesStationAdapter(
									context, new ArrayList<Station>()));
							FavouritesDatabaseUtils
									.deleteFavouriteDatabase(context);
							Toast.makeText(
									context,
									Html.fromHtml(getString(R.string.fav_menu_remove_all_toast)),
									Toast.LENGTH_LONG).show();
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setIcon(android.R.drawable.ic_menu_delete)
						.setTitle(
								getString(R.string.app_dialog_title_important))
						.setMessage(
								Html.fromHtml(getString(R.string.fav_menu_remove_all_confirmation)))
						.setPositiveButton(getString(R.string.app_button_yes),
								dialogClickListener)
						.setNegativeButton(getString(R.string.app_button_no),
								dialogClickListener).show();
			} else {
				Toast.makeText(
						context,
						Html.fromHtml(getString(R.string.fav_menu_remove_all_empty_toast)),
						Toast.LENGTH_LONG).show();
			}

			break;
		}

		adapter.notifyDataSetChanged();

		return true;
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText) {
		searchEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		// Add on focus listener
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, searchEditText);
				}
			}
		});

		// Add on text changes listener
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String searchText = searchEditText.getText().toString();
				List<Station> searchStationList = favouritesDatasource
						.getStationsViaSearch(searchText);
				ArrayAdapter<Station> adapter = new FavouritesStationAdapter(
						context, searchStationList);

				setListAdapter(adapter);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
		});

		// Add a drawable listeners (search and clear icons)
		searchEditText.setDrawableClickListener(new DrawableClickListener() {
			@Override
			public void onClick(DrawablePosition target) {
				switch (target) {
				case LEFT:
					searchEditText.requestFocus();
					ActivityUtils.showKeyboard(context, searchEditText);
					break;
				case RIGHT:
					searchEditText.setText("");
					break;
				default:
					break;
				}
			}

		});
	}
}
