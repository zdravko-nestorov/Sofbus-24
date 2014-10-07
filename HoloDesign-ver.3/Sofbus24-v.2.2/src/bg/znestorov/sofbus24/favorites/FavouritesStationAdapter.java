package bg.znestorov.sofbus24.favorites;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HtmlRequestCodesEnum;
import bg.znestorov.sofbus24.entity.MetroStationEntity;
import bg.znestorov.sofbus24.entity.PositionTypeEnum;
import bg.znestorov.sofbus24.entity.PublicTransportStationEntity;
import bg.znestorov.sofbus24.entity.SortTypeEnum;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.StationMap;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.TranslatorLatinToCyrillic;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.GooglePlayServicesErrorDialog;
import bg.znestorov.sofbus24.virtualboards.RetrieveVirtualBoards;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Array Adapted user for set each row a station from the Favorites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
@SuppressLint({ "DefaultLocale", "InflateParams" })
public class FavouritesStationAdapter extends ArrayAdapter<StationEntity> {

	private Activity context;
	private GlobalEntity globalContext;

	private View emptyView;
	private FavouritesStationFragment favouritesStationFragment;

	private List<StationEntity> originalStations;
	private List<StationEntity> filteredStations;

	private StationsDataSource stationsDataSource;
	private FavouritesDataSource favouritesDatasource;

	private Filter stationsFilter;
	private boolean isPhoneDevice;
	private String language;

	private boolean isReorderVisible;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ProgressBar progressBar;
		FrameLayout favItemLayout;
		TextView stationName;
		TextView stationNumber;
		ImageButton stationStreetView;
		View barView;
		ImageButton expandStation;
		ImageButton reorderStation;
		ImageButton settingsStation;
	}

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;

	private boolean expandedListItem;

	public FavouritesStationAdapter(Activity context, View emptyView,
			FavouritesStationFragment favouritesStationFragment,
			List<StationEntity> stations) {
		super(context, R.layout.activity_favourites_list_item, stations);

		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();
		this.isPhoneDevice = globalContext.isPhoneDevice();
		this.language = LanguageChange.getUserLocale(context);

		this.emptyView = emptyView;
		this.favouritesStationFragment = favouritesStationFragment;

		this.originalStations = stations;
		this.filteredStations = stations;

		this.favouritesDatasource = new FavouritesDataSource(context);
		this.stationsDataSource = new StationsDataSource(context);

		this.displayImageOptions = ActivityUtils.displayImageOptions();
		this.imageLoader.init(ActivityUtils.initImageLoader(context));

		this.isReorderVisible = isReorderVisible();

		setExpandedListItemValue();
	}

	/**
	 * Check if the reorder button should be visible (if the list is containing
	 * more than one element - return true)
	 * 
	 * @return if the reorder button should be visible
	 */
	private boolean isReorderVisible() {
		int originalStationsSize = originalStations.size();
		int filteredStationsSize = filteredStations.size();

		if (originalStationsSize != filteredStationsSize) {
			return isReorderVisible;
		} else {
			if (filteredStationsSize > 1) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Get the current state of the Favorites ListItems
	 */
	public void setExpandedListItemValue() {
		if (!isPhoneDevice) {
			expandedListItem = true;
		} else {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			expandedListItem = sharedPreferences.getBoolean(
					Constants.PREFERENCE_KEY_FAVOURITES_EXPANDED,
					Constants.PREFERENCE_DEFAULT_VALUE_FAVOURITES_EXPANDED);
		}
	}

	@Override
	public StationEntity getItem(int position) {
		return filteredStations.get(position);
	}

	@Override
	public int getCount() {
		return filteredStations != null ? filteredStations.size() : 0;
	}

	@Override
	public boolean isEmpty() {
		return filteredStations.isEmpty();
	}

	/**
	 * Filter the ListView according some criteria (filter)
	 * 
	 * @return a filter constrains data with a filtering pattern
	 */
	@Override
	public Filter getFilter() {
		if (stationsFilter == null) {
			stationsFilter = createFilter();
		}

		return stationsFilter;
	}

	/**
	 * Create a custom filter, so process the list on searching
	 * 
	 * @return a custom filter
	 */
	private Filter createFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				// If there's nothing to filter on, return the original data for
				// your list
				if (constraint == null || constraint.length() == 0) {
					isReorderVisible = true;

					results.values = originalStations;
					results.count = originalStations.size();
				} else {
					isReorderVisible = false;

					List<StationEntity> filterResultsData = new ArrayList<StationEntity>();

					String filterStringOrig = constraint.toString().trim()
							.toUpperCase();
					String filterString = constraint.toString().trim()
							.toUpperCase();
					if ("bg".equals(language)) {
						filterString = TranslatorLatinToCyrillic.translate(
								context, filterString);
					} else {
						filterString = TranslatorCyrillicToLatin.translate(
								context, filterString);
					}

					String filterebaleName;
					String filterebaleNumber;

					// Itterate over all stations and search which ones match
					// the filter
					for (StationEntity station : originalStations) {
						filterebaleName = station.getName().toUpperCase();
						filterebaleNumber = station.getNumber().toUpperCase();

						if (filterebaleName.contains(filterString)
								|| filterebaleNumber.contains(filterString)
								|| filterebaleName.contains(filterStringOrig)
								|| filterebaleNumber.contains(filterStringOrig)) {
							filterResultsData.add(station);
						}
					}

					results.values = filterResultsData;
					results.count = filterResultsData.size();
				}

				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults filterResults) {
				filteredStations = (ArrayList<StationEntity>) filterResults.values;
				notifyDataSetChanged();

				setEmptyListView();
			}
		};
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
			rowView = inflater.inflate(R.layout.activity_favourites_list_item,
					null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.progressBar = (ProgressBar) rowView
					.findViewById(R.id.favourites_item_progress_bar);
			viewHolder.favItemLayout = (FrameLayout) rowView
					.findViewById(R.id.favourites_item_frame_layout);
			viewHolder.stationName = (TextView) rowView
					.findViewById(R.id.favourites_item_station_name);
			viewHolder.stationNumber = (TextView) rowView
					.findViewById(R.id.favourites_item_station_number);
			viewHolder.stationStreetView = (ImageButton) rowView
					.findViewById(R.id.favourites_item_bg_image);
			viewHolder.barView = rowView.findViewById(R.id.favourites_item_bar);
			viewHolder.expandStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_expand);
			viewHolder.reorderStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_reorder);
			viewHolder.settingsStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_settings);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		StationEntity station = filteredStations.get(position);

		// Add the Station Name and the Station Number
		viewHolder.stationName.setText(station.getName());
		viewHolder.stationNumber.setText(String.format(
				context.getString(R.string.fav_item_station_number_text),
				station.getNumber()));

		// Add the image of the station from the street view asynchronously
		if (expandedListItem) {
			expandListItem(viewHolder, station);
		} else {
			collapseListItem(viewHolder);
		}

		// Show the order button only if the custom ordering is choosen
		isReorderVisible = isReorderVisible();
		if (FavouritesPreferences.getFavouritesSortType(context) == SortTypeEnum.CUSTOM
				&& isReorderVisible) {
			viewHolder.reorderStation.setVisibility(View.VISIBLE);
		} else {
			viewHolder.reorderStation.setVisibility(View.GONE);
		}

		// Attach click listeners to the EXPAND, EDIT and REMOVE buttons
		if (isPhoneDevice) {
			expandStation(viewHolder, station);
		} else {
			changeExpandStationIcon(viewHolder.expandStation);
		}

		reorderStation(viewHolder.reorderStation, station, position);
		chooseStation(viewHolder.stationStreetView, viewHolder.barView, station);
		actionsOverSettingsButton(viewHolder.settingsStation, station);

		return rowView;
	}

	/**
	 * Attach a click listener to the EXPAND button
	 * 
	 * @param viewHolder
	 *            the holder containing all elements from the list item layout
	 * @param station
	 *            the station on the current row
	 */
	private void expandStation(final ViewHolder viewHolder,
			final StationEntity station) {
		viewHolder.expandStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isExpanded = viewHolder.favItemLayout.getHeight() >= getExpandedStationImageHeight();

				if (!isExpanded) {
					expandListItem(viewHolder, station);
				} else {
					collapseListItem(viewHolder);
				}
			}
		});
	}

	/**
	 * Used in case of Tablet (change the expand icon to a an info icon)
	 * 
	 * @param expandStation
	 *            the expand station image button
	 */
	private void changeExpandStationIcon(ImageButton expandStation) {
		expandStation.setImageResource(R.drawable.ic_menu_star);
		expandStation.setEnabled(false);
	}

	/**
	 * Expand the list item
	 * 
	 * @param viewHolder
	 *            the holder containing all elements from the list item layout
	 * @param station
	 *            the station on the current row
	 */
	private void expandListItem(final ViewHolder viewHolder,
			final StationEntity station) {
		// Set the visibility of the progress bar
		viewHolder.progressBar.setVisibility(View.VISIBLE);

		// Change the expand image
		viewHolder.expandStation.setImageResource(R.drawable.ic_collapse);

		// Add the image of the station from the street view asynchronously
		LayoutParams params = viewHolder.stationStreetView.getLayoutParams();
		params.height = getExpandedStationImageHeight();
		viewHolder.stationStreetView.setLayoutParams(params);
		loadStationImage(viewHolder, station);
	}

	/**
	 * Get the height of the Expanded StationImage in pixels
	 * 
	 * @return the height of the StationImage in pixels
	 */
	private int getExpandedStationImageHeight() {
		int pixels = (int) context.getResources().getDimension(
				R.dimen.favourites_item_height);

		return pixels;
	}

	/**
	 * Collapse the list item
	 * 
	 * @param viewHolder
	 *            the holder containing all elements from the list item layout
	 */
	private void collapseListItem(final ViewHolder viewHolder) {
		// Set the visibility of the progress bar
		viewHolder.progressBar.setVisibility(View.GONE);

		// Change the expand image
		viewHolder.expandStation.setImageResource(R.drawable.ic_expand);

		// Remove the image
		LayoutParams params = viewHolder.stationStreetView.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		viewHolder.stationStreetView.setLayoutParams(params);
		viewHolder.stationStreetView
				.setImageResource(android.R.color.transparent);
	}

	/**
	 * Add the image of the station from the street view asynchronously
	 * 
	 * @param viewHolder
	 *            the holder containing all elements from the list item layout
	 * @param station
	 *            the station on the current row
	 */
	private void loadStationImage(final ViewHolder viewHolder,
			StationEntity station) {
		String stationLat = station.getLat();
		String stationLon = station.getLon();

		String imageUrl;
		if (stationLat != null
				&& (stationLat.contains(",") || stationLat.contains("."))) {
			imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
					stationLat, stationLon);
		} else {
			imageUrl = "drawable://" + R.drawable.ic_no_image_available;
		}

		imageLoader.displayImage(imageUrl, viewHolder.stationStreetView,
				displayImageOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						viewHolder.progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						viewHolder.progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						viewHolder.progressBar.setVisibility(View.GONE);
					}
				});
	}

	private void reorderStation(ImageButton reorderStation,
			final StationEntity station, final int position) {
		reorderStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				favouritesDatasource.open();

				PositionTypeEnum positionType = favouritesDatasource
						.getStationPosition(station);
				switch (positionType) {
				case FIRST:
				case LAST:
				case MEDIUM:
					FavouritesOrderDialog favouritesOrderDialog = FavouritesOrderDialog
							.newInstance(station, positionType);
					favouritesOrderDialog.setTargetFragment(
							favouritesStationFragment, 0);
					favouritesOrderDialog.show(
							favouritesStationFragment.getFragmentManager(),
							"dialog");
					break;
				default:
					break;
				}

				favouritesDatasource.close();
			}
		});
	}

	/**
	 * Attach a click listener to the current row of the list view
	 * 
	 * @param stationStreetView
	 *            the current row image button
	 * @param barView
	 *            the current row LinearLayout bar
	 * @param station
	 *            the station on the current row
	 */
	private void chooseStation(final ImageButton stationStreetView,
			View barView, final StationEntity station) {
		stationStreetView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onChooseStation(station);
			}
		});

		barView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Handler handler = new Handler();
				Runnable r = new Runnable() {
					public void run() {
						stationStreetView.setPressed(true);
						stationStreetView.invalidate();
						stationStreetView.performClick();

						Handler handler1 = new Handler();
						Runnable r1 = new Runnable() {
							public void run() {
								stationStreetView.setPressed(false);
								stationStreetView.invalidate();

							}
						};
						handler1.postDelayed(r1, 150);

					}
				};
				handler.postDelayed(r, 150);
			}
		});
	}

	/**
	 * Actions taken when a row is selected
	 * 
	 * @param station
	 *            the station on the current row
	 */
	private void onChooseStation(StationEntity station) {
		String stationCustomField = station.getCustomField();
		String metroCustomField = String.format(Constants.METRO_STATION_URL,
				station.getNumber());

		// Update the station info in the Favourites DB
		ActivityUtils.updateFavouritesStationInfo(context, station);

		// Request an update of the Favorites Fragment
		globalContext.setFavouritesChanged(true);

		// Check if the type of the station - BTT or METRO
		if (!stationCustomField.equals(metroCustomField)) {
			RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
					context, null, station, HtmlRequestCodesEnum.FAVOURITES);
			retrieveVirtualBoards.getSumcInformation();
		} else {
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(Html.fromHtml(String.format(
					context.getString(R.string.metro_loading_schedule),
					station.getName(), station.getNumber())));
			RetrieveMetroSchedule retrieveMetroSchedule = new RetrieveMetroSchedule(
					context, progressDialog, station);
			retrieveMetroSchedule.execute();
		}
	}

	/**
	 * Assign a "onClickListener(...)" over the ImageButton
	 * 
	 * @param stationSettings
	 *            the settings image button
	 * @param station
	 *            the station on the current row
	 */
	private void actionsOverSettingsButton(ImageButton stationSettings,
			final StationEntity station) {
		stationSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.favourites_item_settings:
					// TODO: Find a way to implement in 2.2 (API 8)
					PopupMenu popup = new PopupMenu(context, v);
					Menu menu = popup.getMenu();
					popup.getMenuInflater().inflate(
							R.menu.activity_favourites_context_menu, menu);

					// Force the context menu to show icons
					try {
						Field[] fields = popup.getClass().getDeclaredFields();
						for (Field field : fields) {
							if ("mPopup".equals(field.getName())) {
								field.setAccessible(true);
								Object menuPopupHelper = field.get(popup);
								Class<?> classPopupHelper = Class
										.forName(menuPopupHelper.getClass()
												.getName());
								Method setForceIcons = classPopupHelper
										.getMethod("setForceShowIcon",
												boolean.class);
								setForceIcons.invoke(menuPopupHelper, true);
								break;
							}
						}
					} catch (Exception e) {
					}
					popup.show();

					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
							case R.id.menu_favourites_rename:
								editStation(station);
								break;
							case R.id.menu_favourites_remove:
								removeStation(station);
								break;
							case R.id.menu_favourites_google_maps:
								seeStationOnGoogleMaps(station);
								break;
							case R.id.menu_favourites_google_street_view:
								seeStationOnGoogleStreetView(station);
								break;
							case R.id.menu_favourites_information:
								getStationInfo(station);
								break;
							}

							return true;
						}
					});
				}
			}
		});
	}

	/**
	 * Actions over the EDIT button
	 * 
	 * @param station
	 *            the station on the rowView
	 */
	private void editStation(StationEntity station) {
		FavouritesRenameDialog favouritesRenameDialog = FavouritesRenameDialog
				.newInstance(station);
		favouritesRenameDialog.setTargetFragment(favouritesStationFragment, 0);
		favouritesRenameDialog.show(
				favouritesStationFragment.getFragmentManager(), "dialog");
	}

	/**
	 * Actions over the REMOVE button
	 * 
	 * @param station
	 *            the station on the rowView
	 */
	private void removeStation(StationEntity station) {
		favouritesDatasource.open();
		favouritesDatasource.deleteStation(station);
		favouritesDatasource.close();
		remove(station);
		notifyDataSetChanged();

		Toast.makeText(
				context,
				Html.fromHtml(String.format(
						context.getString(R.string.app_toast_remove_favourites),
						station.getName(), station.getNumber())),
				Toast.LENGTH_SHORT).show();

		// Check which type of station is changed (METRO or OTHER)
		stationsDataSource.open();
		StationEntity dbStation = stationsDataSource.getStation(station);
		VehicleTypeEnum stationType = dbStation != null ? dbStation.getType()
				: VehicleTypeEnum.BTT;
		stationsDataSource.open();

		if (stationType != VehicleTypeEnum.METRO1
				&& stationType != VehicleTypeEnum.METRO2) {
			globalContext.setVbChanged(true);
		}
	}

	/**
	 * Actions over GoogleMaps button
	 * 
	 * @param station
	 *            the station on the rowView
	 */
	private void seeStationOnGoogleMaps(StationEntity station) {
		if (!globalContext.areServicesAvailable()) {
			GooglePlayServicesErrorDialog googlePlayServicesErrorDialog = new GooglePlayServicesErrorDialog();
			googlePlayServicesErrorDialog.show(
					favouritesStationFragment.getFragmentManager(),
					"GooglePlayServicesErrorDialog");
		} else {
			if (station.hasCoordinates()) {
				Intent stationMapIntent = new Intent(context, StationMap.class);

				// Check the type of the station
				if (station.isMetroStation()) {
					stationMapIntent.putExtra(Constants.BUNDLE_STATION_MAP,
							new MetroStationEntity(station));
				} else {
					station.setType(VehicleTypeEnum.BUS);
					stationMapIntent.putExtra(Constants.BUNDLE_STATION_MAP,
							new PublicTransportStationEntity(station));
				}

				context.startActivity(stationMapIntent);
			} else {
				ActivityUtils.showNoCoordinatesToast(context);
			}
		}
	}

	/**
	 * Actions over GoogleMaps StreetView button
	 * 
	 * @param station
	 *            the station on the rowView
	 */
	private void seeStationOnGoogleStreetView(StationEntity station) {
		if (!globalContext.areServicesAvailable()) {
			ActivityUtils
					.showGooglePlayServicesErrorDialog(favouritesStationFragment);
		} else {
			if (globalContext.isGoogleStreetViewAvailable()) {
				if (station.hasCoordinates()) {
					Uri streetViewUri = Uri.parse("google.streetview:cbll="
							+ station.getLat() + "," + station.getLon()
							+ "&cbp=1,90,,0,1.0&mz=20");
					Intent streetViewIntent = new Intent(Intent.ACTION_VIEW,
							streetViewUri);
					context.startActivity(streetViewIntent);
				} else {
					ActivityUtils.showNoCoordinatesToast(context);
				}
			} else {
				ActivityUtils
						.showGoogleStreetViewErrorDialog(favouritesStationFragment);
			}
		}
	}

	/**
	 * Actions over the INFO button
	 * 
	 * @param station
	 *            the station on the rowView
	 */
	private void getStationInfo(StationEntity station) {
		FavouritesInfoDialog favouritesInfoDialog = FavouritesInfoDialog
				.newInstance(station);
		favouritesInfoDialog.setTargetFragment(favouritesInfoDialog, 0);
		favouritesInfoDialog.show(
				favouritesStationFragment.getFragmentManager(), "dialog");
	}

	/**
	 * Show or hide the empty view in case of GridView
	 */
	private void setEmptyListView() {
		if (filteredStations == null || filteredStations.size() == 0) {
			if (emptyView != null) {
				emptyView.setVisibility(View.VISIBLE);
			}
		} else {
			if (emptyView != null) {
				emptyView.setVisibility(View.GONE);
			}
		}
	}
}