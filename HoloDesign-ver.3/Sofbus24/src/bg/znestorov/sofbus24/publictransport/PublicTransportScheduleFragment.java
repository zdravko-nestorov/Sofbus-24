package bg.znestorov.sofbus24.publictransport;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.entity.ScheduleEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Metro Fragment containing information about the metro stations
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class PublicTransportScheduleFragment extends ListFragment {

	public static PublicTransportScheduleFragment newInstance(
			ScheduleEntity ptScheduleEntity) {
		PublicTransportScheduleFragment publicTransportFragment = new PublicTransportScheduleFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
				ptScheduleEntity);
		publicTransportFragment.setArguments(bundle);

		return publicTransportFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_public_transport_fragment, container, false);

		return myFragmentView;
	}

}
