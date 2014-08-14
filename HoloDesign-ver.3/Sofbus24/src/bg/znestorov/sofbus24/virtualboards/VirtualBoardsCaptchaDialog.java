package bg.znestorov.sofbus24.virtualboards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.activity.SerialBitmap;

public class VirtualBoardsCaptchaDialog extends DialogFragment {

	public interface OnCaptchaActionsListener {

		public void onCaptchaCompleted(String captchaId, String captchaText);

		public void onCaptchaCancelled();
	}

	private Activity context;
	private String captchaId;
	private Bitmap captchaImage;

	private EditText input;
	private String inputText;

	public static final String BUNDLE_CAPTCHA_ID = "CAPTCHA ID";
	public static final String BUNDLE_CAPTCHA_IMAGE = "CAPTCHA IMAGE";
	private static final String BUNDLE_INPUT_TEXT = "INPUT TEXT";

	public static VirtualBoardsCaptchaDialog newInstance(String captchaId,
			Bitmap captchaImage) {
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_CAPTCHA_ID, captchaId);
		bundle.putSerializable(BUNDLE_CAPTCHA_IMAGE, new SerialBitmap(
				captchaImage));

		VirtualBoardsCaptchaDialog vbCaptchaDialog = new VirtualBoardsCaptchaDialog();
		vbCaptchaDialog.setArguments(bundle);

		return vbCaptchaDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		captchaId = getArguments().getString(BUNDLE_CAPTCHA_ID);
		captchaImage = ((SerialBitmap) getArguments().getSerializable(
				BUNDLE_CAPTCHA_IMAGE)).getBitmap();

		if (savedInstanceState != null) {
			inputText = savedInstanceState.getString(BUNDLE_INPUT_TEXT);
		} else {
			inputText = "";
		}

		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.vb_time_sumc_captcha);
		builder.setMessage(R.string.vb_time_sumc_captcha_msg);

		LinearLayout panel = new LinearLayout(context);
		panel.setOrientation(LinearLayout.VERTICAL);

		ImageView image = new ImageView(context);
		image.setId(2);
		image.setImageBitmap(captchaImage);
		panel.addView(image);

		input = new EditText(context);
		input.setId(1);
		input.setSingleLine();
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_URI
				| InputType.TYPE_TEXT_VARIATION_PHONETIC);
		input.setText(inputText);
		input.setSelection(inputText.length());

		ScrollView view = new ScrollView(context);
		panel.addView(input);
		view.addView(panel);

		builder.setCancelable(true)
				.setPositiveButton(R.string.app_button_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String captchaText = input.getText().toString();

								((OnCaptchaActionsListener) getTargetFragment())
										.onCaptchaCompleted(captchaId,
												captchaText);
							}
						}).setView(view);

		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				((OnCaptchaActionsListener) getTargetFragment())
						.onCaptchaCancelled();
			}
		});

		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(BUNDLE_CAPTCHA_IMAGE,
				input != null ? input.getText().toString() : "");
	}
}