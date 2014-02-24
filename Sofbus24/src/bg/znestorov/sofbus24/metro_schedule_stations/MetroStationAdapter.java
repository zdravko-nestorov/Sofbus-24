package bg.znestorov.sofbus24.metro_schedule_stations;

import java.util.ArrayList;

import android.content.Context;
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
import bg.znestorov.sofbus24.utils.Utils;

// Class for creating the vehicles ListView
public class MetroStationAdapter extends ArrayAdapter<MetroStation> {

	private final FavouritesDataSource datasource;
	private final Context context;
	private final ArrayList<MetroStation> metroStations;

	public MetroStationAdapter(Context context,
			ArrayList<MetroStation> metroStations) {
		super(context, R.layout.activity_vehicle, metroStations);
		this.context = context;
		this.metroStations = metroStations;
		this.datasource = new FavouritesDataSource(this.context);
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		MetroStation ms = metroStations.get(position);
		View rowView = convertView;
		rowView = setDirectionRow(inflater, parent, ms);

		return rowView;
	}

	// Direction row in the ListView
	public View setDirectionRow(LayoutInflater inflater, ViewGroup parent,
			MetroStation ms) {
		View rowView = inflater.inflate(
				R.layout.activity_gps_map_station_choice_list_row, parent,
				false);

		TextView stationInfo = (TextView) rowView
				.findViewById(R.id.station_info);
		TextView stationCode = (TextView) rowView
				.findViewById(R.id.station_distance);

		String stationName = ms.getName();
		String stationNumber = ms.getNumber();

		final GPSStation gpsStation = new GPSStation(stationNumber, stationName);
		final ImageView stationFavorites = (ImageView) rowView
				.findViewById(R.id.station_favorite);

		// Set image on the imageView
		datasource.open();
		if (datasource.getStation(gpsStation) == null) {
			stationFavorites.setImageResource(R.drawable.favorites_empty);
		} else {
			stationFavorites.setImageResource(R.drawable.favorites_full);
		}
		datasource.close();

		stationInfo.setText(stationName);
		stationCode.setText(context
				.getString(R.string.gps_station_choice_station_code)
				+ Utils.formatNumberOfDigits(stationNumber, 4));

		// Set onClick listener
		stationFavorites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				datasource.open();
				if (datasource.getStation(gpsStation) == null) {
					datasource.createStation(gpsStation);
					stationFavorites
							.setImageResource(R.drawable.favorites_full);
					Toast.makeText(context, R.string.toast_favorites_add,
							Toast.LENGTH_SHORT).show();
				} else {
					datasource.deleteStation(gpsStation);
					stationFavorites
							.setImageResource(R.drawable.favorites_empty);
					Toast.makeText(context, R.string.toast_favorites_remove,
							Toast.LENGTH_SHORT).show();
				}
				datasource.close();
			}
		});

		return rowView;
	}
}