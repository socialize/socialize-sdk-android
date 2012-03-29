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
package com.socialize.test.unit;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Propagation;
import com.socialize.entity.PropagationFactory;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
public class PropagationFactoryTest extends SocializeUnitTest {

	public void testToJSONNoParams() throws JSONException {
		
		Propagation prop = new Propagation();
		List<ShareType> networks = new ArrayList<ShareType>(1);
		networks.add(ShareType.TWITTER);
		prop.setThirdParties(networks);
		
		PropagationFactory factory = new PropagationFactory();
		JSONObject json = factory.toJSON(prop);
		
		assertEquals("{\"third_parties\":[\"twitter\"]}", json.toString());
	}
	
	public void testToJSONNoNetworks() throws JSONException {
		Propagation prop = new Propagation();
		PropagationFactory factory = new PropagationFactory();
		JSONObject json = factory.toJSON(prop);
		assertEquals("{}", json.toString());
	}
	
	public void testToJSONWithParams() throws JSONException {
		
		Propagation prop = new Propagation();
		prop.addThirdParty(SocialNetwork.TWITTER);
		prop.addExtraParam("foo", "bar");
		
		PropagationFactory factory = new PropagationFactory();
		JSONObject json = factory.toJSON(prop);
		
		assertEquals("{\"third_parties\":[\"twitter\"],\"extra_params\":\"foo=bar\"}", json.toString());
		
	}	
	
	public void testToJSONWithMultiParams() throws JSONException {
		
		Propagation prop = new Propagation();
		prop.addThirdParty(SocialNetwork.TWITTER);
		prop.addExtraParam("foo", "bar");
		prop.addExtraParam("sna", "fu");
		
		PropagationFactory factory = new PropagationFactory();
		JSONObject json = factory.toJSON(prop);
		
		assertEquals("{\"third_parties\":[\"twitter\"],\"extra_params\":\"sna=fu&foo=bar\"}", json.toString());
		
	}		
	
}
