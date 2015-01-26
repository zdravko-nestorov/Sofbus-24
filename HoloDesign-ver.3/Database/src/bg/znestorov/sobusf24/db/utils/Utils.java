package bg.znestorov.sobusf24.db.utils;

import java.util.Locale;

public class Utils {

	public static String removeSpaces(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\s+", "");
		} else {
			value = "";
		}

		return value;
	}

	public static String getValueBefore(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.indexOf(regex));
		} else {
			return value;
		}
	}

	public static String getValueBeforeLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.lastIndexOf(regex));
		} else {
			return value;
		}
	}

	public static String getValueAfter(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.indexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	public static String getValueAfterLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.lastIndexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	public static String formatNumberOfDigits(String input, int outputLength) {
		String formatType = String.format(Locale.getDefault(), "%%0%dd", outputLength);

		try {
			input = String.format(formatType, Integer.parseInt(input));
		} catch (Exception e) {
		}

		return input;
	}

	public static String formatDirectionName(String directionName) {
		if (directionName != null && !"".equals(directionName)) {
			String[] directionNameParts = directionName.trim().split("-");

			switch (directionNameParts.length) {
			case 1:
				directionName = directionNameParts[0];

				break;
			case 2:
			case 4:
				directionNameParts[0] = directionNameParts[0].trim();
				directionNameParts[0] = directionNameParts[0].replaceAll("\\(", " (");

				directionNameParts[1] = directionNameParts[1].trim();
				directionNameParts[1] = getValueBeforeLast(directionNameParts[1], "(");
				directionNameParts[1] = getValueBefore(directionNameParts[1], "/");

				directionName = directionNameParts[0] + " - " + directionNameParts[1];
				directionName = directionName.replaceAll(" +", " ");

				break;
			case 3:
			case 6:
				boolean isDirectionThreeParts = true;
				if (directionNameParts[0].equals(directionNameParts[2])) {
					isDirectionThreeParts = false;
				}

				directionNameParts[0] = directionNameParts[0].trim();
				directionNameParts[0] = directionNameParts[0].replaceAll("\\(", " (");

				directionNameParts[1] = directionNameParts[1].trim();

				directionNameParts[2] = directionNameParts[2].trim();
				directionNameParts[2] = getValueBeforeLast(directionNameParts[2], "(");
				directionNameParts[2] = getValueBefore(directionNameParts[2], "/");

				if (isDirectionThreeParts) {
					directionName = directionNameParts[0] + " - " + directionNameParts[1] + " - " + directionNameParts[2];
				} else {
					directionName = directionNameParts[0] + " - " + directionNameParts[1];
				}
				directionName = directionName.replaceAll(" +", " ");

				break;
			default:
				break;
			}
		} else {
			directionName = "";
		}

		// Special cases
		directionName = directionName.replaceAll(" - 1 - ", "  1 - ");
		directionName = directionName.replaceAll(" - 1", " 1");
		directionName = directionName.replaceAll(" - 2 - ", " 2 - ");
		directionName = directionName.replaceAll(" - 2", " 2");
		directionName = directionName.replaceAll(" - 3 - ", " 3 - ");
		directionName = directionName.replaceAll(" - 3", " 3");
		directionName = directionName.replaceAll(" - 4 - ", " 4 - ");
		directionName = directionName.replaceAll(" - 4", " 4");
		directionName = directionName.replaceAll(" - 5 - ", " 5 - ");
		directionName = directionName.replaceAll(" - 5", " 5");
		directionName = directionName.replaceAll(" - 8 - ", " 8 - ");
		directionName = directionName.replaceAll(" - 8", " 8");
		directionName = directionName.replaceAll("6 - ", "6-");
		directionName = directionName.replaceAll(" - Г", " Г");
		directionName = directionName.replaceAll("н - з", "н-з");
		directionName = directionName.replaceAll("М - ция", "М-ция");
		directionName = directionName.replaceAll("Ц - р", "Ц-р");
		directionName = directionName.replaceAll("Метр\\.", "Метростанция ");
		directionName = directionName.replaceAll("Метрост\\.", "Метростанция ");
		directionName = directionName.replaceAll("Зап\\.", "Западен ");
		directionName = directionName.replaceAll("Ц\\. гара", "Централна гара");
		directionName = directionName.replaceAll("Ц\\.гара", "Централна гара");
		directionName = directionName.replaceAll("Ц\\. Гара", "Централна гара");
		directionName = directionName.replaceAll("Ц\\.Гара", "Централна гара");
		directionName = directionName.replaceAll("Ст\\.Град", "Студентски Град");
		directionName = directionName.replaceAll("БУЛ\\. НИКОЛА ПЕТКОВ", "бул\\. Никола Петков");
		directionName = directionName.replaceAll("УЛ\\. ДОБРОТИЧ", "ул\\. Добротич");
		directionName = directionName.replaceAll("СЕЛО ДОЛНИ ПАСАРЕЛ", "Село Долни Пасарел");
		directionName = directionName.replaceAll("АВТОБАЗА ИСКЪР - УЛ\\. ГЕН\\. ГУРКО", "Автобаза Искър - ул\\. Генерал Гурко");
		directionName = directionName.replaceAll("Кв\\. Д\\.Миленков - Кв\\. Бусманци", "кв\\. Д\\. Миленков - кв\\. Бусманци");
		directionName = directionName.replaceAll("МАНАСТИР СВ\\. МИНА", "Манастир Света Мина");
		directionName = directionName.replaceAll("Ж\\.К\\.ГОЦЕ ДЕЛЧЕВ", "ж\\.к\\. Гоце Делчев");
		directionName = directionName.replaceAll("ПЛ\\. ОРЛОВ МОСТ", "Площад Орлов Мост");
		directionName = directionName.replaceAll("ЛИФТА - ДРАГАЛЕВЦИ", "Лифта Драгалевци");
		directionName = directionName.replaceAll("СУ СВ\\.КЛИМЕНТ ОХРИДСКИ - СТУДЕНТСКИ ГРАД", "СУ Свети Климент Охридски - Студентски Град");
		directionName = directionName.replaceAll("СУ Св\\. Климент Охридски - СТУДЕНТСКИ ГРАД", "СУ Свети Климент Охридски - Студентски Град");
		directionName = directionName.replaceAll("ФОНДОВИ ЖИЛИЩА", "Фондови Жилища");
		directionName = directionName.replaceAll("СТУДЕНТСКИ ГРАД", "Студентски Град");
		directionName = directionName.replaceAll("В\\.З\\.Бонсови поляни", "в.з. Бонсови поляни");
		directionName = directionName.replaceAll("СЕЛО ЖЕЛЯВА", "село Желява");
		directionName = directionName.replaceAll("СЕЛО БАЛША", "Село Балша");
		directionName = directionName.replaceAll("КУЛИНАРЕН К - Т ПЕЙФИЛ", "Кулинарен комбинат Пейфил");
		directionName = directionName.replaceAll("СУ Св\\. Климент Охридски", "СУ Свети Климент Охридски");
		directionName = directionName.replaceAll(" ухо", " ж\\.к\\. Люлин 3 (ухо)");
		directionName = directionName.replaceAll("ДепоИскър", "Депо Искър");
		directionName = directionName.replaceAll("Ж\\.к\\.Люлин 1,2", "ж\\.к\\. Люлин 1,2 - бул. Илиянци (подлеза)");
		directionName = directionName.replaceAll("Ж\\.К\\.", "Ж\\.к\\.");
		directionName = directionName.replaceAll("Ж\\.К\\. ", "Ж\\.к\\.");
		directionName = directionName.replaceAll("Ж\\.к\\. ", "Ж\\.к\\.");
		directionName = directionName.replaceAll("Ж\\.к\\.", "ж\\.к\\. ");
		directionName = directionName.replaceAll("Кв\\. ", "Кв\\.");
		directionName = directionName.replaceAll("Кв\\.", "кв\\. ");
		directionName = directionName.replaceAll("Ул\\. ", "Ул\\.");
		directionName = directionName.replaceAll("Ул\\.", "ул\\. ");
		directionName = directionName.replaceAll("Пл\\. ", "Пл\\.");
		directionName = directionName.replaceAll("Пл\\.", "Площад ");
		directionName = directionName.replaceAll("С\\. ", "С\\.");
		directionName = directionName.replaceAll("С\\.", "село ");
		directionName = directionName.replaceAll("Бул\\. ", "Бул\\.");
		directionName = directionName.replaceAll("Бул\\.", "бул\\. ");
		directionName = directionName.replaceAll("Бл\\. ", "Бл\\.");
		directionName = directionName.replaceAll("Бл\\.", "бл\\. ");
		directionName = directionName.replaceAll("Св\\. ", "Св\\.");
		directionName = directionName.replaceAll("Св\\.", "Света ");

		// Special cases
		directionName = directionName.replaceAll("кв\\. Орландовци Гара Захарна фабрика", "кв\\. Орландовци - Гара Захарна фабрика");
		directionName = directionName.replaceAll("ул\\. Кораб планина Гара Захарна фабрика", "ул\\. Кораб планина - Гара Захарна фабрика");
		directionName = directionName.replaceAll("Автостанция Орландовци ГАРА КУРИЛО", "Автостанция Орландовци - ГАРА КУРИЛО");
		directionName = directionName.replaceAll("кв\\. Иваняне Гара Обеля", "кв\\. Иваняне - Гара Обеля");
		directionName = directionName.replaceAll("АП Малашевци Град Бухово", "АП Малашевци - Град Бухово");
		directionName = directionName.replaceAll("УМБАЛСМ Пирогов Гара Искър", "УМБАЛСМ Пирогов - Гара Искър");
		directionName = directionName.replaceAll("ЧИТАЛИЩЕ СВЕТЛИНА Гара Искър", "ЧИТАЛИЩЕ СВЕТЛИНА - Гара Искър");
		directionName = directionName.replaceAll("кв\\. Княжево Гара София север", "кв\\. Княжево - Гара София (север)");
		directionName = directionName.replaceAll("АВТОСТАНЦИЯ КНЯЖЕВО - село Мърчаево Толумска махала", "кв. Княжево - с. Мърчаево (Толумска махала)");
		if ("ж.к. Младост 1".equals(directionName)) {
			directionName = directionName.replaceAll("ж\\.к\\. Младост 1", "ж\\.к\\. Младост 1 - ж\\.к\\. Люлин 1,2");
		}

		directionName = directionName.trim().replaceAll("-", " - ");
		directionName = directionName.trim().replaceAll(" +", " ");
		
		// Final adjustments
		directionName = directionName.replaceAll(" - 1", " 1");
		directionName = directionName.replaceAll(" - 2", " 2");
		directionName = directionName.replaceAll(" - 3", " 3");
		directionName = directionName.replaceAll(" - 4", " 4");
		directionName = directionName.replaceAll(" - 5", " 5");
		directionName = directionName.replaceAll(" - 6", " 6");
		directionName = directionName.replaceAll(" - 7", " 7");
		directionName = directionName.replaceAll(" - 8", " 8");

		return directionName;
	}

	public static long getTime() {
		return System.currentTimeMillis();
	}
}
