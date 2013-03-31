package bg.znestorov.sofbus24.station_database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DatabaseUtils {

	// Copy DB from internal memory to the SD Card
	@SuppressWarnings("resource")
	public static void copyDatabase(Context context) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "databg.znestorov.sofbus24.maindatabasesstations.db";
				String backupDBPath = "stations.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

				Toast.makeText(context,
						"¡¿«¿“¿ ≈  Œœ»–¿Õ¿ ”—œ≈ÿÕŒ: " + backupDB.toString(),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {
			Toast.makeText(context,
					"¡¿«¿“¿ Õ≈ ≈  Œœ»–¿Õ¿ ”—œ≈ÿÕŒ: " + e.toString(),
					Toast.LENGTH_LONG).show();

		}
	}

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

	// Generating DB with all stations from XML file (slow operation)
	public static void generateStationsXML(Context context) {
		StationsDataSource datasource = new StationsDataSource(context);

		datasource.open();
		try {
			InputStream inputSrc = null;
			/*
			 * IMPORTANT: When uncomment this, create new folder "raw" in "res"
			 * and put there XML file with all stations in the right format,
			 * named "stations_XML.xml" and delete the line above
			 */
			// InputSource inputSrc = new InputSource(context.getResources()
			// .openRawResource(R.raw.stations_XML));

			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "station/@*";
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSrc,
					XPathConstants.NODESET);

			GPSStation gpsStation = new GPSStation();

			for (int i = 0; i < nodes.getLength(); i = i + 4) {
				gpsStation.setId(nodes.item(i).getTextContent());
				gpsStation.setName(nodes.item(i + 1).getTextContent());
				gpsStation.setLat(nodes.item(i + 2).getTextContent());
				gpsStation.setLon(nodes.item(i + 3).getTextContent());

				datasource.createStation(gpsStation);
			}
		} catch (Exception e) {
			Log.d("DatabaseUtils", "EXCEPTION");
		}
		datasource.close();
	}

	// Generating DB with all stations from XML file (slow operation)
	public static void generateStationsTEXT(Context context) {

		StationsDataSource datasource = new StationsDataSource(context);

		datasource.open();
		try {
			InputStream inputStream = null;
			/*
			 * IMPORTANT: When uncomment this, create new folder "raw" in "res"
			 * and put there XML file with all stations in the right format,
			 * named "stations_XML.xml" and delete the line above
			 */
			// InputStream inputStream = context.getResources().openRawResource(
			// R.raw.stations_TEXT);

			InputStreamReader inputreader = new InputStreamReader(inputStream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;

			while ((line = buffreader.readLine()) != null) {
				String[] temp = line.split("\"");

				GPSStation gpsStation = new GPSStation();

				if (temp.length > 6) {
					Log.d("TAG", temp[1]);
					gpsStation.setId(temp[1]);
					gpsStation.setName(temp[3]);
					gpsStation.setLat(temp[5]);
					gpsStation.setLon(temp[7]);

					datasource.createStation(gpsStation);
				}
			}

		} catch (Exception e) {
			Log.d("DatabaseUtils", "EXCEPTION");
		}
		datasource.close();
	}
}
