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
import bg.znestorov.sofbus24.entity.HomeTabs;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleFloatViewManager;

public class EditTabsFragment extends ListFragment {

	private Activity context;
	private Config config;

	private EditTabsAdapter editTabsAdapter;

	private static final int DRAG_START_MODE = DragSortController.ON_DOWN;
	private static final boolean REMOVE_ENABLED = false;
	private static final boolean DRAG_ENABLED = true;
	private static final int HOME_TABS_COUNT = 4;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				editTabsAdapter.rearrangeView(from, to);
			}
		}
	};

	public static EditTabsFragment newInstance(Config config) {
		EditTabsFragment editTabsFragment = new EditTabsFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_EDIT_TABS, config);
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

		// Get the Fragment position and MetroStation object from the Bundle
		config = (Config) getArguments().getSerializable(
				Constants.BUNDLE_EDIT_TABS);

		// Create the DSLV controller and assign to the view (DragSortListView)
		DragSortController mController = buildController(myFragmentView);
		myFragmentView.setFloatViewManager(mController);
		myFragmentView.setOnTouchListener(mController);
		myFragmentView.setDragEnabled(DRAG_ENABLED);

		return myFragmentView;
	}

	/**
	 * Create the list adapter and set it to the Fragment ListView
	 */
	private void setListAdapter() {
		ArrayList<HomeTabs> homeTabs = createEmptyArrayList();
		homeTabs.set(
				config.getFavouritesPosition(),
				new HomeTabs(config.isFavouritesVisibilå(),
						getString(R.string.edit_tabs_favourites), config
								.getFavouritesPosition()));
		homeTabs.set(
				config.getSearchPosition(),
				new HomeTabs(config.isSearchVisibile(),
						getString(R.string.edit_tabs_search), config
								.getSearchPosition()));
		homeTabs.set(
				config.getSchedulePosition(),
				new HomeTabs(config.isScheduleVisibile(),
						getString(R.string.edit_tabs_schedule), config
								.getSchedulePosition()));
		homeTabs.set(
				config.getMetroPosition(),
				new HomeTabs(config.isMetroVisibile(),
						getString(R.string.edit_tabs_metro), config
								.getMetroPosition()));

		editTabsAdapter = new EditTabsAdapter(context, homeTabs);
		setListAdapter(editTabsAdapter);
	}

	/**
	 * Create empty ArrayList<HomeTabs>, which contains only null objects (this
	 * is workaround as the ArrayList should be ordered at the time of creation)
	 * 
	 * @return an empty ArrayList<HomeTabs>
	 */
	private ArrayList<HomeTabs> createEmptyArrayList() {
		ArrayList<HomeTabs> homeTabs = new ArrayList<HomeTabs>(HOME_TABS_COUNT);

		for (int i = 0; i < HOME_TABS_COUNT; i++) {
			homeTabs.add(null);
		}

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

		for (int i = 0; i < HOME_TABS_COUNT; i++) {
			HomeTabs adapterItem = editTabsAdapter.getItem(i);
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
