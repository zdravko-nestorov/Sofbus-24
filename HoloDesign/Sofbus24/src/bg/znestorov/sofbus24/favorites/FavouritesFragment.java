package bg.znestorov.sofbus24.favorites;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.FavouritesDatabaseUtils;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.ActivityUtils;
import bg.znestorov.sofbus24.utils.LanguageChange;

/**
 * Favourites fragment responsible for visualizing the items from Favourites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesFragment extends ListFragment {

	private FavouritesDataSource favouritesDatasource;
	private Context context;
	private List<Station> favouritesStations;

	public FavouritesFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Fill the list view with the stations from DB
		favouritesDatasource = new FavouritesDataSource(context);
		favouritesDatasource.open();
		favouritesStations = favouritesDatasource.getAllStations();

		// Init the UIL image loader
		ActivityUtils.initImageLoader(context);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new FavouritesStationAdapter(context,
				favouritesStations);
		setListAdapter(adapter);

		// Activate the option menu
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View myFragmentView = inflater.inflate(
				R.layout.activity_favourites_fragment, container, false);

		// Set the message if the list is empty
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.favourites_list_empty_text);
		emptyList.setText(Html
				.fromHtml(getString(R.string.fav_item_empty_list)));

		// Searching over the Favourites
		final EditText editText = (EditText) myFragmentView
				.findViewById(R.id.favourites_search);
		editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, editText);
				}
			}
		});

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Make the search only in cases there is something in
				// Favourites
				String searchText = editText.getText().toString();
				ArrayList<Station> searchStationList = getSearchResult(
						myFragmentView, searchText);
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

		return myFragmentView;
	}

	/**
	 * Creates an ArrayList with all stations that match the search condition
	 * 
	 * @param searchText
	 *            the search text
	 * @return an ArrayList with all stations from Favourites matching the
	 *         search
	 */
	private ArrayList<Station> getSearchResult(View myFragmentView,
			String searchText) {
		ArrayList<Station> searchStationList = new ArrayList<Station>();
		Locale currentLocale = new Locale(LanguageChange.getUserLocale(context));

		// Get the Favourites stations each time a search is made
		favouritesStations = favouritesDatasource.getAllStations();

		if (searchText != null && !"".equals(searchText)) {
			searchText = searchText.toUpperCase(currentLocale);

			for (int i = 0; i < favouritesStations.size(); i++) {
				Station station = favouritesStations.get(i);
				String stationName = station.getName().toUpperCase(
						currentLocale);
				String stationNumber = station.getNumber().toUpperCase(
						currentLocale);

				if (stationName.contains(searchText)
						|| stationNumber.contains(searchText)) {
					searchStationList.add(station);
				}
			}
		} else {
			searchStationList.addAll(favouritesStations);
		}

		return searchStationList;
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
}
