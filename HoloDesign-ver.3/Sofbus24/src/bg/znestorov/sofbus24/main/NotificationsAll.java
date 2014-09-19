package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.NotificationsDataSource;
import bg.znestorov.sofbus24.entity.NotificationEntity;
import bg.znestorov.sofbus24.notifications.NotificationsAdapter;
import bg.znestorov.sofbus24.notifications.NotificationsDeleteAllDialog;
import bg.znestorov.sofbus24.notifications.NotificationsDeleteAllDialog.OnDeleteAllNotificationsListener;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.ListActivity;

/**
 * Activity containing information about the notifications (alarms)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NotificationsAll extends ListActivity implements
		OnDeleteAllNotificationsListener {

	private Activity context;
	private NotificationsDataSource notificationsDatasource;
	private ActionBar actionBar;

	private ProgressBar loadingNotifications;
	private View notificationsContent;

	private NotificationsAdapter notificationsAdapter;
	private ArrayList<NotificationEntity> notificationsList = new ArrayList<NotificationEntity>();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_notifications_all);

		// Get the current context
		context = NotificationsAll.this;
		notificationsDatasource = new NotificationsDataSource(context);

		// Initialize the ActionBar and the Layout fields
		initActionBar();
		initLayoutFields();
		setListAdapter();

		// Start an asynchrnic task to load the data from the preferences file
		new AsyncTask<Void, Void, ArrayList<NotificationEntity>>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ActivityUtils.lockScreenOrientation(context);

				loadingNotifications.setVisibility(View.VISIBLE);
				notificationsContent.setVisibility(View.INVISIBLE);
			}

			@Override
			protected ArrayList<NotificationEntity> doInBackground(
					Void... params) {
				notificationsDatasource.open();
				ArrayList<NotificationEntity> notificationEntities = notificationsDatasource
						.getAllNotifications();
				notificationsDatasource.close();

				return notificationEntities;
			}

			@Override
			protected void onPostExecute(
					ArrayList<NotificationEntity> retrievedHistory) {
				super.onPostExecute(retrievedHistory);

				loadingNotifications.setVisibility(View.INVISIBLE);
				notificationsContent.setVisibility(View.VISIBLE);

				notificationsList.clear();
				notificationsList.addAll(retrievedHistory);
				notificationsAdapter.notifyDataSetChanged();

				ActivityUtils.unlockScreenOrientation(context);
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
				ActivityUtils.unlockScreenOrientation(context);
			}

		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_notifications_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int searchesCount = notificationsList.size();

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_notifications_delete_all:
			if (searchesCount > 0) {
				DialogFragment dialogFragment = NotificationsDeleteAllDialog
						.newInstance();
				dialogFragment.show(getSupportFragmentManager(), "dialog");
			} else {
				Toast.makeText(
						context,
						Html.fromHtml(getString(R.string.notifications_menu_remove_all_empty_toast)),
						Toast.LENGTH_SHORT).show();
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize the ActionBar
	 */
	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.notifications_all_title));
	}

	/**
	 * Initialize the Layout fields
	 */
	private void initLayoutFields() {
		loadingNotifications = (ProgressBar) findViewById(R.id.notifications_loading);
		notificationsContent = findViewById(R.id.notifications_content);
	}

	/**
	 * Set the list adapter
	 */
	private void setListAdapter() {
		notificationsAdapter = new NotificationsAdapter(context,
				notificationsList);
		setListAdapter(notificationsAdapter);
	}

	@Override
	public void onDeleteAllNotificationsClicked() {
		// TODO: Remove the notifications from Android Service...

		notificationsDatasource.open();
		notificationsDatasource.deleteAllNotifications();
		notificationsDatasource.close();

		notificationsList.clear();
		notificationsAdapter.notifyDataSetChanged();

		Toast.makeText(
				context,
				Html.fromHtml(getString(R.string.notifications_menu_remove_all_toast)),
				Toast.LENGTH_SHORT).show();
	}

}