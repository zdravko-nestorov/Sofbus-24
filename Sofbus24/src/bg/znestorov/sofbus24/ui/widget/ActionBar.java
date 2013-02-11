package bg.znestorov.sofbus24.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import bg.znestorov.sofbus24.main.Preferences;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.SplashScreen;

public class ActionBar extends LinearLayout implements OnClickListener {

	private Context context;

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
	}

	public ActionBar(Context context) {
		super(context);

		this.context = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		findViewById(R.id.action_bar_home).setOnClickListener(this);
		findViewById(R.id.action_bar_options).setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.action_bar_home:
			intent = new Intent(context, SplashScreen.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			break;
		case R.id.action_bar_options:
			intent = new Intent(context, Preferences.class);
			context.startActivity(intent);
			break;
		}
	}
}