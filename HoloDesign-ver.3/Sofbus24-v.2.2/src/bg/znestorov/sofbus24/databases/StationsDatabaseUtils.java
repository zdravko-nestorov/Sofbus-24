package bg.znestorov.sofbus24.databases;

import android.app.Activity;

/**
 * Class containing all helping functions for creating the Stations DB from an
 * XML file, copying to the SD card, copying the DB from the assets and so on
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class StationsDatabaseUtils {

	/**
	 * Create an empty database on the system and rewrites it with the ready
	 * database
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void createStationsDatabase(Activity context) {
		StationsSQLite myDbHelper = new StationsSQLite(context);
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

}