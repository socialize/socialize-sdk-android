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
package com.socialize.entity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public class StatsFactory extends SocializeObjectFactory<Stats> {
	
	private static final String LIKES = "likes";
	private static final String SHARES = "shares";
	private static final String COMMENTS = "comments";
	private static final String VIEWS = "views";

	@Override
	public Object instantiateObject(JSONObject object) {
		return new Stats();
	}

	@Override
	protected void postFromJSON(JSONObject from, Stats to) throws JSONException {

		if(from.has(LIKES) && !from.isNull(LIKES)) {
			to.setLikes(from.getInt(LIKES));
		}
		if(from.has(SHARES) && !from.isNull(SHARES)) {
			to.setShares(from.getInt(SHARES));
		}
		if(from.has(COMMENTS) && !from.isNull(COMMENTS)) {
			to.setComments(from.getInt(COMMENTS));
		}
		if(from.has(VIEWS) && !from.isNull(VIEWS)) {
			to.setViews(from.getInt(VIEWS));
		}
	}

	@Override
	protected void postToJSON(Stats from, JSONObject to) throws JSONException {
		// Never sent
	}
	
}
