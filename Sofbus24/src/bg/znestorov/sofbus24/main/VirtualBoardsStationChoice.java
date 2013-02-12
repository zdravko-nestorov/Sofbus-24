package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
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
import bg.znestorov.sofbus24.gps.HtmlResultSumcChoice;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.vb_station_choice.VBStationChoiceAdapter;

public class VirtualBoardsStationChoice extends ListActivity {

	private Context context;
	List<GPSStation> station_list;
	TextView errorLabel;

	// Time_Stamp message
	private static final String unknown = "INCORRECT";
	private static final String noInfo = "Няма намерени съвпадения";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Removing title of the window
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set Content View
		setContentView(R.layout.activity_gps_station_choice);

		context = VirtualBoardsStationChoice.this;
		errorLabel = (TextView) findViewById(R.id.station_choice_label);

		String ss_transfer = getIntent().getStringExtra(
				VirtualBoards.keyHtmlResult);

		String stationCode = null;
		String htmlSrc = null;

		if (ss_transfer != null && !"".equals(ss_transfer)
				&& !ss_transfer.contains(VirtualBoards.htmlErrorMessage)
				&& !ss_transfer.contains(VirtualBoards.captchaErrorMessage)) {

			String[] tempArray = ss_transfer.split("SEPARATOR");
			stationCode = tempArray[0];
			htmlSrc = tempArray[1];

			HtmlResultSumcChoice result = new HtmlResultSumcChoice(stationCode,
					htmlSrc);
			station_list = new ArrayList<GPSStation>();
			station_list = result.showResult();
			String time_stamp = station_list.get(0).getTime_stamp();

			// Error with the HTML source code (unknown)
			if (time_stamp.contains(unknown)) {
				errorLabel.setTextSize(Constants.TEXT_BOX_SIZE
						* TypedValue.COMPLEX_UNIT_DIP);
				errorLabel
						.setText(getString(R.string.gps_station_choice_error_internet));
			} else if (time_stamp.contains(noInfo)) {
				errorLabel.setTextSize(Constants.TEXT_BOX_SIZE
						* TypedValue.COMPLEX_UNIT_DIP);
				errorLabel.setText(time_stamp);
			} else {
				// Use the SimpleCursorAdapter to show the
				// elements in a ListView
				errorLabel.setText(Html.fromHtml(String
						.format(getString(R.string.gps_station_choice_name))));
				errorLabel.setTypeface(null, Typeface.BOLD);
				setListAdapter(new VBStationChoiceAdapter(context, station_list));
			}
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

		HtmlRequestSumc sumc = new HtmlRequestSumc();

		// In case that the StationID is contained in some StationNAME
		boolean flag = false;
		for (int i = 0; i < station_list.size(); i++) {
			if (station.getName().contains(station.getId())) {
				flag = true;
				break;
			}
		}

		if (flag) {
			sumc.getInformation(context, station.getId(), station.getName(),
					null);
		} else {
			sumc.getInformation(context, station.getId(), station.getId(), null);
		}
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