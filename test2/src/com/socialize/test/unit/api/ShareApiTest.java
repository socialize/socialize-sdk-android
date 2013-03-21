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
package com.socialize.test.unit.api;

import android.location.Location;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.SocializeShareSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;

import java.util.List;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, ShareListener.class, SocializeProvider.class})
public class ShareApiTest extends SocializeUnitTest {

	SocializeProvider<Share> provider;
	SocializeSession session;
	ShareListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(ShareListener.class);
	}

	/**
	 * More specific test to ensure the share is actually set.
	 */
	@UsesMocks ({Location.class})
	public void testAddShare() {
		final Entity key = Entity.newInstance("foo", "foo");
		final String shareText = "foobar_text";
		Location location = AndroidMock.createMock(Location.class, "foobar");
		ShareType type = ShareType.OTHER;
		
		SocializeShareSystem api = new SocializeShareSystem(provider){

			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Share> objects, SocializeActionListener listener) {
				addResult(objects);
			}
		};
		
		api.addShare(getContext(), session, key, shareText, type, location, listener);
		
		List<Share> shares = getNextResult();
		
		assertNotNull(shares);
		assertEquals(1, shares.size());
		
		Share result = shares.get(0);
		
		assertNotNull(result);
		assertNotNull(result.getEntityKey());
		assertEquals(key.getKey(), result.getEntityKey());
		
		assertNotNull(result.getText());
		assertEquals(shareText, result.getText());
	}
	
}
