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
		if ("МЕТРОСТ. ДРУЖБА".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ ДРУЖБА";
		}
		if ("МЕТРОСТ. ИСКЪРСКО ШОСЕ".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ ИСКЪРСКО ШОСЕ";
		}
		if ("МЕТРОСТ. СОФИЙСКА СВЕТА ГОРА".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ СОФИЙСКА СВЕТА ГОРА";
		}
		if ("МЕТРОСТ. ЛЕТИЩЕ СОФИЯ".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ ЛЕТИЩЕ СОФИЯ";
		}
		if ("МЕТРОСТ. БИЗНЕС ПАРК СОФИЯ".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ БИЗНЕС ПАРК СОФИЯ";
		}
		if ("МЕТРОСТ. АКАД. АЛ. ТЕОДОРОВ-БАЛАН".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ АКАД. АЛ. ТЕОДОРОВ-БАЛАН";
		}
		if ("МЕТРОСТ. АЛЕКСАНДЪР МАЛИНОВ".equals(formattedName)) {
			formattedName = "МЕТРОСТАНЦИЯ АЛЕКСАНДЪР МАЛИНОВ";
		}

		return formattedName;
	}

	public static String formatDirection(String name) {

		String formattedName;
		if ("м.Летище София-м.Обеля-м.Джеймс Баучер".equals(name)
				|| "м.Бизнес Парк-м.Обеля-м.Джеймс Баучер".equals(name)) {
			formattedName = "м.Цариградско Шосе-м.Обеля-м.Джеймс Баучер";
		} else if ("м.Джеймс Баучер-м.Обеля-м.Летище София".equals(name)
				|| "м.Джеймс Баучер-м.Обеля-м.Бизнес Парк".equals(name)) {
			formattedName = "м.Цариградско Шосе-м.Обеля-м.Джеймс Баучер";
		} else {
			formattedName = name;
		}

		return formattedName;
	}

}
