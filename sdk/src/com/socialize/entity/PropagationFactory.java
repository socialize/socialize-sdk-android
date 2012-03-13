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
package com.socialize.entity;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.networks.SocialNetwork;

/**
 * @author Jason Polites
 *
 */
public class PropagationFactory extends JSONFactory<Propagation> {

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#instantiateObject(org.json.JSONObject)
	 */
	@Override
	public Object instantiateObject(JSONObject object) {
		return new Propagation();
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#fromJSON(org.json.JSONObject, java.lang.Object)
	 */
	@Override
	protected void fromJSON(JSONObject from, Propagation to) throws JSONException {
		// Not needed
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#toJSON(java.lang.Object, org.json.JSONObject)
	 */
	@Override
	protected void toJSON(Propagation from, JSONObject to) throws JSONException {
		
		// Make params
		Set<Entry<String, String>> entrySet = from.getExtraParams().entrySet();
		
		StringBuilder builder = new StringBuilder();
		
		int count = 0;
		
		for (Entry<String, String> entry : entrySet) {
			if(count > 0) {
				builder.append("&");
			}			
			
			builder.append(URLEncoder.encode(entry.getKey()));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue()));
			
			count++;
		}
		
		to.put("extra_params", builder.toString());
		
		JSONArray networks = new JSONArray();
		
		List<SocialNetwork> thirdParties = from.getThirdParties();
		for (SocialNetwork socialNetwork : thirdParties) {
			// TODO: Remove this condition once facebook is supported.
			if(!socialNetwork.equals(SocialNetwork.FACEBOOK)) {
				networks.put(socialNetwork.name().toLowerCase());
			}
		}
		
		to.put("third_parties", networks);
	}

}
