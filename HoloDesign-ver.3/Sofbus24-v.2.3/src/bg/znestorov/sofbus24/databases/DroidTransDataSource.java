package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleStationEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.utils.Constants;
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
public class DroidTransDataSource {

	// Database fields
	private SQLiteDatabase database;
	private Sofbus24SQLite dbHelper;

	private String[] vehiColumns = { Sofbus24SQLite.COLUMN_PK_VEHI_ID,
			Sofbus24SQLite.COLUMN_VEHI_NUMBER, Sofbus24SQLite.COLUMN_VEHI_TYPE,
			Sofbus24SQLite.COLUMN_VEHI_DIRECTION };

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

	/**
	 * Get the stations for the current vehicle in the desired location
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @param vehicleNumber
	 *            the vehicle number
	 * @param vehicleDirection
	 *            the desired location
	 * @return a list with all stations for the vehicle
	 */
	public ArrayList<StationEntity> getVehicleStations(
			VehicleTypeEnum vehicleType, String vehicleNumber,
			Integer vehicleDirection) {

		ArrayList<StationEntity> vehicleStations = new ArrayList<StationEntity>();

		StringBuilder query = new StringBuilder();
		query.append(" SELECT SOF_STAT.STAT_NUMBER, SOF_STAT.STAT_NAME, SOF_STAT.STAT_LATITUDE, SOF_STAT.STAT_LONGITUDE		\n");
		query.append(" FROM SOF_STAT																						\n");
		query.append(" 		JOIN SOF_VEST																					\n");
		query.append(" 			ON SOF_VEST.FK_VEST_STAT_ID = SOF_STAT.PK_STAT_ID											\n");
		query.append(" 			AND SOF_VEST.VEST_DIRECTION = " + vehicleDirection
				+ "																											\n");
		query.append(" 		JOIN SOF_VEHI																					\n");
		query.append(" 			ON SOF_VEHI.PK_VEHI_ID = SOF_VEST.FK_VEST_VEHI_ID											\n");
		query.append(" 			AND SOF_VEHI.VEHI_NUMBER = " + vehicleNumber
				+ "																											\n");
		query.append(" 			AND SOF_VEHI.VEHI_TYPE LIKE '%"
				+ String.valueOf(vehicleType) + "%'																			\n");

		// Selecting the row that contains the stations data
		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StationEntity station = cursorToStation(cursor);
			vehicleStations.add(station);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return vehicleStations;
	}

	/**
	 * Creating new Station object with the data of the current row of the
	 * database
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the station object on the current row
	 */
	private StationEntity cursorToStation(Cursor cursor) {
		StationEntity station = new StationEntity();

		// Check if have to translate the station name
		String stationName = cursor.getString(1);
		if (!"bg".equals(language)) {
			stationName = TranslatorCyrillicToLatin.translate(context,
					stationName);
		}

		// Getting all columns of the row and setting them to a Station object
		station.setNumber(cursor.getString(0));
		station.setName(stationName);
		station.setLat(cursor.getString(2));
		station.setLon(cursor.getString(3));
		station.setCustomField(getCustomField(station));

		return station;
	}

	/**
	 * Define what to put in the custom field in the DB via the station type
	 * 
	 * @param station
	 *            the inputStation
	 * @return what to be inserted in the custom field in the DB
	 */
	private String getCustomField(StationEntity station) {
		String stationCustomField;

		switch (station.getType()) {
		case METRO1:
		case METRO2:
			stationCustomField = String.format(Constants.METRO_STATION_URL,
					station.getNumber());
			break;
		default:
			stationCustomField = "";
			break;
		}

		return stationCustomField;
	}

	/**
	 * Get the stations for the current vehicle in the desired location
	 * 
	 * @param vehicleType
	 *            the vehicle type
	 * @param vehicleNumber
	 *            the vehicle number
	 * @param vehicleDirection
	 *            the desired location
	 * @return a list with all stations for the vehicle
	 */
	public VehicleStationEntity getVehicleStationsUrl(
			VehicleTypeEnum vehicleType, String vehicleNumber,
			Integer vehicleDirection, Integer stationNumber) {

		VehicleStationEntity vehicleStation = null;

		StringBuilder query = new StringBuilder();
		query.append(" SELECT SOF_VEST.VEST_STOP, SOF_VEST.VEST_VEST_LID, SOF_VEST.VEST_VT, SOF_VEST.VEST_RID				\n");
		query.append(" FROM SOF_STAT																						\n");
		query.append(" 		JOIN SOF_VEST																					\n");
		query.append(" 			ON SOF_VEST.FK_VEST_STAT_ID = SOF_STAT.PK_STAT_ID											\n");
		query.append(" 			AND SOF_VEST.VEST_DIRECTION = " + vehicleDirection
				+ "																											\n");
		query.append(" 		JOIN SOF_VEHI																					\n");
		query.append(" 			ON SOF_VEHI.PK_VEHI_ID = SOF_VEST.FK_VEST_VEHI_ID											\n");
		query.append(" 			AND SOF_VEHI.VEHI_NUMBER = " + vehicleNumber
				+ "																											\n");
		query.append(" 			AND SOF_VEHI.VEHI_TYPE LIKE '%"
				+ String.valueOf(vehicleType) + "%'																			\n");
		query.append("		WHERE SOF_STAT.STAT_NUMBER = " + stationNumber
				+ "																											\n");

		// Selecting the row that contains the stations data
		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			vehicleStation = cursorToVehicleStation(cursor);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return vehicleStation;
	}

	/**
	 * Creating new VehicleStation object with the data of the current row of
	 * the database
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the station object on the current row
	 */
	private VehicleStationEntity cursorToVehicleStation(Cursor cursor) {

		Integer stop = cursor.getInt(0);
		Integer lid = cursor.getInt(1);
		Integer vt = cursor.getInt(2);
		Integer rid = cursor.getInt(3);

		return new VehicleStationEntity(stop, lid, vt, rid);
	}

}