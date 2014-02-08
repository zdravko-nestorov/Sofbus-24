package bg.znestorov.sofbus24.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;
import bg.znestorov.sofbus24.gps.station_choice.HtmlResultSumcChoice;
import bg.znestorov.sofbus24.gps.station_choice.VBStationChoiceAdapter;
import bg.znestorov.sofbus24.station_database.GPSStation;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

public class VirtualBoardsStationChoice extends ListActivity {

	// Check how many times the activity is started
	public static int countStarts;

	// Check if codeO is present
	public static boolean checkCodeO = false;

	private Context context;
	private String stationCode;
	private String stationName;
	private List<GPSStation> station_list;
	private TextView errorLabel;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;
	private String language;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		countStarts++;

		// Removing title of the window
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set Content View
		setContentView(R.layout.activity_gps_station_choice);

		context = VirtualBoardsStationChoice.this;
		errorLabel = (TextView) findViewById(R.id.station_choice_label);

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.context);

		// Get "language" value from the Shared Preferences
		language = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_LANGUAGE,
				Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);

		String ss_transfer = getIntent().getStringExtra(
				Constants.KEYWORD_HTML_RESULT);

		String htmlSrc = null;

		if (ss_transfer != null && !"".equals(ss_transfer)
				&& !ss_transfer.contains(Constants.SUMC_HTML_ERROR_MESSAGE)
				&& !ss_transfer.contains(Constants.SUMC_CAPTCHA_ERROR_MESSAGE)
				&& !ss_transfer.contains(Constants.SCHEDULE_NO_INFO)
				&& !ss_transfer.contains(Constants.VB_NO_COORDINATES)) {

			String[] tempArray = ss_transfer
					.split(Constants.GLOBAL_PARAM_SEPARATOR);
			stationCode = tempArray[0];
			htmlSrc = tempArray[1];
			stationName = Utils.getStationName(htmlSrc, htmlSrc, stationCode,
					language);

			HtmlResultSumcChoice result = new HtmlResultSumcChoice(context,
					stationCode, htmlSrc);
			station_list = new ArrayList<GPSStation>();
			station_list = result.showResult();
			String time_stamp = station_list.get(0).getTime_stamp();

			// In case of error with the refresh
			if (time_stamp.contains(Constants.SEARCH_ERROR_WITH_REFRESH)) {
				setErrorLabelText(getString(R.string.error_sumc_refresh));
				// Error with the HTML source code (unknown)
			} else if (time_stamp.contains(Constants.SEARCH_NO_DATA)) {
				setErrorLabelText(getString(R.string.gps_station_choice_error_internet));
				// No information found (example: line number 1)
			} else if (time_stamp.contains(Constants.SEARCH_NO_INFO_STATION)) {
				setErrorLabelText(getString(R.string.error_sumc_no_info_station));
				// No such station
			} else if (time_stamp.contains(Constants.SEARCH_NO_BUS_STOP)) {
				// If the station code is not empty
				if (!"".equals(stationCode)) {
					setErrorLabelText(String.format(
							getString(R.string.error_sumc_no_bus_stop),
							stationCode));
				} else {
					setErrorLabelText(getString(R.string.gps_error_noBusEmpty));
				}
				// No results for the selected station
			} else if (time_stamp.contains(Constants.SEARCH_NO_INFO_NOW)) {
				setErrorLabelText(String.format(
						getString(R.string.error_sumc_no_info_now), stationName
								+ " (" + stationCode + ")"));
			} else if (time_stamp.contains(Constants.SEARCH_NO_STATION_MATCH)) {
				setErrorLabelText(String.format(
						getString(R.string.error_sumc_no_station_match),
						stationCode));
			} else {
				// Use the SimpleCursorAdapter to show the
				// elements in a ListView
				errorLabel.setText(Html.fromHtml(String
						.format(getString(R.string.gps_station_choice_name))));
				errorLabel.setTypeface(null, Typeface.BOLD);
				setListAdapter(new VBStationChoiceAdapter(context, station_list));
			}
			// Case this activity is called from StationListView
		} else if (ss_transfer.contains(Constants.SCHEDULE_NO_INFO)) {
			setErrorLabelText(getString(R.string.veh_ch_direction_choice_error_msg));
			// Case this activity is called from VirtualBoards (no coordinates
			// in the DB)
		} else if (ss_transfer.contains(Constants.VB_NO_COORDINATES)) {
			setErrorLabelText(getString(R.string.gps_error_noCoordinates));
			// All other cases (real time information)
		} else {
			setErrorLabelText(getString(R.string.gps_station_choice_error_internet));
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		GPSStation station = (GPSStation) getListAdapter().getItem(position);
		String selectedRow = station.getName().trim();
		Toast.makeText(this, selectedRow, Toast.LENGTH_SHORT).show();

		new HtmlRequestSumc().getInformation(context, stationCode,
				Constants.MULTIPLE_RESULTS_GPS_PARAM, null);
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

	private void setErrorLabelText(String input) {
		errorLabel.setTextSize(Constants.TEXT_BOX_SIZE
				* TypedValue.COMPLEX_UNIT_DIP);
		errorLabel.setText(input);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		countStarts--;

		// Impossible case - just in case
		if (countStarts < 0) {
			countStarts = 0;
		}
	}
}