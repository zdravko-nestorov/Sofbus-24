package bg.znestorov.sofbus24.history;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Singleton used to make modifications to the history of searches, which are
 * saved in a shared preferences file
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class HistoryOfSearches {

	private static HistoryOfSearches instance = null;

	private SharedPreferences historyPreferences;
	private int nextSearchNumber;

	protected HistoryOfSearches(Activity context) {
		this.historyPreferences = context.getSharedPreferences(
				Constants.HISTORY_PREFERENCES_NAME, Context.MODE_PRIVATE);
		this.nextSearchNumber = historyPreferences.getInt(
				Constants.HISTORY_PREFERENCES_NEXT_SEARCH_NUMBER, 1);
	}

	public static HistoryOfSearches getInstance(Activity context) {
		if (instance == null) {
			instance = new HistoryOfSearches(context);
		}

		return instance;
	}

	/**
	 * Get the next search number from the preferences file and set it to the
	 * object value
	 * 
	 * @return the next searc number
	 */
	public int getNextSearchNumber() {
		nextSearchNumber = historyPreferences.getInt(
				Constants.HISTORY_PREFERENCES_NEXT_SEARCH_NUMBER, 1);

		return nextSearchNumber;
	}

	/**
	 * Change the next search number in the History object and put the new value
	 * in the shared preferences file
	 * 
	 * @param nextSearchNumber
	 *            the next search number
	 */
	public void putNextSearchNumberInPreferences(int nextSearchNumber) {
		if (nextSearchNumber > Constants.TOTAL_HISTORY_COUNT) {
			nextSearchNumber = nextSearchNumber % Constants.TOTAL_HISTORY_COUNT;
		}

		this.nextSearchNumber = nextSearchNumber;

		// Put the value in the shared preferences
		Editor editor = historyPreferences.edit();
		editor.putInt(Constants.HISTORY_PREFERENCES_NEXT_SEARCH_NUMBER,
				nextSearchNumber);
		editor.commit();
	}

	/**
	 * Create a new Editor for these preferences, through which you can make
	 * modifications to the data in the preferences and put an String value
	 * inside
	 * 
	 * @param preferenceKey
	 *            The name of the preference to modify
	 * @param preferenceNumber
	 *            the number of the item to add
	 * @param preferenceValue
	 *            The new value for the preference
	 */
	public void putFiledInPreferences(String preferenceKey,
			int preferenceNumber, String preferenceValue) {
		if (preferenceNumber > Constants.TOTAL_HISTORY_COUNT) {
			preferenceNumber = preferenceNumber % Constants.TOTAL_HISTORY_COUNT;
		}

		Editor editor = historyPreferences.edit();
		editor.putString(preferenceKey + preferenceNumber, preferenceValue);
		editor.commit();

		// Check if the value is not already set (it will be set if a field for
		// this search is set - value or date). This way we prevent to increase
		// the next search number multiple times
		if (nextSearchNumber == preferenceNumber) {
			putNextSearchNumberInPreferences(preferenceNumber + 1);
		}
	}

	/**
	 * Remove all values from the preferences
	 */
	public void clearHistoryOfSearches() {
		Editor editor = historyPreferences.edit();
		editor.clear();
		editor.commit();

		// Set the nextSearchNumber to the default value
		putNextSearchNumberInPreferences(1);
	}

	/**
	 * Get the whole history of searches and sort it by date
	 * 
	 * @return an ArrayList with a history of searches
	 */
	public ArrayList<HistoryEntity> getHistoryOfSearches() {
		ArrayList<HistoryEntity> historyList = new ArrayList<HistoryEntity>();

		int i = 1;
		while (historyPreferences
				.contains(Constants.HISTORY_PREFERENCES_SEARCH_VALUE + i)) {
			String historyName = historyPreferences.getString(
					Constants.HISTORY_PREFERENCES_SEARCH_VALUE + i, null);
			String historyDate = historyPreferences.getString(
					Constants.HISTORY_PREFERENCES_SEARCH_DATE + i, null);
			VehicleType historyType = VehicleType.valueOf(historyPreferences
					.getString(Constants.HISTORY_PREFERENCES_SEARCH_TYPE + i,
							null));

			historyList.add(new HistoryEntity(historyName, historyDate,
					historyType));
			i++;
		}

		// Sort the history list via the date of search
		Collections.sort(historyList, new Comparator<HistoryEntity>() {
			@Override
			public int compare(HistoryEntity history1, HistoryEntity history2) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"dd.MM.yyyy, kk:mm");

				try {
					Date vehicle1Date = formatter.parse(history1
							.getHistoryDate());
					Date vehicle2Date = formatter.parse(history2
							.getHistoryDate());

					return vehicle2Date.compareTo(vehicle1Date);
				} catch (ParseException e) {
					// This case never has to be reached
					return 0;
				}
			}
		});

		return historyList;
	}
}
