package bg.znestorov.sofbus24.metro.stations;

import java.util.logging.Logger;

public class MetroStationsMain {

	public static MetroDirectionTransfer saveStationsInfoToAFile(
			Logger logger) {
		String htmlResponse = HtmlRequest.retrieveStationsInfo(logger);

		if ("".equals(htmlResponse)) {
			logger.warning(
					"Problem with retrieving information about the METRO directions...");
			return null;
		}

		MetroDirectionTransfer mdt = HtmlResult.getMetroDirections(logger,
				htmlResponse);

		if (mdt == null) {
			logger.warning("Problem with parsing the information from SGKT...");
			return null;
		}

		long startTime = System.currentTimeMillis();
		WriteDirectionToXMLFile.saveToXMLFile(logger, mdt);
		long endTime = System.currentTimeMillis();

		logger.info("The information is saved to XML file for "
				+ ((endTime - startTime) / 1000) + " seconds\n");

		return mdt;
	}

}
