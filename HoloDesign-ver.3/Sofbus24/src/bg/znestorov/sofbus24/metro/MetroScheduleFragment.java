package bg.znestorov.sofbus24.metro;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.entity.MetroFragmentEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Metro Schedule Fragment containing all information about the hours of
 * arriving separated in different rows
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroScheduleFragment extends ListFragment {

	private Activity context;
	private MetroScheduleAdapter metroArrayAdapter;
	private MetroFragmentEntity mfe;

	public static MetroScheduleFragment newInstance(MetroFragmentEntity mfe) {
		MetroScheduleFragment metroScheduleFragment = new MetroScheduleFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_METRO_SCHEDULE, mfe);
		metroScheduleFragment.setArguments(bundle);

		return metroScheduleFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_metro_schedule_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the Fragment position and MetroStation object from the Bundle
		mfe = (MetroFragmentEntity) getArguments().getSerializable(
				Constants.BUNDLE_METRO_SCHEDULE);

		// Create the ListAdapter
		metroArrayAdapter = new MetroScheduleAdapter(context, mfe);

		// Set the ListAdapter
		setListAdapter(metroArrayAdapter);

		return myFragmentView;
	}
}
