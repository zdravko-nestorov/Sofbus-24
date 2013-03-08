package bg.znestorov.sofbus24.utils;

import android.graphics.Color;

public class Constants {

	// Indicating if there are more than 1 result
	public static final String SEARCH_TYPE_COUNT_RESULTS_1 = "Õ¿Ã≈–≈Õ» —¿";
	public static final String SEARCH_TYPE_COUNT_RESULTS_2 = "—œ»– » «¿ &QUOT;";

	// Setting the size of the TextBox in VirtualBoardStationChoice
	public static final int TEXT_BOX_SIZE = 18;

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
	public static boolean ROUTE_NO_INTERNET = false;

	// GPS times taken through SCHEDULE
	public static final String SCHEDULE_GPS_FIND_CODEO = "&nbsp;ÒÔËÍ‡&nbsp;";
	public static final String SCHEDULE_GPS_PARAM = "schedule";

	// GPS times taken through FAVORITES
	public static final String FAVORITES_GPS_PARAM = "favorites";

	// Indicating the needed zoom distance for focusing the current position
	public static final int PADDING_ACTIVE_ZOOM = 50;
}
