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
package com.socialize.test.ui.facebook;

import com.socialize.test.ui.SocializeUIActivityTest;

/**
 * @author Jason Polites
 *
 */
public class FacebookSignOutClickListenerTest extends SocializeUIActivityTest {
//	
//	@SuppressWarnings("unchecked")
//	@UsesMocks ({IBeanFactory.class, SocialNetworkSignOutTask.class, SocialNetworkSignOutListener.class, Drawables.class})
//	public void testFacebookSignOutClickListener() throws Throwable {
//
//		final IBeanFactory<SocialNetworkSignOutTask> taskFactory = AndroidMock.createMock(IBeanFactory.class);
//		final SocialNetworkSignOutTask task = AndroidMock.createNiceMock(SocialNetworkSignOutTask.class, getContext());
//		final SocialNetworkSignOutListener facebookSignOutListener = AndroidMock.createMock(SocialNetworkSignOutListener.class);
//		final Drawables drawables = AndroidMock.createMock(Drawables.class);
//		
//		AndroidMock.expect(drawables.getDrawable("fb_button.png")).andReturn(null);
//		AndroidMock.expect(taskFactory.getBean((Context) AndroidMock.anyObject())).andReturn(task);
//		
//		task.setSignOutListener(facebookSignOutListener);
//		task.doExecute((Void[])null);
//		
//		AndroidMock.replay(drawables, taskFactory, task);
//		
//		final PublicFacebookSignOutClickListener listener = new PublicFacebookSignOutClickListener() {
//			@Override
//			public SocialNetworkSignOutListener newSocialNetworkSignOutListener(View v) {
//				return facebookSignOutListener;
//			}
//		};
//		
//		final ViewGroup anchor = new LinearLayout(getContext());
//		
//		final CountDownLatch latch = new CountDownLatch(1);
//		
//		runTestOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				// Just create a view as an achor
//				getActivity().setContentView(anchor);
//				listener.setDrawables(drawables);
//				listener.setFacebookSignOutTaskFactory(taskFactory);
//				listener.onClick(anchor);
//				latch.countDown();
//			}
//		});
//		
//		latch.await(20, TimeUnit.SECONDS);
//		
//		Button button = listener.getDialog().getButton(Dialog.BUTTON_POSITIVE);
//
//		assertNotNull(button);
//		assertTrue(button.performClick());
//		
//		// Wait for click to process
//		sleep(2000);
//		
//		AndroidMock.verify(drawables, taskFactory, task);
//	}
//	
//	class PublicFacebookSignOutClickListener extends SocialNetworkSignOutListener {
//
//		@Override
//		public AlertDialog getDialog() {
//			return super.getDialog();
//		}
//
//		@Override
//		public SocialNetworkSignOutListener newSocialNetworkSignOutListener(View v) {
//			return super.newSocialNetworkSignOutListener(v);
//		}
//
//		@Override
//		public void exitProfileActivity(View v) {
//			super.exitProfileActivity(v);
//		}
//
//		@Override
//		public void logError(String msg, Exception error) {
//			super.logError(msg, error);
//		}
//
//		@Override
//		public SocializeService getSocialize() {
//			return super.getSocialize();
//		}
//	}
	
}
