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
package com.socialize.test.ui.share;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareSystem;
import com.socialize.api.action.share.SocializeShareSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * Tests that SocialNetworkListeners are called as expected when sharing via the UI.
 * @author Jason Polites
 */
public class SocialNetworkShareListenerTest extends SocializeActivityTest {


	public void testListenerOnFacebookShare() {
		
//		
//		// Setup the mocks for the share system.
//		TestUtils.setupSocializeOverrides(true, true);
//		
//		final Share mockShare = new Share();
//		mockShare.setId(-1L);
//		
//		final SocializeShareSystem mockShareSystem = new SocializeShareSystem(null) {
//
//			@Override
//			public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, ShareListener listener, SocialNetwork... network) {
//				// Just call the listener
//				listener.onCreate(mockShare);
//			}
//
//			@Override
//			public void share(Activity context, SocializeSession session, SocializeAction action, String comment, Location location, ShareType destination, SocialNetworkListener listener) {
//				// TODO Auto-generated method stub
//				super.share(context, session, action, comment, location, destination, listener);
//			}
//			
//			
//		};
//		
//		SocializeAccess.setInitListener(new SocializeInitListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				fail();
//			}
//			@Override
//			public void onInit(Context context, IOCContainer container) {
//				ProxyObject<ShareSystem> shareSystemProxy = container.getProxy("shareSystem");
//				if(shareSystemProxy != null) {
//					shareSystemProxy.setDelegate(mockShareSystem);
//				}
//				else {
//					System.err.println("shareSystemProxy is null!!");
//				}				
//			}
//		});	
		
		
		
		
	}

}
