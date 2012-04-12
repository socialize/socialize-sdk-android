package com.socialize.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public interface RequestListener {

	/**
	 * Called when a request completes with the given response.
	 * 
	 * Executed by a background thread: do not update the UI in this method.
	 */
	public void onComplete(String response, Object state);

	/**
	 * Called when a request has a network or request error.
	 * 
	 * Executed by a background thread: do not update the UI in this method.
	 */
	public void onIOException(IOException e, Object state);

	/**
	 * Called when a request fails because the requested resource is invalid or
	 * does not exist.
	 * 
	 * Executed by a background thread: do not update the UI in this method.
	 */
	public void onFileNotFoundException(FileNotFoundException e, Object state);

	/**
	 * Called if an invalid graph path is provided (which may result in a
	 * malformed URL).
	 * 
	 * Executed by a background thread: do not update the UI in this method.
	 */
	public void onMalformedURLException(MalformedURLException e, Object state);

	/**
	 * Called when the server-side Facebook method fails.
	 * 
	 * Executed by a background thread: do not update the UI in this method.
	 */
	public void onFacebookError(FacebookError e, Object state);

}
