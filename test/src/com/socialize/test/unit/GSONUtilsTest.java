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

import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserAuthData;
import com.socialize.auth.DefaultUserAuthDataMap;
import com.socialize.auth.UserAuthData;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.GSONUtils;
/**
 * @author Jason Polites
 *
 */
public class GSONUtilsTest extends SocializeUnitTest {

	public void testSerializeDeserializeUserUserAuthData() {
		DefaultUserAuthData data = new DefaultUserAuthData();
		data.setAccessToken("foobar_token");
		data.setUserId("foobar_user_id");
		
		GSONUtils gsonUtils = new GSONUtils();
		gsonUtils.init();
		
		String json = gsonUtils.toJSON(data);
		
		UserAuthData userAuthData = gsonUtils.fromJSON(json, UserAuthData.class);
		
		assertNotNull(userAuthData);
		
		assertEquals("foobar_token", userAuthData.getAccessToken());
		assertEquals("foobar_user_id", userAuthData.getUserId());
	}
	
	public void testSerializeDeserializeUserAuthDataMapSingleValue() {
		GSONUtils gsonUtils = new GSONUtils();
		gsonUtils.init();
		
		DefaultUserAuthDataMap map = new DefaultUserAuthDataMap();
		DefaultUserAuthData data = new DefaultUserAuthData();
		data.setAccessToken("foobar_token");
		data.setUserId("foobar_user_id");
		
		FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
		info.setAppId("foobar_appId");
		
		data.setAuthProviderInfo(info);
		
		map.put(AuthProviderType.FACEBOOK, data);
		
		String json = gsonUtils.toJSON(map);
		
		DefaultUserAuthDataMap map2 = gsonUtils.fromJSON(json, DefaultUserAuthDataMap.class);
		
		UserAuthData userAuthData = map2.get(AuthProviderType.FACEBOOK);
		
		assertNotNull(userAuthData);
		
		assertEquals("foobar_token", userAuthData.getAccessToken());
		assertEquals("foobar_user_id", userAuthData.getUserId());
		
		FacebookAuthProviderInfo info2 = (FacebookAuthProviderInfo) userAuthData.getAuthProviderInfo();
		
		assertNotNull(info2);
		assertEquals("foobar_appId", info2.getAppId());
	}
	
}
