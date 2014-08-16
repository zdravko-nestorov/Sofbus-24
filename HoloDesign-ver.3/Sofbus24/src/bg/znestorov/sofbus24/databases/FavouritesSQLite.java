package bg.znestorov.sofbus24.databases;

import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import bg.znestorov.sofbus24.entity.StationEntity;

/**
 * Favorites SQLite helper class, responsible for DB life-cycle
 * 
 * @author Zdravko Nestorov
 * @version 2.0
 */
public class FavouritesSQLite extends SQLiteOpenHelper {

	// Current activity context
	private Activity context;

	// Table and columns names
	public static final String TABLE_FAVOURITES = "favourites";
	public static final String COLUMN_NUMBER = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LON = "longitude";
	public static final String COLUMN_CUSTOM_FIELD = "codeo";
	public static final String COLUMN_DATE_ADDED = "date_added";
	public static final String COLUMN_DATE_LAST_ACCESS = "date_last_access";
	public static final String COLUMN_USAGE_COUNT = "usage_count";
	public static final String COLUMN_POSITION = "position";

	// Database name and version
	private static final String DATABASE_NAME = "favourites.db";
	private static final int DATABASE_VERSION = 3;

	// Database creation SQL statement
	private static final String DATABASE_CREATE_FAVOURITES = "CREATE TABLE "
			+ TABLE_FAVOURITES + "(" + COLUMN_NUMBER + " INTEGER PRIMARY KEY, "
			+ COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_LAT
			+ " TEXT NOT NULL, " + COLUMN_LON + " TEXT NOT NULL, "
			+ COLUMN_CUSTOM_FIELD + " TEXT NOT NULL, " + COLUMN_DATE_ADDED
			+ " TEXT NOT NULL, " + COLUMN_DATE_LAST_ACCESS + " TEXT NOT NULL, "
			+ COLUMN_USAGE_COUNT + " INTEGER NOT NULL, " + COLUMN_POSITION
			+ " INTEGER NOT NULL" + ");";

	public FavouritesSQLite(Activity context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_FAVOURITES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		FavouritesDataSource favoritesDatasorce = new FavouritesDataSource(
				context, database);
		List<StationEntity> favoritesList = favoritesDatasorce.getAllStations();

		database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
		database.execSQL(DATABASE_CREATE_FAVOURITES);

		favoritesDatasorce.createStations(favoritesList);
	}
}