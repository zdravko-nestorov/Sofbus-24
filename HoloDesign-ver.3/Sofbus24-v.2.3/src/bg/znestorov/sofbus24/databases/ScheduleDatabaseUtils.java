package bg.znestorov.sofbus24.databases;

import android.app.Activity;

/**
 * Class containing all helping functions for creating and deleting the Schedule
 * DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleDatabaseUtils {

	/**
	 * Delete all records from the Schedule DB (the DB remains empty - it is not
	 * deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteScheduleDatabase(Activity context) {

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		scheduleDatasource.deleteAllScheduleCache();
		scheduleDatasource.close();
	}

}