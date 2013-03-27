package bg.znestorov.sofbus24.main;

import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
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
import bg.znestorov.sofbus24.gps.station_choice.VBStationChoiceAdapter;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;

public class VirtualBoardsMapStationChoice extends ListActivity {

	private Context context;
	private List<GPSStation> station_list;
	private GPSStation[] station_array;
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
			station_array = (GPSStation[]) getIntent().getSerializableExtra(
					"Array Of GPSStations");
		} catch (Exception e) {
			station_array = null;
		}

		if (station_array != null) {
			// Transform the Array to an ArrayList
			station_list = Arrays.asList(station_array);

			// Use the SimpleCursorAdapter to show the
			// elements in a ListView
			errorLabel.setText(Html.fromHtml(String
					.format(getString(R.string.gps_station_choice_name))));
			errorLabel.setTypeface(null, Typeface.BOLD);
			setListAdapter(new VBStationChoiceAdapter(context, station_list));
		} else {
			errorLabel.setTextSize(Constants.TEXT_BOX_SIZE
					* TypedValue.COMPLEX_UNIT_DIP);
			errorLabel
					.setText(getString(R.string.gps_station_choice_error_internet));
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		GPSStation station = (GPSStation) getListAdapter().getItem(position);
		String selectedRow = station.getName();
		Toast.makeText(this, selectedRow, Toast.LENGTH_SHORT).show();

		new HtmlRequestSumc().getInformation(context, station.getId(),
				Integer.toString(position + 1), null);
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

}