/*
 * Copyright (c) 2011 Socialize Inc.
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

/**
 * @author Jason Polites
 *
 */
public class AuthProviderResponse {

	private String userId;
	private String token;
	
	private String firstName;
	private String lastName;
	private String imageData;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * @deprecated 3rd party data is extracted from the server now
	 * @return
	 */
	@Deprecated
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * @deprecated 3rd party data is extracted from the server now
	 * @param name
	 */
	@Deprecated
	public void setFirstName(String name) {
		this.firstName = name;
	}
	
	/**
	 * @deprecated 3rd party data is extracted from the server now
	 * @return
	 */
	@Deprecated
	public String getImageData() {
		return imageData;
	}
	
	/**
	 * @deprecated 3rd party data is extracted from the server now
	 * @param imageUrl
	 */
	@Deprecated
	public void setImageData(String imageUrl) {
		this.imageData = imageUrl;
	}
	
	/**
	 * @deprecated 3rd party data is extracted from the server now
	 * @return
	 */
	@Deprecated
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * @deprecated 3rd party data is extracted from the server now
	 * @param lastName
	 */
	@Deprecated
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
