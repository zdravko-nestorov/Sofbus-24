package bg.znestorov.sofbus24.slidingmenu;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Sliding Menu Fragment containing information about the sliding menu
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class SlidingMenuFragment extends ListFragment {

	private Activity context;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_sliding_menu_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Create the SlidingMenu adapter and set it to the ListView
		SlidingMenuAdapter slidingMenuAdapter = new SlidingMenuAdapter(context,
				createMenuItemsList());
		setListAdapter(slidingMenuAdapter);

		return myFragmentView;
	}

	/**
	 * Create a list with size equals to the items in the SlidingMenu
	 * 
	 * @return the list, which will be used in the SlidingMenuAdapter<String>
	 */
	private ArrayList<String> createMenuItemsList() {
		ArrayList<String> menuItems = new ArrayList<String>();

		for (int i = 0; i < Constants.GLOBAL_PARAM_SLIDING_MENU_ITEMS; i++) {
			menuItems.add(i + "");
		}

		return menuItems;
	}

}
