package com.socialize.sample.util;

import java.io.PrintWriter;

import android.content.Context;

import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;

public final class ErrorHandler {
	
	public static final String handleApiError(Context context, SocializeException error) {
		if(error instanceof SocializeApiError) {
			SocializeApiError serror = (SocializeApiError) error;
			if(serror.getResultCode() == 500) {
				writeError(context, serror);
				return "500 Error, file written to device";
			}
			else if(serror.getResultCode() == 404) {
				writeError(context, serror);
				return "404 Error, file written to device";
			}
			else {
				error.printStackTrace();
			}
		}
		else {
			error.printStackTrace();
		}
		
		return error.getMessage();
	}

	public static final void writeError(Context context, SocializeApiError error) {
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
		
	}
	
}
