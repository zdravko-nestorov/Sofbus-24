package bg.znestorov.sofbus24.history;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * Array Adapted user for set each row a history object from a preference file
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class HistoryAdapter extends ArrayAdapter<HistoryEntity> implements
		Filterable {

	private Activity context;
	private List<HistoryEntity> historyList;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		TextView searchText;
		TextView searchType;
		TextView searchDate;
	}

	public HistoryAdapter(Activity context, List<HistoryEntity> historyList) {
		super(context, R.layout.activity_history_list_item, historyList);

		this.context = context;
		this.historyList = historyList;
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.activity_history_list_item,
					null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.searchText = (TextView) rowView
					.findViewById(R.id.history_item_search_text);
			viewHolder.searchType = (TextView) rowView
					.findViewById(R.id.history_item_search_type);
			viewHolder.searchDate = (TextView) rowView
					.findViewById(R.id.history_item_search_date);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		HistoryEntity history = historyList.get(position);

		String historyValue = history.getHistoryValue();
		String stationName = Utils.getValueBefore(historyValue, "(");
		String stationNumber = Utils.getValueBetween(historyValue, "(", ")");

		viewHolder.searchText.setText(stationName);
		viewHolder.searchType.setText(context.getString(
				R.string.history_item_station_number, stationNumber));
		viewHolder.searchDate.setText(Html.fromHtml(context.getString(
				R.string.history_item_search_date, history.getHistoryDate(),
				getHistoryType(history))));

		return rowView;
	}

	/**
	 * Get the type of the history search
	 * 
	 * @param history
	 *            the current history object
	 * @return the type of the search
	 */
	private String getHistoryType(HistoryEntity history) {
		String historyType;

		switch (history.getHistoryType()) {
		case BTT:
			historyType = context.getString(R.string.history_type_btt);
			break;
		default:
			historyType = context.getString(R.string.history_type_metro);
			break;
		}

		return historyType;
	}
}