package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

public class MetroScheduleFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;
	private ArrayAdapter<String> metroArrayAdapter;
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

		// Get the Fragment position and MetroStation object from the Bundle
		metroScheduleList = (ArrayList<String>) getArguments().getSerializable(
				Constants.BUNDLE_METRO_SCHEDULE);

		// Set the fragment content
		setListFragmentContent(context);

		return myFragmentView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Activity context) {
		if (this.context == null) {
			this.context = context;
		}

		if (metroScheduleList == null || metroScheduleList.isEmpty()) {
			metroScheduleList = (ArrayList<String>) getArguments()
					.getSerializable(Constants.BUNDLE_METRO_SCHEDULE);
		}

		setListFragmentContent(context);
	}

	/**
	 * Find the difference between the current time and the metro schedule time
	 * and create new list containing both
	 * 
	 * @param context
	 *            the current Activity context
	 * @return an ArrayList containg the current time and the time left
	 */
	private ArrayList<String> formatMetroScheduleList(Activity context) {
		ArrayList<String> formattedMetroScheduleList = new ArrayList<String>();
		String currentTime = DateFormat.format("kk:mm", new java.util.Date())
				.toString();

		for (int i = 0; i < metroScheduleList.size(); i++) {
			String metroScheduleTime = metroScheduleList.get(i);
			String differenceTime = Utils.getDifference(context,
					metroScheduleTime, currentTime);

			formattedMetroScheduleList.add(String.format(metroScheduleTime
					+ " (%s)", differenceTime));
		}

		return formattedMetroScheduleList;
	}

	/**
	 * Set the Fragment content
	 * 
	 * @param context
	 *            the current Activity context
	 */
	private void setListFragmentContent(Activity context) {
		ArrayList<String> formattedMetroScheduleList = formatMetroScheduleList(context);

		// Empty the ListAdapter
		if (metroArrayAdapter != null && !metroArrayAdapter.isEmpty()) {
			metroArrayAdapter.clear();
		}

		// Create the ListAdapter
		metroArrayAdapter = new ArrayAdapter<String>(context,
				R.layout.activity_metro_schedule_list_item,
				R.id.metro_schedule_item_hour, formattedMetroScheduleList);

		// Set the ListAdapter
		setListAdapter(metroArrayAdapter);
	}
}
