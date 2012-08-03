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
package com.socialize.api.action;



/**
 * Allows for the specification of options for sharing.
 * @author Jason Polites
 */
public class ActionOptions {
	
	private boolean selfManaged = false;
	private boolean showAuthDialog = true;
	
	protected ActionOptions() {
		super();
	}

	public boolean isSelfManaged() {
		return selfManaged;
	}

	/**
	 * Set to true if the sharing to 3rd party networks will be handled by the client (default: false)
	 * @param selfManaged
	 */
	public void setSelfManaged(boolean selfManaged) {
		this.selfManaged = selfManaged;
	}
	
	/**
	 * Returns true if auth is required for sharing.  Defaults to socialize.require.auth
	 * @return true if auth is required for sharing.
	 */
	public boolean isShowAuthDialog() {
		return showAuthDialog;
	}

	/**
	 * Set to false if you DON'T want the authenticate dialog to show when sharing.
	 * Defaults to socialize.require.auth config property.
	 * @param requireAuth
	 */
	public void setShowAuthDialog(boolean requireAuth) {
		this.showAuthDialog = requireAuth;
	}
	
	/**
	 * Used to merge legacy ActionOptions objects.
	 * @param other The options to be merged into this object.
	 */
	public void merge(ActionOptions other) {
		setShowAuthDialog(other.isShowAuthDialog());
		setSelfManaged(other.isSelfManaged());
	}
}
