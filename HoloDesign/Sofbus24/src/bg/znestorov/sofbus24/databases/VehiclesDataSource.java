package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;

public class VehiclesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private VehiclesSQLite dbHelper;
	private String[] allColumns = { VehiclesSQLite.COLUMN_NUMBER,
			VehiclesSQLite.COLUMN_TYPE, VehiclesSQLite.COLUMN_DIRECTION };

	private Context context;
	private String language;

	public VehiclesDataSource(Context context) {
		this.context = context;
		dbHelper = new VehiclesSQLite(context);
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
	public Vehicle createVehicle(Vehicle vehicle) {
		if (getVehicle(vehicle) == null) {
			// Creating ContentValues object and insert the vehicle data in it
			ContentValues values = new ContentValues();
			values.put(VehiclesSQLite.COLUMN_NUMBER, vehicle.getNumber());
			values.put(VehiclesSQLite.COLUMN_TYPE, vehicle.getType().toString());
			values.put(VehiclesSQLite.COLUMN_DIRECTION, vehicle.getDirection());

			// Insert the ContentValues data into the database
			database.insert(VehiclesSQLite.TABLE_VEHICLES, null, values);

			// Selecting the row that contains the vehicle data
			Cursor cursor = database.query(VehiclesSQLite.TABLE_VEHICLES,
					allColumns,
					VehiclesSQLite.COLUMN_NUMBER + " = " + vehicle.getNumber(),
					null, null, null, null);

			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating new Vehicle and closing the cursor
			Vehicle insertedVehicle = cursorToVehicle(cursor);
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
	public void deleteVehicle(Vehicle vehicle) {
		String where = VehiclesSQLite.COLUMN_NUMBER + " = ? AND "
				+ VehiclesSQLite.COLUMN_TYPE + " = ?";
		String[] whereArgs = new String[] {
				String.valueOf(vehicle.getNumber()),
				String.valueOf(vehicle.getType()) };

		database.delete(VehiclesSQLite.TABLE_VEHICLES, where, whereArgs);
	}

	/**
	 * Check if a vehicle exists in the DB
	 * 
	 * @param vehicle
	 *            the vehicle that will be deleted
	 * @return the vehicle if it is found in the DB and null otherwise
	 */
	public Vehicle getVehicle(Vehicle vehicle) {
		String selection = VehiclesSQLite.COLUMN_NUMBER + " = ? AND "
				+ VehiclesSQLite.COLUMN_TYPE + " = ?";
		String[] selectionArgs = new String[] {
				String.valueOf(vehicle.getNumber()),
				String.valueOf(vehicle.getType()) };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(VehiclesSQLite.TABLE_VEHICLES,
				allColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating vehicle object and closing the cursor
			Vehicle foundVehicle = cursorToVehicle(cursor);
			foundVehicle.setType(vehicle.getType());
			cursor.close();

			return foundVehicle;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Get the vehicles which NUMBER or DIRECTION contains the searched text
	 * 
	 * @param type
	 *            the type of the Vehicle
	 * @param searchText
	 *            the user search text
	 * @return a list with all busses matching the input conditions
	 */
	public List<Vehicle> getVehiclesViaSearch(VehicleType type,
			String searchText) {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		Locale currentLocale = new Locale(language);
		searchText = searchText.toLowerCase(currentLocale);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											");
		query.append(" FROM " + VehiclesSQLite.TABLE_VEHICLES + "			");
		query.append(" WHERE ( 												");
		query.append(" 		lower(CAST(" + VehiclesSQLite.COLUMN_NUMBER
				+ " AS TEXT)) LIKE '%" + searchText + "%'					");
		query.append(" OR 													");
		query.append(" 		lower(" + VehiclesSQLite.COLUMN_DIRECTION
				+ ") LIKE '%" + searchText + "%'		 					");
		query.append(" ) AND												");
		query.append(" 		" + VehiclesSQLite.COLUMN_TYPE + " LIKE '%"
				+ type.toString() + "%'										");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Vehicle vehicle = cursorToVehicle(cursor);
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
		database.delete(VehiclesSQLite.TABLE_VEHICLES, null, null);
	}

	/**
	 * Creating new Vehicle object with the data of the current row of the
	 * database
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the vehicle object on the current row
	 */
	private Vehicle cursorToVehicle(Cursor cursor) {
		Vehicle vehicle = new Vehicle();

		// Check if have to translate the vehicle direction
		String vehicleDirection = cursor.getString(2);
		if (!"bg".equals(language)) {
			vehicleDirection = TranslatorCyrillicToLatin.translate(context,
					vehicleDirection);
		}

		// Getting all columns of the row and setting them to a Vehicle object
		vehicle.setNumber(cursor.getString(0));
		vehicle.setType(VehicleType.valueOf(cursor.getString(1)));
		vehicle.setDirection(vehicleDirection);

		return vehicle;
	}
}