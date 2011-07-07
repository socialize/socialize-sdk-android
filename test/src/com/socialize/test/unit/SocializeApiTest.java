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
package com.socialize.test.unit;

import java.util.LinkedList;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeActivityTest;

public class SocializeApiTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> api;
	private SocializeProvider<SocializeObject> provider;
	
	private SocializeSession mockSession;

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeProvider.class, SocializeSession.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		api = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
		
		mockSession = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.replay(mockSession);
		
	}
	
	public void testApiCallsAuthenticateOnProvider() throws Throwable {

		AndroidMock.expect(provider.authenticate("test_endpoint", "test_key", "test_secret", "test_uuid")).andReturn(mockSession);
		AndroidMock.replay(provider);

		api.authenticate("test_endpoint", "test_key", "test_secret", "test_uuid");
		
		AndroidMock.verify(provider);
	}
	
	public void testApiCallsGetOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String ids = null;
		
		AndroidMock.expect(provider.get(mockSession, endpoint, ids)).andReturn(new SocializeObject());
		AndroidMock.replay(provider);

		api.get(mockSession, endpoint, ids);
		
		AndroidMock.verify(provider);
	}
	
	public void testApiCallsListOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String[] ids = null;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids)).andReturn(returned);
		AndroidMock.replay(provider);

		api.list(mockSession, endpoint, key, ids);

		AndroidMock.verify(provider);
	}
	
	public void testApiCallsPutOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.put(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		api.put(mockSession, endpoint, object);

		AndroidMock.verify(provider);
	}
	
	public void testApiCallsPostOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.post(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		api.post(mockSession, endpoint, object);
		
		AndroidMock.verify(provider);
	}
}
