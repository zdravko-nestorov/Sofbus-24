package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;

import android.graphics.Color;

/**
 * Static class with the constant variables
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Constants {

	/**
	 * Global parameters
	 */
	public static final Integer GLOBAL_TAB_COUNT = 4;
	public static final String GLOBAL_PARAM_EMPTY = "EMPTY";
	public static final Double GLOBAL_PARAM_SOFIA_CENTER_LATITUDE = 42.696492;
	public static final Double GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE = 23.326011;
	public static final BigDecimal GLOBAL_PARAM_CLOSEST_STATION_DISTANCE = new BigDecimal(
			100000);
	public static final int GLOBAL_PARAM_HOME_TABS_COUNT = 4;

	/**
	 * Bundle keys
	 */
	// Sofbus24 --> ClosestStationsList --> ClosestStationsListFragment
	public static final String BUNDLE_CLOSEST_STATIONS_LIST = "CLOSEST STATIONS LIST";
	// EditTabs --> EditTabsFragment
	public static final String BUNDLE_EDIT_TABS = "EDIT TABS";
	public static final String BUNDLE_EDIT_TABS_RESET = "EDIT TABS RESET";
	// VirtualBoardsFragment --> VirtualBoardsTime
	public static final String BUNDLE_VIRTUAL_BOARDS_TIME = "VIRTUAL BOARDS TIME";
	public static final String BUNDLE_VIRTUAL_BOARDS_TIME_EMPTY_LIST = "VIRTUAL BOARDS TIME EMPTY LIST";
	// ScheduleFragment --> PublicTransport --> PublicTransportFragment --> ...
	public static final String BUNDLE_PUBLIC_TRANSPORT_SCHEDULE = "PUBLIC TRANSPORT SCHEDULE";
	// Sofbus24 --> MetroFragment --> MetroStationFragment --> ...
	public static final String BUNDLE_METRO_SCHEDULE = "METRO SCHEDULE";
	// MetroSchedule/PublicTransportSchedule --> StationMap
	public static final String BUNDLE_STATION_MAP = "STATION MAP";
	// MetroFragment/PublicTransport --> StationRouteMap
	public static final String BUNDLE_STATION_ROUTE_MAP = "STATION ROUTE MAP";
	// VirtualBoardsTime --> GoogleStreetView
	public static final String BUNDLE_GOOGLE_STREET_VIEW = "GOOGLE_STREET_VIEW";
	// History --> HistoryFragment
	public static final String BUNDLE_HISTORY_LIST = "HISTORY LIST";

	/**
	 * Preferences
	 */
	public static final String PREFERENCE_KEY_APP_LANGUAGE = "appLanguage";
	public static final String PREFERENCE_DEFAULT_VALUE_APP_LANGUAGE = "bg";
	public static final String PREFERENCE_KEY_FAVOURITES_EXPANDED_CATEGORY = "favouritesExpandedCategory";
	public static final String PREFERENCE_KEY_FAVOURITES_EXPANDED = "favouritesExpanded";
	public static final boolean PREFERENCE_DEFAULT_VALUE_FAVOURITES_EXPANDED = false;
	public static final String PREFERENCE_KEY_TIME_TYPE = "timeType";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_TYPE = "timeRemaining";
	public static final String PREFERENCE_KEY_TIME_SOURCE = "timeSource";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_SOURCE = "timeSkgt";
	public static final String PREFERENCE_KEY_STATIONS_RADIUS = "stationsRadius";
	public static final String PREFERENCE_DEFAULT_VALUE_STATIONS_RADIUS = "600";
	public static final String PREFERENCE_KEY_POSITION_FOCUS = "positionFocus";
	public static final boolean PREFERENCE_DEFAULT_VALUE_POSITION_FOCUS = false;

	/**
	 * Edit Tabs
	 */
	public static final String CONFIGURATION_PREF_FAVOURITES_VISIBILITY_KEY = "FavouritesTabVisibility";
	public static final String CONFIGURATION_PREF_FAVOURITES_POSITION_KEY = "FavouritesTabPosition";
	public static final String CONFIGURATION_PREF_SEARCH_VISIBILITY_KEY = "SearchTabVisibility";
	public static final String CONFIGURATION_PREF_SEARCH_POSITION_KEY = "SearchTabPosition";
	public static final String CONFIGURATION_PREF_SCHEDULE_VISIBILITY_KEY = "ScheduleTabVisibility";
	public static final String CONFIGURATION_PREF_SCHEDULE_POSITION_KEY = "ScheduleTabPosition";
	public static final String CONFIGURATION_PREF_METRO_VISIBILITY_KEY = "MetroTabVisibility";
	public static final String CONFIGURATION_PREF_METRO_POSITION_KEY = "MetroTabPosition";

	/**
	 * About
	 */
	public static final String CONFIGURATION_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/ConfigData/ConfigurationDetails/configuration.xml";
	public static final String CONFIGURATION_STATIONS_DB_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/ConfigData/Databases/stations.db";
	public static final String CONFIGURATION_VEHICLES_DB_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/ConfigData/Databases/vehicles.db";
	public static final String CONFIGURATION_PREF_NAME = "configuration";
	public static final String CONFIGURATION_PREF_STATIONS_KEY = "CurrentStationsDBVersion";
	public static final String CONFIGURATION_PREF_VEHICLES_KEY = "CurrentVehiclesDBVersion";

	/**
	 * Favorites
	 */
	public static final int FAVOURITES_IMG_BUTTON_ACTION_DOWN = Color.argb(150,
			51, 181, 229);
	public static final int FAVOURITES_IMG_BUTTON_ACTION_UP = Color.argb(0,
			155, 155, 155);
	public static final String FAVOURITES_IMAGE_URL = "https://geo0.ggpht.com/cbk?cb_client=maps_sv.tactile&output=thumbnail&thumb=2&w=500&h=165&yaw=1&pitch=1&ll=%s,%s";

	/**
	 * Virtual Boards
	 */
	public static final String VB_PREFERENCES_NAME_SUMC_COOKIES = "sumc_cookies";
	public static final String VB_PREFERENCES_COOKIE_NAME = "name";
	public static final String VB_PREFERENCES_COOKIE_DOMAIN = "domain";
	public static final String VB_PREFERENCES_COOKIE_PATH = "path";
	public static final String VB_PREFERENCES_COOKIE_VALUE = "value";

	public static final String VB_PREFERENCES_NAME_SUMC_HIDDEN_VARIABLES = "sumc_hidden_variables";
	public static final String VB_PREFERENCES_SUMC_HIDDEN_KEY = "hidden_key";
	public static final String VB_PREFERENCES_SUMC_HIDDEN_VALUE = "hidden_value";

	public static final String VB_URL_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1017.2 Safari/535.19";
	public static final String VB_URL_REFERER = "http://m.sofiatraffic.bg/vt/";
	public static final String VB_URL = "http://m.sofiatraffic.bg/vt";
	public static final String VB_URL_STOP_CODE = "stopCode";
	public static final String VB_URL_O = "o";
	public static final String VB_URL_SEC = "sec";
	public static final String VB_URL_VEHICLE_TYPE_ID = "vehicleTypeId";
	public static final String VB_URL_CAPTCHA_TEXT = "sc";
	public static final String VB_URL_CAPTCHA_ID = "poleicngi";
	public static final String VB_URL_GO = "go";
	public static final String VB_URL_SUBMIT = "submit";
	public static final String VB_URL_I = "i";
	public static final String VB_REGEX_HIDDEN_VARIABLE = "<input type=\"hidden\" name=\"sec\"[^^]*?<input type=\"hidden\" name=\"([a-zA-Z0-9]+)\" value=\"([a-zA-Z0-9]+)\"";

	public static final String VB_CAPTCHA_URL = "http://m.sofiatraffic.bg/captcha/%s";
	public static final String VB_CAPTCHA_REQUIRED = "�������� ��������� �� �������������";
	public static final String VB_CAPTCHA_REGEX = "<input name=\"poleicngi\" type=\"hidden\" value=\"(.*?)\"\\/>";

	public static final String VB_REGEX_SCHEDULE_START = "<div class=\"arrivals\">";
	public static final String VB_REGEX_SCHEDULE_BODY = "<div class=\"arrivals\">([^~]*?)\n<\\/div>";

	public static final String VB_REGEX_SKGT_TIME = "<b>���������� ��� (.*?)<\\/b>";
	public static final String VB_REGEX_STATION_INFO = "(.*?)&nbsp;\\(([0-9]{4})\\)&nbsp;";
	public static final String VB_REGEX_MULTIPLE_STATION_INFO = "<input type=\"hidden\" name=\"stopCode\" value=\"([0-9]*)\">\\s+<input type=\"hidden\" name=\"o\" value=\"([0-9]*)\">[^^]*?&nbsp;������&nbsp;([^^]*?)&nbsp;";
	public static final String VB_REGEX_VEHICLE_PARTS = "<div class=\"arr_title_[0-9]{1,}\">";
	public static final String VB_REGEX_VEHICLE_TYPE = "<b>\\n(.*?)<\\/b>";
	public static final String VB_REGEX_VEHICLE_INFO = "<div class=\"arr_info_.*?\">[^^]*?<a href=\".*?\"><b>(.*?)<\\/b><\\/a>&nbsp;-&nbsp;([^^]*?)<br \\/>([^^]*?)<\\/div>";

	public static final String VB_REGEX_VEHICLE_TYPES = "<a\\s*?href=\"#\"\\s*?onClick=\"closethisasap\\('submit[0-9]*?'\\)\">([^~]*?&nbsp;[^~]*?&nbsp;){2}([^~]*?)</a>";
	public static final String VB_VEHICLE_TYPE_BUS = "�������";
	public static final String VB_VEHICLE_TYPE_TROLLEY = "�����";
	public static final String VB_VEHICLE_TYPE_TRAM = "����";

	/**
	 * Schedule direction
	 */
	public static final String SCHECULE_URL_DIRECTION = "http://m.sofiatraffic.bg/schedules?";
	public static final String SCHECULE_URL_DIRECTION_BUS_TYPE = "tt";
	public static final String SCHECULE_URL_DIRECTION_LINE = "ln";
	public static final String SCHECULE_URL_DIRECTION_SEARCH = "s";
	public static final String SCHECULE_URL_DIRECTION_SEARCH_VALUE = "�������";
	public static final String SCHECULE_REGEX_DIRECTION_PARTS = "<form method=\"get\" action=\"/schedules/vehicle\">";
	public static final String SCHECULE_REGEX_DIRECTION_NAME = "<div class=\"info\">(.*?)<\\/div>";
	public static final String SCHECULE_REGEX_DIRECTION_HIDDEN_VARIABLE = "<input type=\"hidden\" value=\"(.*?)\" name=\"%s\"\\/>";
	public static final String SCHECULE_REGEX_DIRECTION_STATION = "<option id=\"(.*?)\" value=\".*?\">([^^]*?)<\\/option>";

	public static final String SCHECULE_URL_STATION_SCHEDULE = "http://m.sofiatraffic.bg/schedules/vehicle?";
	public static final String SCHECULE_URL_STATION_SCHEDULE_STOP = "stop";
	public static final String SCHECULE_URL_STATION_SCHEDULE_CH = "ch";
	public static final String SCHECULE_URL_STATION_SCHEDULE_CH_VALUE = "�������";
	public static final String SCHECULE_URL_STATION_SCHEDULE_VT = "vt";
	public static final String SCHECULE_URL_STATION_SCHEDULE_LID = "lid";
	public static final String SCHECULE_URL_STATION_SCHEDULE_RID = "rid";
	public static final String SCHECULE_URL_STATION_SCHEDULE_H = "h";
	public static final String SCHECULE_URL_STATION_SCHEDULE_H_VALUE = "0";
	public static final String SCHECULE_REGEX_STATION_SCHEDULE = "<td class=\"(schTdBrd|schTdNoBrd)\" align=\"center\">([^^]*?)<\\/td>";

	/**
	 * Metro Schedule
	 */
	public static final String METRO_STATION_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/MetroSchedule/schedule/Station%s.xml";

	/**
	 * History
	 */
	public static final int TOTAL_HISTORY_COUNT = 50;
	public static final String HISTORY_PREFERENCES_NAME = "search_history";
	public static final String HISTORY_PREFERENCES_NEXT_SEARCH_NUMBER = "next_search_number";
	public static final String HISTORY_PREFERENCES_SEARCH_VALUE = "value_";
	public static final String HISTORY_PREFERENCES_SEARCH_DATE = "date_";
	public static final String HISTORY_PREFERENCES_SEARCH_TYPE = "type_";

	/**
	 * Favorites Order
	 */
	public static final String FAVORUITES_ORDER_PREFERENCES_NAME = "favourites_order";
	public static final String FAVOURITES_ORDER_PREFERENCES_TYPE = "type";

	/**
	 * Check For Updates
	 */
	public static final String CHECK_FOR_UPDATES_PREFERENCES_NAME = "application_update";
	public static final String CHECK_FOR_UPDATES_PREFERENCES_LAST_CHECK = "last_check";
}
