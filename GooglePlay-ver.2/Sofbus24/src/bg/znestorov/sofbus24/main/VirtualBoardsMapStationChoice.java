package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.gps_map.station_choice.VBMapStationChoiceAdapter;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;

public class VirtualBoardsMapStationChoice extends ListActivity {

	private Activity context;
	private String station_string;
	private List<GPSStation> station_list = new ArrayList<GPSStation>();
	private TextView errorLabel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Removing title of the window
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set Content View
		setContentView(R.layout.activity_gps_station_choice);

		context = VirtualBoardsMapStationChoice.this;
		errorLabel = (TextView) findViewById(R.id.station_choice_label);

		// Getting the information transfered from StationListView activity
		try {
			station_string = getIntent().getExtras().getString(
					Constants.KEYWORD_CLOSEST_STATIONS);
		} catch (Exception e) {
			station_string = null;
		}

		if (station_string != null) {
			String[] station_array = station_string.split("@");
			for (int i = 0; i < station_array.length; i++) {
				String[] station_data_array = station_array[i].split(",");

				if (station_data_array.length == 3) {
					GPSStation station = new GPSStation();
					station.setName(station_data_array[0]);
					station.setId(station_data_array[1]);
					station.setTime_stamp(station_data_array[2]);

					station_list.add(station);
				}
			}

			if (station_list != null) {
				// Use the SimpleCursorAdapter to show the
				// elements in a ListView
				errorLabel.setText(Html.fromHtml(String
						.format(getString(R.string.gps_station_choice_name))));
				errorLabel.setTypeface(null, Typeface.BOLD);

				Collections.sort(station_list, new Comparator<GPSStation>() {
					public int compare(GPSStation gpsStation1,
							GPSStation gpsStation2) {
						// Get first station distance
						String gpsStation1Distance = gpsStation1
								.getTime_stamp();

						// Get second station distance
						String gpsStation2Distance = gpsStation2
								.getTime_stamp();

						// Compare vehicles' numbers
						try {
							return Double
									.valueOf(gpsStation1Distance)
									.compareTo(
											Double.valueOf(gpsStation2Distance));
						} catch (NumberFormatException e) {
							return 0;
						}
					}
				});

				setListAdapter(new VBMapStationChoiceAdapter(context,
						station_list));
			} else {
				showErrorMessage();
			}
		} else {
			showErrorMessage();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		GPSStation station = (GPSStation) getListAdapter().getItem(position);
		String selectedRow = station.getName();
		Toast.makeText(this, selectedRow, Toast.LENGTH_SHORT).show();

		new HtmlRequestSumc().getInformation(context, station.getId(), "1",
				null);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);

		// Tapped outside so we finish the activity
		if (!dialogBounds.contains((int) event.getX(), (int) event.getY())) {
			this.finish();
		}

		return super.dispatchTouchEvent(event);
	}

	private void showErrorMessage() {
		errorLabel.setTextSize(Constants.TEXT_BOX_SIZE
				* TypedValue.COMPLEX_UNIT_DIP);
		errorLabel
				.setText(getString(R.string.gps_map_station_choice_error_summary));
	}

}