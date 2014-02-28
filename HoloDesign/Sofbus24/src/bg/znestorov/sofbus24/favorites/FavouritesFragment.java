package bg.znestorov.sofbus24.favorites;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.main.R;

public class FavouritesFragment extends ListFragment {

	String names[] = {};

	public FavouritesFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(
				R.layout.activity_favorites_fragment, container, false);
		TextView tv = (TextView) myFragmentView
				.findViewById(R.id.edit_box_search);
		tv.setText("");
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, names));
		return myFragmentView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// on click display the item in toast
		Toast.makeText(getActivity(), (String) l.getItemAtPosition(position),
				Toast.LENGTH_SHORT).show();
	}
}
