package bg.znestorov.sofbus24.station_database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class StationsDataSource {

	// LogCat TAG
	private static final String TAG = "StationsDataSource";

	// Database fields
	private SQLiteDatabase database;
	private StationsSQLite dbHelper;
	private String[] allColumns = { StationsSQLite.COLUMN_ID,
			StationsSQLite.COLUMN_NAME, StationsSQLite.COLUMN_LAT,
			StationsSQLite.COLUMN_LON };

	// Radius of stations
	private static final int stations_count = 8;

	public StationsDataSource(Context context) {
		dbHelper = new StationsSQLite(context);
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
			values.put(StationsSQLite.COLUMN_ID, station.getId());
			values.put(StationsSQLite.COLUMN_NAME, station.getName());
			
			if (station.getLat() != null && !"".equals(station.getLat())) {
				values.put(StationsSQLite.COLUMN_LAT, station.getLat());
			} else {
				// Check for the station in the DB
				if (this.getStation(station.getId()) != null) {
					values.put(StationsSQLite.COLUMN_LAT,
							this.getStation(station.getId()).getLat());
				} else {
					station.setLat("EMPTY");
					values.put(StationsSQLite.COLUMN_LAT, station.getLat());
				}
			}

			if (station.getLon() != null && !"".equals(station.getLon())) {
				values.put(StationsSQLite.COLUMN_LON, station.getLon());
			} else {
				// Check for the station in the DB
				if (this.getStation(station.getId()) != null) {
					values.put(StationsSQLite.COLUMN_LON,
							this.getStation(station.getId()).getLon());
				} else {
					station.setLon("EMPTY");
					values.put(StationsSQLite.COLUMN_LON, station.getLon());
				}
			}

			// Insert the ContentValues data into the database
			database.insert(StationsSQLite.TABLE_STATIONS, null, values);

			// Selecting the row that contains the station data
			Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
					allColumns,
					StationsSQLite.COLUMN_ID + " = " + station.getId(), null,
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
		database.delete(StationsSQLite.TABLE_STATIONS, StationsSQLite.COLUMN_ID
				+ " = " + id, null);
	}

	// Get station gpsStation from the database
	public GPSStation getStation(GPSStation gpsStation) {
		// Selecting the row that contains the station data
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
				allColumns,
				StationsSQLite.COLUMN_ID + " = " + gpsStation.getId(), null,
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

	// Get station gpsStation from the database via stationCode
	public GPSStation getStation(String stationCode) {
		// Selecting the row that contains the station data
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
				allColumns, StationsSQLite.COLUMN_ID + " = " + stationCode,
				null, null, null, null);

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

		// Selecting all fields of the TABLE_STATIONS
		Cursor cursor = database.query(StationsSQLite.TABLE_STATIONS,
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
		database.delete(StationsSQLite.TABLE_STATIONS, null, null);
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

		return station;
	}

	// Making sorting select with Location object
	public List<GPSStation> getClosestStations(Location location) {
		List<GPSStation> stations = new ArrayList<GPSStation>();

		String select = "SELECT * FROM stations ORDER BY (ABS("
				+ StationsSQLite.COLUMN_LAT + " - " + location.getLatitude()
				+ ")" + " + ABS(" + StationsSQLite.COLUMN_LON + " - "
				+ location.getLongitude() + ")) ASC";

		Cursor cursor = database.rawQuery(select, null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		int br = 0;
		while (br < stations_count) {
			GPSStation station = cursorToStation(cursor);
			stations.add(station);
			cursor.moveToNext();
			br++;
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

	// Making sorting select with GeoPoint object
	public List<GPSStation> getClosestStations(GeoPoint geoPoint) {
		List<GPSStation> stations = new ArrayList<GPSStation>();

		String select = "SELECT * FROM stations ORDER BY (ABS("
				+ StationsSQLite.COLUMN_LAT + " - " + geoPoint.getLatitudeE6()
				/ 1E6 + ")" + " + ABS(" + StationsSQLite.COLUMN_LON + " - "
				+ geoPoint.getLongitudeE6() / 1E6 + ")) ASC";

		Cursor cursor = database.rawQuery(select, null);

		// Iterating the cursor and fill the empty List<Station>
		cursor.moveToFirst();
		int br = 0;
		while (br < stations_count) {
			GPSStation station = cursorToStation(cursor);
			stations.add(station);
			cursor.moveToNext();
			br++;
		}

		// Closing the cursor
		cursor.close();

		return stations;
	}

}