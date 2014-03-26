package bg.znestorov.sofbus24.closest.stations.list;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.gms.maps.model.LatLng;

public class ClosestStationsListFragment extends ListFragment {

	public ClosestStationsListFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_closest_stations_list_fragment, container,
				false);

		Bundle bundle = getArguments();
		LatLng currentLocation = (LatLng) bundle
				.get(Constants.BUNDLE_CLOSEST_STATIONS_LIST);

		return myFragmentView;
	}
}
