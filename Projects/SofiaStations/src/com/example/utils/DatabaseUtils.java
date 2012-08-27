package com.example.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.station_database.FavouritesDataSource;
import com.example.station_database.StationsDataSource;
import com.example.station_database.StationsSQLite;

public class DatabaseUtils {

	// Copy DB from internal memory to the SD Card
	@SuppressWarnings("resource")
	public static void copyDatabase(Context context) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//com.example.sofiastations//databases//stations.db";
				String backupDBPath = "stations.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

				Toast.makeText(context, "—“¿¬¿ " + backupDB.toString(),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {
			Toast.makeText(context, "Õ≈ —“¿¬¿ " + e.toString(),
					Toast.LENGTH_LONG).show();

		}
	}

	// Generating DB with all stations from XML file (slow operation)
	// public static void generateStations(Context context) {
	//
	// StationsDataSource datasource = new StationsDataSource(context);
	//
	// datasource.open();
	// try {
	// InputSource inputSrc = new InputSource(context.getResources()
	// .openRawResource(R.raw.stations));
	// XPath xpath = XPathFactory.newInstance().newXPath();
	// String expression = "//station/@*";
	// NodeList nodes = (NodeList) xpath.evaluate(expression, inputSrc,
	// XPathConstants.NODESET);
	//
	// GPSStation gpsStation = new GPSStation();
	//
	// for (int i = 0; i < nodes.getLength(); i = i + 4) {
	// gpsStation.setId(nodes.item(i).getTextContent());
	// gpsStation.setName(nodes.item(i + 1).getTextContent());
	// gpsStation.setLat(nodes.item(i + 2).getTextContent());
	// gpsStation.setLon(nodes.item(i + 3).getTextContent());
	//
	// datasource.createStation(gpsStation);
	// }
	// } catch (Exception e) {
	// Log.d("DatabaseUtils", "EXCEPTION");
	// }
	// datasource.close();
	// }

	// Copy the DB from the APK to the internal memory of the phone
	public static void createStationsDatabase(Context context) {
		StationsSQLite myDbHelper = new StationsSQLite(context);
		try {
			myDbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}
	}

	// Delete all records from the Station DB (the DB remains empty - it is not
	// deleted)
	public static void deleteStationDatabase(Context context) {
		StationsDataSource datasource = new StationsDataSource(context);
		datasource.open();
		datasource.deleteAllStations();
		datasource.close();
	}

	// Delete all records from the Favorites DB (the DB remains empty - it is
	// not deleted)
	public static void deleteFavouriteDatabase(Context context) {
		FavouritesDataSource datasource = new FavouritesDataSource(context);
		datasource.open();
		datasource.deleteAllStations();
		datasource.close();
	}
}
