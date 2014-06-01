package bg.znestorov.sofbus24.edit.tabs;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.entity.Config;
import bg.znestorov.sofbus24.entity.HomeTab;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleFloatViewManager;

/**
 * Fragment used to re-arrange the HomeScreen tabs according to user willings
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class EditTabsFragment extends ListFragment {

	private Activity context;
	private Config config;
	private boolean isReset;

	private EditTabsAdapter editTabsAdapter;

	private static final int DRAG_START_MODE = DragSortController.ON_DOWN;
	private static final boolean REMOVE_ENABLED = false;
	private static final boolean DRAG_ENABLED = true;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				editTabsAdapter.rearrangeView(from, to);
			}
		}
	};

	public static EditTabsFragment newInstance(Config config, boolean isReset) {
		EditTabsFragment editTabsFragment = new EditTabsFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_EDIT_TABS, config);
		bundle.putBoolean(Constants.BUNDLE_EDIT_TABS_RESET, isReset);
		editTabsFragment.setArguments(bundle);

		return editTabsFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		DragSortListView editTabsListView = (DragSortListView) getListView();
		editTabsListView.setDropListener(onDrop);

		SimpleFloatViewManager simpleFloatViewManager = new SimpleFloatViewManager(
				editTabsListView);
		simpleFloatViewManager.setBackgroundColor(Color.TRANSPARENT);
		editTabsListView.setFloatViewManager(simpleFloatViewManager);

		setListAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DragSortListView myFragmentView = (DragSortListView) inflater
				.inflate(R.layout.activity_sofbus24_edit_tabs_fragment,
						container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the configuration object and if the Fragment is started or
		// reset from the Bundle or SavedInstanceState
		if (savedInstanceState == null) {
			config = (Config) getArguments().getSerializable(
					Constants.BUNDLE_EDIT_TABS);
			isReset = getArguments().getBoolean(
					Constants.BUNDLE_EDIT_TABS_RESET);
		} else {
			config = (Config) savedInstanceState
					.getSerializable(Constants.BUNDLE_EDIT_TABS);
			isReset = savedInstanceState
					.getBoolean(Constants.BUNDLE_EDIT_TABS_RESET);
		}

		// Create the DSLV controller and assign to the view (DragSortListView)
		DragSortController mController = buildController(myFragmentView);
		myFragmentView.setFloatViewManager(mController);
		myFragmentView.setOnTouchListener(mController);
		myFragmentView.setDragEnabled(DRAG_ENABLED);

		return myFragmentView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putSerializable(Constants.BUNDLE_EDIT_TABS,
				getNewConfig());
		savedInstanceState.putBoolean(Constants.BUNDLE_EDIT_TABS_RESET, false);
	}

	/**
	 * Create the list adapter and set it to the Fragment ListView
	 */
	private void setListAdapter() {
		ArrayList<HomeTab> homeTabs;

		// Check if the fragment is reset or not
		if (isReset) {
			homeTabs = createDefaultList();
		} else {
			homeTabs = createConfigList();
		}

		editTabsAdapter = new EditTabsAdapter(context, homeTabs);
		setListAdapter(editTabsAdapter);
	}

	/**
	 * Create a List containing the tabs and their visibility according to the
	 * configuration file
	 * 
	 * @return an ArrayList<HomeTabs> with the home tabs, according to the
	 *         configuration file
	 */
	private ArrayList<HomeTab> createConfigList() {
		// Create empty ArrayList<HomeTab>, which contains only null objects
		// (this is workaround as the ArrayList should be ordered at the time of
		// creation)
		ArrayList<HomeTab> homeTabs = new ArrayList<HomeTab>(
				Constants.GLOBAL_PARAM_HOME_TABS_COUNT);

		for (int i = 0; i < Constants.GLOBAL_PARAM_HOME_TABS_COUNT; i++) {
			homeTabs.add(null);
		}

		// Create each home tab according to the configuration
		HomeTab homeTabFavourites = new HomeTab(config.isFavouritesVisibilå(),
				getString(R.string.edit_tabs_favourites),
				config.getFavouritesPosition());
		HomeTab homeTabSearch = new HomeTab(config.isSearchVisibile(),
				getString(R.string.edit_tabs_search),
				config.getSearchPosition());
		HomeTab homeTabSchedule = new HomeTab(config.isScheduleVisibile(),
				getString(R.string.edit_tabs_schedule),
				config.getSchedulePosition());
		HomeTab homeTabMetro = new HomeTab(config.isMetroVisibile(),
				getString(R.string.edit_tabs_metro), config.getMetroPosition());

		// Build the List with the home tabs in the correct ordering
		homeTabs.set(config.getFavouritesPosition(), homeTabFavourites);
		homeTabs.set(config.getSearchPosition(), homeTabSearch);
		homeTabs.set(config.getSchedulePosition(), homeTabSchedule);
		homeTabs.set(config.getMetroPosition(), homeTabMetro);

		return homeTabs;
	}

	/**
	 * Create a default list containing the tabs and their visibility
	 * 
	 * @return a default ArrayList<HomeTabs> with the home tabs
	 */
	private ArrayList<HomeTab> createDefaultList() {
		// Create the home tabs (with their default state)
		HomeTab homeTabFavourites = new HomeTab(true,
				getString(R.string.edit_tabs_favourites), 0);
		HomeTab homeTabSearch = new HomeTab(true,
				getString(R.string.edit_tabs_search), 1);
		HomeTab homeTabSchedule = new HomeTab(true,
				getString(R.string.edit_tabs_schedule), 2);
		HomeTab homeTabMetro = new HomeTab(true,
				getString(R.string.edit_tabs_metro), 3);

		// Build the List with the home tabs in the default ordering
		ArrayList<HomeTab> homeTabs = new ArrayList<HomeTab>();
		homeTabs.add(homeTabFavourites);
		homeTabs.add(homeTabSearch);
		homeTabs.add(homeTabSchedule);
		homeTabs.add(homeTabMetro);

		return homeTabs;
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 * 
	 * @param dragListView
	 *            the listView of the ListFragment
	 * @return a DragSortController with the appropriate settings
	 */
	private DragSortController buildController(DragSortListView dragListView) {
		DragSortController controller = new DragSortController(dragListView);

		controller.setDragHandleId(R.id.edit_tabs_position);
		controller.setRemoveEnabled(REMOVE_ENABLED);
		controller.setDragInitMode(DRAG_START_MODE);

		return controller;
	}

	/**
	 * Get the current Configuration according to the ListView ordering and
	 * checks
	 * 
	 * @return the current configuration
	 */
	public Config getNewConfig() {
		Config currentConfig = new Config(context);

		for (int i = 0; i < Constants.GLOBAL_PARAM_HOME_TABS_COUNT; i++) {
			HomeTab adapterItem = editTabsAdapter.getItem(i);
			String tabName = adapterItem.getTabName();
			int tabPosition = adapterItem.getTabPosition();
			boolean isTabVisible = adapterItem.isTabVisible();

			if (tabName.equals(getString(R.string.edit_tabs_favourites))) {
				currentConfig.setFavouritesPosition(tabPosition);
				currentConfig.setFavouritesVisibilå(isTabVisible);
			} else if (tabName.equals(getString(R.string.edit_tabs_search))) {
				currentConfig.setSearchPosition(tabPosition);
				currentConfig.setSearchVisibile(isTabVisible);
			} else if (tabName.equals(getString(R.string.edit_tabs_schedule))) {
				currentConfig.setSchedulePosition(tabPosition);
				currentConfig.setScheduleVisibile(isTabVisible);
			} else {
				currentConfig.setMetroPosition(tabPosition);
				currentConfig.setMetroVisibile(isTabVisible);
			}
		}

		return currentConfig;
	}
}
