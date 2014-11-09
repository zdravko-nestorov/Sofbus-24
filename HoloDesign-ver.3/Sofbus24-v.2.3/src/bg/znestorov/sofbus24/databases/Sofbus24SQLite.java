package bg.znestorov.sofbus24.databases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sofbus24 SQLite helper class, responsible for DB life-cycle (keeping
 * Stations, Vehicles and VehiclesStations DBs)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class Sofbus24SQLite extends SQLiteOpenHelper {

	// 'Stations' table and its columns
	public static final String TABLE_SOF_STAT = "SOF_STAT";
	public static final String COLUMN_PK_STAT_ID = "PK_STAT_ID";
	public static final String COLUMN_STAT_NUMBER = "STAT_NUMBER";
	public static final String COLUMN_STAT_NAME = "STAT_NAME";
	public static final String COLUMN_STAT_LATITUDE = "STAT_LATITUDE";
	public static final String COLUMN_STAT_LONGITUDE = "STAT_LONGITUDE";
	public static final String COLUMN_STAT_TYPE = "STAT_TYPE";

	// 'Vehicle' table and its columns
	public static final String TABLE_SOF_VEHI = "SOF_VEHI";
	public static final String COLUMN_PK_VEHI_ID = "PK_VEHI_ID";
	public static final String COLUMN_VEHI_NUMBER = "VEHI_NUMBER";
	public static final String COLUMN_VEHI_TYPE = "VEHI_TYPE";
	public static final String COLUMN_VEHI_DIRECTION = "VEHI_DIRECTION";

	// 'VehiclesStations' table and its columns
	public static final String TABLE_SOF_VEST = "SOF_VEST";
	public static final String COLUMN_PK_VEST_ID = "PK_VEST_ID";
	public static final String COLUMN_FK_VEST_VEHI_ID = "FK_VEST_VEHI_ID";
	public static final String COLUMN_FK_VEST_STAT_ID = "FK_VEST_STAT_ID";
	public static final String COLUMN_VEST_STOP = "VEST_STOP";
	public static final String COLUMN_VEST_LID = "VEST_LID";
	public static final String COLUMN_VEST_VT = "VEST_VT";
	public static final String COLUMN_VEST_RID = "VEST_RID";

	// The Android's default system path of the database
	private static String DB_PATH = "//data//data//bg.znestorov.sofbus24.main//databases//";
	private static String DB_NAME = "sofbus24.db";
	private static final int DATABASE_VERSION = 1;

	private final Activity context;
	private SQLiteDatabase dbSofbus24;

	/**
	 * The constructor takes and keeps a reference of the passed context in
	 * order to access to the application assets and resources.
	 * 
	 * @param context
	 *            the current context
	 */
	public Sofbus24SQLite(Activity context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		context.deleteDatabase(DB_NAME);
		createDataBase(null);
	}

	@Override
	public synchronized void close() {
		if (dbSofbus24 != null)
			dbSofbus24.close();

		super.close();
	}

	/**
	 * Create an empty database on the system and rewrites it with the ready
	 * database
	 */
	public void createDataBase(InputStream is) {
		// Check if the DB already exists
		boolean dbExist = checkDataBase();

		if (!dbExist) {
			try {
				copyDataBase(is);
			} catch (IOException e) {
				throw new Error("Error copying database: \n" + e.getMessage());
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time the application is opened
	 * 
	 * @return if the DB exists or not
	 */
	private boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);

		return dbFile.exists();
	}

	/**
	 * Copies the stations database from the local assets-folder to the just
	 * created empty database in the system folder, from where it can be
	 * accessed and handled. This is done by transferring ByteStream.
	 * 
	 * @throws IOException
	 */
	private void copyDataBase(InputStream is) throws IOException {
		// Open the local DB as the input stream
		InputStream myInput;
		if (is != null) {
			myInput = is;
		} else {
			myInput = context.getAssets().open(DB_NAME);
		}

		// Create the folder if it is not already created
		File dbFolder = new File(DB_PATH);
		if (!dbFolder.exists()) {
			dbFolder.mkdirs();
		}

		// Path to the just created empty DB
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty DB as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// Transfer the bytes from the InputFile to the OutputFile
		byte[] buffer = new byte[1024];
		int length;

		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the stations DB in read-only mode
	 * 
	 * @return the stations DB
	 * @throws SQLException
	 */
	public SQLiteDatabase openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		dbSofbus24 = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

		return dbSofbus24;
	}
}