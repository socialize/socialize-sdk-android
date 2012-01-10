/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


/**
 * Callback interface for API requests.
 * 
 * Each method includes a 'state' parameter that identifies the calling
 * request. It will be set to the value passed when originally calling the
 * request method, or null if none was passed.
 */
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
	 * Called when a request fails because the requested resource is invalid
	 * or does not exist.
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
