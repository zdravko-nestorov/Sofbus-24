package bg.znestorov.sofbus24.favorites;

import java.util.List;

import android.content.Context;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.Station;

/**
 * Singleton used for loading the favourites stations on the first creation and
 * used them lately
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class FavouritesLoadStations {

	private static FavouritesLoadStations instance = null;
	private List<Station> favouriteStations;

	protected FavouritesLoadStations(Context context) {
		FavouritesDataSource favouritesDatasource = new FavouritesDataSource(
				context);

		favouritesDatasource.open();
		favouriteStations = favouritesDatasource.getAllStations();
		favouritesDatasource.close();
	}

	public static FavouritesLoadStations getInstance(Context context) {
		if (instance == null) {
			instance = new FavouritesLoadStations(context);
		}

		return instance;
	}

	public List<Station> getFavouriteStations() {
		return favouriteStations;
	}

	public void setFavouriteStations(List<Station> favouriteStations) {
		this.favouriteStations = favouriteStations;
	}

	@Override
	public String toString() {
		return "FavouritesLoadStations [favouriteStations=" + favouriteStations
				+ "]";
	}

}
