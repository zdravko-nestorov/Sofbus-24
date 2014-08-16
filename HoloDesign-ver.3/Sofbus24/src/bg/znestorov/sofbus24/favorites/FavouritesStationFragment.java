package bg.znestorov.sofbus24.favorites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.FavouritesDatabaseUtils;
import bg.znestorov.sofbus24.entity.FragmentLifecycle;
import bg.znestorov.sofbus24.entity.OrderTypeEnum;
import bg.znestorov.sofbus24.entity.SortTypeEnum;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.favorites.FavouritesDeleteAllDialog.OnDeleteAllFavouritesListener;
import bg.znestorov.sofbus24.favorites.FavouritesOrderDialog.OnOrderChoiceListener;
import bg.znestorov.sofbus24.favorites.FavouritesRenameDialog.OnRenameFavouritesListener;
import bg.znestorov.sofbus24.favorites.FavouritesSortDialog.OnSortChoiceListener;
import bg.znestorov.sofbus24.favorites.FavouritesSortTypeDialog.OnSortTypeChoiceListener;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

/**
 * Favourites fragment responsible for visualizing the items from Favorites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesStationFragment extends ListFragment implements
		FragmentLifecycle, OnDeleteAllFavouritesListener,
		OnRenameFavouritesListener, OnOrderChoiceListener,
		OnSortChoiceListener, OnSortTypeChoiceListener {

	private Activity context;

	private List<StationEntity> favouritesStations = new ArrayList<StationEntity>();
	private FavouritesDataSource favouritesDatasource;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_favourites_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Load the Favorites datasource and fill the list view with the
		// stations from the DB
		favouritesStations.clear();
		favouritesStations.addAll(loadFavouritesList(null));

		// Searching over the Favorites
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.favourites_search);
		actionsOverSearchEditText(searchEditText);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<StationEntity> adapter = new FavouritesStationAdapter(
				context, this, favouritesStations);
		setListAdapter(adapter);

		// Set the message if the list is empty
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.favourites_list_empty_text);
		emptyList.setText(Html
				.fromHtml(getString(R.string.fav_item_empty_list)));

		// Activate the option menu
		setHasOptionsMenu(true);

		return myFragmentView;
	}

	@Override
	public void onResumeFragment(Activity context) {
		FavouritesStationAdapter favouritesStationAdapter = (FavouritesStationAdapter) getListAdapter();

		if (favouritesStationAdapter != null) {
			favouritesStations.clear();
			favouritesStations.addAll(loadFavouritesList(null));
			favouritesStationAdapter.setExpandedListItemValue();
			favouritesStationAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int favouritesCount = favouritesStations.size();

		switch (item.getItemId()) {
		case R.id.action_favourites_remove_all:
			if (favouritesCount > 0) {
				DialogFragment dialogFragment = FavouritesDeleteAllDialog
						.newInstance();
				dialogFragment.setTargetFragment(this, 0);
				dialogFragment.show(getFragmentManager(), "dialog");
			} else {
				Toast.makeText(
						context,
						Html.fromHtml(getString(R.string.fav_menu_remove_all_empty_toast)),
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.action_favourites_sort:
			if (favouritesCount > 0) {
				DialogFragment dialogFragment = FavouritesSortDialog
						.newInstance();
				dialogFragment.setTargetFragment(this, 0);
				dialogFragment.show(getFragmentManager(), "dialog");
			} else {
				Toast.makeText(
						context,
						Html.fromHtml(getString(R.string.fav_menu_remove_all_empty_toast)),
						Toast.LENGTH_SHORT).show();
			}

			break;
		}

		return true;
	}

	@Override
	public void onDeleteAllFavouritesClicked() {
		favouritesStations.clear();
		((FavouritesStationAdapter) getListAdapter()).notifyDataSetChanged();

		FavouritesDatabaseUtils.deleteFavouriteDatabase(context);
		Toast.makeText(context,
				Html.fromHtml(getString(R.string.fav_menu_remove_all_toast)),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onOrderChoosed(StationEntity station, OrderTypeEnum orderType) {
		if (favouritesDatasource == null) {
			favouritesDatasource = new FavouritesDataSource(context);
		}

		favouritesDatasource.open();
		favouritesDatasource.swapStations(station, orderType);
		favouritesDatasource.close();

		favouritesStations.clear();
		favouritesStations.addAll(loadFavouritesList(null));

		((FavouritesStationAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onSortChoosed(SortTypeEnum sortType) {
		switch (sortType) {
		case CUSTOM:
			onSortTypeChoosed(sortType);
			break;
		default:
			DialogFragment dialogFragment = FavouritesSortTypeDialog
					.newInstance(sortType);
			dialogFragment.setTargetFragment(this, 0);
			dialogFragment.show(getFragmentManager(), "dialog");
			break;
		}
	}

	@Override
	public void onSortTypeChoosed(SortTypeEnum sortType) {
		// Set the user choosen sort type in a preference file
		FavouritesPreferences.setFavouritesSortType(context, sortType);

		// Reorder the favourites stations
		if (favouritesDatasource == null) {
			favouritesDatasource = new FavouritesDataSource(context);
		}

		favouritesDatasource.open();
		favouritesStations.clear();
		favouritesStations.addAll(favouritesDatasource
				.getAllStationsSorted(sortType));
		favouritesDatasource.close();

		((FavouritesStationAdapter) getListAdapter()).notifyDataSetChanged();

		Toast.makeText(context,
				Html.fromHtml(getString(R.string.fav_menu_sort_success)),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText) {
		// TODO: Find a way to set an alphanumeric keyboard with numeric as
		// default
		searchEditText.setFilters(new InputFilter[] { ActivityUtils
				.createInputFilter() });

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
				((FavouritesStationAdapter) getListAdapter()).getFilter()
						.filter(searchText);
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

	/**
	 * Load all stations, marked as favorites, according to a search text (if it
	 * is left as empty - all favorites stations are loaded)
	 * 
	 * @param searchText
	 *            the search text (if null - return all favorites stations)
	 * @return all stations, marked as favorites, according to a search text
	 */
	private List<StationEntity> loadFavouritesList(String searchText) {
		List<StationEntity> favouritesList;

		if (favouritesDatasource == null) {
			favouritesDatasource = new FavouritesDataSource(context);
		}

		favouritesDatasource.open();
		if (searchText == null) {
			favouritesList = favouritesDatasource
					.getAllStationsSorted(FavouritesPreferences
							.getFavouritesSortType(context));
		} else {
			favouritesList = favouritesDatasource
					.getStationsViaSearch(searchText);
		}
		favouritesDatasource.close();

		return favouritesList;
	}

	@Override
	public void onRenameFavouritesClicked(String stationName,
			StationEntity station) {
		ListAdapter listAdapter = getListAdapter();

		if (listAdapter != null) {
			String oldStationName = station.getName();
			String newStationName = stationName;

			station.setName(newStationName);
			updateStation(station);

			favouritesStations.clear();
			favouritesStations.addAll(loadFavouritesList(null));
			((FavouritesStationAdapter) listAdapter).notifyDataSetChanged();

			Toast.makeText(
					context,
					Html.fromHtml(String.format(context
							.getString(R.string.app_toast_rename_favourites),
							oldStationName, station.getNumber(),
							newStationName, station.getNumber())),
					Toast.LENGTH_LONG).show();
		}
	}

	private void updateStation(StationEntity station) {
		if (favouritesDatasource == null) {
			favouritesDatasource = new FavouritesDataSource(context);
		}

		favouritesDatasource.open();
		favouritesDatasource.updateStation(station);
		favouritesDatasource.close();
	}
}