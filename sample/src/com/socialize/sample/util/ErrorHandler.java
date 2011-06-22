package com.socialize.sample.util;

import java.io.PrintWriter;

import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.sample.Error500Activity;

import android.content.Context;
import android.content.Intent;

public final class ErrorHandler {
	
	public static final void handleApiError(Context context, SocializeException error) {
		if(error instanceof SocializeApiError) {
			SocializeApiError serror = (SocializeApiError) error;
			if(serror.getResultCode() == 500) {
				writeErrorAndView(context, serror);
			}
			else {
				error.printStackTrace();
			}
		}
		else {
			error.printStackTrace();
		}
	}

	public static final void writeErrorAndView(Context context, SocializeApiError error) {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(context.openFileOutput("error.html", Context.MODE_PRIVATE));
			writer.write(error.getDescription());
			writer.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(writer != null) {
				writer.close();
			}
		}
		
		Intent i = new Intent(context, Error500Activity.class);
		context.startActivity(i);
		
		
	}
	
}
