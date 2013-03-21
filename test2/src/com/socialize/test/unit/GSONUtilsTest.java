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

import com.socialize.auth.*;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.GSONUtils;

/**
 * @author Jason Polites
 *
 */
public class GSONUtilsTest extends SocializeUnitTest {

	public void testSerializeDeserializeUserUserProviderCredentials() {
		DefaultUserProviderCredentials data = new DefaultUserProviderCredentials();
		data.setAccessToken("foobar_token");
		data.setUserId("foobar_user_id");
		
		GSONUtils gsonUtils = new GSONUtils();
		gsonUtils.init();
		
		String json = gsonUtils.toJSON(data);
		
		UserProviderCredentials userAuthData = gsonUtils.fromJSON(json, UserProviderCredentials.class);
		
		assertNotNull(userAuthData);
		
		assertEquals("foobar_token", userAuthData.getAccessToken());
		assertEquals("foobar_user_id", userAuthData.getUserId());
	}
	
	public void testSerializeDeserializeUserProviderCredentialsMapSingleValue() {
		GSONUtils gsonUtils = new GSONUtils();
		gsonUtils.init();
		
		DefaultUserProviderCredentialsMap map = new DefaultUserProviderCredentialsMap();
		DefaultUserProviderCredentials data = new DefaultUserProviderCredentials();
		data.setAccessToken("foobar_token");
		data.setUserId("foobar_user_id");
		
		FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
		info.setAppId("foobar_appId");
		
		data.setAuthProviderInfo(info);
		
		map.put(AuthProviderType.FACEBOOK, data);
		
		String json = gsonUtils.toJSON(map);
		
		DefaultUserProviderCredentialsMap map2 = gsonUtils.fromJSON(json, DefaultUserProviderCredentialsMap.class);
		
		UserProviderCredentials userAuthData = map2.get(AuthProviderType.FACEBOOK);
		
		assertNotNull(userAuthData);
		
		assertEquals("foobar_token", userAuthData.getAccessToken());
		assertEquals("foobar_user_id", userAuthData.getUserId());
		
		FacebookAuthProviderInfo info2 = (FacebookAuthProviderInfo) userAuthData.getAuthProviderInfo();
		
		assertNotNull(info2);
		assertEquals("foobar_appId", info2.getAppId());
	}
	
	public void testSerializeDeserializeUserProviderCredentialsMapMultiValue() {
		GSONUtils gsonUtils = new GSONUtils();
		gsonUtils.init();
		
		DefaultUserProviderCredentialsMap map = new DefaultUserProviderCredentialsMap();
		
		DefaultUserProviderCredentials data = new DefaultUserProviderCredentials();
		data.setAccessToken("foobar_token");
		data.setUserId("foobar_user_id");
		FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
		info.setAppId("foobar_appId");
		data.setAuthProviderInfo(info);
		
		DefaultUserProviderCredentials data2 = new DefaultUserProviderCredentials();
		data2.setAccessToken("foobar_token2");
		data2.setUserId("foobar_user_id2");
		SocializeAuthProviderInfo info2 = new SocializeAuthProviderInfo();
		data2.setAuthProviderInfo(info2);		
		
		map.put(AuthProviderType.FACEBOOK, data);
		map.put(AuthProviderType.SOCIALIZE, data2);
		
		String json = gsonUtils.toJSON(map);
		
		DefaultUserProviderCredentialsMap map2 = gsonUtils.fromJSON(json, DefaultUserProviderCredentialsMap.class);
		
		UserProviderCredentials data2After = map2.get(AuthProviderType.SOCIALIZE);
		UserProviderCredentials dataAfter = map2.get(AuthProviderType.FACEBOOK);
		
		assertNotNull(dataAfter);
		assertNotNull(data2After);
		
		assertEquals("foobar_token", dataAfter.getAccessToken());
		assertEquals("foobar_user_id", dataAfter.getUserId());
		
		assertEquals("foobar_token2", data2After.getAccessToken());
		assertEquals("foobar_user_id2", data2After.getUserId());		
		
		FacebookAuthProviderInfo infoAfter = (FacebookAuthProviderInfo) dataAfter.getAuthProviderInfo();
		SocializeAuthProviderInfo info2After = (SocializeAuthProviderInfo) data2After.getAuthProviderInfo();
		
		assertNotNull(infoAfter);
		assertNotNull(info2After);
		
		assertEquals("foobar_appId", infoAfter.getAppId());
	}
		
	
}
