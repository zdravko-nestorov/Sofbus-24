package bg.znestorov.sofbus24.activity;

/**
 * Interface used to set click listener to a EditText, so interact with search
 * and clear buttons
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public interface DrawableClickListener {

	public static enum DrawablePosition {
		TOP, BOTTOM, LEFT, RIGHT
	};

	public void onClick(DrawablePosition target);
}