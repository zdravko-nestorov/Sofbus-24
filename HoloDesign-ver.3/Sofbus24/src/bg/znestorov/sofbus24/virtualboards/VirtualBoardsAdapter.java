package bg.znestorov.sofbus24.virtualboards;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Array Adapted user for set each row a station from the Vehicles DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VirtualBoardsAdapter extends ArrayAdapter<Station> {

	private final FavouritesDataSource favouritesDatasource;
	private final Activity context;
	private final List<Station> stations;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView addToFavourites;
		TextView stationName;
		TextView stationNumber;
	}

	public VirtualBoardsAdapter(Activity context, List<Station> stations) {
		super(context, R.layout.activity_virtual_boards_list_item, stations);
		this.context = context;
		this.stations = stations;
		this.favouritesDatasource = new FavouritesDataSource(context);
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_virtual_boards_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.addToFavourites = (ImageView) rowView
					.findViewById(R.id.vb_item_favourite);
			viewHolder.stationName = (TextView) rowView
					.findViewById(R.id.vb_item_station_name);
			viewHolder.stationNumber = (TextView) rowView
					.findViewById(R.id.vb_item_station_number);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		Station station = stations.get(position);
		viewHolder.addToFavourites.setImageResource(getFavouriteImage(station));
		viewHolder.stationName.setText(station.getName());
		viewHolder.stationNumber.setText(String.format(
				context.getString(R.string.vb_item_station_number_text),
				station.getNumber()));

		// Set the actions over the ImageView
		actionsOverFavouritesImageViews(viewHolder, station);

		return rowView;
	}

	@Override
	public int getCount() {
		return stations == null ? 0 : stations.size();
	}

	/**
	 * Get the favourites image according to this if exists in the Favourites
	 * Database
	 * 
	 * @param station
	 *            the station on the current row
	 * @return the station image id
	 */
	private Integer getFavouriteImage(Station station) {
		Integer favouriteImage;

		favouritesDatasource.open();
		if (favouritesDatasource.getStation(station) == null) {
			favouriteImage = R.drawable.ic_fav_empty;
		} else {
			favouriteImage = R.drawable.ic_fav_full;
		}
		favouritesDatasource.close();

		return favouriteImage;
	}

	/**
	 * Click listeners over the addFavourites image
	 * 
	 * @param viewHolder
	 *            holder containing all elements in the layout
	 * @param station
	 *            the station on the current row
	 */
	public void actionsOverFavouritesImageViews(final ViewHolder viewHolder,
			final Station station) {
		viewHolder.addToFavourites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityUtils.toggleFavouritesStation(context,
						favouritesDatasource, station,
						viewHolder.addToFavourites);
			}
		});
	}
}