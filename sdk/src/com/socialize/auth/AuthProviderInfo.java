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
package com.socialize.auth;

import com.socialize.error.SocializeException;

import java.io.Serializable;

/**
 * @author Jason Polites
 *
 */
public interface AuthProviderInfo extends Serializable {
	/**
	 * Returns the type of this provider.
	 * @return
	 */
	public AuthProviderType getType();
	
	/**
	 * Validates the provider info.
	 * @throws SocializeException If the info is not valid;
	 */
	public void validate() throws SocializeException;
	
	/**
	 * Returns true if this info config is valid
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * Used to compare saved session data with a call to authenticate.
	 * @param info
	 * @return
	 */
	public boolean matches(AuthProviderInfo info);
	
	/**
	 * Merges the info passed with the saved session data
	 * @param info
	 * @return
	 */
	public boolean merge(AuthProviderInfo info);
	
	public int hashCode();
	
	public boolean equals(Object obj);
	
	
	
}
