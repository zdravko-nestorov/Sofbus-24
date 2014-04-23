package bg.znestorov.sofbus24.favorites;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.Sofbus24;
import bg.znestorov.sofbus24.metro.RetrieveMetroSchedule;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Array Adapted user for set each row a station from the Favourites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesStationAdapter extends ArrayAdapter<Station> {

	private final StationsDataSource stationsDataSource;
	private final FavouritesDataSource favouritesDatasource;
	private final Activity context;
	private final List<Station> stations;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ProgressBar progressBar;
		FrameLayout favItemLayout;
		TextView stationName;
		TextView stationNumber;
		ImageButton stationStreetView;
		View barView;
		ImageButton expandStation;
		ImageButton editStation;
		ImageButton removeStation;
	}

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;

	private boolean expandedListItem;

	public FavouritesStationAdapter(Activity context, List<Station> stations) {
		super(context, R.layout.activity_favourites_list_item, stations);
		this.context = context;
		this.stations = stations;
		this.favouritesDatasource = new FavouritesDataSource(context);
		this.stationsDataSource = new StationsDataSource(context);

		displayImageOptions = ActivityUtils.displayImageOptions();

		// Get the current state of the Favourites ListItems
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		expandedListItem = sharedPreferences.getBoolean(
				Constants.PREFERENCE_KEY_FAVOURITES_EXPANDED,
				Constants.PREFERENCE_DEFAULT_VALUE_FAVOURITES_EXPANDED);
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
			viewHolder.editStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_rename);
			viewHolder.removeStation = (ImageButton) rowView
					.findViewById(R.id.favourites_item_remove);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		Station station = stations.get(position);

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
				boolean isExpanded = viewHolder.stationStreetView
						.getVisibility() == View.VISIBLE;

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

		// Set the visibility and height of the favourites item
		viewHolder.stationStreetView.setVisibility(View.VISIBLE);
		viewHolder.favItemLayout.setMinimumHeight(getStationImageHeight());

		// Change the expand image
		viewHolder.expandStation.setImageResource(R.drawable.ic_collapse);

		// Add the image of the station from the street view asynchronously
		loadStationImage(viewHolder, station);
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

		// Set the visibility and height of the favourites item
		viewHolder.favItemLayout.setMinimumHeight(0);

		// Change the expand image
		viewHolder.expandStation.setImageResource(R.drawable.ic_expand);

		// Remove the image
		viewHolder.stationStreetView.setVisibility(View.GONE);
	}

	/**
	 * Get the height of the StationImage in pixels
	 * 
	 * @return the height of the StationImage in pixels
	 */
	private int getStationImageHeight() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float sp = 185f;
		int pixels = (int) (metrics.density * sp + 0.5f);

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

		// TODO: Find better approach
		if (stationLat.contains(",") || stationLat.contains(".")) {
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
	 * Attach a click listener to the EDIT button
	 * 
	 * @param editStation
	 *            the edit ImageButton
	 * @param station
	 *            the station on the rowView
	 * @param position
	 *            the position of the station in the List
	 */
	private void editStation(final ImageButton editStation,
			final Station station, final int position) {
		editStation.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent me) {
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					createEditDialog(station, position);
					return true;
				}

				return false;
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
	 * and if yes - rename the station in the Favourites DB
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
			FavouritesStationAdapter.this.remove(station);

			String oldStationName = station.getName();
			String newStationName = editTextInput;
			station.setName(newStationName);

			// Add the updated station to the List
			FavouritesStationAdapter.this.insert(station, position);
			FavouritesStationAdapter.this.notifyDataSetChanged();

			// Update the station parameters in the DB
			favouritesDatasource.open();
			favouritesDatasource.updateStation(station);
			favouritesDatasource.close();

			// Show toast message
			Toast.makeText(
					context,
					Html.fromHtml(String.format(
							context.getString(R.string.fav_item_rename_toast),
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
		removeStation.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent me) {
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					favouritesDatasource.open();
					favouritesDatasource.deleteStation(station);
					favouritesDatasource.close();
					FavouritesStationAdapter.this.remove(station);
					FavouritesStationAdapter.this.notifyDataSetChanged();

					Toast.makeText(
							context,
							Html.fromHtml(String.format(context
									.getString(R.string.fav_item_remove_toast),
									station.getName(), station.getNumber())),
							Toast.LENGTH_LONG).show();

					// Check which type of station is changed (METRO or OTHER)
					stationsDataSource.open();
					VehicleType stationType = stationsDataSource.getStation(
							station).getType();
					stationsDataSource.open();

					if (stationType == VehicleType.METRO1
							|| stationType == VehicleType.METRO2) {
						Sofbus24.setMetroChanged(true);
					} else {
						Sofbus24.setVBChanged(true);
					}

					return true;
				}

				return false;
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
				stationStreetView.performClick();
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
			// TODO: Retrieve information about the station
			Toast.makeText(context, station.getName(), Toast.LENGTH_SHORT)
					.show();
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