package bg.znestorov.sofbus24.favorites;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.databases.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.ActivityUtils;
import bg.znestorov.sofbus24.utils.Constants;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Array Adapted user for set each row a station from the Favourites DB
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesStationAdapter extends ArrayAdapter<Station> {

	private final FavouritesDataSource favouritesDatasource;
	private final Context context;
	private final List<Station> stations;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;

	public FavouritesStationAdapter(Context context, List<Station> stations) {
		super(context, R.layout.activity_favourites_list_item, stations);
		this.context = context;
		this.stations = stations;
		this.favouritesDatasource = new FavouritesDataSource(context);

		displayImageOptions = ActivityUtils.displayImageOptions();
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Station station = stations.get(position);
		View rowView = convertView;
		rowView = setFavouritesRow(position, inflater, parent, station);

		return rowView;
	}

	/**
	 * Direction row in the ListView
	 * 
	 * @param position
	 *            the row number
	 * @param inflater
	 *            process the XML file for the visual part
	 * @param parent
	 *            used to create a multiple-exclusion scope for a set of radio
	 *            buttons (not used)
	 * @param station
	 *            the station object on the current row
	 * @return a view representing the look on the screen
	 */
	public View setFavouritesRow(int position, LayoutInflater inflater,
			ViewGroup parent, final Station station) {
		View rowView = inflater.inflate(R.layout.activity_favourites_list_item,
				parent, false);

		// Set the station name and number
		TextView stationName = (TextView) rowView
				.findViewById(R.id.favourites_item_station_name);
		TextView stationNumber = (TextView) rowView
				.findViewById(R.id.favourites_item_station_number);

		stationName.setText(station.getName());
		stationNumber.setText(String.format(
				context.getString(R.string.fav_item_station_number_text),
				station.getNumber()));

		// Add the image of the station from the street view asynchronously
		ImageView stationStreetView = (ImageView) rowView
				.findViewById(R.id.favourites_item_bg_image);
		String stationLat = station.getLat();
		String stationLon = station.getLon();
		String imageUrl;

		if (stationLat.contains(",") || stationLat.contains(".")) {
			imageUrl = String.format(Constants.FAVOURITES_IMAGE_URL,
					stationLat, stationLon);
		} else {
			imageUrl = "drawable://" + R.drawable.ic_no_image_available;
		}

		imageLoader.displayImage(imageUrl, stationStreetView,
				displayImageOptions, null);

		// Attach click listeners to the EDIT and REMOVE buttons
		editStation(position, rowView, station);
		removeStation(rowView, station);

		return rowView;
	}

	/**
	 * Attach a click listener to the EDIT button
	 * 
	 * @param rowView
	 *            the current item of the ListView in Favourites section
	 * @param station
	 *            the station on the rowView
	 */
	private void editStation(final int position, View rowView,
			final Station station) {
		ImageButton editStation = (ImageButton) rowView
				.findViewById(R.id.favourites_item_rename);
		editStation.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				createEditDialog(position, station);
			}
		});
	}

	/**
	 * Create an Alert Dialog for editing the name of the station
	 * 
	 * @param station
	 *            the station on the current row
	 */
	private void createEditDialog(final int position, final Station station) {
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
						renameStation(position, station, input);
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
					boolean isRenamed = renameStation(position, station, input);
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
	 * @param position
	 *            the row number
	 * @param station
	 *            the station object on the current row
	 * @param input
	 *            the EditText input field
	 * @return if the station is renamed or not
	 */
	private boolean renameStation(int position, Station station, EditText input) {
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
	 * @param rowView
	 *            the current item of the ListView in Favourites section
	 * @param station
	 *            the station on the rowView
	 */
	private void removeStation(View rowView, final Station station) {
		ImageButton removeStation = (ImageButton) rowView
				.findViewById(R.id.favourites_item_remove);
		removeStation.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
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
			}
		});
	}
}