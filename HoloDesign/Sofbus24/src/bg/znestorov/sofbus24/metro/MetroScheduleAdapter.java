package bg.znestorov.sofbus24.metro;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;

/**
 * Array Adapted used to set each hour of the metro schedule
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroScheduleAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final List<String> metroScheduleList;

	private final boolean isActiveFragment;
	private boolean isClosestRowSet = false;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		TextView scheduleMetroHour;
	}

	public MetroScheduleAdapter(Activity context,
			List<String> metroScheduleList, boolean isActiveFragment) {
		super(context, R.layout.activity_metro_schedule_list_item,
				metroScheduleList);
		this.context = context;
		this.metroScheduleList = metroScheduleList;
		this.isActiveFragment = isActiveFragment;
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_metro_schedule_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.scheduleMetroHour = (TextView) rowView
					.findViewById(R.id.metro_schedule_item_hour);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		String metroSchedule = metroScheduleList.get(position);
		viewHolder.scheduleMetroHour.setText(metroSchedule);

		// Check if this is the current Fragment, so mark the closest vehicle
		if (isActiveFragment) {
			if (metroSchedule.contains("(") && !isClosestRowSet) {
				isClosestRowSet = true;
				rowView.setBackgroundColor(Color.parseColor("#80CEEA"));
			}
		}

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		return rowView;
	}

}