package bg.znestorov.sofbus24.databases;

import android.content.Context;

/**
 * Class containing all helping functions for creating and deleting the
 * Favourites DB
 * 
 * @author zanio
 * @version 1.0
 * 
 */
public class FavouritesDatabaseUtils {

	/**
	 * Delete all records from the Favorites DB (the DB remains empty - it is
	 * not deleted)
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void deleteFavouriteDatabase(Context context) {
		FavouritesDataSource favouritesDatasource = new FavouritesDataSource(
				context);
		favouritesDatasource.open();
		favouritesDatasource.deleteAllStations();
		favouritesDatasource.close();
	}

}
