package bg.znestorov.sofbus24.edit.tabs;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import bg.znestorov.sofbus24.entity.Config;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleFloatViewManager;

public class EditTabsFragment extends ListFragment {

	private ArrayAdapter<String> editTabsAdapter;

	private static final int DRAG_START_MODE = DragSortController.ON_DOWN;
	private static final boolean REMOVE_ENABLED = false;
	private static final boolean DRAG_ENABLED = true;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				String item = editTabsAdapter.getItem(from);
				editTabsAdapter.remove(item);
				editTabsAdapter.insert(item, to);
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
		ArrayList<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");

		editTabsAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.activity_sofbus24_edit_tabs_fragment_item,
				R.id.edit_tabs_name, list);
		setListAdapter(editTabsAdapter);
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 * 
	 * @param dragListView
	 *            the listView of the ListFragment
	 * @return a DragSortController with the appropriate settings
	 */
	public DragSortController buildController(DragSortListView dragListView) {
		DragSortController controller = new DragSortController(dragListView);

		controller.setDragHandleId(R.id.edit_tabs_position);
		controller.setRemoveEnabled(REMOVE_ENABLED);
		controller.setDragInitMode(DRAG_START_MODE);

		return controller;
	}

}
