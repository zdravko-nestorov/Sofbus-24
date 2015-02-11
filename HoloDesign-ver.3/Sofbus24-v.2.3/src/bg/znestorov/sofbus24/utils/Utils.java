package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.UpdateTypeEnum;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.history.HistoryOfSearches;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.updates.check.CheckForUpdatesAsync;
import bg.znestorov.sofbus24.updates.check.CheckForUpdatesPreferences;

/**
 * Utility class
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Utils {

	/**
	 * Check if the input is a number
	 * 
	 * @param input
	 *            the input string
	 * @return if the input is a number
	 */
	public static boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
		} catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	/**
	 * Function that extracts only digits from a given String. In case of an
	 * empty string - return "".
	 * 
	 * @param value
	 *            the input String
	 * @return the digits from the String
	 */
	public static String getOnlyDigits(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\D+", "");
		} else {
			value = "";
		}

		return value;
	}

	/**
	 * Function that remove all whitespaces and non visible characters such as
	 * tab, \n from a string text. In case of an empty string - return "".
	 * 
	 * @param value
	 *            the input String
	 * @return the digits from the String
	 */
	public static String removeSpaces(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\s+", "");
		} else {
			value = "";
		}

		return value;
	}

	/**
	 * Get a value from a string BEFORE some REGEX
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value BEFORE the REGEX, or the value in case of no
	 *         REGEX found
	 */
	public static String getValueBefore(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.indexOf(regex));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string BEFORE some REGEX (LAST)
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value BEFORE the REGEX (LAST), or the value in case
	 *         of no REGEX found
	 */
	public static String getValueBeforeLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.lastIndexOf(regex));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string AFTER some REGEX
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value AFTER the REGEX, or the value in case of no
	 *         REGEX found
	 */
	public static String getValueAfter(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.indexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string AFTER some REGEX (LAST)
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value AFTER the REGEX (LAST), or the value in case
	 *         of no REGEX found
	 */
	public static String getValueAfterLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.lastIndexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string BETWEEN the REGEX1 and REGEX2
	 * 
	 * @param value
	 *            the string value
	 * @param regex1
	 *            the regex that is looked for (after)
	 * @param regex2
	 *            the regex that is looked for (before)
	 * @return the substring value BETWEEN the REGEX1 and REGEX2, or the value
	 *         in case of no REGEX found
	 */
	public static String getValueBetween(String value, String regex1,
			String regex2) {
		if (value.contains(regex1) && value.contains(regex2)) {
			return value.substring(value.indexOf(regex1) + regex1.length(),
					value.indexOf(regex2));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string BETWEEN the last REGEX1 and REGEX2
	 * 
	 * @param value
	 *            the string value
	 * @param regex1
	 *            the regex that is looked for (after)
	 * @param regex2
	 *            the regex that is looked for (before)
	 * @return the substring value BETWEEN the REGEX1 and REGEX2, or the value
	 *         in case of no REGEX found
	 */
	public static String getValueBetweenLast(String value, String regex1,
			String regex2) {
		if (value.contains(regex1) && value.contains(regex2)) {
			return value.substring(value.lastIndexOf(regex1) + regex1.length(),
					value.lastIndexOf(regex2));
		} else {
			return value;
		}
	}

	/**
	 * Filling a number with zeroes ("0") if it is lower than "outputLength"
	 * digits
	 * 
	 * @param context
	 *            the current activity context
	 * @param input
	 *            an input string containing a number
	 * @return a number (in string format) which is always at least
	 *         "outputLength" digits
	 */
	public static String formatNumberOfDigits(String input, int outputLength) {
		String formatType = String.format(Locale.getDefault(), "%%0%dd",
				outputLength);

		try {
			input = String.format(formatType, Integer.parseInt(input));
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Remove the leading zeros in alphanumeric text with regex
	 * 
	 * @param input
	 *            an input string containing a number
	 * @return a number (in string format) with removed leading zeroes (if
	 *         exist)
	 */
	public static String removeLeadingZeroes(String input) {
		try {
			input = input.replaceFirst("^0+(?!$)", "");
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Get the current date time in format DD.MM.YYY, HH:MM
	 * 
	 * @return the current time in format DD.MM.YYY, HH:MM
	 */
	public static String getCurrentDateTime() {
		return DateFormat.format("dd.MM.yyy, kk:mm", new java.util.Date())
				.toString();
	}

	/**
	 * Get the current time in format DD.MM.YYY, HH:MM
	 * 
	 * @return the current time in format DD.MM.YYY, HH:MM
	 */
	public static String getCurrentTime() {
		return DateFormat.format("kk:mm", new java.util.Date()).toString();
	}

	/**
	 * Get the current date in format DD.MM.YYY
	 * 
	 * @return the current date in format DD.MM.YYY
	 */
	public static String getCurrentDate() {
		return DateFormat.format("dd.MM.yyy", new java.util.Date()).toString();
	}

	/**
	 * Get the current day
	 * 
	 * @return the current day
	 */
	public static int getCurrentDay() {
		int day;
		try {
			day = Integer.parseInt(DateFormat
					.format("dd", new java.util.Date()).toString());
		} catch (Exception e) {
			day = 0;
		}

		return day;
	}

	/**
	 * Get the difference between two hours in format HH:MM
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param afterTime
	 *            a time after the current moment in format HH:MM
	 * @param currTime
	 *            the current time in format HH:MM
	 * @return the difference between the times
	 */
	public static String getTimeDifference(Activity context, String afterTime,
			String currTime) {
		String diff = "";
		int afterTimeMilis = 0;
		int currTimeMilis = 0;

		// In cases when it is after midnight
		if (afterTime.startsWith("00:") && !currTime.startsWith("00:")) {
			afterTime = afterTime.replaceAll("00:", "24:");
		}

		try {
			afterTimeMilis = new BigDecimal(afterTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(afterTime.split(":")[1]).intValue();
			currTimeMilis = new BigDecimal(currTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(currTime.split(":")[1]).intValue();

			diff = (afterTimeMilis - currTimeMilis) + "";

			if (!diff.contains("-")) {
				diff = (new BigDecimal(diff).intValue() / 60) + ":"
						+ (new BigDecimal(diff).intValue() % 60);
				diff = formatTime(context, diff);
			} else {
				diff = "---";
			}
		} catch (Exception e) {

		}

		return diff;
	}

	/**
	 * Format the remaining time in format ~1h,20m
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param difference
	 *            the difference between the times (current one and the one
	 *            after)
	 * @return the difference in format ~1h,20m
	 */
	public static String formatTime(Activity context, String difference) {
		String diff = "";
		String[] differenceArr = difference.split(":");

		if (differenceArr.length == 2) {

			if ("".equals(differenceArr[0]) || "0".equals(differenceArr[0])) {
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.app_remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.app_remaining_minutes);
				}

				diff = "~" + differenceArr[1];
			} else {
				differenceArr[0] = differenceArr[0]
						+ context.getString(R.string.app_remaining_hours);
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.app_remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.app_remaining_minutes);
				}

				diff = "~" + differenceArr[0] + " " + differenceArr[1];
			}
		}

		return diff;
	}

	/**
	 * Transform the remaining time in minutes
	 * 
	 * @param remainingTime
	 *            the remaining time in string format
	 * @return the rmaining time in minutes
	 */
	public static int getRemainingMinutes(String remainingTime) {
		int remainingMinutes;

		String[] remainingTimeArray = remainingTime.split(" ");
		if (remainingTimeArray.length == 1) {
			remainingMinutes = Integer.parseInt(Utils
					.getOnlyDigits(remainingTimeArray[0]));
		} else {
			remainingMinutes = 60
					* Integer.parseInt(Utils
							.getOnlyDigits(remainingTimeArray[0]))
					+ Integer.parseInt(Utils
							.getOnlyDigits(remainingTimeArray[1]));
		}

		return remainingMinutes;
	}

	/**
	 * Convert the millis in remaining time format (~�� ��)
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param millis
	 *            remaining time in millis
	 * @return the remaining time in format ~�� ��
	 */
	public static String formatMillisInTime(Activity context, Long millis) {
		String remainingTime;

		long minutes = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		if (hour > 0) {
			remainingTime = "~" + hour
					+ context.getString(R.string.app_remaining_minutes) + " "
					+ minutes
					+ context.getString(R.string.app_remaining_minutes);
		} else {
			remainingTime = "~" + minutes
					+ context.getString(R.string.app_remaining_minutes);
		}

		return remainingTime;
	}

	/**
	 * Function that format the direction name of the station or vehicle to be
	 * in correct format. In case of an empty string - return "".
	 * 
	 * @param directionName
	 *            the name of the direction
	 * @return the correctly formatted direction
	 */
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
		directionName = directionName
				.replaceAll("��\\.����", "���������� ����");
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
				"���� ��\\.���� - ��� - �\\.�\\. ���� ������",
				"���� ��\\.���� - �\\.�\\. ���� ������");
		directionName = directionName.replaceAll(
				"�\\.�\\. ���� ������ - ���� ��\\.���� - ���",
				"�\\.�\\. ���� ������ - ���� ��\\.����");
		directionName = directionName.replaceAll(" ���", " �\\.�\\. ����� 3");
		directionName = directionName.replaceAll("���������", "���� �����");
		directionName = directionName.replaceAll("�\\.�\\.����� 1,2",
				"�\\.�\\. ����� 1,2 - ���. ������� (�������)");
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
		directionName = directionName.replaceAll(
				"�������� �������� ���� �����",
				"�������� �������� - ���� �����");
		if ("�.�. ������� 1".equals(directionName)) {
			directionName = directionName.replaceAll("�\\.�\\. ������� 1",
					"�\\.�\\. ������� 1 - �\\.�\\. ����� 1,2");
		}

		directionName = directionName.trim().replaceAll("-", " - ");
		directionName = directionName.trim().replaceAll(" +", " ");

		return directionName;
	}

	/**
	 * Add a search to the history of searches, using the searched text
	 * 
	 * @param context
	 *            the current Activity context
	 * @param historyType
	 *            the type of the search - <b>public transport</b> or
	 *            <b>metro</b>
	 * @param historyValueArr
	 *            the value that has to be added to the history of searches
	 */
	public static void addSearchInHistory(Activity context,
			VehicleTypeEnum historyType, String... historyValueArr) {
		HistoryOfSearches history = HistoryOfSearches.getInstance(context);

		// Get the name of the search
		String historyValue = "";
		if (historyValueArr.length == 1) {
			historyValue = historyValueArr[0];
		} else {
			if (historyValueArr[1] == null || "".equals(historyValueArr[1])) {
				historyValue = historyValueArr[0];
			} else {
				historyValue = String.format(historyValueArr[0] + " (%s)",
						historyValueArr[1]);
			}
		}

		// Get the search type
		if (historyType == VehicleTypeEnum.METRO1
				|| historyType == VehicleTypeEnum.METRO2) {
			historyType = VehicleTypeEnum.METRO;
		}

		int nextSearchNumber = history.getNextSearchNumber();
		history.putFiledInPreferences(context,
				Constants.HISTORY_PREFERENCES_SEARCH_VALUE, nextSearchNumber,
				historyValue, true);
		history.putFiledInPreferences(context,
				Constants.HISTORY_PREFERENCES_SEARCH_DATE, nextSearchNumber,
				DateFormat.format("dd.MM.yyyy, kk:mm:ss", new java.util.Date())
						.toString(), true);
		history.putFiledInPreferences(context,
				Constants.HISTORY_PREFERENCES_SEARCH_TYPE, nextSearchNumber,
				historyType.name(), false);
	}

	/**
	 * Add a search to the history of searches, using the station
	 * 
	 * @param context
	 *            the current Activity context
	 * @param station
	 *            the station that has to be added to the history of searches
	 */
	public static void addStationInHistory(Activity context,
			StationEntity station) {

		VehicleTypeEnum stationType = station.getType();
		switch (stationType) {
		case METRO:
		case METRO1:
		case METRO2:
			stationType = VehicleTypeEnum.METRO;
			break;
		default:
			stationType = VehicleTypeEnum.BTT;
			break;
		}

		addSearchInHistory(context, stationType, station.getName(),
				station.getNumber());
	}

	/**
	 * Add a search to the history of searches, using the station
	 * 
	 * @param context
	 *            the current Activity context
	 * @param stationsMap
	 *            the map of stations that has to be added to the history of
	 *            searches
	 */
	public static void addListOfStationsInHistory(Activity context,
			HashMap<String, StationEntity> stationsMap) {
		Iterator<Entry<String, StationEntity>> stationIterator = stationsMap
				.entrySet().iterator();

		while (stationIterator.hasNext()) {
			addStationInHistory(context, stationIterator.next().getValue());
		}
	}

	/**
	 * Add a search to the history of searches, using the station
	 * 
	 * @param context
	 *            the current Activity context
	 * @param station
	 *            the station that has to be added to the history of searches
	 */
	public static void addVehicleInHistory(Activity context,
			VehicleEntity vehicle) {
		addSearchInHistory(context, vehicle.getType(), vehicle.getDirection(),
				vehicle.getNumber());
	}

	/**
	 * Check if the device is in landscape mode
	 * 
	 * @param context
	 *            the current activity context
	 * @return if the device is in landscape mode
	 */
	public static boolean isInLandscapeMode(Activity context) {

		int currentOrientation = context.getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the device is tablet in landscape mode
	 * 
	 * @param context
	 *            the current activity context
	 * @return if the device is tablet in landscape mode
	 */
	public static boolean isTabletInLandscapeMode(Activity context) {

		GlobalEntity globalContext = (GlobalEntity) context
				.getApplicationContext();
		int currentOrientation = context.getResources().getConfiguration().orientation;

		if (!globalContext.isPhoneDevice()
				&& currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a list with all items in the NavigationDrawer (each row of the
	 * menu)
	 * 
	 * @param context
	 *            the current activity context
	 * 
	 * @return an ArrayList with all raws of the menu
	 */
	public static ArrayList<String> initNavigationDrawerItems(Activity context) {

		ArrayList<String> navigationItems = new ArrayList<String>();

		navigationItems.add(context.getString(R.string.navigation_drawer_home));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_home_standard));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_home_map));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_home_cars));
		navigationItems.add(context.getString(R.string.navigation_drawer_cs));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_route_changes));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_history));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_options));
		navigationItems.add(context.getString(R.string.navigation_drawer_info));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_update));
		navigationItems.add(context.getString(R.string.navigation_drawer_exit));

		return navigationItems;
	}

	/**
	 * Check if there is an available network connection
	 * 
	 * @param context
	 *            the current activity context
	 * @return if there is a network connection
	 */
	public static boolean haveNetworkConnection(Activity context) {

		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		boolean haveConnected = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();

		for (NetworkInfo networkIngo : allNetworkInfo) {
			if ("WIFI".equalsIgnoreCase(networkIngo.getTypeName())) {
				if (networkIngo.isConnected()) {
					haveConnectedWifi = true;
				}
			}

			if ("MOBILE".equalsIgnoreCase(networkIngo.getTypeName())) {
				if (networkIngo.isConnected()) {
					haveConnectedMobile = true;
				}
			}
		}

		if (!haveConnectedWifi && !haveConnectedMobile) {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			haveConnected = networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected();
		}

		return haveConnectedWifi || haveConnectedMobile || haveConnected;
	}

	/**
	 * Get the difference between two dates in days
	 * 
	 * @param startDateString
	 *            the start date
	 * @param endDateString
	 *            the end date
	 * @return the difference between the start and end date in days
	 */
	@SuppressLint("SimpleDateFormat")
	public static int getDateDifferenceInDays(String startDateString,
			String endDateString) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd.MM.yyyy");
			Date startDate = simpleDateFormat.parse(startDateString);
			Date endDate = simpleDateFormat.parse(endDateString);

			long difference = endDate.getTime() - startDate.getTime();
			int daysDifference = (int) (difference / (1000 * 60 * 60 * 24));

			return daysDifference;
		} catch (ParseException e) {
			return 0;
		}
	}

	/**
	 * Get the closest date when the app had to be updated
	 * 
	 * @param updateType
	 *            the update type (what would be updated - APP or DB)
	 * 
	 * @return the closest date when the app had to be updated
	 */
	public static String getClosestDateForUpdate(UpdateTypeEnum updateType) {
		String closestDateForUpdate;

		Integer[] daysForUpdate;
		int currentDay = getCurrentDay();

		switch (updateType) {
		case APP:
			daysForUpdate = DAYS_FOR_APP_UPDATE
					.toArray(new Integer[DAYS_FOR_APP_UPDATE.size()]);
			closestDateForUpdate = daysForUpdate[0] + "";

			break;
		default:
			daysForUpdate = DAYS_FOR_DB_UPDATE
					.toArray(new Integer[DAYS_FOR_DB_UPDATE.size()]);

			if (currentDay >= daysForUpdate[1]) {
				closestDateForUpdate = daysForUpdate[1] + "";
			} else {
				closestDateForUpdate = daysForUpdate[0] + "";
			}

			break;
		}

		closestDateForUpdate += "." + getValueAfter(getCurrentDate(), ".");

		return closestDateForUpdate;
	}

	/**
	 * Check if the date for update is in range
	 * 
	 * @param startDateString
	 *            the start date in string format
	 * @param endDateString
	 *            the end date in string format
	 * @param updateType
	 *            the update type (what would be updated - APP or DB)
	 * 
	 * @return if the date is in range
	 */
	public static boolean isDateInRange(String startDateString,
			String endDateString, UpdateTypeEnum updateType) {
		int startCheckDifference = getDateDifferenceInDays(startDateString,
				getClosestDateForUpdate(updateType));
		int endCheckDifference = getDateDifferenceInDays(
				getClosestDateForUpdate(updateType), endDateString);

		if (startCheckDifference > 0 && endCheckDifference > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The days that the application and the database should be updated
	 */
	private static final Set<Integer> DAYS_FOR_APP_UPDATE;
	static {
		DAYS_FOR_APP_UPDATE = new LinkedHashSet<Integer>();
		DAYS_FOR_APP_UPDATE.add(1);
	}

	/**
	 * The days that the application and the database should be updated
	 */
	private static final Set<Integer> DAYS_FOR_DB_UPDATE;
	static {
		DAYS_FOR_DB_UPDATE = new LinkedHashSet<Integer>();
		DAYS_FOR_DB_UPDATE.add(1);
		DAYS_FOR_DB_UPDATE.add(15);
	}

	/**
	 * Check if an application or database update is available (APP - one per
	 * month, DB - twice per month)
	 * 
	 * @param context
	 *            the current activity context
	 * @param updateType
	 *            the update type (what would be updated - APP or DB)
	 */
	public static void checkForUpdate(FragmentActivity context,
			UpdateTypeEnum updateType) {

		// Get "automaticUpdate" value from the SharedPreferences file
		boolean automaticUpdate = PreferenceManager
				.getDefaultSharedPreferences(context).getBoolean(
						Constants.PREFERENCE_KEY_AUTOMATIC_UPDATE,
						Constants.PREFERENCE_DEFAULT_VALUE_AUTOMATIC_UPDATE);

		// Update the application if the user selected the option in the
		// Preferences file
		if (automaticUpdate) {
			Set<Integer> daysForUpdate;
			switch (updateType) {
			case APP:
				daysForUpdate = DAYS_FOR_APP_UPDATE;
				break;
			default:
				daysForUpdate = DAYS_FOR_DB_UPDATE;
				break;
			}

			boolean haveInternetConnection = haveNetworkConnection(context);
			boolean isFirstOrDelayedUpdate = CheckForUpdatesPreferences
					.isFirstOrDelayedUpdate(context, getCurrentDate(),
							updateType);
			boolean isDayForUpdate = daysForUpdate.contains(getCurrentDay());
			boolean isUpdateAlreadyChecked = CheckForUpdatesPreferences
					.isUpdateAlreadyChecked(context, getCurrentDate(),
							updateType);

			if (haveInternetConnection
					&& (isFirstOrDelayedUpdate || (isDayForUpdate && !isUpdateAlreadyChecked))) {
				new CheckForUpdatesAsync(context, updateType).execute();
			}
		}
	}

	/**
	 * Check if the version is before HONEYCOMB
	 * 
	 * @return if the version is before HONEYCOMB
	 */
	public static boolean isPreHoneycomb() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * Decode drawable to a Bitmap (save the allocated memory, but doesn't
	 * really help)
	 * 
	 * @param context
	 *            the activity context
	 * @param resId
	 *            the drawable res id
	 * @return the decoded bitmap
	 */
	public static Bitmap decodeDrawable(Activity context, int resId) {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		BitmapFactory.decodeResource(context.getResources(), resId, o);
		o.inJustDecodeBounds = true;

		// In Samsung Galaxy S3, typically max memory is 64mb
		// Camera max resolution is 3264 x 2448, times 4 to get Bitmap
		// memory of 30.5mb for one bitmap
		// If we use scale of 2, resolution will be halved, 1632 x 1224 and
		// x 4 to get Bitmap memory of 7.62mb
		// We try use 25% memory which equals to 16mb maximum for one bitmap
		long maxMemory = Runtime.getRuntime().maxMemory();
		int maxMemoryForImage = (int) (maxMemory / 100 * 25);

		// Refer to
		// http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
		// A full screen GridView filled with images on a device with
		// 800x480 resolution would use around 1.5MB (800*480*4 bytes)
		// When bitmap option's inSampleSize doubled, pixel height and
		// weight both reduce in half
		int scale = 1;
		while ((o.outWidth / scale) * (o.outHeight / scale) * 4 > maxMemoryForImage)
			scale *= 2;

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap b = BitmapFactory.decodeResource(context.getResources(), resId,
				o2);

		return b;
	}

	/**
	 * Get the size of the screen in inches
	 * 
	 * @param context
	 *            the current activity context
	 * @return the screen size in inches
	 */
	public static double getScreenSizeInInches(Activity context) {

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		double screenInches = Math.sqrt(x + y);

		return screenInches;
	}

	/**
	 * Format the schedule cache timestamp in format DD.MM.YYY
	 * 
	 * @param timestamp
	 *            the database timestamp
	 * @return the formatted timestamp
	 */
	public static String formatScheduleCacheTimestamp(String timestamp) {

		String formattedTimestamp;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(timestamp);
			formattedTimestamp = DateFormat.format("dd.MM.yyy", date)
					.toString();
		} catch (ParseException e) {
			// This case should not be reached
			formattedTimestamp = timestamp;
		}

		return formattedTimestamp;
	}
}