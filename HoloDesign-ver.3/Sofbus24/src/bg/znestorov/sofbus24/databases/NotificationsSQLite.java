package bg.znestorov.sofbus24.databases;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Notifications SQLite helper class, responsible for DB life-cycle
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class NotificationsSQLite extends SQLiteOpenHelper {

	// Table and columns names
	public static final String TABLE_NOTIFICATIONS = "notifications";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_INFORMATION = "information";
	public static final String COLUMN_IS_ACTIVE = "is_active";

	// Database name and version
	private static final String DATABASE_NAME = "notifications.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation SQL statement
	private static final String DATABASE_CREATE_NOTIFICATIONS = "CREATE TABLE "
			+ TABLE_NOTIFICATIONS + " (" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
			+ " TEXT NOT NULL, " + COLUMN_INFORMATION + " TEXT NOT NULL, "
			+ COLUMN_IS_ACTIVE + " TEXT NOT NULL" + ");";

	public NotificationsSQLite(Activity context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_NOTIFICATIONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
		database.execSQL(DATABASE_CREATE_NOTIFICATIONS);
	}
}