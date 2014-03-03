package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;
import java.util.List;

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
	private String[] allColumns = { VehiclesSQLite.COULMN_NUMBER,
			VehiclesSQLite.COLUMN_DIRECTION };

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
			values.put(VehiclesSQLite.COULMN_NUMBER, vehicle.getNumber());
			values.put(VehiclesSQLite.COLUMN_DIRECTION, vehicle.getDirection());

			// Insert the ContentValues data into the database
			String dbToInsert = getDBTable(vehicle);
			database.insert(dbToInsert, null, values);

			// Selecting the row that contains the vehicle data
			Cursor cursor = database.query(dbToInsert, allColumns,
					VehiclesSQLite.COULMN_NUMBER + " = " + vehicle.getNumber(),
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
	 * Define the table in which the vehicle will be inserted
	 * 
	 * @param vehicle
	 *            the vehicle that will be inserted
	 * @return table in which the vehicle will be inserted
	 */
	private String getDBTable(Vehicle vehicle) {
		String dbTable;

		switch (vehicle.getType()) {
		case BUS:
			dbTable = VehiclesSQLite.TABLE_BUSSES;
			break;
		case TROLLEY:
			dbTable = VehiclesSQLite.TABLE_TROLLEYS;
			break;
		case TRAM:
			dbTable = VehiclesSQLite.TABLE_TRAMS;
			break;
		default:
			dbTable = VehiclesSQLite.TABLE_BUSSES;
			break;
		}

		return dbTable;
	}

	/**
	 * Delete vehicle from the database
	 * 
	 * @param vehicle
	 *            the vehicle that will be deleted
	 */
	public void deleteVehicle(Vehicle vehicle) {
		String where = VehiclesSQLite.COULMN_NUMBER + " = ?";
		String[] whereArgs = new String[] { String.valueOf(vehicle.getNumber()) };

		database.delete(getDBTable(vehicle), where, whereArgs);
	}

	/**
	 * Check if a vehicle exists in the DB
	 * 
	 * @param vehicle
	 *            the vehicle that will be deleted
	 * @return the vehicle if it is found in the DB and null otherwise
	 */
	public Vehicle getVehicle(Vehicle vehicle) {
		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(getDBTable(vehicle), allColumns,
				VehiclesSQLite.COULMN_NUMBER + " = " + vehicle.getNumber(),
				null, null, null, null);

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
	 * Get all busses from the database
	 * 
	 * @return a list with all busses from the DB
	 */
	public List<Vehicle> getAllBusses() {
		List<Vehicle> busses = new ArrayList<Vehicle>();

		// Selecting all fields of the TABLE_BUSSES
		Cursor cursor = database.query(VehiclesSQLite.TABLE_BUSSES, allColumns,
				null, null, null, null, null);

		// Iterating the cursor and fill the empty List<Vehicle>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Vehicle vehicle = cursorToVehicle(cursor);
			vehicle.setType(VehicleType.BUS);
			busses.add(vehicle);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return busses;
	}

	/**
	 * Delete all busses from the database;
	 */
	public void deleteAllBusses() {
		database.delete(VehiclesSQLite.TABLE_BUSSES, null, null);
	}

	/**
	 * Get all busses from the database
	 * 
	 * @return a list with all busses from the DB
	 */
	public List<Vehicle> getAllTrolleys() {
		List<Vehicle> trolleys = new ArrayList<Vehicle>();

		// Selecting all fields of the TABLE_VEHICLES
		Cursor cursor = database.query(VehiclesSQLite.TABLE_TROLLEYS,
				allColumns, null, null, null, null, null);

		// Iterating the cursor and fill the empty List<Vehicle>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Vehicle vehicle = cursorToVehicle(cursor);
			vehicle.setType(VehicleType.TROLLEY);
			trolleys.add(vehicle);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return trolleys;
	}

	/**
	 * Delete all busses from the database;
	 */
	public void deleteAllTrolleys() {
		database.delete(VehiclesSQLite.TABLE_TROLLEYS, null, null);
	}

	/**
	 * Get all busses from the database
	 * 
	 * @return a list with all busses from the DB
	 */
	public List<Vehicle> getAllTrams() {
		List<Vehicle> trams = new ArrayList<Vehicle>();

		// Selecting all fields of the TABLE_VEHICLES
		Cursor cursor = database.query(VehiclesSQLite.TABLE_TRAMS, allColumns,
				null, null, null, null, null);

		// Iterating the cursor and fill the empty List<Vehicle>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Vehicle vehicle = cursorToVehicle(cursor);
			vehicle.setType(VehicleType.TRAM);
			trams.add(vehicle);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return trams;
	}

	/**
	 * Delete all busses from the database;
	 */
	public void deleteAllTrams() {
		database.delete(VehiclesSQLite.TABLE_TRAMS, null, null);
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
		String vehicleDirection = cursor.getString(1);
		if (!"bg".equals(language)) {
			vehicleDirection = TranslatorCyrillicToLatin.translate(context,
					vehicleDirection);
		}

		// Getting all columns of the row and setting them to a Vehicle object
		vehicle.setNumber(cursor.getString(0));
		vehicle.setDirection(vehicleDirection);

		return vehicle;
	}

}