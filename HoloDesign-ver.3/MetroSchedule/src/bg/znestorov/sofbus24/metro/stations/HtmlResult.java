package bg.znestorov.sofbus24.metro.stations;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bg.znestorov.sofbus24.metro.utils.Constants;
import bg.znestorov.sofbus24.metro.utils.Utils;

public class HtmlResult {

	/**
	 * Create a MetroDirectionTransfer object and filling it with all
	 * information from the source code:
	 * <ul>
	 * <li>Direction id</li>
	 * <li>Direction name</li>
	 * <li>List with all stations ids in each direction</li>
	 * <li>List with all stations names in each direction</li>
	 * </ul>
	 * In case of an error while parsing the information - returns null.
	 * 
	 * @param htmlSrc
	 *            the HTML response
	 * @param lineNo the metro line (M1-M2 or M3)
	 * @return a MetroDirectionTransfer object with all needed information about
	 *         the directions and the stations
	 */
	public static MetroDirectionTransfer getMetroDirections(Logger logger,
			String htmlSrc, int lineNo) {

		logger.info(
				"Start parsing the information about the Metro directions and schedule...");
		long startTime = System.currentTimeMillis();

		MetroDirectionTransfer mdt = new MetroDirectionTransfer();
		String[] htmlSrcParts = htmlSrc.split(Constants.METRO_REGEX_PARTS);
		int htmlSrcPartsCount = (lineNo == 0 ? 9 : 5);

		if (htmlSrcParts.length >= htmlSrcPartsCount) {
			// Find the ID and the NAME of each direction
			Pattern directionPattern = Pattern
					.compile(Constants.METRO_REGEX_DIRECTIONS);
			Matcher directionMatcher = directionPattern
					.matcher(htmlSrcParts[0]);

			while (directionMatcher.find()) {
				MetroDirection md = new MetroDirection();
				md.setId(directionMatcher.group(2));
				md.setName(directionMatcher.group(3));

				mdt.getDirectionsList().add(md);
			}

			try {
				logger.info(mdt.getDirectionsListSize() + " directions found.");
			} catch (Exception e) {
				logger.info("0 directions found.");
			}

			// Fill each MetroDirection with the stations
			for (int i = 0; i < mdt.getDirectionsListSize(); i++) {

				if (htmlSrcParts.length >= i + 1) {
					String[] htmlSrcStationParts = htmlSrcParts[i + 1]
							.split(Constants.METRO_REGEX_STATION_PARTS);

					if (htmlSrcStationParts.length > 0) {
						setStations(mdt, i, htmlSrcStationParts[0]);
					}
				}
			}

			try {
				logger.info(mdt.getDirectionsList().get(0).getStations().size()
						+ " stations per direction found.");
			} catch (Exception e) {
				logger.info("0 stations found.");
			}
		}

		long endTime = System.currentTimeMillis();
		logger.info("The HTTP response information is processed for "
				+ ((endTime - startTime) / 1000) + " seconds");

		int mdtSize = lineNo == 0 ? 4 : 2;
		if (mdt.getDirectionsListSize() >= mdtSize) {
			return mdt;
		} else {
			return null;
		}
	}

	/**
	 * Set the stations to each direction by parsing the HTML code with a REGEX
	 * 
	 * @param mdt
	 *            the MetroDirectionTransfer object, which contains only the
	 *            directions (without any station)
	 * @param directionNumber
	 *            the number of the direction
	 * @param htmlSrcPart
	 *            the part of the HTML code, which is containing the stations
	 *            info
	 */
	private static void setStations(MetroDirectionTransfer mdt,
			int directionNumber, String htmlSrcPart) {
		Pattern stationPattern = Pattern
				.compile(Constants.METRO_REGEX_STATIONS);
		Matcher stationMatcher = stationPattern.matcher(htmlSrcPart);

		while (stationMatcher.find()) {
			mdt.getDirectionsList().get(directionNumber).getStations().put(
					stationMatcher.group(1),
					Utils.formatName(stationMatcher.group(2)));
		}
	}

}
