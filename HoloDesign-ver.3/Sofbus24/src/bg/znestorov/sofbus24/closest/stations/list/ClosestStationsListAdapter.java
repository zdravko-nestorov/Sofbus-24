package bg.znestorov.sofbus24.closest.stations.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.MapUtils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Array Adapter used to set each row of the ClosestStations Fragment
 *
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class ClosestStationsListAdapter extends ArrayAdapter<StationEntity> {

    private final FavouritesDataSource favouritesDatasource;
    private final Activity context;
    private final LatLng currentLocation;
    private final List<StationEntity> stations;

    public ClosestStationsListAdapter(Activity context, LatLng currentLocation,
                                      List<StationEntity> stations) {
        super(context, R.layout.activity_closest_stations_list_item, stations);
        this.context = context;
        this.currentLocation = currentLocation;
        this.stations = stations;
        this.favouritesDatasource = new FavouritesDataSource(context);
    }

    /**
     * Creating the elements of the ListView
     */
    @Override
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        // Reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(
                    R.layout.activity_closest_stations_list_item, null);

            // Configure view holder
            viewHolder = new ViewHolder();
            viewHolder.addToFavourites = (ImageView) rowView
                    .findViewById(R.id.cs_list_item_favourite);
            viewHolder.stationCaption = (TextView) rowView
                    .findViewById(R.id.cs_list_item_station_caption);
            viewHolder.stationDistance = (TextView) rowView
                    .findViewById(R.id.cs_list_item_station_distance);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        // Fill the data
        StationEntity station = stations.get(position);
        viewHolder.addToFavourites.setImageResource(getFavouriteImage(station));
        viewHolder.stationCaption.setText(Html.fromHtml(String.format(
                station.getName() + " (%s)", station.getNumber())));
        viewHolder.stationDistance.setText(String.format(
                context.getString(R.string.app_distance),
                MapUtils.getMapDistance(context, currentLocation, station)));

        // Set the actions over the ImageView
        actionsOverFavouritesImageViews(viewHolder, station);

        return rowView;
    }

    @Override
    public int getCount() {
        return stations.size();
    }

    /**
     * Get the favorites image according to this if exists in the Favorites
     * Database
     *
     * @param station the station on the current row
     * @return the station image id
     */
    private Integer getFavouriteImage(StationEntity station) {
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
     * @param viewHolder holder containing all elements in the layout
     * @param station    the station on the current row
     */
    public void actionsOverFavouritesImageViews(final ViewHolder viewHolder,
                                                final StationEntity station) {
        viewHolder.addToFavourites.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityUtils.toggleFavouritesStation(context,
                        favouritesDatasource, station,
                        viewHolder.addToFavourites);
            }
        });
    }

    // Used for optimize performance of the ListView
    static class ViewHolder {
        ImageView addToFavourites;
        TextView stationCaption;
        TextView stationDistance;
    }
}