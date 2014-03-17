package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.entity.UpdateableFragment;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * Metro Schedule Fragment containing all information about the hours of
 * arriving separated in different rows
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroScheduleFragment extends ListFragment implements
		UpdateableFragment {

	private Activity context;
	private MetroScheduleAdapter metroArrayAdapter;
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
	 * @return an ArrayList containing the current time and the time left
	 */
	private ArrayList<String> formatMetroScheduleList(Activity context) {
		ArrayList<String> formattedMetroScheduleList = new ArrayList<String>();
		String currentTime = DateFormat.format("kk:mm", new java.util.Date())
				.toString();

		for (int i = 0; i < metroScheduleList.size(); i++) {
			String metroScheduleTime = metroScheduleList.get(i);
			String differenceTime = Utils.getDifference(context,
					metroScheduleTime, currentTime);

			if (!"---".equals(differenceTime)) {
				metroScheduleTime = String.format(metroScheduleTime + " (%s)",
						differenceTime);
			}

			formattedMetroScheduleList.add(metroScheduleTime);
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
		metroArrayAdapter = new MetroScheduleAdapter(context,
				formattedMetroScheduleList, isFragmentActive());

		// Set the ListAdapter
		setListAdapter(metroArrayAdapter);
	}

	/**
	 * Check if the current fragment is active
	 * 
	 * @return if the fragment is active
	 */
	private boolean isFragmentActive() {
		boolean isActive = false;

		int currentHour = Integer.parseInt(DateFormat.format("kk",
				new java.util.Date()).toString());
		int firstTimeHour = Integer.parseInt(metroScheduleList.get(0)
				.replaceAll(":.*", ""));

		if (currentHour == firstTimeHour) {
			isActive = true;
		}

		return isActive;
	}
}
