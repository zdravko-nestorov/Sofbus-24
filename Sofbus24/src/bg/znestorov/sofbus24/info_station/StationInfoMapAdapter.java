package bg.znestorov.sofbus24.info_station;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.schedule_stations.Station;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

// Class for creating the vehicles ListView
public class StationInfoMapAdapter extends ArrayAdapter<Station> {

	private final Context context;
	private final List<Station> stations;

	private SharedPreferences sharedPreferences;

	public StationInfoMapAdapter(Context context, List<Station> stations) {
		super(context, R.layout.activity_gps_station_choice, stations);
		this.context = context;
		this.stations = stations;
	}

	// Creating the elements of the ListView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Station station = stations.get(position);
		View rowView = convertView;

		switch (position) {
		case 0:
			rowView = setRowOne(inflater, parent, station);
			break;
		case 1:
			rowView = setRowTwo(inflater, parent, station);
			break;
		case 2:
			rowView = setRowThree(inflater, parent, station);
			break;
		case 3:
			rowView = setRowFour(inflater, parent, station);
			break;
		default:
			rowView = setRowFour(inflater, parent, station);
			break;
		}

		return rowView;
	}

	// First row
	public View setRowOne(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(
				R.layout.activity_google_map_station_info_list_row, parent,
				false);

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		TextView listTitle = (TextView) rowView.findViewById(R.id.list_title);
		listTitle.setText(station.getVehicleType() + " ¹ "
				+ station.getVehicleNumber());

		TextView listSummary = (TextView) rowView
				.findViewById(R.id.list_summary);
		listSummary.setText(String.format(
				context.getString(R.string.st_inf_time),
				android.text.format.DateFormat.format("dd.MM.yyy, kk:mm",
						new java.util.Date()).toString()));

		ImageView ListImage = (ImageView) rowView.findViewById(R.id.list_image);
		Drawable drawable;
		if (station.getVehicleType().equals(
				context.getString(R.string.title_bus))) {
			drawable = context.getResources().getDrawable(R.drawable.bus_icon);
		} else if (station.getVehicleType().equals(
				context.getString(R.string.title_trolley))) {
			drawable = context.getResources().getDrawable(
					R.drawable.trolley_icon);
		} else {
			drawable = context.getResources().getDrawable(R.drawable.tram_icon);
		}
		ListImage.setImageDrawable(drawable);

		return rowView;
	}

	// First row
	public View setRowTwo(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(
				R.layout.activity_google_map_station_info_list_row, parent,
				false);

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		String stationName = station.getStation();
		String stationCode = Utils.getValueAfter(stationName, "(");
		stationCode = Utils.getValueBefore(stationCode, ")");
		stationName = Utils.getValueBefore(stationName, "(").trim();

		TextView listTitle = (TextView) rowView.findViewById(R.id.list_title);
		listTitle.setText(stationName);

		TextView listSummary = (TextView) rowView
				.findViewById(R.id.list_summary);
		listSummary.setText(context
				.getString(R.string.gps_station_choice_station_code)
				+ Utils.formatNumberOfDigits(stationCode));

		ImageView ListImage = (ImageView) rowView.findViewById(R.id.list_image);
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.st_inf_location);
		ListImage.setImageDrawable(drawable);

		return rowView;
	}

	// First row
	public View setRowThree(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(
				R.layout.activity_google_map_station_info_list_row, parent,
				false);

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		TextView listTitle = (TextView) rowView.findViewById(R.id.list_title);
		listTitle.setText(context.getString(R.string.st_inf_direction));

		TextView listSummary = (TextView) rowView
				.findViewById(R.id.list_summary);
		listSummary.setText(station.getDirection());

		ImageView ListImage = (ImageView) rowView.findViewById(R.id.list_image);
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.st_inf_direction);
		ListImage.setImageDrawable(drawable);

		return rowView;
	}

	// First row
	public View setRowFour(LayoutInflater inflater, ViewGroup parent,
			Station station) {
		View rowView = inflater.inflate(
				R.layout.activity_google_map_station_info_list_row, parent,
				false);

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		TextView listTitle = (TextView) rowView.findViewById(R.id.list_title);

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);

		// Get "exitAlert" value from the Shared Preferences
		String timeSchedule = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_SCHEDULE,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_SCHEDULE);
		String listTitleText = "";

		if ("timeSchedule_remaining".equals(timeSchedule)) {
			listTitleText = context.getString(R.string.st_inf_times_remaining);
		} else {
			listTitleText = context.getString(R.string.st_inf_times_arrival);
		}
		listTitle.setText(listTitleText);

		TextView listSummary = (TextView) rowView
				.findViewById(R.id.list_summary);
		listSummary.setText(station.getTime_stamp());

		ImageView ListImage = (ImageView) rowView.findViewById(R.id.list_image);
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.st_inf_time);
		ListImage.setImageDrawable(drawable);

		return rowView;
	}
}