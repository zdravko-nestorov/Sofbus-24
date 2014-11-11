package bg.znestorov.sofbus24.navigation;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Class used to set the items ot the navigation drawer
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NavDrawerArrayAdapter extends ArrayAdapter<String> {

	private Activity context;
	private ArrayList<String> navigationItems;
	private ArrayList<Integer> navigationItemsImgs;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		View navDrawerLayout;
		ImageView navDrawerImg;
		TextView navDrawerText;
		ImageView navDrawerCheckedImg;
	}

	public NavDrawerArrayAdapter(Activity context,
			ArrayList<String> navigationItems) {
		super(context, R.layout.activity_navigation_drawer_list_item,
				navigationItems);
		this.context = context;
		this.navigationItems = navigationItems;
		this.navigationItemsImgs = new ArrayList<Integer>();

		for (int i = 0; i < navigationItems.size(); i++) {
			switch (i) {
			case 0:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_home);
				break;
			case 1:
				this.navigationItemsImgs
						.add(R.drawable.ic_slide_menu_home_standard);
				break;
			case 2:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_home_map);
				break;
			case 3:
				this.navigationItemsImgs
						.add(R.drawable.ic_slide_menu_home_cars);
				break;
			case 4:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_cs);
				break;
			case 5:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_history);
				break;
			case 6:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_options);
				break;
			case 7:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_info);
				break;
			default:
				this.navigationItemsImgs.add(R.drawable.ic_slide_menu_exit);
				break;
			}
		}
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_navigation_drawer_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.navDrawerLayout = rowView
					.findViewById(R.id.navigation_drawer_list_item_layout);
			viewHolder.navDrawerImg = (ImageView) rowView
					.findViewById(R.id.navigation_drawer_list_img);
			viewHolder.navDrawerText = (TextView) rowView
					.findViewById(R.id.navigation_drawer_list_text);
			viewHolder.navDrawerCheckedImg = (ImageView) rowView
					.findViewById(R.id.navigation_drawer_list_home_img);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Initialize each row of the NavigationDrawer
		initSubTagsBackground(position, viewHolder);
		viewHolder.navDrawerImg.setImageResource(navigationItemsImgs
				.get(position));
		viewHolder.navDrawerText.setText(navigationItems.get(position));
		initCheckedImage(position, viewHolder);

		return rowView;
	}

	@Override
	public boolean isEnabled(int position) {
		switch (position) {
		case 0:
			return false;
		default:
			return true;
		}
	}

	/**
	 * Initialize the Sub-tags of the Navigation Drawer (change the background
	 * color and fix the padding)
	 * 
	 * @param position
	 *            the position of the navigation drawer
	 * @param viewHolder
	 *            the view holder of the current row
	 */
	private void initSubTagsBackground(int position, ViewHolder viewHolder) {
		switch (position) {
		case 1:
		case 2:
		case 3:
			viewHolder.navDrawerLayout.setBackgroundColor(0x30BBBBBB);
			viewHolder.navDrawerLayout.setPadding(
					ActivityUtils.dpToPx(context, 25),
					ActivityUtils.dpToPx(context, 8),
					ActivityUtils.dpToPx(context, 10),
					ActivityUtils.dpToPx(context, 8));
			break;
		default:
			viewHolder.navDrawerLayout.setBackgroundColor(0xECECEC);
			viewHolder.navDrawerLayout.setPadding(
					ActivityUtils.dpToPx(context, 10),
					ActivityUtils.dpToPx(context, 8),
					ActivityUtils.dpToPx(context, 10),
					ActivityUtils.dpToPx(context, 8));
			break;
		}
	}

	/**
	 * Define the image that marks the choosen home screen
	 * 
	 * @param position
	 *            the current row position
	 * @param viewHolder
	 *            the view holder of the current row
	 */
	private void initCheckedImage(int position, ViewHolder viewHolder) {
		int userHomeScreenChoice = 0;
		boolean isUserHomeScreenChoosen = NavDrawerHomeScreenPreferences
				.isUserHomeScreenChoosen(context);

		if (isUserHomeScreenChoosen) {
			userHomeScreenChoice = NavDrawerHomeScreenPreferences
					.getUserHomeScreenChoice(context);
		} else {
			NavDrawerHomeScreenPreferences.setUserChoice(context, 0);
		}

		if (userHomeScreenChoice == position - 1) {
			viewHolder.navDrawerCheckedImg.setVisibility(View.VISIBLE);
			viewHolder.navDrawerCheckedImg
					.setImageResource(R.drawable.ic_slide_menu_checked);
		} else {
			switch (position) {
			case 0:
				viewHolder.navDrawerCheckedImg.setVisibility(View.VISIBLE);
				viewHolder.navDrawerCheckedImg
						.setImageResource(R.drawable.ic_slide_menu_arrow);
				break;
			case 1:
			case 2:
			case 3:
				viewHolder.navDrawerCheckedImg.setVisibility(View.VISIBLE);
				viewHolder.navDrawerCheckedImg
						.setImageResource(R.drawable.ic_slide_menu_unchecked);
				break;
			default:
				viewHolder.navDrawerCheckedImg.setVisibility(View.GONE);
				break;
			}
		}
	}

}