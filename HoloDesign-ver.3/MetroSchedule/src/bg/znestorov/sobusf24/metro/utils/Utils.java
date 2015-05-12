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

	/**
	 * Format metro station name accordingly
	 * 
	 * @param name
	 *            the name of the metro station
	 * @return the formatted name
	 */
	public static String formatName(String name) {

		String formattedName = name.toUpperCase();
		if ("лерпняр. дпсфаю".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ дпсфаю";
		}
		if ("лерпняр. хяйзпяйн ьняе".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ хяйзпяйн ьняе";
		}
		if ("лерпняр. янтхияйю яберю цнпю".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ янтхияйю яберю цнпю";
		}
		if ("лерпняр. керхые янтхъ".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ керхые янтхъ";
		}
		if ("лерпняр. ахгмея оюпй янтхъ".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ ахгмея оюпй янтхъ";
		}
		if ("лерпняр. юйюд. юк. ренднпнб-аюкюм".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ юйюд. юк. ренднпнб-аюкюм";
		}
		if ("лерпняр. юкейяюмдзп люкхмнб".equals(formattedName)) {
			formattedName = "лерпнярюмжхъ юкейяюмдзп люкхмнб";
		}

		return formattedName;
	}

}