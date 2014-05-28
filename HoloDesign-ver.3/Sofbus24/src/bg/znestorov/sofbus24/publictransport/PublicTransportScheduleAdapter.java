package bg.znestorov.sofbus24.publictransport;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.ScheduleEntity;
import bg.znestorov.sofbus24.main.R;

/**
 * Array Adapted used to set each hour of the public transport schedule
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class PublicTransportScheduleAdapter extends ArrayAdapter<String> {

	private Activity context;
	private ScheduleEntity ptScheduleEntity;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		TextView schedulePTHour;
	}

	public PublicTransportScheduleAdapter(Activity context,
			ScheduleEntity ptScheduleEntity) {
		super(context, R.layout.activity_public_transport_schedule_list_item,
				ptScheduleEntity.getFormattedScheduleList());
		this.context = context;
		this.ptScheduleEntity = ptScheduleEntity;
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
			rowView = inflater
					.inflate(
							R.layout.activity_public_transport_schedule_list_item,
							null);

			// Configure the view holder
			viewHolder = new ViewHolder();
			viewHolder.schedulePTHour = (TextView) rowView
					.findViewById(R.id.pt_schedule_item_hour);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		String ptSchedule = ptScheduleEntity.getFormattedScheduleList().get(
				position);
		viewHolder.schedulePTHour.setText(ptSchedule);

		// Check if this is the current Fragment, so mark the closest vehicle
		if (ptScheduleEntity.isActive()
				&& position == ptScheduleEntity.getCurrentScheduleIndex()) {
			rowView.setBackgroundColor(Color.parseColor("#80CEEA"));
		} else {
			// Set the bacground to each row (even or odd)
			if (position % 2 == 1) {
				rowView.setBackgroundColor(Color.parseColor("#F1F1F1"));
			} else {
				rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}

			// Make the rows that the vehicle already passed inactive
			if (ptSchedule.contains("~")) {
				viewHolder.schedulePTHour.setTextColor(Color
						.parseColor("#000000"));
			} else {
				viewHolder.schedulePTHour.setTextColor(Color
						.parseColor("#8B8B8B"));
			}
		}

		rowView.setOnClickListener(null);
		rowView.setOnLongClickListener(null);
		rowView.setLongClickable(false);

		return rowView;
	}

}