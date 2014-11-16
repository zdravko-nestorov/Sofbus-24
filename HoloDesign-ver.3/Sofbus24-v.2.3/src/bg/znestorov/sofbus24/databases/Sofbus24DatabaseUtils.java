package bg.znestorov.sofbus24.databases;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;

/**
 * Class containing all helping functions for creating the Stations DB from an
 * XML file, copying to the SD card, copying the DB from the assets and so on
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Sofbus24DatabaseUtils {

	private static String DB_PATH = "//data//data//bg.znestorov.sofbus24.main//databases//";
	private static String DB_STATIONS_NAME = "stations.db";
	private static String DB_STATIONS_JOURNAL_NAME = "stations.db-journal";
	private static String DB_VEHICLES_NAME = "vehicles.db";
	private static String DB_VEHICLES_JOURNAL_NAME = "vehicles.db-journal";

	/**
	 * Delete the old databases (stations.db and vehicles.db) and create an
	 * empty database on the system and rewrites it with the ready database
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void createSofbus24Database(Activity context) {
		deleteOldDatabases(context);

		InputStream dbInputStream = getDatabaseInputStream(context);
		Sofbus24SQLite myDbHelper = new Sofbus24SQLite(context);
		myDbHelper.createDataBase(dbInputStream);
		myDbHelper.getWritableDatabase();
		myDbHelper.close();

		// Delete the new db file after the DB is updated
		if (dbInputStream != null) {
			context.deleteFile(Sofbus24SQLite.DB_NAME);
		}
	}

	/**
	 * Delete all records from the Station DB (the DB remains empty - it is not
	 * deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteStationDatabase(Activity context) {
		StationsDataSource stationsDatasource = new StationsDataSource(context);
		stationsDatasource.open();
		stationsDatasource.deleteAllStations();
		stationsDatasource.close();
	}

	/**
	 * Delete the database file in the current path
	 * 
	 * @param context
	 *            the current Activity context
	 * @param path
	 *            the dabatase file
	 */
	private static void deleteDabatase(Activity context, String path) {
		File dbFile = new File(path);
		dbFile.delete();
	}

	/**
	 * Delete the old databases that were used (Stations and Vehicles)
	 * 
	 * @param context
	 *            the current activity context
	 */
	private static void deleteOldDatabases(Activity context) {
		deleteDabatase(context, DB_PATH + DB_STATIONS_NAME);
		deleteDabatase(context, DB_PATH + DB_STATIONS_JOURNAL_NAME);
		deleteDabatase(context, DB_PATH + DB_VEHICLES_NAME);
		deleteDabatase(context, DB_PATH + DB_VEHICLES_JOURNAL_NAME);
	}

	/**
	 * Get the input dabatase stream (if a dabatase update is found - get the
	 * stream from the Files folder and delete the existing database)
	 * 
	 * @param context
	 *            the current activity context
	 * @return the input database stream
	 */
	private static InputStream getDatabaseInputStream(Activity context) {

		FileInputStream dbInputStream;
		try {
			dbInputStream = context.openFileInput(Sofbus24SQLite.DB_NAME);
			deleteDabatase(context, DB_PATH + Sofbus24SQLite.DB_NAME);
		} catch (Exception e) {
			dbInputStream = null;
		}

		return dbInputStream;
	}

}