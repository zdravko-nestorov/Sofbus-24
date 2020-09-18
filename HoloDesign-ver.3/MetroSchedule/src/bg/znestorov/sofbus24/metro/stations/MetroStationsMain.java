package bg.znestorov.sofbus24.metro.stations;

import bg.znestorov.sofbus24.metro.utils.Constants;

import java.util.logging.Logger;

public class MetroStationsMain {

	public static MetroDirectionTransfer saveStationsInfoToAFile(
			Logger logger) {
		MetroDirectionTransfer mdt = new MetroDirectionTransfer();

		for (int lineNo = 0; lineNo < Constants.METRO_SCHEDULE_URL.length; lineNo++) {
			String htmlResponse = HtmlRequest.retrieveStationsInfo(logger, lineNo);

			if ("".equals(htmlResponse)) {
				logger.warning(
						"Problem with retrieving information about the METRO directions...");
				return null;
			}

			MetroDirectionTransfer mdtPerLine = HtmlResult.getMetroDirections(logger,
					htmlResponse, lineNo);
			mdt.getDirectionsList().addAll(mdtPerLine.getDirectionsList());
		}

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
