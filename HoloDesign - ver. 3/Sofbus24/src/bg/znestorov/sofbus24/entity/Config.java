package bg.znestorov.sofbus24.entity;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Configuration object containing some information about the application
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Config {

	private int versionCode;
	private String versionName;
	private int stationsDbVersion;
	private int vehiclesDbVersion;

	public Config() {
		this.versionCode = 0;
		this.versionName = null;
		this.stationsDbVersion = 0;
		this.vehiclesDbVersion = 0;
	}

	public Config(Activity context) {
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
		this.stationsDbVersion = Integer.parseInt(sharedPreferences.getString(
				Constants.CONFIGURATION_PREF_STATIONS_KEY, null));
		this.vehiclesDbVersion = Integer.parseInt(sharedPreferences.getString(
				Constants.CONFIGURATION_PREF_VEHICLES_KEY, null));
	}

	public Config(Document doc) {
		int versionCode = 0;
		String versionName = null;
		int stationsDbVersion = 0;
		int vehiclesDbVersion = 0;

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
					"Configuration/NewStationsDBVersion/text()", doc,
					XPathConstants.NODE);
			stationsDbVersion = Integer.parseInt(stationsDbVersionNode
					.getTextContent());

			Node vehiclesDbVersionNode = (Node) xpath.evaluate(
					"Configuration/NewVehiclesDBVersion/text()", doc,
					XPathConstants.NODE);
			vehiclesDbVersion = Integer.parseInt(vehiclesDbVersionNode
					.getTextContent());
		} catch (Exception e) {
		}

		this.versionCode = versionCode;
		this.versionName = versionName;
		this.stationsDbVersion = stationsDbVersion;
		this.vehiclesDbVersion = vehiclesDbVersion;
	}

	public Config(int versionCode, String versionName, int stationsDbVersion,
			int vehiclesDbVersion) {
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.stationsDbVersion = stationsDbVersion;
		this.vehiclesDbVersion = vehiclesDbVersion;
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

	public int getStationsDbVersion() {
		return stationsDbVersion;
	}

	public void setStationsDbVersion(int stationsDbVersion) {
		this.stationsDbVersion = stationsDbVersion;
	}

	public int getVehiclesDbVersion() {
		return vehiclesDbVersion;
	}

	public void setVehiclesDbVersion(int vehiclesDbVersion) {
		this.vehiclesDbVersion = vehiclesDbVersion;
	}

	/**
	 * Check if the Configuration entity is valid
	 * 
	 * @return if the entity is valid
	 */
	public boolean isValidConfig() {
		boolean isValidConfig = false;

		if (versionCode > 0 && versionName != null && stationsDbVersion > 0
				&& vehiclesDbVersion > 0) {
			isValidConfig = true;
		}

		return isValidConfig;
	}

	@Override
	public String toString() {
		return "Config [versionCode=" + versionCode + ", versionName="
				+ versionName + ", stationsDbVersion=" + stationsDbVersion
				+ ", vehiclesDbVersion=" + vehiclesDbVersion + "]";
	}

}