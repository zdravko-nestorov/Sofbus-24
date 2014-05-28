package bg.znestorov.sofbus24.virtualboards;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
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
import bg.znestorov.sofbus24.entity.HtmlRequestCodes;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

public class VirtualBoardsFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;
	private TextView emptyList;
	private SearchEditText searchEditText;

	private String vbSearchText;
	private ArrayList<Station> vbList;

	private static final String BUNDLE_VB_SEARCH_TEXT = "VB SEARCH TEXT";
	private static final String BUNDLE_VB_STATIONS_LIST = "VB STATION LIST";

	public VirtualBoardsFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_virtual_boards_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the vbSearchText and vbList from the Bundle (savedInstanceState)
		if (savedInstanceState != null) {
			vbSearchText = savedInstanceState.getString(BUNDLE_VB_SEARCH_TEXT);
			vbList = (ArrayList<Station>) savedInstanceState
					.getSerializable(BUNDLE_VB_STATIONS_LIST);
		} else {
			vbSearchText = "";
		}

		// Find all of TextView and SearchEditText tabs in the layout
		emptyList = (TextView) myFragmentView
				.findViewById(R.id.vb_list_empty_text);
		searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.vb_search);

		// In case of screen rotation (recreate screen)
		searchEditText.setText(vbSearchText);

		// Set the list adapter to the Fragment
		setListAdapterViaSearch();

		// Set the actions over the TextViews and SearchEditText
		actionsOverSearchEditText();

		return myFragmentView;
	}

	@Override
	public void update(Activity context, Object obj) {
		setListAdapterViaSearch();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(BUNDLE_VB_SEARCH_TEXT, vbSearchText);
		savedInstanceState.putSerializable(BUNDLE_VB_STATIONS_LIST, vbList);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
				context, this, station, HtmlRequestCodes.SINGLE_RESULT);
		retrieveVirtualBoards.getSumcInformation();

		Toast.makeText(getActivity(), station.getName(), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * Set list adapter and the appropriate text message to it (using a list as
	 * a parameter)
	 * 
	 * @param stationsList
	 *            the stationList that need to be set to the listView
	 */
	public void setListAdapterViaSearch(ArrayList<Station> stationsList) {
		vbList = stationsList;
		setListAdapterViaSearch();
	}

	/**
	 * Set list adapter and the appropriate text message to it, using the
	 * default fragment list (vbList)
	 */
	private void setListAdapterViaSearch() {
		ArrayAdapter<Station> adapter = new VirtualBoardsAdapter(context,
				vbList);

		if (adapter.isEmpty()) {
			setEmptyListAdapter();
		} else {
			setListAdapter(adapter);
		}
	}

	/**
	 * Set an empty list adapter and the appropriate text message to it
	 */
	private void setEmptyListAdapter() {
		setListAdapter(null);
		setEmptyListText();
	}

	/**
	 * Set a message to the empty list according to the search text view:
	 * <ul>
	 * <li>If contains <b>no</b> text - set the default search message</li>
	 * <li>If contains <b>some</b> text - set that there are no results</li>
	 * </ul>
	 */
	private void setEmptyListText() {
		if (vbSearchText == null || "".equals(vbSearchText)) {
			emptyList.setText(Html
					.fromHtml(getString(R.string.vb_item_search_list)));
		} else {
			emptyList.setText(Html.fromHtml(String.format(
					getString(R.string.vb_item_empty_list), vbSearchText)));
		}
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText() {
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
				vbSearchText = searchEditText.getText().toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
		});

		// Add the editor action listener
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					performSearch();

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
					performSearch();

					break;
				case RIGHT:
					searchEditText.setText("");
					performSearch();

					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * Perform a search via the search text from the SearchEditText
	 */
	private void performSearch() {
		if (checkSearchText(vbSearchText)) {
			Station station = new Station();
			station.setNumberUnformatted(vbSearchText);

			RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
					context, this, station, HtmlRequestCodes.MULTIPLE_RESULTS);
			retrieveVirtualBoards.getSumcInformation();
		} else {
			setEmptyListAdapter();
		}
	}

	/**
	 * Check the searched text is containing only digits and if not - if its
	 * size is more than 3 characters
	 * 
	 * @param searchText
	 *            the searched text
	 * @return if the searched text fulfill the criteria
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

}
