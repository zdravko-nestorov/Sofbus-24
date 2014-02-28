package bg.znestorov.sofbus24.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Favorites SQLite helper class, responsible for DB life-cycle
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class FavouritesSQLite extends SQLiteOpenHelper {

	// Table and columns names
	public static final String TABLE_FAVOURITES = "favourites";
	public static final String COLUMN_NUMBER = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LON = "longitude";
	public static final String COLUMN_CUSTOM_FIELD = "codeo";

	// Database name and version
	private static final String DATABASE_NAME = "favourites.db";
	private static final int DATABASE_VERSION = 2;

	// Database creation SQL statement
	private static final String DATABASE_CREATE_FAVOURITES = "CREATE TABLE "
			+ TABLE_FAVOURITES + "(" + COLUMN_NUMBER + " INTEGER PRIMARY KEY, "
			+ COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_LAT
			+ " TEXT NOT NULL, " + COLUMN_LON + " TEXT NOT NULL, "
			+ COLUMN_CUSTOM_FIELD + " TEXT NOT NULL" + ");";

	public FavouritesSQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_FAVOURITES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FavouritesSQLite.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
		onCreate(db);
	}

}