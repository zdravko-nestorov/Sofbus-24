package bg.znestorov.sobusf24.metro.utils;

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

}
