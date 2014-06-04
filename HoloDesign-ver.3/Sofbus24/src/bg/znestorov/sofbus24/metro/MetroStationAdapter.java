package bg.znestorov.sofbus24.metro;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.TranslatorLatinToCyrillic;

/**
 * Array Adapted user for set each row a station from the Vehicles DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroStationAdapter extends ArrayAdapter<Station> {

	private Activity context;
	private String language;

	private TextView emptyList;
	private String directionName;

	private List<Station> originalStations;
	private List<Station> filteredStations;

	private Filter stationsFilter;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView stationIcon;
		TextView stationName;
		TextView stationNumber;
	}

	public MetroStationAdapter(Activity context, TextView emptyList,
			String directionName, List<Station> stations) {
		super(context, R.layout.activity_metro_station_list_item, stations);

		this.context = context;
		this.language = LanguageChange.getUserLocale(context);

		this.emptyList = emptyList;
		this.directionName = directionName;

		this.originalStations = stations;
		this.filteredStations = stations;

		this.stationsFilter = createFilter();
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
			rowView = inflater.inflate(R.layout.activity_metro_station_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.stationIcon = (ImageView) rowView
					.findViewById(R.id.metro_item_station_icon);
			viewHolder.stationName = (TextView) rowView
					.findViewById(R.id.metro_item_station_name);
			viewHolder.stationNumber = (TextView) rowView
					.findViewById(R.id.metro_item_station_number);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		Station station = filteredStations.get(position);
		viewHolder.stationIcon.setImageResource(getFavouriteImage(station));
		viewHolder.stationName.setText(station.getName());
		viewHolder.stationNumber.setText(String.format(
				context.getString(R.string.metro_item_station_number_text),
				station.getNumber()));

		return rowView;
	}

	@Override
	public Station getItem(int position) {
		return filteredStations.get(position);
	}

	@Override
	public int getCount() {
		return filteredStations != null ? filteredStations.size() : 0;
	}

	@Override
	public boolean isEmpty() {
		return filteredStations != null ? filteredStations.isEmpty() : false;
	}

	/**
	 * Filter the ListView according some criteria (filter)
	 * 
	 * @return a filter constrains data with a filtering pattern
	 */
	@Override
	public Filter getFilter() {
		if (stationsFilter == null) {
			stationsFilter = createFilter();
		}

		return stationsFilter;
	}

	/**
	 * Create a custom filter, so process the list on searching
	 * 
	 * @return a custom filter
	 */
	private Filter createFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				// If there's nothing to filter on, return the original data for
				// your list
				if (constraint == null || constraint.length() == 0) {
					results.values = originalStations;
					results.count = originalStations.size();
				} else {
					List<Station> filterResultsData = new ArrayList<Station>();

					String filterString = constraint.toString().trim()
							.toUpperCase();
					if ("bg".equals(language)) {
						filterString = TranslatorLatinToCyrillic.translate(
								context, filterString);
					} else {
						filterString = TranslatorCyrillicToLatin.translate(
								context, filterString);
					}

					String filterebaleName;
					String filterebaleNumber;

					// Itterate over all stations and search which ones match
					// the filter
					for (Station station : originalStations) {
						filterebaleName = station.getName().toUpperCase();
						filterebaleNumber = station.getNumber().toUpperCase();

						if (filterebaleName.contains(filterString)
								|| filterebaleNumber.contains(filterString)) {
							filterResultsData.add(station);
						}
					}

					results.values = filterResultsData;
					results.count = filterResultsData.size();
				}

				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults filterResults) {
				filteredStations = (ArrayList<Station>) filterResults.values;
				notifyDataSetChanged();

				if (isEmpty()) {
					emptyList.setText(Html.fromHtml(String.format(
							context.getString(R.string.metro_item_empty_list),
							constraint, directionName)));
				}
			}
		};
	}

	/**
	 * Get the favorites image according to the station type
	 * 
	 * @param station
	 *            the station on the current row
	 * @return the station image id
	 */
	private Integer getFavouriteImage(Station station) {
		Integer favouriteImage;

		switch (station.getType()) {
		case METRO1:
			favouriteImage = R.drawable.ic_metro_1;
			break;
		default:
			favouriteImage = R.drawable.ic_metro_2;
			break;
		}

		return favouriteImage;
	}
}