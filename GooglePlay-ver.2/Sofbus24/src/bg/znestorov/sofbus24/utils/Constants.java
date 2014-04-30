package bg.znestorov.sofbus24.utils;

import android.graphics.Color;

public class Constants {

	// GLOBAL PARAMETERS
	public static final String GLOBAL_PARAM_SEPARATOR = "SEPARATOR";
	public static final String GLOBAL_PARAM_EMPTY = "EMPTY";
	public static final String GLOBAL_PARAM_SOFIA_CENTER_LATITUDE = "42.696492";
	public static final String GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE = "23.326011";
	public static final int GLOBAL_TIMEOUT_CONNECTION = 5000;
	public static final int GLOBAL_TIMEOUT_SOCKET = 8000;

	// EXTRA KEYWORDS USED FOR TRANSFERING INFORMATION BETWEEEN ACTIVITIES
	// HtmlRequestSumc -> VirtualBoards/VirtualBoardsStationChoice;
	// HtmlRequestMetroDirection -> VirtualBoardsStationChoice
	public static final String KEYWORD_HTML_RESULT = "HTML_Result";
	// ObtainCurrentCordinates -> VirtualBoardsMapStationChoice
	public static final String KEYWORD_CLOSEST_STATIONS = "Closest_Stations";
	// VirtualBoardsMapGPS/StationTabView -> Help
	public static final String KEYWORD_HELP = "Help";
	// StationTabView -> StationInfoRouteMap
	public final static String KEYWORD_ROUTE_MAP = "Vehicle_Route";
	// VehicleTabView -> VehicleListView
	public static final String KEYWORD_VEHICLE_TYPE = "Vehicle_Type";
	// StationListView -> StationInfoMap
	public static final String KEYWORD_BUNDLE_STATION = "Station";
	// VehicleListView -> StationTabView -> StationListView
	public static final String KEYWORD_BUNDLE_DIRECTION_TRANSFER = "Direction_Transfer";
	// VirtualBoards -> VirtualBoardsMap
	public static final String KEYWORD_BUNDLE_GPS_STATION = "GPS_Station";
	// HtmlRequestMetroDirection -> MetroTabView
	public static final String KEYWORD_BUNDLE_METRO_DIRECTION_TRANSFER = "Metro_Direction_Tranfer";

	// PARAMS WHICH HELPS TO EXTRACT THE METRO STATIONS
	// (CLASS: HtmlRequestMetroDirection)
	// METRO - URL address
	public static final String METRO_SCHEDULE_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/MetroSchedule/schedule/MetroSchedule.xml";
	// Possible HTML errors while retrieving information
	public static final String METRO_INTERNET_PROBLEM = "METRO INTERNET PROBLEM";

	// (CLASS: MetroDirection)
	// The keys in the NAME-URL map
	public static final String METRO_STATION_NAME_KEY = "name";
	public static final String METRO_STATION_URL_KEY = "url";

	// PARAMS WHICH HELP TO EXTRACT THE INFORMATION FROM VIRTUAL BOARDS
	// (CLASS: HtmlRequestSumc)
	// CAPTCHA position in the source file
	public static final String CAPTCHA_START = "<img src=\"/captcha/";
	public static final String CAPTCHA_END = "\"";
	public static final String REQUIRES_CAPTCHA = "Въведете символите от изображението";
	// CAPTCHA URL link
	public static final String CAPTCHA_IMAGE = "http://m.sofiatraffic.bg/captcha/%s";
	// SUMC - URL and variables
	public static final String VB_URL = "http://m.sofiatraffic.bg/vt";
	public static final String QUERY_BUS_STOP_ID = "stopCode";
	public static final String QUERY_O = "o";
	public static final String QUERY_SEC = "sec";
	public static final String QUERY_VEHICLE_TYPE_ID = "vehicleTypeId";
	public static final String QUERY_CAPTCHA_TEXT = "sc";
	public static final String QUERY_CAPTCHA_ID = "poleicngi";
	public static final String QUERY_GO = "go";
	// User Agent and Referrer
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1017.2 Safari/535.19";
	public static final String REFERER = "http://m.sofiatraffic.bg/vt/";
	// SUMC cookies
	public static final String SHARED_PREFERENCES_NAME_SUMC_COOKIES = "sumc_cookies";
	public static final String PREFERENCES_COOKIE_NAME = "name";
	public static final String PREFERENCES_COOKIE_DOMAIN = "domain";
	public static final String PREFERENCES_COOKIE_PATH = "path";
	public static final String PREFERENCES_COOKIE_VALUE = "value";
	// In case of HTML Request error
	public static final String EXCEPTION = "EXCEPTION";
	// Used for extracting the information for all types of vehicles
	public static final String VB_REGEX_SCHEDULE_START = "<div class=\"arrivals\">";
	public static final String VB_REGEX_SCHEDULE_BODY = "<div class=\"arrivals\">([^~]*?)\n<\\/div>";
	public static final String VB_REGEX_VEHICLE_TYPES = "<a\\s*?href=\"#\"\\s*?onClick=\"closethisasap\\('form[0-9]*?'\\)\">([^~]*?&nbsp;[^~]*?&nbsp;){2}([^~]*?)</a>";
	public static final String VB_VEHICLE_TYPE_BUS = "АВТОБУС";
	public static final String VB_VEHICLE_TYPE_TROLLEY = "ТРОЛЕ";
	public static final String VB_VEHICLE_TYPE_TRAM = "ТРАМ";

	// (CLASS: HtmlRequestSumc)
	// GPS times taken through SCHEDULE
	public static final String SCHEDULE_GPS_FIND_CODEO = "&nbsp;спирка&nbsp;";
	public static final String SCHEDULE_GPS_PARAM = "schedule";
	// GPS times taken through GPS Times Google Maps
	public static final String GPS_TIMES_GPS_PARAM = "gpsTimes";
	// GPS times taken through FAVORITES
	public static final String FAVORITES_GPS_PARAM = "favorites";
	// GPS times taken through REFRESH
	public static final String MULTIPLE_RESULTS_GPS_PARAM = "multiple_results";

	// PARAMS WHICH CAUSE MULTIPLE REQUESTS TO THE SUMC SERVER
	// (CLASS: HtmlRequestSumc)
	public static final String TIME_ZONE = "Europe/Sofia";
	public static final int MAX_CONSECUTIVE_REQUESTS_1 = 2;
	public static final int CONSECUTIVE_REQUESTS_START_HOUR_1 = 1;
	public static final int CONSECUTIVE_REQUESTS_END_HOUR_1 = 5;
	public static final int MAX_CONSECUTIVE_REQUESTS_2 = 1;
	public static final int CONSECUTIVE_REQUESTS_START_HOUR_2 = 4;
	public static final int CONSECUTIVE_REQUESTS_END_HOUR_2 = 5;

	// PARAMS WHICH HELP TO DEFINE THE TYPE OF THE VEHICLES
	// (CLASS: HtmlResultSumc)
	// Name of the vehicles in the HTML source
	public static final String VEHICLE_BUS_CHECK = "втоб";
	public static final String VEHICLE_TROLLEY_CHECK = "роле";
	public static final String VEHICLE_TRAM_CHECK = "рамв";
	// Needed information for creating body
	public static final String BODY_START = "<div class=\"arrivals\">";
	public static final String BODY_END = "\n</div>";
	// Needed information for fixing body
	public static final String SUMC_INFO_BEGIN = "<div class=\"arr_info_";
	public static final int SUMC_INFO_BEGIN_LENGTH = (SUMC_INFO_BEGIN + "1\">")
			.length();
	public static final String SUMC_INFO_END = "</div>";
	public static final String SUMC_INFO_SPLITTER = "<a href=\"|\">|<b>|</b>|</a>&nbsp;-&nbsp;|<br />";
	public static final int SUMC_INFO_SPLIT_SIZE = 7;

	// PARAMS WHICH DEFINE THE MAP ROUTE
	// (CLASSES: MapRoute & MapRouteOverlay)
	// URL used for finding the GeoPoints between the START and the END
	public static final String SZ_URL = "http://maps.googleapis.com/maps/api/directions/xml";
	// GPS GoogleMaps ROUTE
	public static final int ROUTE_MODE = 2;
	public static final int ROUTE_DEFAULT_COLOR = Color.RED;
	public static final int ROUTE_COLOR = Color.RED;
	public static final int ROUTE_STROKE_WIDTH = 6;
	public static final int ROUTE_ALPHA = 180;

	// PARAMS WHICH DEFINES THE ERRORS IN THE HTML SOURCE WHILE
	// RETRIEVING/TRANSFERING INFORMATION
	// (CLASS: HtmlResultSumcChoice)
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
	public static final String SEARCH_ERROR_WITH_REFRESH = "REFRESH ERROR";
	public static final String SEARCH_NO_DATA = "INCORRECT";
	// START and END of the needed information
	public static final String MULTIPLE_RESULTS_BEGIN = "<br /><br />";
	public static final String MULTIPLE_RESULTS_END = "</div>";
	// Separators
	public static final String MULTIPLE_RESULTS_SEPARATOR_1 = "&nbsp;спирка&nbsp;";
	public static final String MULTIPLE_RESULTS_SEPARATOR_2 = "</b>";
	public static final String MULTIPLE_RESULTS_SPACE = "&nbsp;";

	// PARAM WHICH DEFINES THE OFFSET/PADDINGS OF THE MAP BALLOON PICTURE
	// (CLASSES: BalloonItemizedOverlay & BalloonOverlayView)
	// Balloon offset
	public static final int BALLOON_VIEW_OFFSET = 13;
	// Balloon paddings
	public static final int BALLOON_PADDING_LEFT = 10;
	public static final int BALLOON_PADDING_TOP = 0;
	public static final int BALLOON_PADDING_RIGHT = 10;
	public static final int BALLOON_PADDING_BOTTOM = 0;

	// PARAMS WHICH HELP TO EXTRACT THE INFORMATION FROM SCHEDULE
	// (CLASS: HtmlRequestStation)
	// URL and variables
	public static final String SCHEDULE_URL = "http://m.sumc.bg/schedules/vehicle?";
	public static final String QUERY_STOP = "stop";
	public static final String QUERY_CHECK = "ch";
	public static final String QUERY_VT = "vt";
	public static final String QUERY_LID = "lid";
	public static final String QUERY_RID = "rid";

	// PARAMS WHICH HELP TO EXTRACT THE INFORMATION FROM SCHEDULE
	// (CLASS: HtmlRequestDirection)
	// URL and variables
	public static final String DIRECTION_URL = "http://m.sofiatraffic.bg/schedules?";
	public static final String QUERY_BUS_TYPE = "tt";
	public static final String QUERY_LINE = "ln";
	public static final String QUERY_SEARCH = "s";

	// PARAMS WHICH HELP TO EXTRACT THE DIRECTION FROM SCHEDULE
	// (CLASS: HtmlResultDirection)
	// HTML source variables
	public static final String INFO_BEGIN = "<form method=\"get\" action=\"/schedules/vehicle\">";
	public static final String INFO_END = "</form>";
	public static final String DIRECTION_BEGIN = "<div class=\"info\">";
	public static final String DIRECTION_END = "</div>";
	public static final String VAR_BEGIN = "<input type=\"hidden\" value=\"";
	public static final String VT_END = "\" name=\"vt\"/>";
	public static final String LID_END = "\" name=\"lid\"/>";
	public static final String RID_END = "\" name=\"rid\"/>";
	public static final String STOP_BEGIN = "<select name=\"stop\">";
	public static final String STOP_END = "</select>";
	public static final String SPLITTER = "</option>";
	public static final String STOP_ID_BEGIN = "\" value=\"";
	public static final String STOP_ID_END = "\">";

	// PARAMS WHICH HELP TO GET THE STATION SCHEDULE
	// (CLASS: HtmlResultStation)
	// MAX count of time schedules from the SKGT site
	public static final int MAX_COUNT_SCHEDULE_TIME = 10;

	// PARAMS WHICH HELP TO EXTRACT STATION ID, NAME AND CODEO FROM HTML SOURCE
	// (CLASS: Utils)
	// HTML source variables
	public static final String STATION_INFO_BEGIN = "<b>спирка";
	public static final String STATION_INFO_END_1 = "</b>";
	public static final String STATION_INFO_END_2 = "(";
	public static final String STATION_INFO_END_3 = ")";
	public static final String STATION_INFO_SEPARATOR_SPACE = "&nbsp;";
	public static final String STATION_INFO_SEPARATOR_BOLD = "<b>";
	public static final String STATION_INFO_SEPARATOR_POINT = ".";

	// PARAMS WHICH DEFINES STATIONS LISTVIEWS
	// (CLASS: StationTabView)
	// Extra info for the ListViews
	public static final String DIRECTION_1 = "DIRECTION_1";
	public static final String DIRECTION_2 = "DIRECTION_2";

	// PARAMS WHICH DEFINES VEHICLES LISTVIEWS
	// (CLASS: VehicleTabView)
	// Extra info for the ListViews
	public static final String VEHICLE_BUS = "BUS";
	public static final String VEHICLE_TROLLEY = "TROLLEY";
	public static final String VEHICLE_TRAM = "TRAM";

	// PARAMS WHICH DEFINES SCHEDULE ERROR
	// (CLASS: StationListView)
	// Extra info for VirtualBoardsStationChoice
	public static final String SCHEDULE_NO_INFO = "SCHEDULE NO INFO";

	// PARAMS WHICH DEFINES THE ERRORS AFTER EXTRACT THE INFORMATION FROM
	// VIRTUAL BOARDS
	// (CLASS: VirtualBoards)
	// Possible errors after extracting the information from SUMC
	public static final String SUMC_HTML_ERROR_MESSAGE = "HTML_ERROR";
	public static final String SUMC_CAPTCHA_ERROR_MESSAGE = "CAPTCHA_ERROR";
	public static final String SUMC_UNKNOWN_INFO = "В момента няма информация за тази спирка";
	// Time retrieval string from the source file
	public static final String TIME_RETRIEVAL_BEGIN = "<b>Информация към ";
	public static final String TIME_RETRIEVAL_END = "</b>";
	// Extra info for VirtualBoardsStationChoice
	public static final String VB_NO_COORDINATES = "VB NO COORDINATES";

	// PARAMS WHICH HELP FOR RETRIEVING/PROCESSING INFORMATION
	// (CLASS: VirtualBoardStationChoice)
	// Indicating if there are more than 1 result
	public static final String SEARCH_TYPE_COUNT_RESULTS_1 = "НАМЕРЕНИ СА";
	public static final String SEARCH_TYPE_COUNT_RESULTS_2 = "СПИРКИ ЗА &QUOT;";
	// Setting the size of the TextBox
	public static final int TEXT_BOX_SIZE = 18;

	// PARAMS WHICH HELP FOR STATUS BAR ANIMATION
	// (CLASS: VirtualBoardsMapGPS)
	// GPS Map - times for the status bar animation
	public static final int TIME_STATUS_BAR_SLIDE_IN = 1000;
	public static final int TIME_STATUS_BAR_SLIDE_OUT = 750;

	// PARAMS WHICH INDICATE THE DIFFERENT USER SETTINGS
	// (CLASS: Preferences)
	// Preferences constants
	public static final String PREFERENCE_KEY_HOME_SCREEN = "homeScreen";
	public static final String PREFERENCE_DEFAULT_VALUE_HOME_SCREEN = "version_2";
	public static final String PREFERENCE_KEY_LANGUAGE = "language";
	public static final String PREFERENCE_DEFAULT_VALUE_LANGUAGE = "bg";
	public static final String PREFERENCE_KEY_GPS_MAP_FUNCT = "gpsMapFunct";
	public static final String PREFERENCE_DEFAULT_VALUE_GPS_MAP_FUNCT = "funct_1";
	public static final String PREFERENCE_KEY_EXIT_ALERT = "exitAlert";
	public static final boolean PREFERENCE_DEFAULT_VALUE_EXIT_ALERT = false;
	public static final String PREFERENCE_KEY_TIME_GPS = "timeGPS_NEW";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_GPS = "timeGPS_remaining";
	public static final String PREFERENCE_KEY_TIME_SCHEDULE = "timeSchedule_NEW";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_SCHEDULE = "timeSchedule_remaining";
	public static final String PREFERENCE_KEY_CLOSEST_STATIONS = "closestStations";
	public static final String PREFERENCE_DEFAULT_VALUE_CLOSEST_STATIONS = "8";
	public static final String PREFERENCE_KEY_TIME_INFO_RETRIEVAL = "timeInfoRetrieval";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_INFO_RETRIEVAL = "time_skgt";
	public static final String PREFERENCE_KEY_MAP_TYPE = "mapType";
	public static final String PREFERENCE_DEFAULT_VALUE_MAP_TYPE = "map_street";
	public static final String PREFERENCE_KEY_COMPASS = "compass";
	public static final boolean PREFERENCE_DEFAULT_VALUE_COMPASS = false;
	public static final String PREFERENCE_KEY_MAP_VIEW = "mapView";
	public static final boolean PREFERENCE_DEFAULT_VALUE_MAP_VIEW = true;
	public static final String PREFERENCE_KEY_POSITION = "position";
	public static final boolean PREFERENCE_DEFAULT_VALUE_POSITION = false;
}
