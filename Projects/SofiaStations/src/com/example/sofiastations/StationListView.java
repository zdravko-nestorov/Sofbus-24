package com.example.sofiastations;

import static com.example.utils.StationCoordinates.getLocation;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.info_station.LoadingStationInfo;
import com.example.schedule_stations.Direction;
import com.example.schedule_stations.DirectionTransfer;
import com.example.schedule_stations.Station;
import com.example.schedule_stations.StationAdapter;

public class StationListView extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = StationListView.this;
		DirectionTransfer directionTransfer;
		try {
			directionTransfer = (DirectionTransfer) getIntent()
					.getSerializableExtra("DirectionTransfer");
		} catch (Exception e) {
			directionTransfer = null;
		}

		ArrayList<Station> stations = new ArrayList<Station>();

		if (directionTransfer.getChoice() == 0) {
			Direction direction = directionTransfer.getDirection1();
			for (int i = 0; i < direction.getStations().size(); i++) {
				stations.add(new Station(direction, i));
			}

			setListAdapter(new StationAdapter(context, stations));
		} else {
			Direction direction = directionTransfer.getDirection2();
			for (int i = 0; i < direction.getStations().size(); i++) {
				stations.add(new Station(direction, i));
			}

			setListAdapter(new StationAdapter(context, stations));
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Station station = (Station) getListAdapter().getItem(position);
		String selectedValue = station.getStation();
		Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

		// Getting the coordinates of the station
		String stationCode = station.getStation();
		if (stationCode.contains("(") && stationCode.contains(")")) {
			stationCode = stationCode.substring(stationCode.indexOf("(") + 1,
					stationCode.indexOf(")"));
		}

		// Getting the HtmlResult and showing a ProgressDialog
		Context context = StationListView.this;
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		String time_stamp;

		try {
			time_stamp = new LoadingStationInfo(station, progressDialog)
					.execute().get();
		} catch (Exception e) {
			time_stamp = null;
		}

		// Getting the coordinates of the station
		String[] coordinates = getLocation(context, stationCode);

		// Creating AlertDialog with the information, if TimeStamp and
		// Coordinates exists, otherwise showing an error message
		Builder dialog = new AlertDialog.Builder(this);

		if (time_stamp != null && !"".equals(time_stamp)
				&& time_stamp.length() > 2 && coordinates != null
				&& !"".equals(coordinates) && coordinates.length > 0) {

			// Fixing the time_stamp
			time_stamp = time_stamp.substring(1, time_stamp.length() - 1);
			station.setTime_stamp(time_stamp);

			// Setting the map coordinates into the station object
			station.setCoordinates(coordinates);

			Bundle bundle = new Bundle();
			Intent stationInfoIntent = new Intent(context, StationInfoMap.class);
			bundle.putSerializable("Station", station);
			stationInfoIntent.putExtras(bundle);
			startActivityForResult(stationInfoIntent, 1);
		} else if (coordinates == null || "".equals(coordinates)
				|| coordinates.length == 0) {
			dialog.setTitle(R.string.veh_ch_direction_choice_error)
					.setMessage(R.string.veh_ch_coordinates_error)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("OK", new OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
						}
					}).show();
		} else {
			dialog.setTitle(R.string.veh_ch_direction_choice_error)
					.setMessage(R.string.veh_ch_direction_choice_error_msg)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("OK", new OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
						}
					}).show();
		}
	}
}