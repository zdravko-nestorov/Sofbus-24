package bg.znestorov.sofbus24.entity;

import android.app.Application;
import bg.znestorov.sofbus24.main.R;

/**
 * Global class that extends Application and save state across several
 * Activities and all parts of your application. Each Activity is also a
 * Context, which is information about its execution environment in the broadest
 * sense. Your application also has a context, and Android guarantees that it
 * will exist as a single instance across your application.
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class GlobalEntity extends Application {

	private boolean isPhoneDevice;

	private boolean hasToRestart = false;
	private boolean isFavouritesChanged = false;
	private boolean isVbChanged = false;
	private boolean isHomeScreenChanged = false;

	@Override
	public void onCreate() {
		super.onCreate();
		initialize();
	}

	public boolean isPhoneDevice() {
		return isPhoneDevice;
	}

	public void setPhoneDevice(boolean isPhoneDevice) {
		this.isPhoneDevice = isPhoneDevice;
	}

	public boolean isHasToRestart() {
		return hasToRestart;
	}

	public void setHasToRestart(boolean hasToRestart) {
		this.hasToRestart = hasToRestart;
	}

	public boolean isFavouritesChanged() {
		return isFavouritesChanged;
	}

	public void setFavouritesChanged(boolean isFavouritesChanged) {
		this.isFavouritesChanged = isFavouritesChanged;
	}

	public boolean isVbChanged() {
		return isVbChanged;
	}

	public void setVbChanged(boolean isVbChanged) {
		this.isVbChanged = isVbChanged;
	}

	public boolean isHomeScreenChanged() {
		return isHomeScreenChanged;
	}

	public void setHomeScreenChanged(boolean isHomeScreenChanged) {
		this.isHomeScreenChanged = isHomeScreenChanged;
	}

	private void initialize() {
		isPhoneDevice = getResources().getBoolean(R.bool.isPhone);
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\thasToRestart: " + hasToRestart
				+ "\n\tisFavouritesChanged: " + isFavouritesChanged
				+ "\n\tisVbChanged: " + isVbChanged
				+ "\n\tisHomeScreenChanged: " + isHomeScreenChanged + "\n}";
	}

}