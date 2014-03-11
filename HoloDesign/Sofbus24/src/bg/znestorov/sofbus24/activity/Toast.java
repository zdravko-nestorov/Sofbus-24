package bg.znestorov.sofbus24.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;

public class Toast {

	public static final int LENGTH_SHORT = 0;
	public static final int LENGTH_LONG = 1;

	private static android.widget.Toast toast;

	private Toast(Activity context, CharSequence text, int duration) {
		LayoutInflater inflater = context.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) context.findViewById(R.id.toast_layout_root));

		TextView textView = (TextView) layout
				.findViewById(R.id.custom_toast_text);
		textView.setText(text);

		toast = new android.widget.Toast(context);
		toast.setDuration(duration);
		toast.setView(layout);
	}

	/**
	 * Make a custom toast that just contains a text view.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object
	 * @param text
	 *            The text to show. Can be formatted text
	 * @param duration
	 *            How long to display the message. Either LENGTH_SHORT or
	 *            LENGTH_LONG
	 */
	public static Toast makeText(Activity context, CharSequence text,
			int duration) {
		Toast customToast = new Toast(context, text, duration);

		return customToast;
	}

	/**
	 * Show the custom toast
	 */
	public void show() {
		toast.show();
	}

}
