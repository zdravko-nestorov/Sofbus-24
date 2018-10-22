package bg.znestorov.sofbus24.metro.utils;

import java.util.List;

public class Utils {

	/**
	 * Convert a list object to a string, separated by comma
	 * 
	 * @param list
	 *            input list file
	 * @return a string represantion of the list, separated by comma
	 */
	public static String listToString(List<String> list) {
		if (list != null && !list.isEmpty()) {
			StringBuilder output = new StringBuilder("");

			for (String listElement : list) {
				output.append(listElement).append(",");
			}

			return output.substring(0, output.length() - 1);
		}

		return "";
	}

	/**
	 * Format metro station name accordingly
	 * 
	 * @param name
	 *            the name of the metro station
	 * @return the formatted name
	 */
	public static String formatName(String name) {

		String formattedName = name.toUpperCase();
		if ("ÌÅÒĞÎÑÒ. ÄĞÓÆÁÀ".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ÄĞÓÆÁÀ";
		}
		if ("ÌÅÒĞÎÑÒ. ÈÑÊÚĞÑÊÎ ØÎÑÅ".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ÈÑÊÚĞÑÊÎ ØÎÑÅ";
		}
		if ("ÌÅÒĞÎÑÒ. ÑÎÔÈÉÑÊÀ ÑÂÅÒÀ ÃÎĞÀ".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ÑÎÔÈÉÑÊÀ ÑÂÅÒÀ ÃÎĞÀ";
		}
		if ("ÌÅÒĞÎÑÒ. ËÅÒÈÙÅ ÑÎÔÈß".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ËÅÒÈÙÅ ÑÎÔÈß";
		}
		if ("ÌÅÒĞÎÑÒ. ÁÈÇÍÅÑ ÏÀĞÊ ÑÎÔÈß".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ÁÈÇÍÅÑ ÏÀĞÊ ÑÎÔÈß";
		}
		if ("ÌÅÒĞÎÑÒ. ÀÊÀÄ. ÀË. ÒÅÎÄÎĞÎÂ-ÁÀËÀÍ".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ÀÊÀÄ. ÀË. ÒÅÎÄÎĞÎÂ-ÁÀËÀÍ";
		}
		if ("ÌÅÒĞÎÑÒ. ÀËÅÊÑÀÍÄÚĞ ÌÀËÈÍÎÂ".equals(formattedName)) {
			formattedName = "ÌÅÒĞÎÑÒÀÍÖÈß ÀËÅÊÑÀÍÄÚĞ ÌÀËÈÍÎÂ";
		}

		return formattedName;
	}

	/**
	 * Used to format the name of a direction. It is used to change the
	 * direction name of stations, which are before the "Ìëàäîñò 1" metro
	 * station, because the trains passing through these stations can go to the
	 * Bussiness Park or the Airport
	 * 
	 * @param name
	 *            the name of the direction
	 * @return the formatted name of the direction
	 */
	public static String formatDirection(String name) {

		String formattedName;
		if ("ì.Ëåòèùå Ñîôèÿ-ì.Îáåëÿ-ì.Âèòîøà".equals(name)
				|| "ì.Áèçíåñ Ïàğê-ì.Îáåëÿ-ì.Âèòîøà".equals(name)) {
			formattedName = "ì.Ìëàäîñò 1-ì.Îáåëÿ-ì.Âèòîøà";
		} else if ("ì.Âèòîøà-ì.Îáåëÿ-ì.Ëåòèùå Ñîôèÿ".equals(name)
				|| "ì.Âèòîøà-ì.Îáåëÿ-ì.Áèçíåñ Ïàğê".equals(name)) {
			formattedName = "ì.Âèòîøà-ì.Îáåëÿ-ì.Ìëàäîñò 1";
		} else {
			formattedName = name;
		}

		return formattedName;
	}

	/**
	 * Tests if the supplied string is NULL or 0-length.
	 * 
	 * @param input
	 *            the input string
	 * @return TRUE if empty, otherwise FALSE
	 */
	public static boolean isEmpty(String input) {
		return input == null || input.length() == 0;
	}

}