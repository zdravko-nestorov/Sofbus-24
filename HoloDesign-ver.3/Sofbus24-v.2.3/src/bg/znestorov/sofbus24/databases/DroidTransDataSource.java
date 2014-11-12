package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.utils.LanguageChange;

/**
 * Vehicles data source class, responsible for all interactions with the
 * vehicles database
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class DroidTransDataSource {

	// Database fields
	private SQLiteDatabase database;
	private Sofbus24SQLite dbHelper;

	private String[] statColumns = { Sofbus24SQLite.COLUMN_PK_STAT_ID,
			Sofbus24SQLite.COLUMN_STAT_NUMBER, Sofbus24SQLite.COLUMN_STAT_NAME,
			Sofbus24SQLite.COLUMN_STAT_LATITUDE,
			Sofbus24SQLite.COLUMN_STAT_LONGITUDE,
			Sofbus24SQLite.COLUMN_STAT_TYPE };

	private String[] vehiColumns = { Sofbus24SQLite.COLUMN_PK_VEHI_ID,
			Sofbus24SQLite.COLUMN_VEHI_NUMBER, Sofbus24SQLite.COLUMN_VEHI_TYPE,
			Sofbus24SQLite.COLUMN_VEHI_DIRECTION };

	private String[] vestColumns = { Sofbus24SQLite.COLUMN_PK_VEST_ID,
			Sofbus24SQLite.COLUMN_FK_VEST_STAT_ID,
			Sofbus24SQLite.COLUMN_FK_VEST_VEHI_ID,
			Sofbus24SQLite.COLUMN_VEST_STOP, Sofbus24SQLite.COLUMN_VEST_LID,
			Sofbus24SQLite.COLUMN_VEST_VT, Sofbus24SQLite.COLUMN_VEST_RID };

	private Activity context;
	private String language;

	public DroidTransDataSource(Activity context) {
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
	 * Get a list with all vehicle types from the DB
	 * 
	 * @return a list of vehicles type
	 */
	public ArrayList<VehicleTypeEnum> getVehicleTypes() {

		ArrayList<VehicleTypeEnum> vehicleTypes = new ArrayList<VehicleTypeEnum>();

		boolean isDistinct = true;
		String[] vehicleColumns = new String[] { vehiColumns[2] };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(isDistinct,
				Sofbus24SQLite.TABLE_SOF_VEHI, vehicleColumns, null, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			VehicleTypeEnum vehicleType = cursorToVehicleType(cursor);
			vehicleTypes.add(vehicleType);

			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return vehicleTypes;
	}

	/**
	 * Getting the vehicle type from the cursor object
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the vehicle type on the current row
	 */
	private VehicleTypeEnum cursorToVehicleType(Cursor cursor) {

		VehicleTypeEnum vehicleType = VehicleTypeEnum.valueOf(cursor
				.getString(0));
		if (vehicleType == VehicleTypeEnum.METRO1
				|| vehicleType == VehicleTypeEnum.METRO2) {
			vehicleType = VehicleTypeEnum.METRO;
		}

		return vehicleType;
	}

	/**
	 * Get the vehcile numbers for the selected type
	 * 
	 * @param vehicleType
	 *            the choosen vehicle type
	 * @return the vehicle numbers by type
	 */
	public ArrayList<String> getVehicleNumbers(VehicleTypeEnum vehicleType) {

		ArrayList<String> vehiclesNumbers = new ArrayList<String>();

		String[] vehicleColumns = new String[] { vehiColumns[1] };
		String selection = Sofbus24SQLite.COLUMN_VEHI_TYPE + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(vehicleType) };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(Sofbus24SQLite.TABLE_SOF_VEHI,
				vehicleColumns, selection, selectionArgs, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String vehicleNumber = cursor.getString(0);
			vehiclesNumbers.add(vehicleNumber);

			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return vehiclesNumbers;
	}

	/**
	 * Get the directions according a vehicle type and vehicle number
	 * 
	 * @param vehicleType
	 *            the type of the vehicle
	 * @param vehicleNumber
	 *            the number of the vehicle
	 * @return the directions of the vehicle
	 */
	public ArrayList<String> getVehicleDirections(VehicleTypeEnum vehicleType,
			String vehicleNumber) {

		ArrayList<String> vehiclesDirections = new ArrayList<String>();

		String[] vehicleColumns = new String[] { vehiColumns[3] };
		String selection = Sofbus24SQLite.COLUMN_VEHI_TYPE + " = ? AND "
				+ Sofbus24SQLite.COLUMN_VEHI_NUMBER + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(vehicleType),
				vehicleNumber };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(Sofbus24SQLite.TABLE_SOF_VEHI,
				vehicleColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			String vehicleDirection = cursor.getString(0);
			vehiclesDirections.add(vehicleDirection);
			vehiclesDirections.add(getOppositeDirection(vehicleDirection));
		}

		// Closing the cursor
		cursor.close();

		return vehiclesDirections;
	}

	/**
	 * Get the opposite direction of the current one (just permute the words)
	 * 
	 * @param direction
	 *            the current direction
	 * @return the opposite direction
	 */
	public String getOppositeDirection(String direction) {

		String oppositeDirection;
		String directionSeparator = " - ";
		String[] directionParts = direction.split("-");

		switch (directionParts.length) {
		case 0:
		case 1:
			oppositeDirection = direction;
			break;
		case 2:
			oppositeDirection = directionParts[1] + directionSeparator
					+ directionParts[0];
			break;
		case 3:
			oppositeDirection = directionParts[2] + directionSeparator
					+ directionParts[1] + directionSeparator
					+ directionParts[0];
			break;
		default:
			oppositeDirection = directionParts[3] + directionSeparator
					+ directionParts[2] + directionSeparator
					+ directionParts[1] + directionSeparator
					+ directionParts[0];
			break;
		}

		return oppositeDirection;
	}

	public ArrayList<String> getVehicleStations(VehicleTypeEnum vehicleType,
			String vehicleNumber, Integer vehicleDirection) {

		ArrayList<String> vehiclesDirections = new ArrayList<String>();

		String[] vehicleColumns = new String[] { vehiColumns[3] };
		String selection = Sofbus24SQLite.COLUMN_VEHI_TYPE + " = ? AND "
				+ Sofbus24SQLite.COLUMN_VEHI_NUMBER + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(vehicleType),
				vehicleNumber };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(Sofbus24SQLite.TABLE_SOF_VEHI,
				vehicleColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			String vehicleDirection1 = cursor.getString(0);
			vehiclesDirections.add(vehicleDirection1);
			vehiclesDirections.add(getOppositeDirection(vehicleDirection1));
		}

		// Closing the cursor
		cursor.close();

		return vehiclesDirections;
	}

}