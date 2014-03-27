package bg.znestorov.sofbus24.utils;

import android.graphics.Color;

public class Constants {

	/**
	 * Global parameters
	 */
	public static final Integer GLOBAL_TAB_COUNT = 4;
	public static final String GLOBAL_PARAM_EMPTY = "EMPTY";
	public static final Double GLOBAL_PARAM_SOFIA_CENTER_LATITUDE = 42.696492;
	public static final Double GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE = 23.326011;

	/**
	 * Bundle keys
	 */
	// Sofbus24 --> ClosestStationsList --> ClosestStationsListFragment
	public static final String BUNDLE_CLOSEST_STATIONS_LIST = "CLOSEST STATIONS LIST";
	// MetroFragment --> MetroSchedule --> MetroScheduleFragment
	public static final String BUNDLE_METRO_SCHEDULE = "METRO SCHEDULE";
	// MetroSchedule --> StationMap
	public static final String BUNDLE_STATION_MAP = "STATION MAP";
	// MetroSchedule --> StationRouteMap
	public static final String BUNDLE_STATION_ROUTE_MAP = "STATION ROUTE MAP";

	/**
	 * Preferences
	 */
	public static final String PREFERENCE_KEY_APP_LANGUAGE = "appLanguage";
	public static final String PREFERENCE_DEFAULT_VALUE_APP_LANGUAGE = "bg";
	public static final String PREFERENCE_KEY_FAVOURITES_EXPANDED = "favouritesExpanded";
	public static final boolean PREFERENCE_DEFAULT_VALUE_FAVOURITES_EXPANDED = true;
	public static final String PREFERENCE_KEY_TIME_VEHICLES = "timeVehicles";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_VEHICLES = "timeRemaining";
	public static final String PREFERENCE_KEY_CLOSEST_STATIONS = "closestStations";
	public static final String PREFERENCE_DEFAULT_VALUE_CLOSEST_STATIONS = "8";
	public static final String PREFERENCE_KEY_TIME_INFO_RETRIEVAL = "timeInfoRetrieval";
	public static final String PREFERENCE_DEFAULT_VALUE_TIME_INFO_RETRIEVAL = "time_skgt";

	/**
	 * Favorites
	 */
	public static final int FAVOURITES_IMG_BUTTON_ACTION_DOWN = Color.argb(150,
			51, 181, 229);
	public static final int FAVOURITES_IMG_BUTTON_ACTION_UP = Color.argb(0,
			155, 155, 155);
	public static final String FAVOURITES_IMAGE_URL = "https://geo0.ggpht.com/cbk?cb_client=maps_sv.tactile&output=thumbnail&thumb=2&w=500&h=165&yaw=1&pitch=1&ll=%s,%s";

	/**
	 * Metro Schedule
	 */
	public static final String METRO_STATION_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/MetroSchedule/schedule/Station%s.xml";

	/**
	 * About
	 */
	public static final String CONFIGURATION_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/ConfigData/ConfigurationDetails/configuration.xml";
	public static final String CONFIGURATION_STATIONS_DB_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/ConfigData/Databases/stations.db";
	public static final String CONFIGURATION_VEHICLES_DB_URL = "https://sofia-stations.googlecode.com/svn/HoloDesign-ver.3/ConfigData/Databases/vehicles.db";
	public static final String CONFIGURATION_PREF_NAME = "configuration";
	public static final String CONFIGURATION_PREF_STATIONS_KEY = "CurrentStationsDBVersion";
	public static final String CONFIGURATION_PREF_VEHICLES_KEY = "CurrentVehiclesDBVersion";
}