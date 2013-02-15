package bg.znestorov.sofbus24.station_database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FavouritesDataSource {

	// LogCat TAG
	private static final String TAG = "FavouritesDataSource";

	// Database fields
	private SQLiteDatabase database;
	private FavouritesSQLite dbHelper;
	private String[] allColumns = { FavouritesSQLite.COLUMN_ID,
			FavouritesSQLite.COLUMN_NAME, FavouritesSQLite.COLUMN_LAT,
			FavouritesSQLite.COLUMN_LON, FavouritesSQLite.COLUMN_CODEO };
	private Context context;

	public FavouritesDataSource(Context context) {
		dbHelper = new FavouritesSQLite(context);
		this.context = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// Adding station to the database
	public GPSStation createStation(GPSStation station) {
		if (getStation(station) == null) {
			// Creating ContentValues object and insert the station data in it
			ContentValues values = new ContentValues();

			values.put(FavouritesSQLite.COLUMN_ID, station.getId());
			values.put(FavouritesSQLite.COLUMN_NAME, station.getName());

			StationsDataSource stationDS = new StationsDataSource(context);
			stationDS.open();

			if (station.getLat() != null && !"".equals(station.getLat())) {
				values.put(StationsSQLite.COLUMN_LAT, station.getLat());
			} else {
				// Check for the station in the DB
				if (stationDS.getStation(station) != null) {
					values.put(StationsSQLite.COLUMN_LAT,
							stationDS.getStation(station).getLat());
				} else {
					station.setLat("EMPTY");
					values.put(StationsSQLite.COLUMN_LAT, station.getLat());
				}
			}

			if (station.getLon() != null && !"".equals(station.getLon())) {
				values.put(StationsSQLite.COLUMN_LON, station.getLon());
			} else {
				// Check for the station in the DB
				if (stationDS.getStation(station) != null) {
					values.put(StationsSQLite.COLUMN_LON,
							stationDS.getStation(station).getLon());
				} else {
					station.setLon("EMPTY");
					values.put(StationsSQLite.COLUMN_LON, station.getLon());
				}
			}
			stationDS.close();

			values.put(FavouritesSQLite.COLUMN_CODEO, station.getCodeO());

			// Insert the ContentValues data into the database
			database.insert(FavouritesSQLite.TABLE_FAVOURITES, null, values);

			// Selecting the row that contains the station data
			Cursor cursor = database.query(FavouritesSQLite.TABLE_FAVOURITES,
					allColumns,
					FavouritesSQLite.COLUMN_ID + " = " + station.getId(), null,
					null, null, null);

			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating newStation and closing the cursor
			GPSStation newStation = cursorToStation(cursor);
			cursor.close();

			return newStation;
		} else {
			return null;
		}
	}

	// Delete station from the database
	public void deleteStation(GPSStation station) {
		String id = station.getId();
		Log.d(TAG, "Station deleted with id: " + id);
		database.delete(FavouritesSQLite.TABLE_FAVOURITES,
				FavouritesSQLite.COLUMN_ID + " = " + id, null);
	}

	// Get all stations from the database
	public GPSStation getStation(GPSStation gpsStation) {
		// Selecting the row that contains the station data
		Cursor cursor = database.query(FavouritesSQLite.TABLE_FAVOURITES,
				allColumns,
				FavouritesSQLite.COLUMN_ID + " = " + gpsStation.getId(), null,
				null, null, null);

		if (cursor.getCount() > 0) {
			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			// Creating station object and closing the cursor
			GPSStation station = cursorToStation(cursor);
			cursor.close();

			return station;
		} else {
			return null;
		}
	}

	// Get all stations from the database
	public List<GPSStation> getAllStations() {
		List<GPSStation> stations = new ArrayList<GPSStation>();

		// Selecting all fields of the TABLE_FAVOURITES
		Cursor cursor = database.query(FavouritesSQLite.TABLE_FAVOURITES,
				allColumns, null, null, null, null, null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GPSStation station = cursorToStation(cursor);
			stations.add(station);
			cursor.moveToNext();
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

	// Delete all stations from the database;
	public void deleteAllStations() {
		database.delete(FavouritesSQLite.TABLE_FAVOURITES, null, null);
	}

	// Creating new Station object with the data of the current row of the
	// database
	private GPSStation cursorToStation(Cursor cursor) {
		GPSStation station = new GPSStation();

		// Getting all columns of the row and setting them to a Station object
		station.setId(cursor.getString(0));
		station.setName(cursor.getString(1));
		station.setLat(cursor.getString(2));
		station.setLon(cursor.getString(3));
		station.setCodeO(cursor.getString(4));

		return station;
	}
}