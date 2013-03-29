package bg.znestorov.sofbus24.utils;

import android.graphics.Color;

public class Constants {

	// Help activity
	public static final String HELP_ACTIVITY = "HELP";

	// GPS Map - time for the status bar
	public static final int TIME_STATUS_BAR_SLIDE_IN = 1000;
	public static final int TIME_STATUS_BAR_SLIDE_OUT = 750;

	// GPS GoogleMaps ROUTE
	public static final int ROUTE_MODE = 2;
	public static final int ROUTE_DEFAULT_COLOR = Color.RED;
	public static final int ROUTE_COLOR = Color.RED;
	public static final int ROUTE_STROKE_WIDTH = 6;
	public static final int ROUTE_ALPHA = 180;

	// GPS times taken through SCHEDULE
	public static final String SCHEDULE_GPS_FIND_CODEO = "&nbsp;спирка&nbsp;";
	public static final String SCHEDULE_GPS_PARAM = "schedule";

	// GPS times taken through GPS Times Google Maps
	public static final String GPS_TIMES_GPS_PARAM = "gpsTimes";

	// GPS times taken through FAVORITES
	public static final String FAVORITES_GPS_PARAM = "favorites";

	// Indicating the needed zoom distance for focusing the current position
	public static final int PADDING_ACTIVE_ZOOM = 50;

	// CONSTANTS FOR RETRIEVING/PROCESSING INFORMATION
	// Setting the size of the TextBox in VirtualBoardStationChoice
	public static final int TEXT_BOX_SIZE = 18;

	// Indicating if there are more than 1 result
	public static final String SEARCH_TYPE_COUNT_RESULTS_1 = "НАМЕРЕНИ СА";
	public static final String SEARCH_TYPE_COUNT_RESULTS_2 = "СПИРКИ ЗА &QUOT;";

	// Needed information for creating body
	public static final String BODY_START = "<div class=\"arrivals\">";
	public static final String BODY_END = "\n</div>";

	// Time retrieval string from the source file
	public static final String TIME_RETRIEVAL_BEGIN = "<b>Информация към ";
	public static final String TIME_RETRIEVAL_END = "</b>";

	// Possible HTML errors while retrieving information
	public static final String ERORR_NONE = "Намерени са";
	public static final String ERROR_NO_INFO_STATION = "В момента нямаме информация. Моля, опитайте по-късно.";
	public static final String ERROR_RETRIEVE_NO_INFO_STATION = "В момента няма информация за тази спирка. Моля опитайте пак по-късно.";
	public static final String ERROR_NO_INFO_NOW = "Няма информация";
	public static final String ERROR_RETRIEVE_NO_INFO_NOW = "В момента няма информация за спирка \"%s\". Моля опитайте пак по-късно.";
	public static final String ERROR_NO_INFO = "Няма намерена информация";
	public static final String ERROR_RETRIEVE_NO_BUS_STOP = "Спирката \"%s\" не съществува.";
	public static final String ERROR_RETRIEVE_NO_STATION_MATCH = "Няма намерени съвпадения за \"%s\".";
	public static final String ERROR_RETRIEVE_NO_DATA = "INCORRECT";

	// Possible HTML errors in TIME_STAMP after processing the information
	public static final String SEARCH_NO_INFO_STATION = "В момента няма информация за тази спирка";
	public static final String SEARCH_NO_INFO_NOW = "В момента няма информация за спирка";
	public static final String SEARCH_NO_BUS_STOP = "не съществува.";
	public static final String SEARCH_NO_STATION_MATCH = "Няма намерени съвпадения";
	public static final String SEARCH_NO_DATA = "INCORRECT";

	// Preferences constants
	public static final String PREFERENCE_KEY_TIME_INFO_RETRIEVAL = "timeInfoRetrieval";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_INFO_RETRIEVAL = "time_skgt";
	public static final String PREFERENCE_KEY_TIME_GPS = "timeGPS";
	public static final boolean PREFERENCE_DEFAULT_VALUE_TIME_GPS = true;
	public static final String PREFERENCE_KEY_TIME_SCHEDULE = "timeSchedule";
	public static final boolean PREFERENCE_DEFAULT_VALUE_TIME_SCHEDULE = true;
	public static final String PREFERENCE_KEY_HOME_SCREEN = "homeScreen";
	public static final String PREFERENCE_DEFAULT_VALUE_HOME_SCREEN = "version_2";
	public static final String PREFERENCE_KEY_EXIT_ALERT = "exitAlert";
	public static final boolean PREFERENCE_DEFAULT_VALUE_EXIT_ALERT = false;
	public static final String PREFERENCE_KEY_SATELLITE = "satellite";
	public static final boolean PREFERENCE_DEFAULT_VALUE_SATELLITE = false;
	public static final String PREFERENCE_KEY_MAP_VIEW = "mapView";
	public static final boolean PREFERENCE_DEFAULT_VALUE_MAP_VIEW = true;
	public static final String PREFERENCE_KEY_COMPASS = "compass";
	public static final boolean PREFERENCE_DEFAULT_VALUE_COMPASS = false;
	public static final String PREFERENCE_KEY_CLOSEST_STATIONS = "closestStations";
	public static final String PREFERENCE_DEFAULT_VALUE_CLOSEST_STATIONS = "8";
	public static final String PREFERENCE_KEY_LANGUAGE = "language";
	public static final String PREFERENCE_DEFAULT_VALUE_LANGUAGE = "bg";
	public static final String PREFERENCE_KEY_POSITION = "position";
	public static final boolean PREFERENCE_DEFAULT_VALUE_POSITION = false;
	public static final String PREFERENCE_KEY_GPS_MAP_FUNCT = "gpsMapFunct";
	public static final String PREFERENCE_DEFAULT_VALUE_GPS_MAP_FUNCT = "funct_1";
}
