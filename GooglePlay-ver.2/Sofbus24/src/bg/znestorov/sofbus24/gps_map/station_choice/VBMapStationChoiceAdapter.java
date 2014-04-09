package bg.znestorov.sofbus24.gps_map.station_choice;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.station_database.FavouritesDataSource;
import bg.znestorov.sofbus24.station_database.GPSStation;

// Class for creating the vehicles ListView
public class VBMapStationChoiceAdapter extends ArrayAdapter<GPSStation> {

	private final FavouritesDataSource datasource;
	private final Activity context;
	private final List<GPSStation> stations;

	static class ViewHolder {
		TextView stationInfo;
		TextView stationDistance;
		ImageView stationFavorites;
	}

	public VBMapStationChoiceAdapter(Activity context, List<GPSStation> stations) {
		super(context, R.layout.activity_gps_station_choice, stations);
		this.context = context;
		this.stations = stations;
		this.datasource = new FavouritesDataSource(this.context);
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GPSStation station = stations.get(position);

		return setRow(convertView, station);
	}

	// Station row in the ListView
	public View setRow(View convertView, GPSStation station) {
		View rowView = convertView;
		ViewHolder viewHolder;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_gps_map_station_choice_list_row, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.stationInfo = (TextView) rowView
					.findViewById(R.id.station_info);
			viewHolder.stationDistance = (TextView) rowView
					.findViewById(R.id.station_distance);
			viewHolder.stationFavorites = (ImageView) rowView
					.findViewById(R.id.station_favorite);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Create final variable for GPSStation, so can be used in
		// onClickListener
		final GPSStation gpsStation = station;

		// Set image on the imageView
		datasource.open();
		if (datasource.getStation(gpsStation) == null) {
			viewHolder.stationFavorites
					.setImageResource(R.drawable.favorites_empty);
		} else {
			viewHolder.stationFavorites
					.setImageResource(R.drawable.favorites_full);
		}
		datasource.close();

		viewHolder.stationInfo.setText(gpsStation.getName() + " ("
				+ gpsStation.getId() + ")");
		viewHolder.stationDistance.setText(context
				.getString(R.string.gps_map_station_choice_distance)
				+ gpsStation.getTime_stamp()
				+ context.getString(R.string.gps_map_station_choice_meters));

		actionsOverStationFavourites(viewHolder, gpsStation);

		return rowView;
	}

	// Activate the onClickListener over the Favourites ImageView
	private void actionsOverStationFavourites(final ViewHolder viewHolder,
			final GPSStation gpsStation) {
		// Set onClick listener
		viewHolder.stationFavorites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				datasource.open();
				if (datasource.getStation(gpsStation) == null) {
					datasource.createStation(gpsStation);
					viewHolder.stationFavorites
							.setImageResource(R.drawable.favorites_full);
					Toast.makeText(context, R.string.toast_favorites_add,
							Toast.LENGTH_SHORT).show();
				} else {
					datasource.deleteStation(gpsStation);
					viewHolder.stationFavorites
							.setImageResource(R.drawable.favorites_empty);
					Toast.makeText(context, R.string.toast_favorites_remove,
							Toast.LENGTH_SHORT).show();
				}
				datasource.close();
			}
		});
	}
}