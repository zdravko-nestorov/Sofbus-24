package bg.znestorov.sofbus24.favorites;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
			final Station station) {
		View rowView = inflater.inflate(R.layout.activity_favourites_list_item,
				parent, false);

		// Set the station name and number
		TextView stationName = (TextView) rowView
				.findViewById(R.id.favourites_item_station_name);
		TextView stationNumber = (TextView) rowView
				.findViewById(R.id.favourites_item_station_number);

		stationName.setText(station.getName());
		stationNumber.setText(station.getNumber());

		// Attach a click listener to the remove button
		ImageButton removeStation = (ImageButton) rowView
				.findViewById(R.id.favourites_item_remove);
		removeStation.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				favouritesDatasource.deleteStation(station);
			}
		});

		return rowView;
	}
}