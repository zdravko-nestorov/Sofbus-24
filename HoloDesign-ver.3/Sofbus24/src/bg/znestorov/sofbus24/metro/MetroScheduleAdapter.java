package bg.znestorov.sofbus24.metro;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.MetroFragmentEntity;
import bg.znestorov.sofbus24.main.R;

/**
 * Array Adapted used to set each hour of the metro schedule
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class MetroScheduleAdapter extends ArrayAdapter<String> {

	private Activity context;
	private MetroFragmentEntity mfe;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		TextView scheduleMetroHour;
	}

	public MetroScheduleAdapter(Activity context, MetroFragmentEntity mfe) {
		super(context, R.layout.activity_metro_schedule_list_item, mfe
				.getFormattedScheduleList());
		this.context = context;
		this.mfe = mfe;
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Used to reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_metro_schedule_list_item, null);

			// Configure the view holder
			viewHolder = new ViewHolder();
			viewHolder.scheduleMetroHour = (TextView) rowView
					.findViewById(R.id.metro_schedule_item_hour);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		String metroSchedule = mfe.getFormattedScheduleList().get(position);
		viewHolder.scheduleMetroHour.setText(metroSchedule);

		// Check if this is the current Fragment, so mark the closest vehicle
		if (mfe.isActive() && position == mfe.getCurrentScheduleIndex()) {
			rowView.setBackgroundColor(Color.parseColor("#80CEEA"));
		} else {
			// Set the bacground to each row (even or odd)
			if (position % 2 == 1) {
				rowView.setBackgroundColor(Color.parseColor("#F1F1F1"));
			} else {
				rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}

			// Make the rows that the vehicle already passed inactive
			if (metroSchedule.contains("~")) {
				viewHolder.scheduleMetroHour.setTextColor(Color
						.parseColor("#000000"));
			} else {
				viewHolder.scheduleMetroHour.setTextColor(Color
						.parseColor("#8B8B8B"));
			}
		}

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		return rowView;
	}

}