package bg.znestorov.sofbus24.utils;

import android.graphics.Color;

public class Constants {

	/**
	 * Global parameters
	 */
	public static final Integer GLOBAL_TAB_COUNT = 4;
	public static final String GLOBAL_PARAM_EMPTY = "EMPTY";

	/**
	 * Preferences
	 */
	public static final String PREFERENCE_KEY_LANGUAGE = "language";
	public static final String PREFERENCE_DEFAULT_VALUE_LANGUAGE = "bg";

	/**
	 * Favorites
	 */
	public static final int FAVOURITES_IMG_BUTTON_ACTION_DOWN = Color.argb(150,
			51, 181, 229);
	public static final int FAVOURITES_IMG_BUTTON_ACTION_UP = Color.argb(0,
			155, 155, 155);
	public static final String FAVOURITES_IMAGE_URL = "https://geo0.ggpht.com/cbk?cb_client=maps_sv.tactile&output=thumbnail&thumb=2&w=500&h=165&yaw=1&pitch=1&ll=%s,%s";
}
