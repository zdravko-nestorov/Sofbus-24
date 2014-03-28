package bg.znestorov.sofbus24.entity;

import android.app.Activity;

/**
 * This interface is used to update a Fragment. It is called from an Activity,
 * FragmentActivity or AsyncTask and update the Adapter of the ListFragment
 * implementing this interface
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public interface UpdateableFragment {

	/**
	 * Use to update the Fragment. It is called from an Activity,
	 * FragmentActivity or AsyncTask and update the Adapter of the ListFragment
	 * implementing this interface
	 * 
	 * @param context
	 *            the current Activity context
	 * @param obj
	 *            any type of object that has to be passed to the update method
	 */
	public void update(Activity context, Object obj);

}
