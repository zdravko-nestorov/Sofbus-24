package bg.znestorov.sofbus24.metro.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.Constants;
import bg.znestorov.sobusf24.metro.utils.LogFormatter;
import bg.znestorov.sofbus24.metro.schedule.MetroStation;
import bg.znestorov.sofbus24.metro.schedule.MetroStationsScheduleMain;
import bg.znestorov.sofbus24.metro.stations.MetroDirection;
import bg.znestorov.sofbus24.metro.stations.MetroDirectionTransfer;
import bg.znestorov.sofbus24.metro.stations.MetroStationsMain;

public class RetrieveInfoMain {

	private static Logger logger = Logger
			.getLogger("***RETRIEVE METRO SCHEDULE***");
	private static FileHandler fh;

	public static void main(String[] args) {
		try {
			logger.setUseParentHandlers(false);
			fh = new FileHandler(Constants.METRO_LOG_FILE);
			logger.addHandler(fh);
			LogFormatter formatter = new LogFormatter();
			fh.setFormatter(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("***RETRIEVE METRO SCHEDULE***\n");

		logger.info("Load the properties file...");
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(Constants.METRO_PROPERTIES_FILE);
			prop.load(input);

			logger.info("Start creating the MAIN FILE with the METRO DIRECTIONS and STATIONS");
			MetroDirectionTransfer mdt = MetroStationsMain
					.saveStationsInfoToAFile(logger);

			if (mdt == null) {
				return;
			}

			logger.info("Start creating each STATION FILE with the METRO SCHEDULE");
			long startTime = System.currentTimeMillis();

			// Contains all metro directions. Each direction contains a list of
			// metro stations
			ArrayList<ArrayList<MetroStation>> metroDirectionsStations = new ArrayList<ArrayList<MetroStation>>();

			// Iterate over all metro directions and retrieve the information
			// for each station
			for (MetroDirection md : mdt.getDirectionsList()) {
				Iterator<Entry<String, String>> iterator = md.getStations()
						.entrySet().iterator();

				// Contains all metro stations for the choosen direction
				ArrayList<MetroStation> metroStations = new ArrayList<MetroStation>();

				logger.info("RETRIEVE and PARSE information about direction: "
						+ md.getName());
				while (iterator.hasNext()) {
					Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator
							.next();

					logger.info("Start retrieving/parsing information about station name = "
							+ mapEntry.getValue()
							+ " and number = "
							+ mapEntry.getKey());

					// Retrieve the information (schedule) for each station
					MetroStation ms = MetroStationsScheduleMain
							.retrieveStationsSchedule(logger, md.getId(),
									md.getName(), mapEntry);
					if (ms != null) {
						metroStations.add(ms);
					}
				}

				// Build the metro directions stations list
				metroDirectionsStations.add(metroStations);
			}

			// Manipulate the results, so merge the information from different
			// stations with a same number
			MetroStationsScheduleMain.saveStationsScheduleToAFile(logger,
					metroDirectionsStations, prop);

			long endTime = System.currentTimeMillis();
			logger.info("The information is saved to XML files for "
					+ ((endTime - startTime) / 1000) + " seconds");
		} catch (IOException ex) {
			logger.warning("Problem with loading the properties file!");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.warning("Problem with closing the properties file!");
				}
			}
		}
	}
}
