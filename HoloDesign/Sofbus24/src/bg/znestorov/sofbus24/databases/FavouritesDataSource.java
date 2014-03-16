package bg.znestorov.sofbus24.databases;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.TranslatorLatinToCyrillic;

/**
 * Favorites data source used for DB interactions
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class FavouritesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private FavouritesSQLite dbHelper;
	private String[] allColumns = { FavouritesSQLite.COLUMN_NUMBER,
			FavouritesSQLite.COLUMN_NAME, FavouritesSQLite.COLUMN_LAT,
			FavouritesSQLite.COLUMN_LON, FavouritesSQLite.COLUMN_CUSTOM_FIELD };

	private Context context;
	private String language;

	public FavouritesDataSource(Context context) {
		this.context = context;
		dbHelper = new FavouritesSQLite(context);
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
	public Station createStation(Station station) {
		if (getStation(station) == null) {

			// Check if have to translate the station name
			String stationName = station.getName();
			if (!"bg".equals(language)) {
				stationName = TranslatorLatinToCyrillic.translate(context,
						stationName);
			}

			// Creating ContentValues object and insert the station data in it
			ContentValues values = new ContentValues();
			values.put(FavouritesSQLite.COLUMN_NUMBER, station.getNumber());
			values.put(FavouritesSQLite.COLUMN_NAME, stationName);
			values.put(FavouritesSQLite.COLUMN_LAT,
					getCoordinates(station.getNumber(), station.getLat()));
			values.put(FavouritesSQLite.COLUMN_LON,
					getCoordinates(station.getNumber(), station.getLon()));

			values.put(FavouritesSQLite.COLUMN_CUSTOM_FIELD,
					getCustomField(station));

			// Insert the ContentValues data into the database
			database.insert(FavouritesSQLite.TABLE_FAVOURITES, null, values);

			// Selecting the row that contains the station data
			Cursor cursor = database.query(FavouritesSQLite.TABLE_FAVOURITES,
					allColumns, FavouritesSQLite.COLUMN_NUMBER + " = "
							+ station.getNumber(), null, null, null, null);

			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating newStation and closing the cursor
			Station insertedStation = cursorToStation(cursor);
			cursor.close();

			return insertedStation;
		} else {
			return null;
		}
	}

	/**
	 * Figure out which coordinate to be taken, so add the station in the
	 * dabatase (lattitude and longitute)
	 * 
	 * @param stationNumber
	 *            the number of the station (used to search the DB for the
	 *            station)
	 * @param stationCoordinate
	 *            the coordinate of the station (lattitude or longitude)
	 * @return the coordinate (lattitude or longitude), which will be inserted
	 *         in the dabatase
	 */
	private String getCoordinates(String stationNumber, String stationCoordinate) {
		String coordinate = Constants.GLOBAL_PARAM_EMPTY;

		StationsDataSource stationsDS = new StationsDataSource(context);
		stationsDS.open();

		if (stationCoordinate != null && !"".equals(stationCoordinate)) {
			coordinate = stationCoordinate;
		} else if (stationsDS.getStation(stationNumber) != null) {
			coordinate = stationsDS.getStation(stationNumber).getLat();
		}
		stationsDS.close();

		return coordinate;
	}

	/**
	 * Define what to put in the custom field in the DB via the station type
	 * 
	 * @param station
	 *            the inputStation
	 * @return what to be inserted in the custom field in the DB
	 */
	private String getCustomField(Station station) {
		String stationCustomField;

		switch (station.getType()) {
		case METRO1:
		case METRO2:
			stationCustomField = String.format(Constants.METRO_STATION_URL,
					station.getNumber());
			break;
		default:
			stationCustomField = String.valueOf(station.getCustomField());
			break;
		}

		return stationCustomField;
	}

	/**
	 * Delete station from the database
	 * 
	 * @param station
	 *            the input station
	 */
	public void deleteStation(Station station) {
		String where = FavouritesSQLite.COLUMN_NUMBER + " = ?";
		String[] whereArgs = new String[] { String.valueOf(station.getNumber()) };

		database.delete(FavouritesSQLite.TABLE_FAVOURITES, where, whereArgs);
	}

	/**
	 * Update a station from the database. The search is done by the station
	 * code and the custom field is updated.
	 * 
	 * @param station
	 *            the new station parameters
	 */
	public void updateStation(Station station) {
		ContentValues dataToInsert = new ContentValues();
		dataToInsert.put(FavouritesSQLite.COLUMN_NAME, station.getName());
		dataToInsert.put(FavouritesSQLite.COLUMN_LAT, station.getLat());
		dataToInsert.put(FavouritesSQLite.COLUMN_LON, station.getLon());
		dataToInsert.put(FavouritesSQLite.COLUMN_CUSTOM_FIELD,
				station.getCustomField());

		String where = FavouritesSQLite.COLUMN_NUMBER + " = ?";
		String[] whereArgs = new String[] { String.valueOf(station.getNumber()) };

		database.update(FavouritesSQLite.TABLE_FAVOURITES, dataToInsert, where,
				whereArgs);
	}

	/**
	 * Check if a station exists in the DB
	 * 
	 * @param station
	 *            the input station
	 * @return the station if it is found in the DB and null otherwise
	 */
	public Station getStation(Station station) {
		// Selecting the row that contains the station data
		Cursor cursor = database.query(FavouritesSQLite.TABLE_FAVOURITES,
				allColumns,
				FavouritesSQLite.COLUMN_NUMBER + " = " + station.getNumber(),
				null, null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating station object and closing the cursor
			Station foundStation = cursorToStation(cursor);
			cursor.close();

			return foundStation;
		} else {
			cursor.close();

			return null;
		}
	}

	/**
	 * Get the stations which NUMBER or NAME contains the searched text
	 * 
	 * @param searchText
	 *            the user search text
	 * @return a list with all stations matching the input conditions
	 */
	public List<Station> getStationsViaSearch(String searchText) {
		List<Station> stations = new ArrayList<Station>();
		Locale currentLocale = new Locale(language);
		searchText = searchText.toLowerCase(currentLocale);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * 											");
		query.append(" FROM " + FavouritesSQLite.TABLE_FAVOURITES + "		");
		query.append(" WHERE 												");
		query.append(" 	lower(CAST(" + FavouritesSQLite.COLUMN_NUMBER
				+ " AS TEXT)) LIKE '%" + searchText + "%'					");
		query.append(" OR 													");
		query.append(" 	lower(" + FavouritesSQLite.COLUMN_NAME + ") LIKE '%"
				+ searchText + "%'		 									");

		Cursor cursor = database.rawQuery(query.toString(), null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Station station = cursorToStation(cursor);
			stations.add(station);
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
	public List<Station> getAllStations() {
		List<Station> stations = new ArrayList<Station>();

		// Selecting all fields of the TABLE_FAVOURITES
		Cursor cursor = database.query(FavouritesSQLite.TABLE_FAVOURITES,
				allColumns, null, null, null, null, null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Station station = cursorToStation(cursor);
			stations.add(station);
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
		database.delete(FavouritesSQLite.TABLE_FAVOURITES, null, null);
	}

	/**
	 * Creating new Station object with the data of the current row of the
	 * database
	 * 
	 * @param cursor
	 *            the input cursor for interacting with the DB
	 * @return the station object on the current row
	 */
	private Station cursorToStation(Cursor cursor) {
		Station station = new Station();

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
		station.setCustomField(cursor.getString(4));

		return station;
	}
}