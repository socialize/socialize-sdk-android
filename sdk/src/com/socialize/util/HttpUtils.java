package com.socialize.util;

public final class HttpUtils {

	public static final boolean isHttpError(int code) {
		return (code < 200 || code >= 300);
	}

	
	public static final String getMessageFor(int code) {
		// TODO: finish
		return null;
	}
}
