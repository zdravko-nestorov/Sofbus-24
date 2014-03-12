package bg.znestorov.sofbus24.virtualboards;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import bg.znestorov.sofbus24.activity.ActivityUtils;
import bg.znestorov.sofbus24.activity.DrawableClickListener;
import bg.znestorov.sofbus24.activity.SearchEditText;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.main.R;

public class VirtualBoardsFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;

	private StationsDataSource stationsDatasource;
	private List<Station> vbList;
	private String vbSearchText = "";

	public VirtualBoardsFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_virtual_boards_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Load the Stations Datasource
		stationsDatasource = new StationsDataSource(context);

		// Find all of TextView and SearchEditText tabs in the layout
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.vb_search);
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.vb_list_empty_text);

		// Add an empty list to the Fragment
		performSearch(searchEditText, emptyList);

		// Set the actions over the TextViews and SearchEditText
		actionsOverSearchEditText(searchEditText, emptyList);

		return myFragmentView;
	}

	@Override
	public void update() {
	}

	@Override
	public void update(Activity context) {
		SearchEditText searchEditText = (SearchEditText) context
				.findViewById(R.id.vb_search);
		TextView emptyList = (TextView) context
				.findViewById(R.id.vb_list_empty_text);
		performSearch(searchEditText, emptyList);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		// TODO: Retrieve information about the vehicle

		Toast.makeText(getActivity(), station.getName(), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText,
			final TextView emptyList) {
		searchEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		// Add on focus listener
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, searchEditText);
				}
			}
		});

		// Àdd the editor action listener
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					performSearch(searchEditText, emptyList);

					return true;
				}

				return false;
			}
		});

		// Add a drawable listeners (search and clear icons)
		searchEditText.setDrawableClickListener(new DrawableClickListener() {
			@Override
			public void onClick(DrawablePosition target) {
				switch (target) {
				case LEFT:
					performSearch(searchEditText, emptyList);

					break;
				case RIGHT:
					searchEditText.setText("");
					performSearch(searchEditText, emptyList);

					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * Perform a search via the search text from the SearchEditText
	 * 
	 * @param searchEditText
	 *            the SearchEditText
	 * @param emptyList
	 *            the empty list
	 */
	private void performSearch(final SearchEditText searchEditText,
			final TextView emptyList) {
		vbSearchText = searchEditText.getText().toString();

		// Check if the search is legal
		if (!checkSearchText(vbSearchText)) {
			vbSearchText = "";
		}

		vbList = loadStationsList(vbSearchText);
		ArrayAdapter<Station> adapter = new VirtualBoardsAdapter(context,
				vbList);
		setListAdapter(adapter);

		// Set a message if the list is empty
		if (adapter.isEmpty()) {
			if (!"".equals(vbSearchText)) {
				emptyList.setText(Html.fromHtml(String.format(
						getString(R.string.vb_item_empty_list), vbSearchText)));
			} else {
				emptyList.setText(Html
						.fromHtml(getString(R.string.vb_item_search_list)));
			}
		}
	}

	/**
	 * Check the searched text is containing only digits and if not - if its
	 * size is more than 3 charecters
	 * 
	 * @param searchText
	 *            the searched text
	 * @return if the searched text fullfill the criterias
	 */
	private boolean checkSearchText(String searchText) {
		boolean result = false;

		if (searchText != null && !"".equals(searchText)) {
			String searchTextNoDigits = searchText.replaceAll("\\d+", "");

			if ("".equals(searchTextNoDigits)) {
				result = true;
			} else {
				if (searchText.length() > 2) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Load all stations according to a search text (if it is left as empty -
	 * all stations of the current tab type are loaded)
	 * 
	 * @param searchText
	 *            the search text
	 * @return all stations according to a search text
	 */
	private List<Station> loadStationsList(String searchText) {
		List<Station> stationsList;

		if (searchText != null && !"".equals(searchText)) {
			if (stationsDatasource == null) {
				stationsDatasource = new StationsDataSource(context);
			}

			stationsDatasource.open();
			stationsList = stationsDatasource.getStationsViaSearch(null,
					searchText);
			stationsDatasource.close();
		} else {
			stationsList = new ArrayList<Station>();
		}

		return stationsList;
	}

}
