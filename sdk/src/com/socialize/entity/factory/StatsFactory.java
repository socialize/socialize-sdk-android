/*
 * Copyright (c) 2011 SocializeService Inc.
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
package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Stats;

/**
 * @author Jason Polites
 *
 */
public class StatsFactory extends SocializeObjectFactory<Stats> {

	@Override
	public Stats instantiateObject() {
		return new Stats();
	}

	@Override
	protected void fromJSON(JSONObject from, Stats to) throws JSONException {
		to.setComments(from.getInt("comments"));
		to.setLikes(from.getInt("likes"));
		to.setShares(from.getInt("share"));
		to.setViews(from.getInt("views"));
	}

	@Override
	protected void toJSON(Stats from, JSONObject to) throws JSONException {
		// Never sent
	}
	
}
