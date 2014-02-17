package bg.znestorov.sofbus24.metro.main;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import bg.znestorov.sobusf24.metro.utils.LogFormatter;
import bg.znestorov.sofbus24.metro.stations.MetroMain;

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
		MetroMain.saveStationsInfoToAFile(logger);
	}
}
