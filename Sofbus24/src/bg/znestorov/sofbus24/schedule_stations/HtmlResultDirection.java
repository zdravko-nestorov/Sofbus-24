package bg.znestorov.sofbus24.schedule_stations;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;

// Filling the Direction object with data
public class HtmlResultDirection {

	// URL variables
	private static final String INFO_BEGIN = "<form method=\"get\" action=\"/schedules/vehicle\">";
	private static final String INFO_END = "</form>";
	private static final String DIRECTION_BEGIN = "<div class=\"info\">";
	private static final String DIRECTION_END = "</div>";
	private static final String VAR_BEGIN = "<input type=\"hidden\" value=\"";
	private static final String VT_END = "\" name=\"vt\"/>";
	private static final String LID_END = "\" name=\"lid\"/>";
	private static final String RID_END = "\" name=\"rid\"/>";
	private static final String STOP_BEGIN = "<select name=\"stop\">";
	private static final String STOP_END = "</select>";
	private static final String SPLITTER = "</option>";
	private static final String STOP_ID_BEGIN = "\" value=\"";
	private static final String STOP_ID_END = "\">";

	// The HtmlResult got from HtmlReqeustDirection class
	private final Context context;
	private String htmlResult;
	private String vehicleType;
	private String vehicleNumber;

	// Shared Preferences (option menu)
	private SharedPreferences sharedPreferences;

	public HtmlResultDirection(Context context, String vehicleChoice,
			String htmlResult) {
		this.context = context;
		this.vehicleType = getValueBefore(vehicleChoice, "$");
		this.vehicleNumber = getValueAfter(vehicleChoice, "$");
		this.htmlResult = htmlResult;
	}

	// Checking the HtmlResult and filling Direction's objects
	public ArrayList<Direction> showResult() {
		ArrayList<Direction> directionList = null;
		boolean checkHtmlResult = (htmlResult != null && !"".equals(htmlResult)
				&& htmlResult.contains(INFO_BEGIN)
				&& htmlResult.contains(INFO_END)
				&& htmlResult.contains(DIRECTION_BEGIN)
				&& htmlResult.contains(DIRECTION_END)
				&& htmlResult.contains(VAR_BEGIN)
				&& htmlResult.contains(LID_END) && htmlResult.contains(RID_END)
				&& htmlResult.contains(STOP_BEGIN)
				&& htmlResult.contains(STOP_END)
				&& htmlResult.contains(SPLITTER)
				&& htmlResult.contains(STOP_ID_BEGIN) && htmlResult
				.contains(STOP_ID_END));

		if (checkHtmlResult) {
			directionList = new ArrayList<Direction>(2);
			directionList = setValues(directionList);
		}

		// Get SharedPreferences from option menu
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		// Get "language" value from the Shared Preferences
		String language = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_LANGUAGE,
				Constants.PREFERENCE_DEFAULT_VALUE_LANGUAGE);

		if ("bg".equals(language)) {
			return directionList;
		} else {
			return TranslatorCyrillicToLatin.translateDirection(directionList);
		}
	}

	// Setting values to the Direction object
	private ArrayList<Direction> setValues(ArrayList<Direction> directionList) {
		ArrayList<Direction> list = new ArrayList<Direction>(2);
		int br = 0;

		// String containing both direction of the vehicle
		String[] localResult = new String[2];
		localResult[0] = getValueAfter(htmlResult, INFO_BEGIN);
		localResult[1] = getValueAfter(localResult[0], INFO_BEGIN);
		localResult[0] = getValueBefore(localResult[0], INFO_END);
		localResult[1] = getValueBefore(localResult[1], INFO_END);

		do {
			Direction dir = new Direction();

			dir.setVehicleType(vehicleType);
			dir.setVehicleNumber(vehicleNumber);

			String direction = getValueAfter(localResult[br], DIRECTION_BEGIN);
			direction = getValueBefore(direction, DIRECTION_END);
			dir.setDirection(direction.trim());

			String vt = getValueAfter(localResult[br], VAR_BEGIN);
			String lid = getValueAfter(vt, VAR_BEGIN);
			String rid = getValueAfter(lid, VAR_BEGIN);

			vt = getValueBefore(vt, VT_END);
			dir.setVt(vt.trim());

			lid = getValueBefore(lid, LID_END);
			dir.setLid(lid.trim());

			rid = getValueBefore(rid, RID_END);
			dir.setRid(rid.trim());

			String stops = getValueAfter(localResult[br], STOP_BEGIN);
			stops = getValueBefore(stops, STOP_END);
			String[] stopInfo = stops.split(SPLITTER);
			ArrayList<String> stopName = new ArrayList<String>();
			stopName.add("0");
			ArrayList<String> stopId = new ArrayList<String>();
			stopId.add("0");

			for (int i = 0; i < stopInfo.length - 1; i++) {
				stopName.add(getValueAfter(stopInfo[i], STOP_ID_END).trim());
				stopInfo[i] = getValueAfter(stopInfo[i], STOP_ID_BEGIN);
				stopInfo[i] = getValueBefore(stopInfo[i], STOP_ID_END);
				stopId.add(stopInfo[i].trim());
			}

			dir.setStations(stopName);
			dir.setStop(stopId);

			br++;
			dir.setId(br);

			list.add(dir);
		} while (br < 2);

		return list;
	}

}
