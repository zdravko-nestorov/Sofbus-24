package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;

/**
 * Vehicles data source class, responsible for all interactions with the
 * vehicles database
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VehiclesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private Sofbus24SQLite dbHelper;
	private String[] allColumns = { Sofbus24SQLite.COLUMN_PK_VEHI_ID,
			Sofbus24SQLite.COLUMN_VEHI_NUMBER, Sofbus24SQLite.COLUMN_VEHI_TYPE,
			Sofbus24SQLite.COLUMN_VEHI_DIRECTION };

	private Activity context;
	private String language;

	public VehiclesDataSource(Activity context) {
		this.context = context;
		dbHelper = new Sofbus24SQLite(context);
		language = LanguageChange.getUserLocale(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Adding a vehicle to the database
	 * 
	 * @param vehicle
	 *            the input vehicle
	 * @return the vehicle if it is added successfully and null if already
	 *         exists
	 */
	public VehicleEntity createVehicle(VehicleEntity vehicle) {
		if (getVehicle(vehicle) == null) {
			// Creating ContentValues object and insert the vehicle data in it
			ContentValues values = new ContentValues();
			values.put(Sofbus24SQLite.COLUMN_VEHI_NUMBER, vehicle.getNumber());
			values.put(Sofbus24SQLite.COLUMN_VEHI_TYPE, vehicle.getType()
					.toString());
			values.put(Sofbus24SQLite.COLUMN_VEHI_DIRECTION,
					vehicle.getDirection());

			// Insert the ContentValues data into the database
			database.insert(Sofbus24SQLite.TABLE_SOF_VEHI, null, values);

			// Selecting the row that contains the vehicle data
			Cursor cursor = database.query(Sofbus24SQLite.TABLE_SOF_VEHI,
					allColumns, Sofbus24SQLite.COLUMN_VEHI_NUMBER + " = "
							+ vehicle.getNumber(), null, null, null, null);

			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating new Vehicle and closing the cursor
			VehicleEntity insertedVehicle = cursorToVehicle(cursor);
			insertedVehicle.setType(vehicle.getType());

			cursor.close();

			return insertedVehicle;
		} else {
			return null;
		}
	}

	/**
	 * Delete vehicle from the database
	 * 
	 * @param vehicle
	 *            the vehicle that will be deleted
	 */
	public void deleteVehicle(VehicleEntity vehicle) {
		String where = Sofbus24SQLite.COLUMN_VEHI_NUMBER + " = ? AND "
				+ Sofbus24SQLite.COLUMN_VEHI_TYPE + " = ?";
		String[] whereArgs = new String[] {
				String.valueOf(vehicle.getNumber()),
				String.valueOf(vehicle.getType()) };

		database.delete(Sofbus24SQLite.TABLE_SOF_VEHI, where, whereArgs);
	}

	/**
	 * Check if a vehicle exists in the DB
	 * 
	 * @param vehicle
	 *            the current vehicle
	 * @return the vehicle if it is found in the DB and null otherwise
	 */
	public VehicleEntity getVehicle(VehicleEntity vehicle) {
		String selection = Sofbus24SQLite.COLUMN_VEHI_NUMBER + " = ? AND "
				+ Sofbus24SQLite.COLUMN_VEHI_TYPE + " = ?";
		String[] selectionArgs = new String[] {
				String.valueOf(vehicle.getNumber()),
				String.valueOf(vehicle.getType()) };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(Sofbus24SQLite.TABLE_SOF_VEHI,
				allColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating vehicle object and closing the cursor
			VehicleEntity foundVehicle = cursorToVehicle(cursor);
			foundVehicle.setType(vehicle.getType());
			cursor.close();

			return foundVehicle;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Get the vehicle direction
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @return the vehicle direction if it is found in the DB and empty string
	 *         otherwise
	 */
	public String getVehicleDirection(VehicleTypeEnum vehicleType) {
		String selection = Sofbus24SQLite.COLUMN_VEHI_TYPE + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(vehicleType) };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(Sofbus24SQLite.TABLE_SOF_VEHI,
				allColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating vehicle object and closing the cursor
			VehicleEntity foundVehicle = cursorToVehicle(cursor);
			String vehicleDirection = foundVehicle.getDirection();
			cursor.close();

			return vehicleDirection;
		} else {
			cursor.close();

			return "";
		}
	}

	/**
	 * Get the vehicles which NUMBER or DIRECTION contains the searched text
	 * 
	 * @param type
	 *            the type of the Vehicle
	 * @param searchText
	 *            the user search text
	 * @return a list with all vehicles matching the input conditions
	 */
	public List<VehicleEntity> getVehiclesViaSearch(VehicleTypeEnum type,
			String searchText) {
		List<VehicleEntity> vehicles = new ArrayList<VehicleEntity>();
		Locale currentLocale = new Locale(language);
		searchText = searchText.toLowerCase(currentLocale);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											");
		query.append(" FROM " + Sofbus24SQLite.TABLE_SOF_VEHI + "			");
		query.append(" WHERE ( 												");
		query.append(" 		lower(CAST(" + Sofbus24SQLite.COLUMN_VEHI_NUMBER
				+ " AS TEXT)) LIKE '%" + searchText + "%'					");
		query.append(" OR 													");
		query.append(" 		lower(" + Sofbus24SQLite.COLUMN_VEHI_DIRECTION
				+ ") LIKE '%" + searchText + "%'		 					");
		query.append(" ) AND												");
		query.append(" 		" + Sofbus24SQLite.COLUMN_VEHI_TYPE + " LIKE '%"
				+ type.toString() + "%'										");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			VehicleEntity vehicle = cursorToVehicle(cursor);
			vehicles.add(vehicle);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return vehicles;
	}

	/**
	 * Delete all vehicles from the database;
	 */
	public void deleteAllVehicles() {
		database.delete(Sofbus24SQLite.TABLE_SOF_VEHI, null, null);
	}

	/**
	 * Creating new Vehicle object with the data of the current row of the
	 * database
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the vehicle object on the current row
	 */
	private VehicleEntity cursorToVehicle(Cursor cursor) {
		VehicleEntity vehicle = new VehicleEntity();

		// Check if have to translate the vehicle direction
		String vehicleDirection = cursor.getString(3);
		if (!"bg".equals(language)) {
			vehicleDirection = TranslatorCyrillicToLatin.translate(context,
					vehicleDirection);
		}

		// Getting all columns of the row and setting them to a Vehicle object
		vehicle.setNumber(cursor.getString(1));
		vehicle.setType(VehicleTypeEnum.valueOf(cursor.getString(2)));
		vehicle.setDirection(vehicleDirection);

		return vehicle;
	}
}