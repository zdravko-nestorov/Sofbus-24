package bg.znestorov.sofbus24.virtualboards;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Virtual Boards Time Fragment containing information about the vehicles in
 * real time for a choosen station
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VirtualBoardsTimeFragment extends ListFragment {

	private Activity context;

	private VirtualBoardsStation vbTimeStation;
	private VirtualBoardsTimeAdapter vbTimeAdapter;

	public static VirtualBoardsTimeFragment newInstance(
			VirtualBoardsStation vbTimeStation) {
		VirtualBoardsTimeFragment vbTimeFragment = new VirtualBoardsTimeFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_VIRTUAL_BOARDS_TIME,
				vbTimeStation);
		vbTimeFragment.setArguments(bundle);

		return vbTimeFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_virtual_boards_time_fragment, container,
				false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the Fragment position and MetroStation object from the Bundle
		vbTimeStation = (VirtualBoardsStation) getArguments().getSerializable(
				Constants.BUNDLE_VIRTUAL_BOARDS_TIME);

		// Create the ListAdapter
		vbTimeAdapter = new VirtualBoardsTimeAdapter(context, vbTimeStation);

		// Set the ListAdapter
		setListAdapter(vbTimeAdapter);

		return myFragmentView;
	}

}
