package bg.znestorov.sofbus24.utils;

import android.graphics.Color;

public class Constants {

	// Defining the search type - name or number
	// (connection with "HtmlRequestSumc" constructor)
	public static final String SEARCH_TYPE_NUMBER = "NUMBER";
	public static final String SEARCH_TYPE_NAME = "NAME";
	public static final String SEARCH_TYPE_COUNT_RESULTS_1 = "Õ¿Ã≈–≈Õ» —¿";
	public static final String SEARCH_TYPE_COUNT_RESULTS_2 = "—œ»– » «¿ &QUOT;";
	public static boolean SEARCH_TYPE_FLAG = false;

	// Setting the size of the TextBox in VirtualBoardStationChoice
	public static final int TEXT_BOX_SIZE = 18;

	// Help activity
	public static final String HELP_ACTIVITY = "HELP";

	// GPS Map - time for the status bar
	public static final int TIME_STATUS_BAR_SLIDE_IN = 1000;
	public static final int TIME_STATUS_BAR_SLIDE_OUT = 750;

	// Action Bar
	public static final int ACTION_BAR_LABEL_SIZE = 26;

	// GPS GoogleMaps ROUTE
	public static final int ROUTE_MODE = 2;
	public static final int ROUTE_DEFAULT_COLOR = Color.RED;
	public static final int ROUTE_COLOR = Color.RED;
	public static final int ROUTE_STROKE_WIDTH = 6;
	public static final int ROUTE_ALPHA = 180;
	public static boolean ROUTE_NO_INTERNET = false;

}
