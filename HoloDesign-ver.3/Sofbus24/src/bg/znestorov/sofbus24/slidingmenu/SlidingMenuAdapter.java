package bg.znestorov.sofbus24.slidingmenu;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;

/**
 * Array Adapted used to set a sliding menu item
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class SlidingMenuAdapter extends ArrayAdapter<String> {

	private Activity context;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView menuIcon;
		TextView menuTitle;
	}

	public SlidingMenuAdapter(Activity context, List<String> menuItems) {
		super(context, R.layout.activity_sliding_menu_list_item, menuItems);

		this.context = context;
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
					R.layout.activity_sliding_menu_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.menuIcon = (ImageView) rowView
					.findViewById(R.id.menu_item_icon);
			viewHolder.menuTitle = (TextView) rowView
					.findViewById(R.id.menu_item_title);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		viewHolder.menuIcon.setImageResource(getSlidingMenuIcon(position));
		viewHolder.menuTitle.setText(getSlidingMenuTitle(position));

		return rowView;
	}

	/**
	 * Get the icon for the current row
	 * 
	 * @param position
	 *            the position of the row in the ListView
	 * @return the image corresponding to the row position
	 */
	private int getSlidingMenuIcon(int position) {
		int slidingMenuIcon;

		switch (position) {
		case 0:
			slidingMenuIcon = R.drawable.ic_sliding_menu_home;
			break;
		case 1:
			slidingMenuIcon = R.drawable.ic_sliding_menu_history;
			break;
		case 2:
			slidingMenuIcon = R.drawable.ic_sliding_menu_db;
			break;
		case 3:
			slidingMenuIcon = R.drawable.ic_sliding_menu_app;
			break;
		case 4:
			slidingMenuIcon = R.drawable.ic_sliding_menu_settings;
			break;
		default:
			slidingMenuIcon = R.drawable.ic_sliding_menu_exit;
			break;
		}

		return slidingMenuIcon;
	}

	/**
	 * Get the title for the current row
	 * 
	 * @param position
	 *            the position of the row in the ListView
	 * @return the title corresponding to the row position
	 */
	private String getSlidingMenuTitle(int position) {
		String slidingMenuTitle;

		switch (position) {
		case 0:
			slidingMenuTitle = context.getString(R.string.menu_item_home);
			break;
		case 1:
			slidingMenuTitle = context.getString(R.string.menu_item_history);
			break;
		case 2:
			slidingMenuTitle = context.getString(R.string.menu_item_db);
			break;
		case 3:
			slidingMenuTitle = context.getString(R.string.menu_item_app);
			break;
		case 4:
			slidingMenuTitle = context.getString(R.string.menu_item_settings);
			break;
		default:
			slidingMenuTitle = context.getString(R.string.menu_item_exit);
			break;
		}

		return slidingMenuTitle;
	}
}