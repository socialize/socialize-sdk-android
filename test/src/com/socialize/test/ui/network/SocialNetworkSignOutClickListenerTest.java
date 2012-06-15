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
package com.socialize.test.ui.network;

import android.app.AlertDialog;
import android.content.Context;
import com.socialize.networks.SocialNetworkSignOutClickListener;
import com.socialize.test.SocializeActivityTest;


/**
 * @author Jason Polites
 *
 */
public class SocialNetworkSignOutClickListenerTest extends SocializeActivityTest {

	// TODO: Test broken (incompete).. performClick returns true but DOESN'T call button click handler!
//	public void testOnClickYes() throws Throwable {
//		
//		final PublicSocialNetworkSignOutClickListener listener = new PublicSocialNetworkSignOutClickListener() {
//			@Override
//			protected void doSignOut(Context context) {
//				addResult("doSignOut");
//			}
//		};
//		
//		final CountDownLatch latch = new CountDownLatch(1);
////		final CountDownLatch latch1 = new CountDownLatch(1);
//		
//		final AlertDialog dialog = listener.makeDialog(getContext());
//		
//		runTestOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				dialog.show();
//				Button button = dialog.getButton(Dialog.BUTTON_POSITIVE);
//				
//				for (int i = 0; i < 5; i++) {
//					System.out.println(button.performClick());
//				}
////				assertTrue(button.performClick());
//				latch.countDown();
//			}
//		});
//	
//		latch.await(10, TimeUnit.SECONDS);
//		
//		// Wait for show
//		getInstrumentation().waitForIdleSync();
//		
////		runTestOnUiThread(new Runnable() {
////			@Override
////			public void run() {
////
////			}
////		});
//		
////		latch1.await(10, TimeUnit.SECONDS);
//		
//		// Wait for click
////		getInstrumentation().waitForIdleSync();
//		
////		assertNotNull(getNextResult());
//		
//		dialog.dismiss();
//	}
	
	class PublicSocialNetworkSignOutClickListener extends SocialNetworkSignOutClickListener {
		@Override
		public AlertDialog makeDialog(Context context) {
			return super.makeDialog(context);
		}
	}
			
}
