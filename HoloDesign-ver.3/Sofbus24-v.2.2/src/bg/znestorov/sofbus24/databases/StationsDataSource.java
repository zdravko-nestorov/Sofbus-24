package bg.znestorov.sofbus24.databases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.MapUtils;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.TranslatorLatinToCyrillic;

import com.google.android.gms.maps.model.LatLng;

/**
 * Stations data source class, responsible for all interactions with the
 * stations database
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class StationsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private StationsSQLite dbHelper;
	private String[] allColumns = { StationsSQLite.COLUMN_NUMBER,
			StationsSQLite.COLUMN_NAME, StationsSQLite.COLUMN_LAT,
			StationsSQLite.COLUMN_LON, StationsSQLite.COLUMN_TYPE };

	private Context context;
	private String language;

	// Number of nearest stations
	public static int nearestStationsCount = 8;

	public StationsDataSource(Activity context) {
		this.context = context;
		dbHelper = new StationsSQLite(context);
		language = LanguageChange.getUserLocale(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Adding a station to the database
	 * 
	 * @param station
	 *            the input station
	 * @return the station if it is added successfully and null if already
	 *         exists
	 */
	public StationEntity createStation(StationEntity station) {
		if (getStation(station) == null) {
			// Creating ContentValues object and insert the station data in it
			ContentValues values = new ContentValues();
			values.put(StationsSQLite.COLUMN_NUMBER, station.getNumber());
			values.put(StationsSQLite.COLUMN_NAME, station.getName());
			values.put(StationsSQLite.COLUMN_LAT,
					getCoordinates(station.getNumber(), station.getLat()));
			values.put(StationsSQLite.COLUMN_LON,
					getCoordinates(station.getNumber(), station.getLon()));
			values.put(StationsSQLite.COLUMN_TYPE, station.getType().toString());

			// Insert the ContentValues data into the database
			database.insert(StationsSQLite.TABLE_STATIONS, null, values);

			// Selecting the row that contains the station data
			Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
					allColumns,
					StationsSQLite.COLUMN_NUMBER + " = " + station.getNumber(),
					null, null, null, null);

			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating newStation and closing the cursor
			StationEntity insertedStation = cursorToStation(cursor);
			cursor.close();

			return insertedStation;
		} else {
			return null;
		}
	}

	/**
	 * Figure out which coordinate to be taken, so add the station in the
	 * dabatase (latitude and longitude)
	 * 
	 * @param stationNumber
	 *            the number of the station (used to search the DB for the
	 *            station)
	 * @param stationCoordinate
	 *            the coordinate of the station (latitude or longitude)
	 * @return the coordinate (latitude or longitude), which will be inserted in
	 *         the database
	 */
	private String getCoordinates(String stationNumber, String stationCoordinate) {
		String coordinate = Constants.GLOBAL_PARAM_EMPTY;

		if (stationCoordinate != null && !"".equals(stationCoordinate)) {
			coordinate = stationCoordinate;
		} else if (getStation(stationNumber) != null) {
			coordinate = getStation(stationNumber).getLat();
		}

		return coordinate;
	}

	/**
	 * Delete station from the database
	 * 
	 * @param station
	 *            the input station
	 */
	public void deleteStation(StationEntity station) {
		String where = StationsSQLite.COLUMN_NUMBER + " = ?";
		String[] whereArgs = new String[] { String.valueOf(station.getNumber()) };

		database.delete(StationsSQLite.TABLE_STATIONS, where, whereArgs);
	}

	/**
	 * Check if a station exists in the DB
	 * 
	 * @param station
	 *            the input station
	 * @return the station if it is found in the DB and null otherwise
	 */
	public StationEntity getStation(StationEntity station) {
		// Selecting the row that contains the station data
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
				allColumns,
				StationsSQLite.COLUMN_NUMBER + " = " + station.getNumber(),
				null, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating station object and closing the cursor
			StationEntity foundStation = cursorToStation(cursor);
			if (station.getType() == VehicleTypeEnum.METRO1
					|| station.getType() == VehicleTypeEnum.METRO2) {
				foundStation.setCustomField(String.format(
						Constants.METRO_STATION_URL, foundStation.getNumber()));
			}

			cursor.close();

			return foundStation;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Check if a station exists in the DB via the station number
	 * 
	 * @param stationNumber
	 *            the input station number
	 * @return the station if it is found in the DB and null otherwise
	 */
	public StationEntity getStation(String stationNumber) {
		// Selecting the row that contains the station data
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
				allColumns, StationsSQLite.COLUMN_NUMBER + " = "
						+ stationNumber, null, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating station object and closing the cursor
			StationEntity foundStation = cursorToStation(cursor);
			if (foundStation.getType() == VehicleTypeEnum.METRO1
					|| foundStation.getType() == VehicleTypeEnum.METRO2) {
				foundStation.setCustomField(String.format(
						Constants.METRO_STATION_URL, foundStation.getNumber()));
			}

			cursor.close();

			return foundStation;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Get all stations via type from the database
	 * 
	 * @param vehicleType
	 *            type of the station
	 * @return a list with all stations matching the input type from the DB
	 */
	public List<StationEntity> getStationsViaType(VehicleTypeEnum vehicleType) {
		List<StationEntity> stations = new ArrayList<StationEntity>();

		String selection = StationsSQLite.COLUMN_TYPE + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(vehicleType) };

		database.query(StationsSQLite.TABLE_STATIONS, allColumns, selection,
				selectionArgs, null, null, null);

		// Selecting all fields of the TABLE_STATIONS
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
				allColumns, selection, selectionArgs, null, null, null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StationEntity foundStation = cursorToStation(cursor);
			if (vehicleType == VehicleTypeEnum.METRO1
					|| vehicleType == VehicleTypeEnum.METRO2) {
				foundStation.setCustomField(String.format(
						Constants.METRO_STATION_URL, foundStation.getNumber()));
			}

			stations.add(foundStation);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

	/**
	 * Get the stations which NUMBER or NAME contains the searched text
	 * 
	 * @param vehicleType
	 *            the type of the Station
	 * @param searchText
	 *            the user search text
	 * @return a list with all stations matching the input conditions
	 */
	public List<StationEntity> getStationsViaSearch(
			VehicleTypeEnum vehicleType, String searchText) {
		String searchType;
		if (vehicleType == null) {
			searchType = "";
		} else {
			searchType = vehicleType.toString();
		}

		List<StationEntity> stations = new ArrayList<StationEntity>();
		Locale currentLocale = new Locale(language);
		searchText = searchText.toLowerCase(currentLocale);
		searchText = TranslatorLatinToCyrillic.translate(context, searchText);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											\n");
		query.append(" FROM " + StationsSQLite.TABLE_STATIONS + "			\n");
		query.append(" WHERE ( 												\n");
		query.append(" 		lower(CAST(" + StationsSQLite.COLUMN_NUMBER
				+ " AS TEXT)) LIKE '%" + searchText + "%'					\n");
		query.append(" OR 													\n");
		query.append(" 		lower(" + StationsSQLite.COLUMN_NAME + ") LIKE '%"
				+ searchText + "%'		 									\n");
		query.append(" ) AND												\n");
		query.append(" 		" + VehiclesSQLite.COLUMN_TYPE + " LIKE '%"
				+ searchType + "%'											\n");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StationEntity foundStation = cursorToStation(cursor);
			if (vehicleType == VehicleTypeEnum.METRO1
					|| vehicleType == VehicleTypeEnum.METRO2) {
				foundStation.setCustomField(String.format(
						Constants.METRO_STATION_URL, foundStation.getNumber()));
			}

			stations.add(foundStation);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

	/**
	 * Get all stations from the database
	 * 
	 * @return a list with all stations from the DB
	 */
	public List<StationEntity> getAllStations() {
		List<StationEntity> stations = new ArrayList<StationEntity>();

		// Selecting all fields of the TABLE_STATIONS
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
				allColumns, null, null, null, null, null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StationEntity foundStation = cursorToStation(cursor);
			if (foundStation.getType() == VehicleTypeEnum.METRO1
					|| foundStation.getType() == VehicleTypeEnum.METRO2) {
				foundStation.setCustomField(String.format(
						Constants.METRO_STATION_URL, foundStation.getNumber()));
			}

			stations.add(foundStation);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

	/**
	 * Delete all stations from the database;
	 */
	public void deleteAllStations() {
		database.delete(StationsSQLite.TABLE_STATIONS, null, null);
	}

	/**
	 * Get the nearest station from the DB to a location according to a radius
	 * 
	 * @param context
	 *            current activity context
	 * @param currentPosition
	 *            the current position
	 * @param stationsRadius
	 *            current position radiusF
	 * @return a list with the closest station
	 */
	public List<StationEntity> getClosestStations(Activity context,
			LatLng currentPosition, BigDecimal stationsRadius) {
		FavouritesDataSource favouritesDatasource = new FavouritesDataSource(
				context);
		List<StationEntity> stations = new ArrayList<StationEntity>();

		// IMPORTANT: Used for correct ordering
		Double fudge = Math.pow(
				Math.cos(Math.toRadians(currentPosition.latitude)), 2);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											\n");
		query.append(" FROM stations	 									\n");
		query.append(" ORDER BY												\n");
		query.append(" 		( (												\n");
		query.append(StationsSQLite.COLUMN_LAT + " - "
				+ currentPosition.latitude);
		query.append(" 		) * (											\n");
		query.append(StationsSQLite.COLUMN_LAT + " - "
				+ currentPosition.latitude);
		query.append(" 		) + (											\n");
		query.append(StationsSQLite.COLUMN_LON + " - "
				+ currentPosition.longitude);
		query.append(" 		) * (											\n");
		query.append(StationsSQLite.COLUMN_LON + " - "
				+ currentPosition.longitude);
		query.append(" 		) * " + fudge + " ) ASC							\n");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();

		// Open the Favourites DB to check if the station is already there
		favouritesDatasource.open();

		while (!cursor.isAfterLast()) {
			StationEntity foundStation = cursorToStation(cursor);

			// Check if the station has coordinates in the database
			if (foundStation.hasCoordinates()) {

				// Get the distance to the current station
				BigDecimal distance = new BigDecimal(MapUtils.getMapDistance(
						currentPosition, foundStation));

				// Check if the station is in the given radius
				if (distance.compareTo(stationsRadius) != 1) {

					// Check the type of the station and if it is METRO add the
					// schedule URL to the custom field
					if ((foundStation.getType() == VehicleTypeEnum.METRO1 || foundStation
							.getType() == VehicleTypeEnum.METRO2)) {
						foundStation.setCustomField(String.format(
								Constants.METRO_STATION_URL,
								foundStation.getNumber()));
					}

					// Check if the station is not in Favorites
					if (favouritesDatasource.getStation(foundStation) == null) {
						stations.add(foundStation);
					}

					cursor.moveToNext();
				} else {
					break;
				}
			}
		}

		// Closing the Favourites DB
		favouritesDatasource.close();

		// Closing the cursor
		cursor.close();

		return stations;
	}

	/**
	 * Get the nearest station from the DB to a location according to the needed
	 * page
	 * 
	 * @param currentPosition
	 *            the current position
	 * @param stationsToLoad
	 *            number of stations to load
	 * @param searchText
	 *            if there is any criteria for searching
	 * @return a list with the closest station
	 */
	public List<StationEntity> getClosestStations(LatLng currentPosition,
			int stationsToLoad, String searchText) {
		List<StationEntity> stations = new ArrayList<StationEntity>();

		Locale currentLocale = new Locale(language);
		searchText = searchText.toLowerCase(currentLocale);
		searchText = TranslatorLatinToCyrillic.translate(context, searchText);

		// IMPORTANT: Used for correct ordering
		Double fudge = Math.pow(
				Math.cos(Math.toRadians(currentPosition.latitude)), 2);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											\n");
		query.append(" FROM stations	 									\n");
		query.append(" WHERE ( 												\n");
		query.append(" 		lower(CAST(" + StationsSQLite.COLUMN_NUMBER
				+ " AS TEXT)) LIKE '%" + searchText + "%'					\n");
		query.append(" OR 													\n");
		query.append(" 		lower(" + StationsSQLite.COLUMN_NAME + ") LIKE '%"
				+ searchText + "%'		 									\n");
		query.append(" )													\n");
		query.append(" ORDER BY												\n");
		query.append(" 		( (												\n");
		query.append(StationsSQLite.COLUMN_LAT + " - "
				+ currentPosition.latitude);
		query.append(" 		) * (											\n");
		query.append(StationsSQLite.COLUMN_LAT + " - "
				+ currentPosition.latitude);
		query.append(" 		) + (											\n");
		query.append(StationsSQLite.COLUMN_LON + " - "
				+ currentPosition.longitude);
		query.append(" 		) * (											\n");
		query.append(StationsSQLite.COLUMN_LON + " - "
				+ currentPosition.longitude);
		query.append(" 		) * " + fudge + " ) ASC							\n");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();

		int stationsCount = 1;
		while (!cursor.isAfterLast() && stationsCount <= stationsToLoad) {
			StationEntity foundStation = cursorToStation(cursor);
			boolean isStationInRange = true;
			try {
				BigDecimal distance = new BigDecimal(MapUtils.getMapDistance(
						currentPosition, foundStation));
				if (distance
						.compareTo(Constants.GLOBAL_PARAM_CLOSEST_STATION_DISTANCE) == 1) {
					isStationInRange = false;
				}
			} catch (Exception e) {
				isStationInRange = false;
			}

			// Check if the station is in range
			if (isStationInRange) {
				if ((foundStation.getType() == VehicleTypeEnum.METRO1 || foundStation
						.getType() == VehicleTypeEnum.METRO2)) {
					foundStation.setCustomField(String.format(
							Constants.METRO_STATION_URL,
							foundStation.getNumber()));
				}

				stations.add(foundStation);

				stationsCount++;
				cursor.moveToNext();
			} else {
				break;
			}
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

	/**
	 * Get the nearest station from the DB to a location according to the needed
	 * page
	 * 
	 * @param currentPosition
	 *            the current position
	 * @param stationPage
	 *            each page contains 10 stations
	 * @param searchText
	 *            if there is any criteria for searching
	 * @return a list with the closest station
	 */
	public List<StationEntity> getClosestStationsByPage(LatLng currentPosition,
			int stationPage, String searchText) {
		List<StationEntity> stations = new ArrayList<StationEntity>();

		Locale currentLocale = new Locale(language);
		searchText = searchText.toLowerCase(currentLocale);
		searchText = TranslatorLatinToCyrillic.translate(context, searchText);

		// IMPORTANT: Used for correct ordering
		Double fudge = Math.pow(
				Math.cos(Math.toRadians(currentPosition.latitude)), 2);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											\n");
		query.append(" FROM stations	 									\n");
		query.append(" WHERE ( 												\n");
		query.append(" 		lower(CAST(" + StationsSQLite.COLUMN_NUMBER
				+ " AS TEXT)) LIKE '%" + searchText + "%'					\n");
		query.append(" OR 													\n");
		query.append(" 		lower(" + StationsSQLite.COLUMN_NAME + ") LIKE '%"
				+ searchText + "%'		 									\n");
		query.append(" )													\n");
		query.append(" ORDER BY												\n");
		query.append(" 		( (												\n");
		query.append(StationsSQLite.COLUMN_LAT + " - "
				+ currentPosition.latitude);
		query.append(" 		) * (											\n");
		query.append(StationsSQLite.COLUMN_LAT + " - "
				+ currentPosition.latitude);
		query.append(" 		) + (											\n");
		query.append(StationsSQLite.COLUMN_LON + " - "
				+ currentPosition.longitude);
		query.append(" 		) * (											\n");
		query.append(StationsSQLite.COLUMN_LON + " - "
				+ currentPosition.longitude);
		query.append(" 		) * " + fudge + " ) ASC							\n");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();

		int stationsCount = 1;
		while (!cursor.isAfterLast() && stationsCount <= stationPage * 10) {
			StationEntity foundStation = cursorToStation(cursor);
			boolean isStationInRange = true;
			try {
				BigDecimal distance = new BigDecimal(MapUtils.getMapDistance(
						currentPosition, foundStation));
				if (distance
						.compareTo(Constants.GLOBAL_PARAM_CLOSEST_STATION_DISTANCE) == 1) {
					isStationInRange = false;
				}
			} catch (Exception e) {
				isStationInRange = false;
			}

			// Check if the station is in range
			if (isStationInRange) {
				if (stationsCount > ((stationPage - 1) * 10)) {
					if (foundStation.getType() == VehicleTypeEnum.METRO1
							|| foundStation.getType() == VehicleTypeEnum.METRO2) {
						foundStation.setCustomField(String.format(
								Constants.METRO_STATION_URL,
								foundStation.getNumber()));
					}

					stations.add(foundStation);
				}

				stationsCount++;
				cursor.moveToNext();
			} else {
				break;
			}
		}

		// Closing the cursor
		cursor.close();

		return stations;
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
		station.setType(VehicleTypeEnum.valueOf(cursor.getString(4)));
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
}