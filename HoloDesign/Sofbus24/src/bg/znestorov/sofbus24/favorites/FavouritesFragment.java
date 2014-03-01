package bg.znestorov.sofbus24.favorites;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.Station;
import bg.znestorov.sofbus24.main.R;

/**
 * Favourites fragment responsible for visualizing the items from Favourites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesFragment extends ListFragment {

	private FavouritesDataSource favouritesDatasource;
	private Context context;
	private List<Station> favouritesStations;

	public FavouritesFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_favourites_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();
		favouritesDatasource = new FavouritesDataSource(context);
		favouritesDatasource.open();
		favouritesStations = favouritesDatasource.getAllStations();

		// Use an ArrayAdapter to show the elements in a ListView
		ArrayAdapter<Station> adapter = new FavouritesStationAdapter(context,
				favouritesStations);
		setListAdapter(adapter);

		return myFragmentView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// on click display the item in toast
		Toast.makeText(getActivity(), (String) l.getItemAtPosition(position),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		favouritesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		favouritesDatasource.close();
		super.onPause();
	}
}
