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

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.networks.SocialNetwork;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class PropagatorFactory extends JSONFactory<Propagator> {

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#instantiateObject(org.json.JSONObject)
	 */
	@Override
	public Object instantiateObject(JSONObject object) {
		return new Propagator();
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#fromJSON(org.json.JSONObject, java.lang.Object)
	 */
	@Override
	protected void fromJSON(JSONObject from, Propagator to) throws JSONException {
//		
//		to.setText(getString(from, "text"));
//		
//		if(exists(from, "third_party")) {
//			JSONArray array = from.getJSONArray("third_party");
//			int length = array.length();
//			
//			if(length > 0) {
//				List<SocialNetwork> networks = new ArrayList<SocialNetwork>(length);
//				for (int i = 0; i < length; i++) {
//					String value = array.getString(i);
//					try {
//						networks.add(SocialNetwork.valueOf(value));
//					} 
//					catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//				}
//				
//				if(networks.size() > 0) {
//					to.setNetworks(networks.toArray(new SocialNetwork[networks.size()]));
//				}
//			}
//		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#toJSON(java.lang.Object, org.json.JSONObject)
	 */
	@Override
	protected void toJSON(Propagator from, JSONObject to) throws JSONException {
		if(!StringUtils.isEmpty(from.getText())) {
			to.put("text", from.getText());
		}
		
		SocialNetwork network = from.getNetwork();
		
		// TODO: Don't hard code this
		if(!network.equals(SocialNetwork.FACEBOOK)) {
			to.put("third_party", network.name().toLowerCase());
		}
	
//		if(networks != null && networks.length > 0) {
//			JSONArray array = new JSONArray();
//			for (SocialNetwork socialNetwork : networks) {
//				
//				// TODO: Don't hard code this
//				if(!socialNetwork.equals(SocialNetwork.FACEBOOK)) {
//					array.put(socialNetwork.name().toLowerCase());
//				}
//			}
//			
//			if(array.length() > 0) {
//				to.put("third_party", array);
//			}
//		}
	}
}
