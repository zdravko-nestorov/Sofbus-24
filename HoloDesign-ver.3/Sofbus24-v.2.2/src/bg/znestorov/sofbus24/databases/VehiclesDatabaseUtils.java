package bg.znestorov.sofbus24.databases;

import android.content.Context;

/**
 * Class containing all helping functions for creating the Vehicles DB from an
 * XML file, copying to the SD card, copying the DB from the assets and so on
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VehiclesDatabaseUtils {

	/**
	 * Create an empty database on the system and rewrites it with the ready
	 * database
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void createVehiclesDatabase(Context context) {
		VehiclesSQLite myDbHelper = new VehiclesSQLite(context);
		myDbHelper.createDataBase(null);
		myDbHelper.getWritableDatabase();
		myDbHelper.close();
	}

	/**
	 * Delete all records from the Vehicles DB (the DB remains empty - it is not
	 * deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteVehiclesDatabase(Context context) {
		VehiclesDataSource vehiclesDatasource = new VehiclesDataSource(context);
		vehiclesDatasource.open();
		vehiclesDatasource.deleteAllVehicles();
		vehiclesDatasource.close();
	}

}