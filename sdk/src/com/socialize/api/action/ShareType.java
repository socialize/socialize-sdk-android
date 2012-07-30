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

import com.socialize.networks.SocialNetwork;


/**
 * @author Jason Polites
 */
public enum ShareType {
	TWITTER ("twitter", "Twitter", 1),
	FACEBOOK ("facebook", "Facebook", 2),
	EMAIL ("email", "Email", 3),
	SMS ("sms", "SMS", 4),
	GOOGLE_PLUS ("google_plus", "Google+", 101),
	OTHER ("other", "Other",  101);
	
	private final String name; 
	private final String displayName;
    private final int id; 
	
	ShareType(String name, String displayName, int id) {
		this.name = name;
		this.displayName = displayName;
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public static ShareType valueOf(SocialNetwork socialNetwork) {
		if(socialNetwork == null) {
			return OTHER;
		}
		return valueOf(socialNetwork.name().toUpperCase());
	}
	
	public static ShareType valueOf(int id) {
		switch(id) {
		case 1 : 
			return TWITTER;
		case 2 : 
			return FACEBOOK;	
		case 3 : 
			return EMAIL;	
		case 4 : 
			return SMS;				
		default :
			return OTHER;
		}
	}
}
