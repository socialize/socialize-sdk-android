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

import com.socialize.api.action.ShareType;
import com.socialize.networks.SocialNetwork;

/**
 * @author Jason Polites
 *
 */
public enum AuthProviderType {
	
	SOCIALIZE ("socialize", 0),
	FACEBOOK ("facebook", 1),
	TWITTER ("twitter", 2);
	
	private final String name;   
    private final int id; 
	
	AuthProviderType(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public static AuthProviderType valueOf(SocialNetwork socialNetwork) {
		if(socialNetwork == null) {
			return SOCIALIZE;
		}
		return valueOf(socialNetwork.name().toUpperCase());
	}
	
	public static AuthProviderType valueOf(ShareType shareType) {
		if(shareType == null) {
			return SOCIALIZE;
		}
		
		switch (shareType) {
			case EMAIL:
				return SOCIALIZE;
			case SMS:
				return SOCIALIZE;
			case OTHER:
				return SOCIALIZE;				
			default:
				return valueOf(shareType.getName().toUpperCase());
		}
	}	
	
	public static AuthProviderType valueOf(int id) {
		AuthProviderType[] values = AuthProviderType.values();
		for (AuthProviderType authProviderType : values) {
			if(authProviderType.id == id) {
				return authProviderType;
			}
		}
		return SOCIALIZE;
	}
}
