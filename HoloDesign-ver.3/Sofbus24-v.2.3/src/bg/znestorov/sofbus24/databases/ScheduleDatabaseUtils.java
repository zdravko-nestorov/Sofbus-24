package bg.znestorov.sofbus24.databases;

import android.app.Activity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
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

	/**
	 * Check if the schedule cache is available for the BTT vehicles (buses,
	 * trolleys, trams)
	 * 
	 * @return if the schedule cache is available
	 */
	public static boolean isAnyScheduleCacheAvaialble(Activity context) {

		boolean isScheduleCacheAvailable;

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		isScheduleCacheAvailable = scheduleDatasource
				.isAnyScheduleCacheAvailable();
		scheduleDatasource.close();

		return isScheduleCacheAvailable;
	}

	/**
	 * Delete all schedule cache from the database
	 * 
	 * @return if the schedule cache is successfully deleted
	 */
	public static boolean deleteAllScheduleCache(Activity context) {

		boolean isScheduleCacheDeleted;

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		isScheduleCacheDeleted = scheduleDatasource.deleteAllScheduleCache();
		scheduleDatasource.close();

		return isScheduleCacheDeleted;
	}

	/**
	 * Check if the schedule cache is available for the selected type and number
	 * 
	 * @param dataType
	 *            the selected schedule cache type
	 * @param dataNumber
	 *            the selected schedule cache number
	 * @return if the schedule cache is available
	 */
	public static boolean isVehiclesScheduleCacheAvaialble(Activity context,
			VehicleTypeEnum dataType, String dataNumber) {

		boolean isScheduleCacheAvailable;

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		isScheduleCacheAvailable = scheduleDatasource
				.isVehiclesScheduleCacheAvailable(dataType, dataNumber);
		scheduleDatasource.close();

		return isScheduleCacheAvailable;
	}

	/**
	 * Check if the schedule cache is available for the selected type and number
	 * 
	 * @param dataType
	 *            the selected schedule cache type
	 * @param dataNumber
	 *            the selected schedule cache number
	 * @return if the schedule cache is available
	 */
	public static boolean deleteVehiclesScheduleCache(Activity context,
			VehicleTypeEnum dataType, String dataNumber) {

		boolean isScheduleCacheAvailable;

		ScheduleDataSource scheduleDatasource = new ScheduleDataSource(context);
		scheduleDatasource.open();
		isScheduleCacheAvailable = scheduleDatasource
				.deleteVehiclesScheduleCache(dataType, dataNumber);
		scheduleDatasource.close();

		return isScheduleCacheAvailable;
	}

}