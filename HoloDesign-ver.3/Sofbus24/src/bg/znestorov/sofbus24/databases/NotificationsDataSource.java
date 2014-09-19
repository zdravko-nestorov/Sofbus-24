package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.NotificationEntity;

/**
 * Notifications data source class, responsible for all interactions with the
 * notifications database
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NotificationsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private NotificationsSQLite dbHelper;
	private String[] allColumns = { NotificationsSQLite.COLUMN_ID,
			NotificationsSQLite.COLUMN_NAME,
			NotificationsSQLite.COLUMN_INFORMATION,
			NotificationsSQLite.COLUMN_IS_ACTIVE };

	private final static String SEPARATOR = "@";

	public NotificationsDataSource(Activity context) {
		dbHelper = new NotificationsSQLite(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Adding a new NotificationEntity object to the database
	 * 
	 * @param notification
	 *            the input NotificationEntity object
	 * @return the vehicle if it is added successfully and null if already
	 *         exists
	 */
	public NotificationEntity createNotification(NotificationEntity notification) {
		if (getNotification(notification) == null) {
			// Creating a ContentValues object and insert the notification data
			// inside
			ContentValues values = new ContentValues();
			values.put(NotificationsSQLite.COLUMN_NAME, notification.getName());
			values.put(NotificationsSQLite.COLUMN_INFORMATION,
					createNotificationInfo(notification.getInformation()));
			values.put(NotificationsSQLite.COLUMN_IS_ACTIVE, "Y");

			// Insert the ContentValues data into the database
			database.insert(NotificationsSQLite.TABLE_NOTIFICATIONS, null,
					values);

			// Creating new NotificationEntity object and closing the cursor
			NotificationEntity insertedNotification = getNotification(notification);

			return insertedNotification;
		} else {
			return null;
		}
	}

	/**
	 * Adding a new NotificationEntity object to the database
	 * 
	 * @param notificationInformation
	 *            the information of the notification
	 * @return the vehicle if it is added successfully and null if already
	 *         exists
	 */
	public NotificationEntity createNotification(
			String[] notificationInformation) {
		return createNotification(new NotificationEntity(
				notificationInformation));
	}

	/**
	 * Convert the information array to a string object separated by a SEPARATOR
	 * 
	 * @param information
	 *            the information array
	 * @return a string object
	 */
	private String createNotificationInfo(String[] information) {
		StringBuilder informationText = new StringBuilder();
		for (int i = 0; i < information.length; i++) {
			informationText.append(information[i]).append(SEPARATOR);
		}

		return informationText.toString();
	}

	/**
	 * Delete NotificationEntity object from the database
	 * 
	 * @param notification
	 *            the notification object that will be deleted
	 */
	public void deleteNotification(NotificationEntity notification) {
		deleteNotification(notification.getName());
	}

	/**
	 * Delete NotificationEntity object from the database
	 * 
	 * @param notificationName
	 *            the name of the notification
	 */
	public void deleteNotification(String notificationName) {
		String where = NotificationsSQLite.COLUMN_NAME + " = ?";
		String[] whereArgs = new String[] { String.valueOf(notificationName) };

		database.delete(NotificationsSQLite.TABLE_NOTIFICATIONS, where,
				whereArgs);
	}

	/**
	 * Check if a NotificationEntity object exists in the DB
	 * 
	 * @param notificationName
	 *            the name of the notification (in format
	 *            ["Station Number"~"Vehicle Number"]
	 * @return the notification object if it is found in the DB and null
	 *         otherwise
	 */
	public NotificationEntity getNotification(String notificationName) {
		String selection = NotificationsSQLite.COLUMN_NAME + " = ?";
		String[] selectionArgs = new String[] { String
				.valueOf(notificationName) };

		// Selecting the row that contains the notification data
		Cursor cursor = database.query(NotificationsSQLite.TABLE_NOTIFICATIONS,
				allColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating notification object and closing the cursor
			NotificationEntity foundNotification = cursorToNotification(cursor);
			cursor.close();

			return foundNotification;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Check if a NotificationEntity object exists in the DB
	 * 
	 * @param vehicle
	 *            the current NotificationEntity object
	 * @return the notification object if it is found in the DB and null
	 *         otherwise
	 */
	public NotificationEntity getNotification(NotificationEntity notification) {
		String selection = NotificationsSQLite.COLUMN_NAME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(notification
				.getName()) };

		// Selecting the row that contains the notification data
		Cursor cursor = database.query(NotificationsSQLite.TABLE_NOTIFICATIONS,
				allColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating notification object and closing the cursor
			NotificationEntity foundNotification = cursorToNotification(cursor);
			cursor.close();

			return foundNotification;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Get all notifications from the database
	 * 
	 * @return a list with all notifications from the DB
	 */
	public ArrayList<NotificationEntity> getAllNotifications() {
		ArrayList<NotificationEntity> notifications = new ArrayList<NotificationEntity>();

		Cursor cursor = database.query(NotificationsSQLite.TABLE_NOTIFICATIONS,
				allColumns, null, null, null, null, null);

		// Iterating the cursor and fill the empty List<NotificationEntity>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			NotificationEntity notification = cursorToNotification(cursor);
			notifications.add(notification);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return notifications;
	}

	/**
	 * Delete all NotificationEntity objects from the database;
	 */
	public void deleteAllNotifications() {
		database.delete(NotificationsSQLite.TABLE_NOTIFICATIONS, null, null);
	}

	/**
	 * Creating a new NotificationEntity object with the data of the current row
	 * of the database
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the NotificationEntity object on the current row
	 */
	private NotificationEntity cursorToNotification(Cursor cursor) {
		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		String[] information = cursor.getString(2).split(SEPARATOR);
		boolean isActive = "Y".equals(cursor.getString(3)) ? true : false;

		return new NotificationEntity(id, name, information, isActive);
	}
}