package bg.znestorov.sofbus24.favorites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.FavouritesDatabaseUtils;
import bg.znestorov.sofbus24.entity.FragmentLifecycle;
import bg.znestorov.sofbus24.entity.Station;
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
		FragmentLifecycle {

	private Activity context;

	private List<Station> favouritesStations = new ArrayList<Station>();
	private FavouritesDataSource favouritesDatasource;

	public FavouritesStationFragment() {
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

		// Load the Favorites datasource and fill the list view with the
		// stations from the DB
		favouritesStations.clear();
		favouritesStations.addAll(loadFavouritesList(null));

		// Searching over the Favorites
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.favourites_search);
		actionsOverSearchEditText(searchEditText);

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new FavouritesStationAdapter(context,
				favouritesStations);
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
		switch (item.getItemId()) {
		case R.id.action_favourites_remove_all:
			int favouritesCount = favouritesStations.size();

			// Check if the Favorites database is empty or not
			if (favouritesCount > 0) {
				OnClickListener positiveOnClickListener = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Change the ListView content
						favouritesStations.clear();
						((FavouritesStationAdapter) getListAdapter())
								.notifyDataSetChanged();

						FavouritesDatabaseUtils
								.deleteFavouriteDatabase(context);
						Toast.makeText(
								context,
								Html.fromHtml(getString(R.string.fav_menu_remove_all_toast)),
								Toast.LENGTH_SHORT).show();
					}
				};

				ActivityUtils
						.showCustomAlertDialog(
								context,
								android.R.drawable.ic_menu_delete,
								getString(R.string.app_dialog_title_important),
								Html.fromHtml(getString(R.string.fav_menu_remove_all_confirmation)),
								getString(R.string.app_button_yes),
								positiveOnClickListener,
								getString(R.string.app_button_no), null);
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

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText) {
		searchEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
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
	private List<Station> loadFavouritesList(String searchText) {
		List<Station> favouritesList;

		if (favouritesDatasource == null) {
			favouritesDatasource = new FavouritesDataSource(context);
		}

		favouritesDatasource.open();
		if (searchText == null) {
			favouritesList = favouritesDatasource.getAllStations();
		} else {
			favouritesList = favouritesDatasource
					.getStationsViaSearch(searchText);
		}
		favouritesDatasource.close();

		return favouritesList;
	}
}
