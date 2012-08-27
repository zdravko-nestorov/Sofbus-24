package com.example.station_database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class StationsSQLite extends SQLiteOpenHelper {

	// Table and columns names
	public static final String TABLE_STATIONS = "stations";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LON = "longitude";

	// The Android's default system path of the database
	private static String DB_PATH = "//data//data//com.example.sofiastations//databases//";
	private static String DB_NAME = "stations.db";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	// Constructor Takes and keeps a reference of the passed context in order to
	// access to the application assets and resources.
	public StationsSQLite(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	// Create an empty database on the system and rewrites it with the ready
	// database.
	public void createDataBase() throws IOException {
		// Check if the DB already exists
		boolean dbExist = checkDataBase();

		if (!dbExist) {
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	// Check if the database already exist to avoid re-copying the file each
	// time the application is opened
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	// Copies your database from your local assets-folder to the just created
	// empty database in the system folder, from where it can be accessed and
	// handled. This is done by transferring ByteStream.
	private void copyDataBase() throws IOException {
		// Open the local DB as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

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

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}