package bg.znestorov.sofbus24.databases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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

	private static String LOG_TAG = "DatabaseUtils";

	/**
	 * Create an empty database on the system and rewrites it with the ready
	 * database
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void createStationsDatabase(Context context) {
		StationsSQLite myDbHelper = new StationsSQLite(context);
		myDbHelper.createDataBase();
	}

	/**
	 * Delete all records from the Station DB (the DB remains empty - it is not
	 * deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteStationDatabase(Context context) {
		StationsDataSource stationsDatasource = new StationsDataSource(context);
		stationsDatasource.open();
		stationsDatasource.deleteAllStations();
		stationsDatasource.close();
	}

	/**
	 * Delete all records from the Favorites DB (the DB remains empty - it is
	 * not deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteFavouriteDatabase(Context context) {
		FavouritesDataSource favouritesDatasource = new FavouritesDataSource(
				context);
		favouritesDatasource.open();
		favouritesDatasource.deleteAllStations();
		favouritesDatasource.close();
	}

	/**
	 * Generates a stations dabatase from a XML file and copy it to the SD card,
	 * so be easily accessible for modifying and further actions
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void generateAndCopyStationsDB(Context context) {
		generateStationsDB(context);
		copyStationsDB(context);
	}

	/**
	 * Generating a DB with all stations by parsing them from a XML file. It is
	 * slow operation (between 2-3 mins)
	 * 
	 * @param context
	 *            the current activity context
	 */
	private static void generateStationsDB(Context context) {
		long startTime = System.currentTimeMillis();

		StationsDataSource datasource = new StationsDataSource(context);
		datasource.open();

		try {
			/*
			 * IMPORTANT: When uncomment this, create new folder "raw" in "res"
			 * and put there XML file with all stations in the right format,
			 * named "stations_coordinates.xml" and delete the line above
			 */
			// InputSource inputSrc = new
			// InputSource(context.getResources().openRawResource(R.raw.stations_coordinates));
			InputStream inputSrc = null;

			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "stations/station/@*";
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSrc,
					XPathConstants.NODESET);

			Station gpsStation = new Station();

			for (int i = 0; i < nodes.getLength(); i = i + 4) {
				gpsStation.setNumber(nodes.item(i).getTextContent());
				gpsStation.setName(nodes.item(i + 1).getTextContent());
				gpsStation.setLat(nodes.item(i + 2).getTextContent());
				gpsStation.setLon(nodes.item(i + 3).getTextContent());

				datasource.createStation(gpsStation);
			}
		} catch (Exception e) {
			Log.d("DatabaseUtils", "EXCEPTION");
		}
		datasource.close();

		long endTime = System.currentTimeMillis();
		Log.i(LOG_TAG, "The information is saved to XML files for "
				+ ((endTime - startTime) / 1000) + " seconds");
	}

	/**
	 * Copy the stations DB from internal phone memory to the SD Card (used to
	 * take the DB easily)
	 * 
	 * @param context
	 *            the current activity context
	 */
	@SuppressWarnings("resource")
	private static void copyStationsDB(Context context) {
		long startTime = System.currentTimeMillis();

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
						"ÁÀÇÀÒÀ Å ÊÎÏÈÐÀÍÀ ÓÑÏÅØÍÎ: " + backupDB.toString(),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {
			Toast.makeText(context,
					"ÁÀÇÀÒÀ ÍÅ Å ÊÎÏÈÐÀÍÀ ÓÑÏÅØÍÎ: " + e.toString(),
					Toast.LENGTH_LONG).show();

		}

		long endTime = System.currentTimeMillis();
		Log.i(LOG_TAG,
				"The stations database is copied from the internal memory to the SD card for "
						+ ((endTime - startTime) / 1000) + " seconds");
	}
}
