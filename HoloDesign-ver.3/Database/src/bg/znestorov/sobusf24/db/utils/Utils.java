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
		directionName = directionName.replaceAll("��\\.����", "���������� ����");
		directionName = directionName.replaceAll("���\\. ������ ������", "���\\. ������ ������");
		directionName = directionName.replaceAll("��\\. ��������", "��\\. ��������");
		directionName = directionName.replaceAll("���� ����� �������", "���� ����� �������");
		directionName = directionName.replaceAll("�������� ����� - ��\\. ���\\. �����", "�������� ����� - ��\\. ������� �����");
		directionName = directionName.replaceAll("��\\. �\\.�������� - ��\\. ��������", "��\\. �\\. �������� - ��\\. ��������");
		directionName = directionName.replaceAll("�������� ��\\. ����", "�������� ����� ����");
		directionName = directionName.replaceAll("�\\.�\\.���� ������", "�\\.�\\. ���� ������");
		directionName = directionName.replaceAll("��\\. ����� ����", "������ ����� ����");
		directionName = directionName.replaceAll("����� - ����������", "����� ����������");
		directionName = directionName.replaceAll("�� ��\\.������� �������� - ���������� ����", "�� ����� ������� �������� - ���������� ����");
		directionName = directionName.replaceAll("�� ��\\. ������� �������� - ���������� ����", "�� ����� ������� �������� - ���������� ����");
		directionName = directionName.replaceAll("������� ������", "������� ������");
		directionName = directionName.replaceAll("���������� ����", "���������� ����");
		directionName = directionName.replaceAll("�\\.�\\.������� ������", "�.�. ������� ������");
		directionName = directionName.replaceAll("���� ������", "���� ������");
		directionName = directionName.replaceAll("���� �����", "���� �����");
		directionName = directionName.replaceAll("��������� � - � ������", "��������� �������� ������");
		directionName = directionName.replaceAll("�� ��\\. ������� ��������", "�� ����� ������� ��������");
		directionName = directionName.replaceAll(" ���", " �\\.�\\. ����� 3 (���)");
		directionName = directionName.replaceAll("���������", "���� �����");
		directionName = directionName.replaceAll("�\\.�\\.����� 1,2", "�\\.�\\. ����� 1,2 - ���. ������� (�������)");
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
		directionName = directionName.replaceAll("��\\. ���������� ���� ������� �������", "��\\. ���������� - ���� ������� �������");
		directionName = directionName.replaceAll("��\\. ����� ������� ���� ������� �������", "��\\. ����� ������� - ���� ������� �������");
		directionName = directionName.replaceAll("����������� ���������� ���� ������", "����������� ���������� - ���� ������");
		directionName = directionName.replaceAll("��\\. ������� ���� �����", "��\\. ������� - ���� �����");
		directionName = directionName.replaceAll("�� ��������� ���� ������", "�� ��������� - ���� ������");
		directionName = directionName.replaceAll("������� ������� ���� �����", "������� ������� - ���� �����");
		directionName = directionName.replaceAll("�������� �������� ���� �����", "�������� �������� - ���� �����");
		directionName = directionName.replaceAll("��\\. ������� ���� ����� �����", "��\\. ������� - ���� ����� (�����)");
		directionName = directionName.replaceAll("����������� ������� - ���� �������� �������� ������", "��. ������� - �. �������� (�������� ������)");
		if ("�.�. ������� 1".equals(directionName)) {
			directionName = directionName.replaceAll("�\\.�\\. ������� 1", "�\\.�\\. ������� 1 - �\\.�\\. ����� 1,2");
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
