package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.TabType;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.NonSwipeableViewPager;

/**
 * Schedule Fragment containing information about the public transport vehicles
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleFragment extends Fragment {

	private ViewPager mNonSwipeableViewPager;
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

	private int currentVehicle;
	private static final String BUNDLE_CURRENT_VEHICLE = "CURRENT VEHICLE";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initBundleInfo(savedInstanceState);
		initLayoutFields();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Activate the option menu
		setHasOptionsMenu(true);

		return inflater.inflate(R.layout.activity_schedule_fragment, container,
				false);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putInt(BUNDLE_CURRENT_VEHICLE, currentVehicle);
	}

	/**
	 * Get the current vehicle code from the Bundle object
	 * 
	 * @param savedInstanceState
	 *            object containing the state of the saved values
	 */
	private void initBundleInfo(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			currentVehicle = savedInstanceState.getInt(BUNDLE_CURRENT_VEHICLE);
		} else {
			currentVehicle = 0;
		}
	}

	/**
	 * Initialize the layout fields and assign the appropriate listeners over
	 * them (vehicles tabs (TextViews) and the ViewPager)
	 */
	private void initLayoutFields() {
		// Create the fragments list
		createFragmentsList();

		// Create the adapter that will return a fragment for each of the
		// vehicles
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mNonSwipeableViewPager = (NonSwipeableViewPager) getView()
				.findViewById(R.id.schedule_pager);
		mNonSwipeableViewPager.setOffscreenPageLimit(2);
		mNonSwipeableViewPager.setAdapter(mSectionsPagerAdapter);

		// Get the direction tabs and assign them onClickListeners
		TextView textViewBus = (TextView) getView().findViewById(
				R.id.schedule_bus_tab);
		TextView textViewTrolley = (TextView) getView().findViewById(
				R.id.schedule_trolley_tab);
		TextView textViewTram = (TextView) getView().findViewById(
				R.id.schedule_tram_tab);
		actionsOverDirectionsTextViews(textViewBus, textViewTrolley,
				textViewTram);

		// Set the active tab
		setActiveFragment(currentVehicle, textViewBus, textViewTrolley,
				textViewTram);
	}

	/**
	 * Create the FragmentsList, where each element contains a separate type of
	 * vehicles
	 */
	private void createFragmentsList() {
		fragmentsList.add(ScheduleVehicleFragment.newInstance(0));
		fragmentsList.add(ScheduleVehicleFragment.newInstance(1));
		fragmentsList.add(ScheduleVehicleFragment.newInstance(2));
	}

	/**
	 * Activate the listeners over the directions' tabs (TextViews)
	 * 
	 * @param textViewBus
	 *            bus tab (TextView)
	 * @param textViewTrolley
	 *            trolley tab (TextView)
	 * @param textViewTrolley
	 *            tram tab (TextView)
	 */
	private void actionsOverDirectionsTextViews(final TextView textViewBus,
			final TextView textViewTrolley, final TextView textViewTram) {
		// Assign the bus TextView a click listener
		textViewBus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setActiveFragment(0, textViewBus, textViewTrolley, textViewTram);
			}
		});

		// Assign the trolley TextView a click listener
		textViewTrolley.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setActiveFragment(1, textViewBus, textViewTrolley, textViewTram);
			}
		});

		// Assign the tram TextView a click listener
		textViewTram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setActiveFragment(2, textViewBus, textViewTrolley, textViewTram);
			}
		});
	}

	/**
	 * Set a metro tab to be active - change the background and text colors
	 * 
	 * @param tabType
	 *            the position of the tab
	 * @param textView
	 *            the TextView which is selected
	 */
	private void setTabActive(TabType tabType, TextView textView) {
		int backgroundResource;
		switch (tabType) {
		case LEFT:
			backgroundResource = R.drawable.tab_left_selected;
			break;
		case MIDDLE:
			backgroundResource = R.drawable.tab_middle_selected;
			break;
		default:
			backgroundResource = R.drawable.tab_right_selected;
			break;
		}

		textView.setBackgroundResource(backgroundResource);
		textView.setTextColor(getResources().getColor(R.color.white));
	}

	/**
	 * Set a metro tab to be inactive - change the background and text colors
	 * 
	 * @param tabType
	 *            the position of the tab
	 * @param textView
	 *            the TextView that has to be deactivated
	 */
	private void setTabInactive(TabType tabType, TextView textView) {
		int backgroundResource;
		switch (tabType) {
		case LEFT:
			backgroundResource = R.drawable.tab_left_unselected;
			break;
		case MIDDLE:
			backgroundResource = R.drawable.tab_middle_unselected;
			break;
		default:
			backgroundResource = R.drawable.tab_right_unselected;
			break;
		}

		textView.setBackgroundResource(backgroundResource);
		textView.setTextColor(getResources().getColor(R.color.inner_tab_grey));
	}

	/**
	 * Set a fragment to be visible (on top)
	 * 
	 * @param tabNumber
	 *            the number of the direction
	 * @param textViewBus
	 *            bus tab (TextView)
	 * @param textViewTrolley
	 *            trolley tab (TextView)
	 * @param textViewTrolley
	 *            tram tab (TextView)
	 */
	private void setActiveFragment(int tabNumber, TextView textViewBus,
			TextView textViewTrolley, TextView textViewTram) {
		switch (tabNumber) {
		case 0:
			currentVehicle = 0;
			setTabActive(TabType.LEFT, textViewBus);
			setTabInactive(TabType.MIDDLE, textViewTrolley);
			setTabInactive(TabType.RIGHT, textViewTram);
			break;
		case 1:
			currentVehicle = 1;
			setTabActive(TabType.MIDDLE, textViewTrolley);
			setTabInactive(TabType.LEFT, textViewBus);
			setTabInactive(TabType.RIGHT, textViewTram);
			break;
		default:
			currentVehicle = 2;
			setTabActive(TabType.RIGHT, textViewTram);
			setTabInactive(TabType.LEFT, textViewBus);
			setTabInactive(TabType.MIDDLE, textViewTrolley);
			break;
		}

		mNonSwipeableViewPager.setCurrentItem(currentVehicle);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentsList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}
	}
}