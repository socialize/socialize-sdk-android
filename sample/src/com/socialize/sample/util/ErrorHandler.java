package com.socialize.sample.util;

import java.io.PrintWriter;

import android.content.Context;

import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.util.StringUtils;

public final class ErrorHandler {
	
	public static final String handleApiError(Context context, SocializeException error) {
		if(error instanceof SocializeApiError) {
			SocializeApiError serror = (SocializeApiError) error;
			if(serror.getResultCode() >= 400) {
				if(writeError(context, serror)) {
					return serror.getResultCode() + " Error, file written to device";
				}
				else {
					return serror.getResultCode() + " Error, no additional info";
				}
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

	public static final boolean writeError(Context context, SocializeApiError error) {
		PrintWriter writer = null;
		
		try {
			if(!StringUtils.isEmpty(error.getDescription())) {
				writer = new PrintWriter(context.openFileOutput("error.html", Context.MODE_PRIVATE));
				writer.write(error.getDescription());
				writer.flush();
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(writer != null) {
				writer.close();
			}
		}
		
		return false;
		
	}
	
}
