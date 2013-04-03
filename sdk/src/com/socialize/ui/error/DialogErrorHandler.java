package com.socialize.ui.error;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.DialogRegistration;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

public class DialogErrorHandler implements SocializeErrorHandler {

	private Drawables drawables;
	private String message;
	
	@Override
	public void handleError(Context context, Exception e) {
		SocializeLogger.e("", e);
		String message = "An unexpected error occurred.  Please try again";
		if(e instanceof SocializeApiError) {
			int code = ((SocializeApiError)e).getResultCode();
			if(code == 500) {
				message += "\n\nServer Error (" + code + ")";
			}
			else {
				message += "\n\n" + e.getMessage();
			}
		}
		
		handleError(context, message);
	}

	protected void handleError(Context context, String message) {
		try {
			AlertDialog.Builder builder = makeBuilder(context);
			builder.setTitle("Error");
			
			if(drawables != null) {
				builder.setIcon(drawables.getDrawable("socialize_icon_white.png"));
			}
			
			if(!StringUtils.isEmpty(message)) {
				builder.setMessage(message);
			}
			else {
				builder.setMessage(this.message);
			}
			
			builder.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			
			AlertDialog alert = builder.create();
			
			DialogRegistration.register(context, alert);
			
			alert.show();
		}
		catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Error displaying dialog, original message was [" +
					message +
					"]", e);
		}
	}
	
	protected Builder makeBuilder(Context context) {
		return new AlertDialog.Builder(context);
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
