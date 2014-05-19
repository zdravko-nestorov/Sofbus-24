package bg.znestorov.sofbus24.publictransport;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.StationRouteMap;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

/**
 * Metro Fragment containing information about the metro stations
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class PublicTransportFragment extends ListFragment {

	private Activity context;

	private DirectionsEntity ptDirectionsEntity;
	private PublicTransportAdapter ptAdapter;

	private String stationSearchText;
	private static final String BUNDLE_STATION_SEARCH_TEXT = "STATION SEARCH TEXT";

	public static PublicTransportFragment newInstance(
			DirectionsEntity ptDirectionsEntity) {
		PublicTransportFragment publicTransportFragment = new PublicTransportFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
				ptDirectionsEntity);
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

		// Set the context (activity) associated with this fragment
		context = getActivity();

		// Get the Fragment position and MetroStation object from the Bundle
		ptDirectionsEntity = (DirectionsEntity) getArguments()
				.getSerializable(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE);

		// Get the stationSearchText from the Bundle (savedInstanceState)
		if (savedInstanceState != null) {
			stationSearchText = savedInstanceState
					.getString(BUNDLE_STATION_SEARCH_TEXT);
		} else {
			stationSearchText = "";
		}

		// Find all of TextView and SearchEditText tabs in the layout
		SearchEditText searchEditText = (SearchEditText) myFragmentView
				.findViewById(R.id.pt_search);
		TextView emptyList = (TextView) myFragmentView
				.findViewById(R.id.pt_list_empty_text);
		actionsOverSearchEditText(searchEditText, emptyList);

		// Create the ListAdapter
		int activeDirection = ptDirectionsEntity.getActiveDirection();
		ArrayList<Station> directionList = ptDirectionsEntity.getDirectionsList()
				.get(activeDirection);
		ptAdapter = new PublicTransportAdapter(context, directionList);

		// Set the ListAdapter
		setListAdapter(ptAdapter);

		return myFragmentView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(BUNDLE_STATION_SEARCH_TEXT,
				stationSearchText);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_pt_route_map:
			Intent ptMapRouteIntent = new Intent(context, StationRouteMap.class);
			int activeDirection = ptDirectionsEntity.getActiveDirection();
			ArrayList<Station> directionList = ptDirectionsEntity.getDirectionsList()
					.get(activeDirection);
			ptMapRouteIntent.putExtra(Constants.BUNDLE_STATION_ROUTE_MAP,
					directionList);
			this.startActivity(ptMapRouteIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);

		// TODO: Retrieve information about the vehicle

		String toastText = station.getName() + " " + station.getNumber();
		Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 * 
	 * @param searchEditText
	 *            the search EditText
	 */
	private void actionsOverSearchEditText(final SearchEditText searchEditText,
			final TextView emptyList) {
		searchEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		searchEditText.setText(stationSearchText);

		// Add on focus listener
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, searchEditText);
				}
			}
		});

		// Add on text changes listener
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				stationSearchText = searchEditText.getText().toString();
				ptAdapter.getFilter().filter(stationSearchText);

				// Set a message if the list is empty
				if (ptAdapter.isEmpty()) {
					int activeDirection = ptDirectionsEntity.getActiveDirection();
					String directionName = ptDirectionsEntity.getDirectionsNames()
							.get(activeDirection);

					emptyList.setText(Html.fromHtml(String.format(
							getString(R.string.pt_empty_list),
							stationSearchText, directionName)));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});

		// Add a drawable listeners (search and clear icons)
		searchEditText.setDrawableClickListener(new DrawableClickListener() {
			@Override
			public void onClick(DrawablePosition target) {
				switch (target) {
				case LEFT:
					searchEditText.requestFocus();
					ActivityUtils.showKeyboard(context, searchEditText);
					break;
				case RIGHT:
					searchEditText.setText("");
					break;
				default:
					break;
				}
			}
		});
	}

}
