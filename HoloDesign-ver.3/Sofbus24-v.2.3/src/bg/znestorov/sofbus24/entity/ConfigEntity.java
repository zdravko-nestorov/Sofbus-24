package bg.znestorov.sofbus24.entity;

import java.io.Serializable;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Configuration object containing some information about the application
 * 
 * @author Zdravko Nestorov
 * @version 2.0
 * 
 */
public class ConfigEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private int versionCode;
	private String versionName;

	private boolean favouritesVisiblå;
	private int favouritesPosition;
	private boolean searchVisible;
	private int searchPosition;
	private boolean scheduleVisible;
	private int schedulePosition;
	private boolean metroVisible;
	private int metroPosition;

	private int sofbus24DbVersion;

	public ConfigEntity() {
		this.versionCode = 0;
		this.versionName = null;

		this.favouritesVisiblå = true;
		this.favouritesPosition = 0;
		this.searchVisible = true;
		this.searchPosition = 1;
		this.scheduleVisible = true;
		this.schedulePosition = 2;
		this.metroVisible = true;
		this.metroPosition = 3;

		this.sofbus24DbVersion = 0;
	}

	public ConfigEntity(Activity context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);

		int versionCode;
		String versionName;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = null;
			versionCode = 0;
		}

		this.versionCode = versionCode;
		this.versionName = versionName;

		try {
			this.favouritesVisiblå = Boolean
					.parseBoolean(sharedPreferences
							.getString(
									Constants.CONFIGURATION_PREF_FAVOURITES_VISIBILITY_KEY,
									favouritesVisiblå + ""));
			this.favouritesPosition = Integer
					.parseInt(sharedPreferences
							.getString(
									Constants.CONFIGURATION_PREF_FAVOURITES_POSITION_KEY,
									favouritesPosition + ""));
		} catch (Exception e) {
			this.favouritesVisiblå = true;
			this.favouritesPosition = 0;
		}

		try {
			this.searchVisible = Boolean.parseBoolean(sharedPreferences
					.getString(
							Constants.CONFIGURATION_PREF_SEARCH_VISIBILITY_KEY,
							searchVisible + ""));
			this.searchPosition = Integer.parseInt(sharedPreferences.getString(
					Constants.CONFIGURATION_PREF_SEARCH_POSITION_KEY,
					searchPosition + ""));
		} catch (Exception e) {
			this.searchVisible = true;
			this.searchPosition = 1;
		}

		try {
			this.scheduleVisible = Boolean
					.parseBoolean(sharedPreferences
							.getString(
									Constants.CONFIGURATION_PREF_SCHEDULE_VISIBILITY_KEY,
									scheduleVisible + ""));
			this.schedulePosition = Integer.parseInt(sharedPreferences
					.getString(
							Constants.CONFIGURATION_PREF_SCHEDULE_POSITION_KEY,
							schedulePosition + ""));
		} catch (Exception e) {
			this.scheduleVisible = true;
			this.schedulePosition = 2;
		}

		try {
			this.metroVisible = Boolean.parseBoolean(sharedPreferences
					.getString(
							Constants.CONFIGURATION_PREF_METRO_VISIBILITY_KEY,
							metroVisible + ""));
			this.metroPosition = Integer.parseInt(sharedPreferences.getString(
					Constants.CONFIGURATION_PREF_METRO_POSITION_KEY,
					metroPosition + ""));
		} catch (Exception e) {
			this.metroVisible = true;
			this.metroPosition = 3;
		}

		try {
			this.sofbus24DbVersion = Integer.parseInt(sharedPreferences
					.getString(Constants.CONFIGURATION_PREF_SOFBUS24_KEY,
							sofbus24DbVersion + ""));
		} catch (Exception e) {
			this.sofbus24DbVersion = 0;
		}
	}

	public ConfigEntity(Document doc) {
		int versionCode = 0;
		String versionName = null;
		int stationsDbVersion = 0;

		try {
			XPath xpath = XPathFactory.newInstance().newXPath();

			Node versionCodeNode = (Node) xpath.evaluate(
					"Configuration/NewVersionCode/text()", doc,
					XPathConstants.NODE);
			versionCode = Integer.parseInt(versionCodeNode.getTextContent());

			Node versionNameNode = (Node) xpath.evaluate(
					"Configuration/NewVersionName/text()", doc,
					XPathConstants.NODE);
			versionName = versionNameNode.getTextContent();

			Node stationsDbVersionNode = (Node) xpath.evaluate(
					"Configuration/NewSofbus24DBVersion/text()", doc,
					XPathConstants.NODE);
			stationsDbVersion = Integer.parseInt(stationsDbVersionNode
					.getTextContent());
		} catch (Exception e) {
		}

		this.versionCode = versionCode;
		this.versionName = versionName;
		this.sofbus24DbVersion = stationsDbVersion;
	}

	public ConfigEntity(int versionCode, String versionName,
			int stationsDbVersion, boolean favouritesVisibilå,
			int favouritesPosition, boolean searchVisibile, int searchPosition,
			boolean scheduleVisibile, int schedulePosition,
			boolean metroVisibile, int metroPosition, int vehiclesDbVersion) {
		this.versionCode = versionCode;
		this.versionName = versionName;

		this.favouritesVisiblå = favouritesVisibilå;
		this.favouritesPosition = favouritesPosition;
		this.searchVisible = searchVisibile;
		this.searchPosition = searchPosition;
		this.scheduleVisible = scheduleVisibile;
		this.schedulePosition = schedulePosition;
		this.metroVisible = metroVisibile;
		this.metroPosition = metroPosition;

		this.sofbus24DbVersion = stationsDbVersion;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public boolean isFavouritesVisibilå() {
		return favouritesVisiblå;
	}

	public void setFavouritesVisibilå(boolean favouritesVisibilå) {
		this.favouritesVisiblå = favouritesVisibilå;
	}

	public int getFavouritesPosition() {
		return favouritesPosition;
	}

	public void setFavouritesPosition(int favouritesPosition) {
		this.favouritesPosition = favouritesPosition;
	}

	public boolean isSearchVisibile() {
		return searchVisible;
	}

	public void setSearchVisibile(boolean searchVisibile) {
		this.searchVisible = searchVisibile;
	}

	public int getSearchPosition() {
		return searchPosition;
	}

	public void setSearchPosition(int searchPosition) {
		this.searchPosition = searchPosition;
	}

	public boolean isScheduleVisibile() {
		return scheduleVisible;
	}

	public void setScheduleVisibile(boolean scheduleVisibile) {
		this.scheduleVisible = scheduleVisibile;
	}

	public int getSchedulePosition() {
		return schedulePosition;
	}

	public void setSchedulePosition(int schedulePosition) {
		this.schedulePosition = schedulePosition;
	}

	public boolean isMetroVisibile() {
		return metroVisible;
	}

	public void setMetroVisibile(boolean metroVisibile) {
		this.metroVisible = metroVisibile;
	}

	public int getMetroPosition() {
		return metroPosition;
	}

	public void setMetroPosition(int metroPosition) {
		this.metroPosition = metroPosition;
	}

	public int getSofbus24DbVersion() {
		return sofbus24DbVersion;
	}

	public void setSofbus24DbVersion(int stationsDbVersion) {
		this.sofbus24DbVersion = stationsDbVersion;
	}

	public HomeTabEntity getTabByPosition(Activity context, int position) {
		if (position >= 0 && position < Constants.GLOBAL_PARAM_HOME_TABS_COUNT) {
			boolean tabVisible;
			String tabName;

			if (favouritesPosition == position) {
				tabVisible = favouritesVisiblå;
				tabName = context.getString(R.string.edit_tabs_favourites);
			} else if (searchPosition == position) {
				tabVisible = searchVisible;
				tabName = context.getString(R.string.edit_tabs_search);
			} else if (schedulePosition == position) {
				tabVisible = scheduleVisible;
				tabName = context.getString(R.string.edit_tabs_schedule);
			} else {
				tabVisible = metroVisible;
				tabName = context.getString(R.string.edit_tabs_metro);
			}

			return new HomeTabEntity(tabVisible, tabName, position);
		} else {
			return new HomeTabEntity();
		}
	}

	/**
	 * Check if the Configuration entity is valid
	 * 
	 * @return if the entity is valid
	 */
	public boolean isValidConfig() {
		boolean isValidConfig = false;

		if (versionCode > 0 && versionName != null && sofbus24DbVersion > 0) {
			isValidConfig = true;
		}

		return isValidConfig;
	}

	/**
	 * Check if the Configuration is in default state (used to determine if the
	 * reset button in EditTabs should be visible)
	 * 
	 * @return if it is a default configuration
	 */
	public boolean isDefaultConfig() {
		boolean isDefaultConfig = favouritesVisiblå && favouritesPosition == 0
				&& searchVisible && searchPosition == 1 && scheduleVisible
				&& schedulePosition == 2 && metroVisible && metroPosition == 3;

		return isDefaultConfig;
	}

	/**
	 * Check if two configs are same
	 * 
	 * @param config
	 *            the current config
	 * @return if the configurations are the same
	 */
	public boolean isSameConfig(ConfigEntity config) {
		boolean isSameConfig = this.isFavouritesVisibilå() == config
				.isFavouritesVisibilå()
				&& this.getFavouritesPosition() == config
						.getFavouritesPosition()
				&& this.isSearchVisibile() == config.isSearchVisibile()
				&& this.getSearchPosition() == config.getSearchPosition()
				&& this.isScheduleVisibile() == config.isScheduleVisibile()
				&& this.getSchedulePosition() == config.getSchedulePosition()
				&& this.isMetroVisibile() == config.isMetroVisibile()
				&& this.getMetroPosition() == config.getMetroPosition();

		return isSameConfig;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tversionCode: " + versionCode
				+ "\n\tversionName: " + versionName
				+ "\n\tfavouritesVisibilå: " + favouritesVisiblå
				+ "\n\tfavouritesPosition: " + favouritesPosition
				+ "\n\tsearchVisibile: " + searchVisible
				+ "\n\tsearchPosition: " + searchPosition
				+ "\n\tscheduleVisibile: " + scheduleVisible
				+ "\n\tschedulePosition: " + schedulePosition
				+ "\n\tmetroVisibile: " + metroVisible + "\n\tmetroPosition: "
				+ metroPosition + "\n\tstationsDbVersion: " + sofbus24DbVersion
				+ "\n}";
	}

}