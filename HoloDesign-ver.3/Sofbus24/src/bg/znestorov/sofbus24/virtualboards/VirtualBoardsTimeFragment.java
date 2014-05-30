package bg.znestorov.sofbus24.virtualboards;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Virtual Boards Time Fragment containing information about the vehicles in
 * real time for a chosen station
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VirtualBoardsTimeFragment extends ListFragment {

	private Activity context;

	public static VirtualBoardsTimeFragment newInstance(
			VirtualBoardsStation vbTimeStation, String vbTimeEmptyText) {
		VirtualBoardsTimeFragment vbTimeFragment = new VirtualBoardsTimeFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_VIRTUAL_BOARDS_TIME,
				vbTimeStation);
		bundle.putString(Constants.BUNDLE_VIRTUAL_BOARDS_TIME_EMPTY_LIST,
				vbTimeEmptyText);
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

		// Get the empty list TextView
		TextView vbListEmptyTextView = (TextView) myFragmentView
				.findViewById(R.id.vb_list_empty_text);

		// Get the VirtualBoardsStation object and the empty list text from the
		// Bundle
		String vbTimeEmptyText = getArguments().getString(
				Constants.BUNDLE_VIRTUAL_BOARDS_TIME_EMPTY_LIST);
		VirtualBoardsStation vbTimeStation = (VirtualBoardsStation) getArguments()
				.getSerializable(Constants.BUNDLE_VIRTUAL_BOARDS_TIME);

		// Set the ListAdapter
		setListAdapter(vbListEmptyTextView, vbTimeEmptyText, vbTimeStation);

		return myFragmentView;
	}

	/**
	 * Set list adapter and the appropriate text message to it
	 */
	private void setListAdapter(TextView vbListEmptyTextView,
			String vbTimeEmptyText, VirtualBoardsStation vbTimeStation) {
		VirtualBoardsTimeAdapter vbTimeAdapter = new VirtualBoardsTimeAdapter(
				context, vbTimeStation);

		if (vbTimeAdapter.isEmpty()) {
			setListAdapter(null);
			vbListEmptyTextView.setText(Html.fromHtml(vbTimeEmptyText));
		} else {
			setListAdapter(vbTimeAdapter);
		}
	}

}
