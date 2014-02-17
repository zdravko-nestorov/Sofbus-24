package bg.znestorov.sofbus24.metro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bg.znestorov.sofbus24.utils.Constants;

/**
 * It is used for parsing the information and filling an object with the
 * directions and all the stations for each of them
 * 
 * @author zanio
 * 
 */
public class HtmlResultMetroDirection {

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
	 * @return a MetroDirectionTransfer object with all needed information about
	 *         the directions and the stations
	 */
	public static MetroDirectionTransfer getMetroDirections(String htmlSrc) {
		MetroDirectionTransfer mdt = new MetroDirectionTransfer();
		String[] htmlSrcParts = htmlSrc.split(Constants.METRO_REGEX_PARTS);

		if (htmlSrcParts.length >= 5) {
			// Find the ID and the NAME of each direction
			Pattern directionPattern = Pattern
					.compile(Constants.METRO_REGEX_DIRECTIONS);
			Matcher directionMatcher = directionPattern
					.matcher(htmlSrcParts[0]);

			while (directionMatcher.find()) {
				MetroDirection md = new MetroDirection();
				md.setId(directionMatcher.group(1));
				md.setName(directionMatcher.group(2));

				mdt.getDirectionsList().add(md);
			}

			// Fill each MetroDirection with the stations
			for (int i = 0; i < mdt.getDirectionsListSize(); i++) {
				if (htmlSrcParts.length >= i * 2 + 1) {
					String[] htmlSrcStationParts = htmlSrcParts[i * 2 + 1]
							.split(Constants.METRO_REGEX_STATION_PARTS);

					if (htmlSrcStationParts.length > 0) {
						setStations(mdt, i, htmlSrcStationParts[0]);
					}
				}
			}
		}

		if (mdt.getDirectionsListSize() >= 2) {
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
			mdt.getDirectionsList().get(directionNumber).getStationNames()
					.add(stationMatcher.group(1));
			mdt.getDirectionsList().get(directionNumber).getStationNumbers()
					.add(stationMatcher.group(2));
		}
	}

}
