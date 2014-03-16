package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

public class MetroScheduleFragment extends ListFragment {

	private Activity context;

	private ArrayList<String> metroScheduleList;

	public MetroScheduleFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_metro_schedule_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Set the arrows to be always on top
		ImageView leftArrow = (ImageView) context
				.findViewById(R.id.metro_schedule_img_left);
		ImageView rightArrow = (ImageView) context
				.findViewById(R.id.metro_schedule_img_right);
		leftArrow.bringToFront();
		rightArrow.bringToFront();

		// Get the MetroStation object from the Bundle
		metroScheduleList = (ArrayList<String>) getArguments().getSerializable(
				Constants.BUNDLE_METRO_SCHEDULE);

		// Convert the List to an Array
		String[] metroScheduleArray = new String[metroScheduleList.size()];
		for (int i = 0; i < metroScheduleList.size(); i++) {
			metroScheduleArray[i] = metroScheduleList.get(i);
		}

		// Set the ListAdapter
		setListAdapter(new ArrayAdapter<String>(context,
				R.layout.activity_metro_schedule_list_item,
				R.id.metro_schedule_item_hour, metroScheduleArray));

		return myFragmentView;
	}
}
