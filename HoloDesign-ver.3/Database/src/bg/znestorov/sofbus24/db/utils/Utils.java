package bg.znestorov.sofbus24.db.utils;

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

  /**
   * Removes leading zeroes, but leaves one if necessary (i.e. it wouldn't just turn "0" to a blank string)
   *
   * @param value
   * @return
   */
  public static String removeLeadingZeros(String value) {
    if (value != null && !"".equals(value)) {
      value = value.replaceFirst("^0+(?!$)", "");
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

  public static String getOnlyDigits(String value) {
    if (value != null && !value.isEmpty()) {
      value = value.replaceAll("\\D+", "");
    } else {
      value = "";
    }

    return value;
  }

  public static String getOnlyChars(String value) {
    if (value != null && !value.isEmpty()) {
      value = value.replaceAll("\\d+", "");
    } else {
      value = "";
    }

    return value;
  }

  public static String formatNumberOfDigits(String input, int outputLength) {
    String formatType = String.format(Locale.getDefault(), "%%0%dd",
        outputLength);

    try {
      input = String.format(formatType, Integer.parseInt(input));
    } catch (Exception e) {
    }

    return input;
  }

  public static String formatDirectionName(String directionName) {
    if (directionName != null && !"".equals(directionName)) {

      /*
       * Here is the place to put all formatting for vehicle directions
       * when there is a chance after the split to produce equal results
       * for different vehicles
       */
      directionName = directionName.trim();

      // Problem with 111 and 309
      if (directionName.contains("Бул.Илиянци-подлеза")) {
        directionName = directionName.replaceAll(
            "Бул\\.Илиянци-подлеза",
            "бул\\. Илиянци \\(подлеза\\)");
      }
      if (directionName.contains("Ж.к.Люлин-1,2")) {
        directionName = directionName.replaceAll("Ж\\.к\\.Люлин-1,2",
            "ж\\.к\\. Люлин 1,2");
      }
      if (directionName.contains("Ж.к. Младост-1")) {
        directionName = directionName.replaceAll("Ж\\.к\\. Младост-1",
            "ж\\.к\\. Младост 1");
      }

      String[] directionNameParts = directionName.trim().split("-");

      switch (directionNameParts.length) {
        case 1:
          directionName = directionNameParts[0];

          break;
        case 2:
        case 4:
          directionNameParts[0] = directionNameParts[0].trim();
          directionNameParts[0] = directionNameParts[0].replaceAll("\\(",
              " (");

          directionNameParts[1] = directionNameParts[1].trim();
          directionNameParts[1] = getValueBeforeLast(
              directionNameParts[1], "(");
          directionNameParts[1] = getValueBefore(directionNameParts[1],
              "/");

          directionName = directionNameParts[0] + " - "
              + directionNameParts[1];
          directionName = directionName.replaceAll(" +", " ");

          break;
        case 3:
        case 6:
          boolean isDirectionThreeParts = true;
          if (directionNameParts[0].equals(directionNameParts[2])) {
            isDirectionThreeParts = false;
          }

          directionNameParts[0] = directionNameParts[0].trim();
          directionNameParts[0] = directionNameParts[0].replaceAll("\\(",
              " (");

          directionNameParts[1] = directionNameParts[1].trim();

          directionNameParts[2] = directionNameParts[2].trim();
          directionNameParts[2] = getValueBeforeLast(
              directionNameParts[2], "(");
          directionNameParts[2] = getValueBefore(directionNameParts[2],
              "/");

          if (isDirectionThreeParts) {
            directionName = directionNameParts[0] + " - "
                + directionNameParts[1] + " - "
                + directionNameParts[2];
          } else {
            directionName = directionNameParts[0] + " - "
                + directionNameParts[1];
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
    directionName = directionName.replaceAll("&quot;", "");
    directionName = directionName.replaceAll(" - 1 - ", "-1 - ");
    directionName = directionName.replaceAll(" - 1", " 1");
    directionName = directionName.replaceAll("-1", " 1");
    directionName = directionName.replaceAll(" - 2 - ", "-2 - ");
    directionName = directionName.replaceAll(" - 2", " 2");
    directionName = directionName.replaceAll(" - 3 - ", "-3 - ");
    directionName = directionName.replaceAll(" - 3", " 3");
    directionName = directionName.replaceAll(" - 4 - ", "-4 - ");
    directionName = directionName.replaceAll(" - 4", " 4");
    directionName = directionName.replaceAll(" - 5 - ", "-5 - ");
    directionName = directionName.replaceAll(" - 5", " 5");
    directionName = directionName.replaceAll(" - 8 - ", "-8 - ");
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
    directionName = directionName.replaceAll("Ст\\.Град",
        "Студентски Град");
    directionName = directionName.replaceAll("БУЛ\\. НИКОЛА ПЕТКОВ",
        "бул\\. Никола Петков");
    directionName = directionName.replaceAll("УЛ\\. ДОБРОТИЧ",
        "ул\\. Добротич");
    directionName = directionName.replaceAll("СЕЛО ДОЛНИ ПАСАРЕЛ",
        "Село Долни Пасарел");
    directionName = directionName.replaceAll(
        "АВТОБАЗА ИСКЪР - УЛ\\. ГЕН\\. ГУРКО",
        "Автобаза Искър - ул\\. Генерал Гурко");
    directionName = directionName.replaceAll(
        "Кв\\. Д\\.Миленков - Кв\\. Бусманци",
        "кв\\. Д\\. Миленков - кв\\. Бусманци");
    directionName = directionName.replaceAll("МАНАСТИР СВ\\. МИНА",
        "Манастир Света Мина");
    directionName = directionName.replaceAll("Ж\\.К\\.ГОЦЕ ДЕЛЧЕВ",
        "ж\\.к\\. Гоце Делчев");
    directionName = directionName.replaceAll("ПЛ\\. ОРЛОВ МОСТ",
        "Площад Орлов Мост");
    directionName = directionName.replaceAll("ЛИФТА - ДРАГАЛЕВЦИ",
        "Лифта Драгалевци");
    directionName = directionName.replaceAll(
        "СУ СВ\\.КЛИМЕНТ ОХРИДСКИ - СТУДЕНТСКИ ГРАД",
        "СУ Свети Климент Охридски - Студентски Град");
    directionName = directionName.replaceAll(
        "СУ Св\\. Климент Охридски - СТУДЕНТСКИ ГРАД",
        "СУ Свети Климент Охридски - Студентски Град");
    directionName = directionName.replaceAll("ФОНДОВИ ЖИЛИЩА",
        "Фондови Жилища");
    directionName = directionName.replaceAll("СТУДЕНТСКИ ГРАД",
        "Студентски Град");
    directionName = directionName.replaceAll("В\\.З\\.Бонсови поляни",
        "в.з. Бонсови поляни");
    directionName = directionName.replaceAll("СЕЛО ЖЕЛЯВА", "село Желява");
    directionName = directionName.replaceAll("СЕЛО БАЛША", "Село Балша");
    directionName = directionName.replaceAll("КУЛИНАРЕН К - Т ПЕЙФИЛ",
        "Кулинарен комбинат Пейфил");
    directionName = directionName.replaceAll("СУ Св\\. Климент Охридски",
        "СУ Свети Климент Охридски");
    directionName = directionName.replaceAll(
        "УМБАЛ Света Анна - ухо - ж\\.к\\. Гоце Делчев",
        "УМБАЛ Света Анна - ж\\.к\\. Гоце Делчев");
    directionName = directionName.replaceAll(
        "ж\\.к\\. Гоце Делчев - УМБАЛ Света Анна - ухо",
        "ж\\.к\\. Гоце Делчев - УМБАЛ Света Анна");
    directionName = directionName.replaceAll(" ухо", " ж\\.к\\. Люлин 3");
    directionName = directionName.replaceAll("ДепоИскър", "Депо Искър");
    directionName = directionName.replaceAll("Ж\\.к\\.Люлин 1,2",
        "ж\\.к\\. Люлин 1,2");
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
    directionName = directionName.replaceAll(
        "кв\\. Орландовци Гара Захарна фабрика",
        "кв\\. Орландовци - Гара Захарна фабрика");
    directionName = directionName.replaceAll(
        "ул\\. Кораб планина Гара Захарна фабрика",
        "ул\\. Кораб планина - Гара Захарна фабрика");
    directionName = directionName.replaceAll(
        "Автостанция Орландовци ГАРА КУРИЛО",
        "Автостанция Орландовци - ГАРА КУРИЛО");
    directionName = directionName.replaceAll("кв\\. Иваняне Гара Обеля",
        "кв\\. Иваняне - Гара Обеля");
    directionName = directionName.replaceAll("АП Малашевци Град Бухово",
        "АП Малашевци - Град Бухово");
    directionName = directionName.replaceAll("УМБАЛСМ Пирогов Гара Искър",
        "УМБАЛСМ Пирогов - Гара Искър");
    directionName = directionName.replaceAll("ЧИТАЛИЩЕ СВЕТЛИНА Гара Искър",
        "ЧИТАЛИЩЕ СВЕТЛИНА - Гара Искър");
    directionName = directionName.replaceAll("жк.Младост 4",
        "ж.к. Младост 4");

    if ("ж.к. Младост 1".equals(directionName)) {
      directionName = directionName.replaceAll("ж\\.к\\. Младост 1",
          "ж\\.к\\. Младост 1 - ж\\.к\\. Люлин 1,2");
    }

    directionName = directionName.trim().replaceAll("-", " - ");
    directionName = directionName.trim().replaceAll(" +", " ");

    // Special cases
    if (directionName.contains(" - временна")) {
      directionName = directionName.replaceAll(" - временна",
          "-временна");
    }

    // BUS #7
    if (directionName.contains("ЧИТАЛИЩЕ СВЕТЛИНА")) {
      directionName = directionName.replaceAll("ЧИТАЛИЩЕ СВЕТЛИНА",
          "Читалище Светлина");
    }

    // BUS #8
    if (directionName.contains("Село Герман - село Кривина")) {
      directionName = "Село Герман - Село Кривина";
    }

    // BUS #9
    if (directionName.contains("АВТОБАЗА ИСКЪР")) {
      directionName = directionName.replaceAll("АВТОБАЗА ИСКЪР",
          "Автобаза Искър");
    }
    if (directionName.contains("УЛ. ГЕН. ГУРКО")) {
      directionName = directionName.replaceAll("УЛ\\. ГЕН\\. ГУРКО",
          "ул\\. Генерал Гурко");
    }

    // BUS #23
    if (directionName.contains("ГАРА КУРИЛО")) {
      directionName = directionName.replaceAll("ГАРА КУРИЛО",
          "Гара Курило");
    }

    // BUS #27
    if (directionName.contains("Село Кътина Гара София север")) {
      directionName = directionName.replaceAll(
          "Село Кътина Гара София север",
          "Село Кътина - Гара София север");
    }

    // BUS #59
    if (directionName.contains("АВТОСТАНЦИЯ КНЯЖЕВО")) {
      directionName = directionName.replaceAll("АВТОСТАНЦИЯ КНЯЖЕВО",
          "Автостанция Княжево");
    }
    if (directionName.contains("село Мърчаево Толумска махала")) {
      directionName = directionName.replaceAll(
          "село Мърчаево Толумска махала",
          "село Мърчаево (Толумска махала)");
    }

    // BUS #76
    if (directionName.contains("жк.Младост 4")) {
      directionName = directionName.replaceAll("жк\\.Младост 4",
          "ж\\.к\\. Младост 4");
    }

    // BUS #84
    if (directionName.contains("ул. Ген Гурко")) {
      directionName = directionName.replaceAll("ул\\. Ген Гурко",
          "ул\\. Генерал Гурко");
    }

    // BUS #82, #108 and #310
    if (directionName.contains("ж.к. Люлин - 5")) {
      directionName = directionName.replaceAll("ж\\.к\\. Люлин - 5",
          "ж\\.к\\. Люлин 5");
    }

    // BUS #94
    if (directionName.contains("СУ СВ.КЛИМЕНТ ОХРИДСКИ")) {
      directionName = directionName.replaceAll("СУ СВ\\.КЛИМЕНТ ОХРИДСКИ",
          "СУ Свети Климент Охридски");
    }

    // BUS #117
    if (directionName.contains("Автостанция Изток Град Бухово")) {
      directionName = directionName.replaceAll(
          "Автостанция Изток Град Бухово",
          "Автостанция Изток - Град Бухово");
    }

    // BUS #280 and #306
    if (directionName.contains("СУ Света Климент Охридски")) {
      directionName = directionName.replaceAll(
          "СУ Света Климент Охридски", "СУ Свети Климент Охридски");
    }

    // BUS #309
    if (directionName.contains("ж.к. Люлин 1,2 - бул. Илиянци")) {
      directionName = directionName.replaceAll("бул\\. Илиянци",
          "бул\\. Илиянци (подлеза)");
    }

    // BUS #413
    if (directionName.contains("Технополис ж.к.Младост")) {
      directionName = directionName.replaceAll(
          "Технополис ж\\.к\\.Младост",
          "Технополис ж\\.к\\. Младост 4");
    }

    // TROLLEY #1
    if ("ж.к. Левски Г".equals(directionName)) {
      directionName = "ж.к. Левски Г - ВМА";
    }

    // TROLLEY #6, 7
    if (directionName.contains("ж.к. Люлин 3 - ж.к. Люлин 3")) {
      directionName = directionName.replaceAll(
          "ж\\.к\\. Люлин 3 - ж\\.к\\. Люлин 3", "ж\\.к\\. Люлин 3");
    }

    // TROLLEY #8
    if (directionName.contains(
        "ж.к. Гоце Делчев - УМБАЛ Света Анна - ж.к. Люлин 3")) {
      directionName = directionName.replaceAll(
          "ж\\.к\\. Гоце Делчев - УМБАЛ Света Анна - ж\\.к\\. Люлин 3",
          "ж\\.к\\. Гоце Делчев - УМБАЛ Света Анна");
    }

    if (directionName.contains(
        "УМБАЛ Света Анна - ж.к. Люлин 3 - ж.к. Гоце Делчев")) {
      directionName = directionName.replaceAll(
          "УМБАЛ Света Анна - ж\\.к\\. Люлин 3 - ж\\.к\\. Гоце Делчев",
          "УМБАЛ Света Анна - ж\\.к\\. Гоце Делчев");
    }

    // TROLLEY #9
    if (directionName.contains("ж. к. Борово")) {
      directionName = directionName.replaceAll("ж\\. к\\. Борово",
          "ж\\.к\\. Борово");
    }

    // TRAM #18
    if ("пл. Журналист - н - з Надежда".equals(directionName)) {
      directionName = "пл. Журналист - н-з Надежда";
    }

    // TRAM #3
    if ("Площад Централна гара Гара Захарна фабрика"
        .equals(directionName)) {
      directionName = "Площад Централна гара - Гара Захарна фабрика";
    }

    // TRAM #6
    if ("Ж. к. Обеля - 2 - ж.к. Иван Вазов".equals(directionName)) {
      directionName = "ж.к. Обеля 2 - ж.к. Иван Вазов";
    }

    // TRAM #10
    if ("Метростанция Витоша - Жк. Западен парк".equals(directionName)) {
      directionName = "Метростанция Витоша - ж.к. Западен парк";
    }

    // TRAM #19
    if ("кв. Княжево Гара София север".equals(directionName)) {
      directionName = "кв. Княжево - Гара София север";
    }

    // TRAM #23
    if ("Младежки театър - ж.к. Дружба 2".equals(directionName)) {
      directionName = "Младежки театър - ж.к. Дружба 2 (ул. Обиколна)";
    }

    return directionName;
  }

  public static long getTime() {
    return System.currentTimeMillis();
  }

  public static boolean isNullOrEmpty(String input) {
    return input == null || input.trim().isEmpty();
  }
}
