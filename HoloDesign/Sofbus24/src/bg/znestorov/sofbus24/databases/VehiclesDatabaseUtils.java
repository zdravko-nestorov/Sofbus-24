package bg.znestorov.sofbus24.databases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;

/**
 * Class containing all helping functions for creating the Vehicles DB from an
 * XML file, copying to the SD card, copying the DB from the assets and so on
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VehiclesDatabaseUtils {

	private static String LOG_TAG = "VehiclesDatabaseUtils";

	/**
	 * Create an empty database on the system and rewrites it with the ready
	 * database
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void createVehiclesDatabase(Context context) {
		VehiclesSQLite myDbHelper = new VehiclesSQLite(context);
		myDbHelper.createDataBase();
	}

	/**
	 * Delete all records from the Vehicles DB (the DB remains empty - it is not
	 * deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteVehiclesDatabase(Context context) {
		VehiclesDataSource vehiclesDatasource = new VehiclesDataSource(context);
		vehiclesDatasource.open();
		vehiclesDatasource.deleteAllVehicles();
		vehiclesDatasource.close();
	}

	/**
	 * Generates a vehicles dabatase from an XML file and copy it to the SD
	 * card, so be easily accessible for modifying and further actions
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void generateAndCopyStationsDB(Context context) {
		generateVehiclesDB(context);
		copyVehiclesDB(context);
	}

	/**
	 * Generating a DB with all vehicles by parsing them from an XML file. It is
	 * slow operation (between 2-3 mins)
	 * 
	 * @param context
	 *            the current activity context
	 */
	private static void generateVehiclesDB(Context context) {
		long startTime = System.currentTimeMillis();

		VehiclesDataSource datasource = new VehiclesDataSource(context);
		datasource.open();

		try {
			/*
			 * IMPORTANT: When uncomment this, create new folder "raw" in "res"
			 * and put there XML file with all stations in the right format,
			 * named "vehicles_list.xml" and delete the line above
			 */
			// InputSource inputSrc = new InputSource(context.getResources()
			// .openRawResource(R.raw.vehicles_list));
			InputSource inputSrc = null;

			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "vehicles/vehicle/@*";
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSrc,
					XPathConstants.NODESET);

			Vehicle vehicle = new Vehicle();

			for (int i = 0; i < nodes.getLength(); i = i + 3) {
				vehicle.setNumber(nodes.item(i + 1).getTextContent());
				vehicle.setType(VehicleType.valueOf(nodes.item(i)
						.getTextContent()));
				vehicle.setDirection(nodes.item(i + 2).getTextContent());

				datasource.createVehicle(vehicle);
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "EXCEPTION");
		}
		datasource.close();

		long endTime = System.currentTimeMillis();
		Log.i(LOG_TAG, "The information is saved to SQLite file for "
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
	private static void copyVehiclesDB(Context context) {
		long startTime = System.currentTimeMillis();

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "data//bg.znestorov.sofbus24.main//databases//vehicles.db";
				String backupDBPath = "vehicles.db";
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
