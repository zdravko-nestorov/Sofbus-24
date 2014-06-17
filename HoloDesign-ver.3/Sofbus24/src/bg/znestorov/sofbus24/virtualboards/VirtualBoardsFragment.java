package bg.znestorov.sofbus24.virtualboards;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import bg.znestorov.sofbus24.entity.FragmentLifecycle;
import bg.znestorov.sofbus24.entity.HtmlRequestCodes;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

public class VirtualBoardsFragment extends ListFragment implements
		FragmentLifecycle {

	private Activity context;
	private TextView emptyList;
	private SearchEditText searchEditText;

	private String vbSearchText;
	private ArrayList<Station> vbList = new ArrayList<Station>();

	private String emptyListMsg;

	private static final String BUNDLE_VB_SEARCH_TEXT = "VB SEARCH TEXT";
	private static final String BUNDLE_VB_STATIONS_LIST = "VB STATION LIST";
	private static final String BUNDLE_VB_EMPTY_LIST_MSG = "VB EMPTY LIST MESSAGE";

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
		if (savedInstanceState != null
				&& savedInstanceState.getSerializable(BUNDLE_VB_STATIONS_LIST) != null) {
			vbSearchText = savedInstanceState.getString(BUNDLE_VB_SEARCH_TEXT);
			vbList.clear();
			vbList.addAll((ArrayList<Station>) savedInstanceState
					.getSerializable(BUNDLE_VB_STATIONS_LIST));
			emptyListMsg = savedInstanceState
					.getString(BUNDLE_VB_EMPTY_LIST_MSG);
		} else {
			vbSearchText = "";
			emptyListMsg = "";
		}

		// Find all of TextView and SearchEditText tabs in the layout
		emptyList = (TextView) myFragmentView
				.findViewById(R.id.vb_list_empty_text);
		searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.vb_search);

		// In case of screen rotation (recreate screen)
		searchEditText.setText(vbSearchText);

		// Set the list adapter to the Fragment
		ArrayAdapter<Station> virtualBoardsAdapter = new VirtualBoardsAdapter(
				context, vbList);
		setListAdapter(virtualBoardsAdapter);
		setEmptyListText();

		// Set the actions over the TextViews and SearchEditText
		actionsOverSearchEditText();

		return myFragmentView;
	}

	@Override
	public void onResumeFragment(Activity context) {
		setListAdapterViaSearch();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(BUNDLE_VB_SEARCH_TEXT, vbSearchText);
		savedInstanceState.putSerializable(BUNDLE_VB_STATIONS_LIST, vbList);
		savedInstanceState.putString(BUNDLE_VB_EMPTY_LIST_MSG, emptyListMsg);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
				context, this, station, HtmlRequestCodes.SINGLE_RESULT);
		retrieveVirtualBoards.getSumcInformation();
	}

	/**
	 * Set list adapter and the appropriate text message to it (using a list as
	 * a parameter)
	 * 
	 * @param stationsList
	 *            the stationList that need to be set to the listView
	 * @param emptyListMsg
	 *            the text that will show if the list is empty
	 */
	public void setListAdapterViaSearch(ArrayList<Station> stationsList,
			String emptyListMsg) {
		vbList.clear();
		vbList.addAll(stationsList);

		this.emptyListMsg = emptyListMsg;

		setListAdapterViaSearch();
	}

	/**
	 * Set list adapter and the appropriate text message to it, using the
	 * default fragment list (vbList)
	 */
	private void setListAdapterViaSearch() {
		ArrayAdapter<Station> virtualBoardsAdapter = (VirtualBoardsAdapter) getListAdapter();

		if (virtualBoardsAdapter != null) {
			virtualBoardsAdapter.notifyDataSetChanged();

			if (virtualBoardsAdapter.isEmpty()) {
				setEmptyListText();
			}
		}
	}

	/**
	 * Set a message to the empty list according to the search text view:
	 * <ul>
	 * <li>If contains <b>no</b> text - set the default search message</li>
	 * <li>If contains <b>some</b> text - set that there are no results</li>
	 * </ul>
	 */
	private void setEmptyListText() {
		// Check if the fragment is currently added to its activity
		if (isAdded()) {
			if (emptyListMsg != null && !"".equals(emptyListMsg)) {
				emptyList.setText(Html.fromHtml(emptyListMsg));
			} else {
				if (vbSearchText == null || "".equals(vbSearchText)) {
					emptyList.setText(Html
							.fromHtml(getString(R.string.vb_item_search_list)));
				} else {
					emptyList.setText(Html.fromHtml(String.format(
							getString(R.string.vb_item_empty_list),
							vbSearchText)));
				}
			}
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
		searchEditText.setFilters(new InputFilter[] { createInputFilter() });

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
					emptyListMsg = null;
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
			vbSearchText = "";
			if (vbList != null) {
				vbList.clear();
			}

			setListAdapterViaSearch();
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

	/**
	 * Create an input filter to limit characters in an EditText (letters,
	 * digits, spaces and enter)
	 * 
	 * @return an input filter
	 */
	private InputFilter createInputFilter() {
		InputFilter inputFilter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {

				// InputFilters are a little complicated in Android versions
				// that display dictionary suggestions. You sometimes get a
				// SpannableStringBuilder, sometimes a plain String in the
				// source parameter
				if (source instanceof SpannableStringBuilder) {
					SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
					for (int i = end - 1; i >= start; i--) {
						char currentChar = source.charAt(i);

						// Check if the charecter has to be removed
						if (!Character.isLetterOrDigit(currentChar)
								&& !Character.isSpaceChar(currentChar)) {
							sourceAsSpannableBuilder.delete(i, i + 1);
						}

						// Check if a search should be performed
						if (currentChar == '\n') {
							performSearch();
						}
					}

					return source;
				} else {
					StringBuilder filteredStringBuilder = new StringBuilder();
					for (int i = start; i < end; i++) {
						char currentChar = source.charAt(i);

						// Check if the charecter should be appended
						if (Character.isLetterOrDigit(currentChar)
								|| Character.isSpaceChar(currentChar)) {
							filteredStringBuilder.append(currentChar);
						}

						// Check if a search should be performed
						if (currentChar == '\n') {
							performSearch();
						}
					}

					return filteredStringBuilder.toString();
				}
			}
		};

		return inputFilter;
	}

}
