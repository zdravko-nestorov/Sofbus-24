How to run Sofbus 24:
1. Install Android Studio 2.3
2. Import Sofbus 24 Android application
3. Go to "Android Studio -> Preferences -> Appearance & Behavour -> System Settings -> Android SDK" and do the following:
	- SDK Platforms: Install only "Android 5.1 (Lolipop)"
	- SDK Tools (tick "Show Package Details"): Install "Android SDK Build-Tools 25.0.3"
4. Go to "File -> Project Structure" and do the following:
	- Libraries: Remove all "android-support-v4" libraries, except the first one. Only "android-support-v4" and "sofbus-libs" should be present
	- SDKs: Ensure that only "Java 1.8" and Android API 22 Platform" are present
	- Modules: Add "android-support-v4" library to "ActionBarSherlock", "DragSortListView" and "PagerSlidingTabStrip" in "Provided" scope ("Sofbus24" module always has it in compile time)
5. Go to "Tools -> Android -> AVD Manager" and do the following:
	- Click "Create Virtual Device"
	- Go to "x86 Images" section
	- Download "Marshmallow, 23, x86_x64, Android 6.0 (with Google APIs)"
	- Create the new device
6. Start "Sofbus 24" and ENJOY