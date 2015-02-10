package bg.znestorov.sofbus24.databases;

import android.app.Activity;
import bg.znestorov.sofbus24.schedule.ScheduleCachePreferences;

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
	public static void emptyScheduleDatabase(Activity context) {

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		scheduleDatasource.deleteAllScheduleCache();
		scheduleDatasource.close();
	}

	/**
	 * Delete the files in the cache after the maximum number of days have
	 * passed
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteOldScheduleCache(Activity context) {

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		scheduleDatasource.deleteScheduleCache(ScheduleCachePreferences
				.getNumberOfDays(context));
		scheduleDatasource.close();
	}

}