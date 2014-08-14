package bg.znestorov.sofbus24.publictransport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.main.PublicTransport;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;

public class ChooseDirectionDialog extends DialogFragment {

	private Activity context;
	private String title;
	private DialogInterface.OnClickListener onListItemClickListener;
	private DirectionsEntity ptDirectionsEntity;

	public static final String BUNDLE_PT_DIRECTION_ENTITY = "PT DIRECTION ENTITY";

	public static ChooseDirectionDialog newInstance(
			DirectionsEntity ptDirectionsEntity) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_PT_DIRECTION_ENTITY, ptDirectionsEntity);

		ChooseDirectionDialog updateApplicationDialog = new ChooseDirectionDialog();
		updateApplicationDialog.setArguments(bundle);

		return updateApplicationDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		title = getString(R.string.sch_item_direction_choice);
		ptDirectionsEntity = (DirectionsEntity) getArguments().getSerializable(
				BUNDLE_PT_DIRECTION_ENTITY);

		ArrayAdapter<String> ptDirectionsAdapter = new ArrayAdapter<String>(
				context, R.layout.activity_public_transport_directions_item,
				ptDirectionsEntity.getDirectionsNames());

		onListItemClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				ptDirectionsEntity.setActiveDirection(i);
				Intent publicTransport = new Intent(context,
						PublicTransport.class);
				publicTransport.putExtra(
						Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
						ptDirectionsEntity);
				context.startActivity(publicTransport);
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setAdapter(ptDirectionsAdapter,
				onListItemClickListener);

		return builder.create();
	}
}