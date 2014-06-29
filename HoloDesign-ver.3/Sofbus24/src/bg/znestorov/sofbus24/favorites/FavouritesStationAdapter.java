package bg.znestorov.sofbus24.favorites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HtmlRequestCodes;
import bg.znestorov.sofbus24.entity.MetroStation;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.StationMap;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.TranslatorLatinToCyrillic;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
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
public class FavouritesStationAdapter extends ArrayAdapter<Station> {

	private Activity context;
	private GlobalEntity globalContext;

	private List<Station> originalStations;
	private List<Station> filteredStations;

	private StationsDataSource stationsDataSource;
	private FavouritesDataSource favouritesDatasource;

	private Filter stationsFilter;
	private String language;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ProgressBar progressBar;
		FrameLayout favItemLayout;
		TextView stationName;
		TextView stationNumber;
		ImageButton stationStreetView;
		View barView;
		ImageButton expandStation;
		ImageButton googleMaps;
		ImageButton googleStreetView;
		ImageButton editStation;
		ImageButton removeStation;
	}

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;

	private boolean expandedListItem;

	public FavouritesStationAdapter(Activity context, List<Station> stations) {
		super(context, R.layout.activity_favourites_list_item, stations);
		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();
		this.language = LanguageChange.getUserLocale(context);

		this.originalStations = stations;
		this.filteredStations = stations;

		this.favouritesDatasource = new FavouritesDataSource(context);
		this.stationsDataSource = new StationsDataSource(context);

		displayImageOptions = ActivityUtils.displayImageOptions();
		setExpandedListItemValue();
	}

	/**
	 * Get the current state of the Favorites ListItems
	 */
	public void setExpandedListItemValue() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		expandedListItem = sharedPreferences.getBoolean(
				Constants.PREFERENCE_KEY_FAVOURITES_EXPANDED,
				Constants.PREFERENCE_DEFAULT_VALUE_FAVOURITES_EXPANDED);
	}

	@Override
	public Station getItem(int position) {
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
					results.values = originalStations;
					results.count = originalStations.size();
				} else {
					List<Station> filterResultsData = new ArrayList<Station>();

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
					for (Station station : originalStations) {
						filterebaleName = station.getName().toUpperCase();
						filterebaleNumber = station.getNumber().toUpperCase();

						if (filterebaleName.contains(filterString)
								|| filterebaleNumber.contains(filterString)) {
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
				filteredStations = (ArrayList<Station>) filterResults.values;
				notifyDataSetChanged();
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
			viewHolder.googleMaps = (ImageButton) rowView
					.findViewById(R.id.favourites_item_google_maps);
			viewHolder.googleStreetView = (ImageButton) rowView
					.findViewById(R.id.favourites_item_google_street_view);
			viewHolder.editStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_rename);
			viewHolder.removeStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_remove);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		Station station = filteredStations.get(position);

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

		// Attach click listeners to the EXPAND, EDIT and REMOVE buttons
		expandStation(viewHolder, station);
		seeStationOnGoogleMaps(viewHolder.googleMaps, station);
		seeStationOnGoogleStreetView(viewHolder.googleStreetView, station);
		editStation(viewHolder.editStation, station, position);
		removeStation(viewHolder.removeStation, station);
		chooseStation(viewHolder.stationStreetView, viewHolder.barView, station);

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
			final Station station) {
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
	 * Expand the list item
	 * 
	 * @param viewHolder
	 *            the holder containing all elements from the list item layout
	 * @param station
	 *            the station on the current row
	 */
	private void expandListItem(final ViewHolder viewHolder,
			final Station station) {
		// Set the visibility of the progress bar
		viewHolder.progressBar.setVisibility(View.VISIBLE);

		// Set the visibility and height of the favorites item
		viewHolder.favItemLayout
				.setMinimumHeight(getExpandedStationImageHeight());

		// Change the expand image
		viewHolder.expandStation.setImageResource(R.drawable.ic_collapse);

		// Add the image of the station from the street view asynchronously
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

		// Set the visibility and height of the favorites item
		viewHolder.favItemLayout
				.setMinimumHeight(getCollapsedStationImageHeight(viewHolder.barView));

		// Change the expand image
		viewHolder.expandStation.setImageResource(R.drawable.ic_expand);

		// Remove the image
		viewHolder.stationStreetView
				.setMinimumHeight(getCollapsedStationImageHeight(viewHolder.barView));
		viewHolder.stationStreetView
				.setImageResource(android.R.color.transparent);
	}

	/**
	 * Get the height of the Collapsed StationImage in pixels
	 * 
	 * @return the height of the StationImage in pixels
	 */
	private int getCollapsedStationImageHeight(View barView) {
		int pixels = (int) barView.getHeight();

		return pixels;
	}

	/**
	 * Add the image of the station from the street view asynchronously
	 * 
	 * @param viewHolder
	 *            the holder containing all elements from the list item layout
	 * @param station
	 *            the station on the current row
	 */
	private void loadStationImage(final ViewHolder viewHolder, Station station) {
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

	/**
	 * Attach a click listener to the GoogleMaps button (only for landscape
	 * mode)
	 * 
	 * @param googleMaps
	 *            the GoogleMaps ImageButton
	 * @param station
	 *            the station on the rowView
	 */
	private void seeStationOnGoogleMaps(ImageButton googleMaps,
			final Station station) {
		// In case of Landscape mode
		if (googleMaps != null) {
			googleMaps.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (station.hasCoordinates()) {
						Intent stationMapIntent = new Intent(context,
								StationMap.class);

						// Check the type of the station
						if (station.isMetroStation()) {
							stationMapIntent.putExtra(
									Constants.BUNDLE_STATION_MAP,
									new MetroStation(station));
						} else {
							station.setType(VehicleType.BUS);
							stationMapIntent.putExtra(
									Constants.BUNDLE_STATION_MAP,
									new PublicTransportStation(station));
						}

						context.startActivity(stationMapIntent);
					} else {
						ActivityUtils.showNoCoordinatesToast(context);
					}
				}
			});
		}
	}

	/**
	 * Attach a click listener to the GoogleStreetView button (only for
	 * landscape mode)
	 * 
	 * @param googleStreetView
	 *            the GoogleStreetView ImageButton
	 * @param station
	 *            the station on the rowView
	 */
	private void seeStationOnGoogleStreetView(ImageButton googleStreetView,
			final Station station) {
		// In case of Landscape mode
		if (googleStreetView != null) {
			googleStreetView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (station.hasCoordinates()) {
						Uri streetViewUri = Uri.parse("google.streetview:cbll="
								+ station.getLat() + "," + station.getLon()
								+ "&cbp=1,90,,0,1.0&mz=20");
						Intent streetViewIntent = new Intent(
								Intent.ACTION_VIEW, streetViewUri);
						context.startActivity(streetViewIntent);
					} else {
						ActivityUtils.showNoCoordinatesToast(context);
					}
				}
			});
		}
	}

	/**
	 * Attach a click listener to the EDIT button
	 * 
	 * @param editStation
	 *            the edit ImageButton
	 * @param station
	 *            the station on the rowView
	 * @param position
	 *            the position of the station in the List
	 */
	private void editStation(ImageButton editStation, final Station station,
			final int position) {
		editStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				createEditDialog(station, position);
			}
		});
	}

	/**
	 * Create an Alert Dialog for editing the name of the station
	 * 
	 * @param station
	 *            the station on the current row
	 * @param position
	 *            the position of the station in the List
	 */
	private void createEditDialog(final Station station, final int position) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.fav_item_rename_title);
		alert.setIcon(android.R.drawable.ic_menu_edit);
		alert.setMessage(Html.fromHtml(String.format(
				context.getString(R.string.fav_item_rename_msg),
				station.getName(), station.getNumber())));

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		input.setHint(context.getString(R.string.fav_item_rename_hint));
		input.setMaxLines(1);

		alert.setView(input);

		alert.setPositiveButton(context.getString(R.string.app_button_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						renameStation(input, station, position);
					}
				});

		alert.setNegativeButton(context.getString(R.string.app_button_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do Nothing
					}
				});

		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);

		// Add a click listener when ENTER key is pressed
		input.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					// Check if any value is entered and if so - rename the
					// station and close the dialog
					boolean isRenamed = renameStation(input, station, position);
					if (isRenamed) {
						dialog.cancel();
					}

					return true;
				}

				return false;
			}
		});

		// Add on change text listener on the input field
		input.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String inputText = input.getText().toString();

				if (inputText.length() == 0) {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}
		});

		// Add Focus listener on the input field
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, input);
				} else {
					dialog.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		// Request focus
		input.requestFocus();
	}

	/**
	 * Check if the text entered in the EditText field fulfill some conditions
	 * and if yes - rename the station in the Favorites DB
	 * 
	 * 
	 * @param input
	 *            the EditText input field
	 * @param station
	 *            the station object on the current row
	 * @param position
	 *            the row number
	 * @return if the station is renamed or not
	 */
	private boolean renameStation(EditText input, Station station, int position) {
		String editTextInput = input.getText().toString();

		if (editTextInput != null && !"".equals(editTextInput)) {
			// Hide the keyboard
			ActivityUtils.hideKeyboard(context, input);

			// Remove the station from the List
			remove(station);

			String oldStationName = station.getName();
			String newStationName = editTextInput;
			station.setName(newStationName);

			// Add the updated station to the List
			insert(station, position);
			notifyDataSetChanged();

			// Update the station parameters in the DB
			favouritesDatasource.open();
			favouritesDatasource.updateStation(station);
			favouritesDatasource.close();

			// Show toast message
			Toast.makeText(
					context,
					Html.fromHtml(String.format(context
							.getString(R.string.app_toast_rename_favourites),
							oldStationName, station.getNumber(),
							newStationName, station.getNumber())),
					Toast.LENGTH_LONG).show();

			return true;
		}

		return false;
	}

	/**
	 * Attach a click listener to the REMOVE button
	 * 
	 * @param removeStation
	 *            the remove ImageButton
	 * @param station
	 *            the station on the rowView
	 */
	private void removeStation(final ImageButton removeStation,
			final Station station) {
		removeStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
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
				Station dbStation = stationsDataSource.getStation(station);
				VehicleType stationType = dbStation != null ? dbStation
						.getType() : VehicleType.BTT;
				stationsDataSource.open();

				if (stationType != VehicleType.METRO1
						&& stationType != VehicleType.METRO2) {
					globalContext.setVbChanged(true);
				}
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
			View barView, final Station station) {
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
	private void onChooseStation(Station station) {
		String stationCustomField = station.getCustomField();
		String metroCustomField = String.format(Constants.METRO_STATION_URL,
				station.getNumber());

		// Check if the type of the station - BTT or METRO
		if (!stationCustomField.equals(metroCustomField)) {
			RetrieveVirtualBoards retrieveVirtualBoards = new RetrieveVirtualBoards(
					context, null, station, HtmlRequestCodes.FAVOURITES);
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
}