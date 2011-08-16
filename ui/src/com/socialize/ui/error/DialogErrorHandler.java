package com.socialize.ui.error;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class DialogErrorHandler implements SocializeUIErrorHandler {

	@Override
	public void handleError(Context context, String message) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Error");
			builder.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Error displaying dialog, original message was [" +
					message +
					"]", e);
		}
	}
}
