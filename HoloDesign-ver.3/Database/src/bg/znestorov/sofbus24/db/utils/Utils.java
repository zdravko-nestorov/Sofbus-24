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
      if (directionName.contains("���.�������-�������")) {
        directionName = directionName.replaceAll(
            "���\\.�������-�������",
            "���\\. ������� \\(�������\\)");
      }
      if (directionName.contains("�.�.�����-1,2")) {
        directionName = directionName.replaceAll("�\\.�\\.�����-1,2",
            "�\\.�\\. ����� 1,2");
      }
      if (directionName.contains("�.�. �������-1")) {
        directionName = directionName.replaceAll("�\\.�\\. �������-1",
            "�\\.�\\. ������� 1");
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
    directionName = directionName.replaceAll(" - �", " �");
    directionName = directionName.replaceAll("� - �", "�-�");
    directionName = directionName.replaceAll("� - ���", "�-���");
    directionName = directionName.replaceAll("� - �", "�-�");
    directionName = directionName.replaceAll("����\\.", "������������ ");
    directionName = directionName.replaceAll("�������\\.", "������������ ");
    directionName = directionName.replaceAll("���\\.", "������� ");
    directionName = directionName.replaceAll("�\\. ����", "��������� ����");
    directionName = directionName.replaceAll("�\\.����", "��������� ����");
    directionName = directionName.replaceAll("�\\. ����", "��������� ����");
    directionName = directionName.replaceAll("�\\.����", "��������� ����");
    directionName = directionName.replaceAll("��\\.����",
        "���������� ����");
    directionName = directionName.replaceAll("���\\. ������ ������",
        "���\\. ������ ������");
    directionName = directionName.replaceAll("��\\. ��������",
        "��\\. ��������");
    directionName = directionName.replaceAll("���� ����� �������",
        "���� ����� �������");
    directionName = directionName.replaceAll(
        "�������� ����� - ��\\. ���\\. �����",
        "�������� ����� - ��\\. ������� �����");
    directionName = directionName.replaceAll(
        "��\\. �\\.�������� - ��\\. ��������",
        "��\\. �\\. �������� - ��\\. ��������");
    directionName = directionName.replaceAll("�������� ��\\. ����",
        "�������� ����� ����");
    directionName = directionName.replaceAll("�\\.�\\.���� ������",
        "�\\.�\\. ���� ������");
    directionName = directionName.replaceAll("��\\. ����� ����",
        "������ ����� ����");
    directionName = directionName.replaceAll("����� - ����������",
        "����� ����������");
    directionName = directionName.replaceAll(
        "�� ��\\.������� �������� - ���������� ����",
        "�� ����� ������� �������� - ���������� ����");
    directionName = directionName.replaceAll(
        "�� ��\\. ������� �������� - ���������� ����",
        "�� ����� ������� �������� - ���������� ����");
    directionName = directionName.replaceAll("������� ������",
        "������� ������");
    directionName = directionName.replaceAll("���������� ����",
        "���������� ����");
    directionName = directionName.replaceAll("�\\.�\\.������� ������",
        "�.�. ������� ������");
    directionName = directionName.replaceAll("���� ������", "���� ������");
    directionName = directionName.replaceAll("���� �����", "���� �����");
    directionName = directionName.replaceAll("��������� � - � ������",
        "��������� �������� ������");
    directionName = directionName.replaceAll("�� ��\\. ������� ��������",
        "�� ����� ������� ��������");
    directionName = directionName.replaceAll(
        "����� ����� ���� - ��� - �\\.�\\. ���� ������",
        "����� ����� ���� - �\\.�\\. ���� ������");
    directionName = directionName.replaceAll(
        "�\\.�\\. ���� ������ - ����� ����� ���� - ���",
        "�\\.�\\. ���� ������ - ����� ����� ����");
    directionName = directionName.replaceAll(" ���", " �\\.�\\. ����� 3");
    directionName = directionName.replaceAll("���������", "���� �����");
    directionName = directionName.replaceAll("�\\.�\\.����� 1,2",
        "�\\.�\\. ����� 1,2");
    directionName = directionName.replaceAll("�\\.�\\.", "�\\.�\\.");
    directionName = directionName.replaceAll("�\\.�\\. ", "�\\.�\\.");
    directionName = directionName.replaceAll("�\\.�\\. ", "�\\.�\\.");
    directionName = directionName.replaceAll("�\\.�\\.", "�\\.�\\. ");
    directionName = directionName.replaceAll("��\\. ", "��\\.");
    directionName = directionName.replaceAll("��\\.", "��\\. ");
    directionName = directionName.replaceAll("��\\. ", "��\\.");
    directionName = directionName.replaceAll("��\\.", "��\\. ");
    directionName = directionName.replaceAll("��\\. ", "��\\.");
    directionName = directionName.replaceAll("��\\.", "������ ");
    directionName = directionName.replaceAll("�\\. ", "�\\.");
    directionName = directionName.replaceAll("�\\.", "���� ");
    directionName = directionName.replaceAll("���\\. ", "���\\.");
    directionName = directionName.replaceAll("���\\.", "���\\. ");
    directionName = directionName.replaceAll("��\\. ", "��\\.");
    directionName = directionName.replaceAll("��\\.", "��\\. ");
    directionName = directionName.replaceAll("��\\. ", "��\\.");
    directionName = directionName.replaceAll("��\\.", "����� ");

    // Special cases
    directionName = directionName.replaceAll(
        "��\\. ���������� ���� ������� �������",
        "��\\. ���������� - ���� ������� �������");
    directionName = directionName.replaceAll(
        "��\\. ����� ������� ���� ������� �������",
        "��\\. ����� ������� - ���� ������� �������");
    directionName = directionName.replaceAll(
        "����������� ���������� ���� ������",
        "����������� ���������� - ���� ������");
    directionName = directionName.replaceAll("��\\. ������� ���� �����",
        "��\\. ������� - ���� �����");
    directionName = directionName.replaceAll("�� ��������� ���� ������",
        "�� ��������� - ���� ������");
    directionName = directionName.replaceAll("������� ������� ���� �����",
        "������� ������� - ���� �����");
    directionName = directionName.replaceAll("�������� �������� ���� �����",
        "�������� �������� - ���� �����");
    directionName = directionName.replaceAll("��.������� 4",
        "�.�. ������� 4");

    if ("�.�. ������� 1".equals(directionName)) {
      directionName = directionName.replaceAll("�\\.�\\. ������� 1",
          "�\\.�\\. ������� 1 - �\\.�\\. ����� 1,2");
    }

    directionName = directionName.trim().replaceAll("-", " - ");
    directionName = directionName.trim().replaceAll(" +", " ");

    // Special cases
    if (directionName.contains(" - ��������")) {
      directionName = directionName.replaceAll(" - ��������",
          "-��������");
    }

    // BUS #7
    if (directionName.contains("�������� ��������")) {
      directionName = directionName.replaceAll("�������� ��������",
          "�������� ��������");
    }

    // BUS #8
    if (directionName.contains("���� ������ - ���� �������")) {
      directionName = "���� ������ - ���� �������";
    }

    // BUS #9
    if (directionName.contains("�������� �����")) {
      directionName = directionName.replaceAll("�������� �����",
          "�������� �����");
    }
    if (directionName.contains("��. ���. �����")) {
      directionName = directionName.replaceAll("��\\. ���\\. �����",
          "��\\. ������� �����");
    }

    // BUS #23
    if (directionName.contains("���� ������")) {
      directionName = directionName.replaceAll("���� ������",
          "���� ������");
    }

    // BUS #27
    if (directionName.contains("���� ������ ���� ����� �����")) {
      directionName = directionName.replaceAll(
          "���� ������ ���� ����� �����",
          "���� ������ - ���� ����� �����");
    }

    // BUS #59
    if (directionName.contains("����������� �������")) {
      directionName = directionName.replaceAll("����������� �������",
          "����������� �������");
    }
    if (directionName.contains("���� �������� �������� ������")) {
      directionName = directionName.replaceAll(
          "���� �������� �������� ������",
          "���� �������� (�������� ������)");
    }

    // BUS #76
    if (directionName.contains("��.������� 4")) {
      directionName = directionName.replaceAll("��\\.������� 4",
          "�\\.�\\. ������� 4");
    }

    // BUS #84
    if (directionName.contains("��. ��� �����")) {
      directionName = directionName.replaceAll("��\\. ��� �����",
          "��\\. ������� �����");
    }

    // BUS #82, #108 and #310
    if (directionName.contains("�.�. ����� - 5")) {
      directionName = directionName.replaceAll("�\\.�\\. ����� - 5",
          "�\\.�\\. ����� 5");
    }

    // BUS #94
    if (directionName.contains("�� ��.������� ��������")) {
      directionName = directionName.replaceAll("�� ��\\.������� ��������",
          "�� ����� ������� ��������");
    }

    // BUS #117
    if (directionName.contains("����������� ����� ���� ������")) {
      directionName = directionName.replaceAll(
          "����������� ����� ���� ������",
          "����������� ����� - ���� ������");
    }

    // BUS #280 and #306
    if (directionName.contains("�� ����� ������� ��������")) {
      directionName = directionName.replaceAll(
          "�� ����� ������� ��������", "�� ����� ������� ��������");
    }

    // BUS #309
    if (directionName.contains("�.�. ����� 1,2 - ���. �������")) {
      directionName = directionName.replaceAll("���\\. �������",
          "���\\. ������� (�������)");
    }

    // BUS #413
    if (directionName.contains("���������� �.�.�������")) {
      directionName = directionName.replaceAll(
          "���������� �\\.�\\.�������",
          "���������� �\\.�\\. ������� 4");
    }

    // TROLLEY #1
    if ("�.�. ������ �".equals(directionName)) {
      directionName = "�.�. ������ � - ���";
    }

    // TROLLEY #6, 7
    if (directionName.contains("�.�. ����� 3 - �.�. ����� 3")) {
      directionName = directionName.replaceAll(
          "�\\.�\\. ����� 3 - �\\.�\\. ����� 3", "�\\.�\\. ����� 3");
    }

    // TROLLEY #8
    if (directionName.contains(
        "�.�. ���� ������ - ����� ����� ���� - �.�. ����� 3")) {
      directionName = directionName.replaceAll(
          "�\\.�\\. ���� ������ - ����� ����� ���� - �\\.�\\. ����� 3",
          "�\\.�\\. ���� ������ - ����� ����� ����");
    }

    if (directionName.contains(
        "����� ����� ���� - �.�. ����� 3 - �.�. ���� ������")) {
      directionName = directionName.replaceAll(
          "����� ����� ���� - �\\.�\\. ����� 3 - �\\.�\\. ���� ������",
          "����� ����� ���� - �\\.�\\. ���� ������");
    }

    // TROLLEY #9
    if (directionName.contains("�. �. ������")) {
      directionName = directionName.replaceAll("�\\. �\\. ������",
          "�\\.�\\. ������");
    }

    // TRAM #18
    if ("��. ��������� - � - � �������".equals(directionName)) {
      directionName = "��. ��������� - �-� �������";
    }

    // TRAM #3
    if ("������ ��������� ���� ���� ������� �������"
        .equals(directionName)) {
      directionName = "������ ��������� ���� - ���� ������� �������";
    }

    // TRAM #6
    if ("�. �. ����� - 2 - �.�. ���� �����".equals(directionName)) {
      directionName = "�.�. ����� 2 - �.�. ���� �����";
    }

    // TRAM #10
    if ("������������ ������ - ��. ������� ����".equals(directionName)) {
      directionName = "������������ ������ - �.�. ������� ����";
    }

    // TRAM #19
    if ("��. ������� ���� ����� �����".equals(directionName)) {
      directionName = "��. ������� - ���� ����� �����";
    }

    // TRAM #23
    if ("�������� ������ - �.�. ������ 2".equals(directionName)) {
      directionName = "�������� ������ - �.�. ������ 2 (��. ��������)";
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
