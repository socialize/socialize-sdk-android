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

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.socialize.SocializeService;
import com.socialize.SocializeServiceImpl;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.PublicShareSystem;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class ShareSystemTest extends SocializeActivityTest {

	public void testAddShareNotAuthed() {
		
		final Activity context = TestUtils.getActivity(this);
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		final String text = "foobar";
		
		final SocializeServiceImpl socialize = new SocializeServiceImpl() {
			@Override
			public boolean isAuthenticated(AuthProviderType providerType) {
				return false;
			}
			
			public void authenticate(Context context, AuthProviderType authProvider, SocializeAuthListener authListener, String...permissions) {
				// Call authenticated
				authListener.onAuthSuccess(null);
			}
		};
		
		PublicShareSystem shareSystem = new PublicShareSystem(null) {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
			
			public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener, SocialNetwork...network) {
				addResult(0, entity);
				addResult(1, shareType);
				addResult(2, network);
				addResult(3, text);
			}
		};
		
		shareSystem.addShare(context, null, entity, text, ShareType.TWITTER, SocialNetwork.TWITTER, null, null);
		
		Entity entityAfter = getResult(0);
		ShareType shareTypeAfter = getResult(1);
		SocialNetwork[] networkAfter = getResult(2);
		String textAfter = getResult(3);
		
		assertNotNull(entityAfter);
		assertNotNull(shareTypeAfter);
		assertNotNull(networkAfter);
		assertNotNull(textAfter);
		
		assertEquals(entity.getKey(), entityAfter.getKey());
		assertEquals(ShareType.TWITTER, shareTypeAfter);
		assertEquals(SocialNetwork.TWITTER, networkAfter[0]);
		assertEquals(text, textAfter);
	}
	
}
