package bg.znestorov.sofbus24.databases;

import java.io.File;

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

		Sofbus24SQLite myDbHelper = new Sofbus24SQLite(context);
		myDbHelper.createDataBase(null);
		myDbHelper.getWritableDatabase();
		myDbHelper.close();
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
	 * Delete the old databases that were used (Stations and Vehicles)
	 * 
	 * @param context
	 *            the current activity context
	 */
	private static void deleteOldDatabases(Activity context) {
		File stationsDb = new File(DB_PATH + DB_STATIONS_NAME);
		stationsDb.delete();
		stationsDb = new File(DB_PATH + DB_STATIONS_JOURNAL_NAME);
		stationsDb.delete();

		File vehiclesDb = new File(DB_PATH + DB_VEHICLES_NAME);
		vehiclesDb.delete();
		vehiclesDb = new File(DB_PATH + DB_VEHICLES_JOURNAL_NAME);
		vehiclesDb.delete();
	}

}