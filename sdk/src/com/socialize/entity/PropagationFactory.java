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

import com.socialize.api.action.ShareType;
import com.socialize.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		Map<String, String> extraParams = from.getExtraParams();
		
		if(extraParams != null && extraParams.size() > 0) {
			
			Set<Entry<String, String>> entrySet = extraParams.entrySet();
			
			if(entrySet != null && entrySet.size() > 0) {
				
				int count = 0;

				StringBuilder builder = new StringBuilder();
				
				for (Entry<String, String> entry : entrySet) {
					
					String key = entry.getKey();
					String value = entry.getValue();
					
					if(!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
						if(count > 0) {
							builder.append("&");
						}			
						
						builder.append(URLEncoder.encode(key));
						builder.append("=");
						builder.append(URLEncoder.encode(value));
						
						count++;
					}
				}
				
				to.put("extra_params", builder.toString());
			}
		}
		
		JSONArray networks = new JSONArray();
		
		List<ShareType> thirdParties = from.getThirdParties();
		
		if(thirdParties != null && thirdParties.size() > 0) {
			for (ShareType socialNetwork : thirdParties) {
				networks.put(socialNetwork.name().toLowerCase());
			}
			to.put("third_parties", networks);
		}
	}
}
