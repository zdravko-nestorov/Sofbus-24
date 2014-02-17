package bg.znestorov.sofbus24.metro.main;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.LogFormatter;
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
			fh = new FileHandler("log/MyLogFile.log");
			logger.addHandler(fh);
			LogFormatter formatter = new LogFormatter();
			fh.setFormatter(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("***RETRIEVE METRO SCHEDULE***\n");

		logger.info("Start creating the MAIN FILE with the METRO DIRECTIONS and STATIONS");
		MetroDirectionTransfer mdt = MetroStationsMain
				.saveStationsInfoToAFile(logger);

		if (mdt == null) {
			return;
		}

		logger.info("Start creating each STATION FILE with the METRO SCHEDULE");
		long startTime = System.currentTimeMillis();

		for (MetroDirection md : mdt.getDirectionsList()) {
			Iterator<Entry<String, String>> iterator = md.getStations()
					.entrySet().iterator();

			logger.info("RETRIEVE and PARSE information about direction: "
					+ md.getName());
			while (iterator.hasNext()) {
				Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator
						.next();
				MetroStationsScheduleMain.saveStationsScheduleToAFile(logger,
						md.getId(), mapEntry);

				logger.info("Start retrieving/parsing information about station name = "
						+ mapEntry.getValue()
						+ " and number = "
						+ mapEntry.getKey());
			}
		}

		long endTime = System.currentTimeMillis();
		logger.info("The information is saved to XML files for "
				+ ((endTime - startTime) / 1000) + " seconds");
	}
}
