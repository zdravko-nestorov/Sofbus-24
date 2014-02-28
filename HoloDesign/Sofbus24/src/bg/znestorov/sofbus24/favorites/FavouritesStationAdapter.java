package bg.znestorov.sofbus24.favorites;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.Station;
import bg.znestorov.sofbus24.main.R;

/**
 * Array Adapted user for set each row a station from the Favourites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesStationAdapter extends ArrayAdapter<Station> {

	private final FavouritesDataSource favouritesDatasource;
	private final Context context;
	private final List<Station> stations;

	public FavouritesStationAdapter(Context context, List<Station> stations) {
		super(context, R.layout.activity_favourites_list_item, stations);
		this.context = context;
		this.stations = stations;
		this.favouritesDatasource = new FavouritesDataSource(context);
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Station station = stations.get(position);
		View rowView = convertView;
		rowView = setFavouritesRow(inflater, parent, station);

		return rowView;
	}

	// Direction row in the ListView
	public View setFavouritesRow(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(R.layout.activity_favourites_list_item,
				parent, false);

		favouritesDatasource.close();

		return rowView;
	}
}