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
package com.socialize.test.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.sample.ui.SampleActivity2;
import com.socialize.test.SocializeManagedActivityTest;
import com.socialize.test.ui.util.TestUtils;

/**
 * @author Jason Polites
 */
public abstract class SocializeUIRobotiumTest extends SocializeManagedActivityTest<SampleActivity2> {

	public static final int DEFAULT_TIMEOUT_SECONDS = 100;
	public static final String DEFAULT_ENTITY_URL = "http://socialize.integration.tests.com?somekey=somevalue&anotherkey=anothervalue";
	public static final String DEFAULT_GET_ENTITY = "http://entity1.com";
	public static final String SOCIALIZE_FACEBOOK_ID = "209798315709193";
	
	
	public static final int BTN_FACEBOOK_SSO = 0;
	public static final int BTN_MOCK_FACEBOOK = 1;
	public static final int BTN_MOCK_SOCIALIZE = 2;
	
	public static final String BTN_CLEAR_CACHE = "Clear Auth Cache";
	public static final String BTN_SHOW_COMMENTS = "Show Comments";
	
	
	public static final String BTN_NOTIFICATIONS_ENABLED = "Notifications Enabled";
	public static final String BTN_LOCATION_ENABLED = "Location Enabled";
	
	public static final String BTN_SHOW_ACTION_BAR_AUTO = "Show Action Bar (auto)";
	public static final String BTN_SHOW_ACTION_BAR_MANUAL = "Show Action Bar (manual)";
	
	
	public static final int TXT_ENTITY_KEY = 0;
	public static final int TXT_ENTITY_NAME = 1;
	public static final int TXT_FACEBOOK_ID = 2;
	public static final int TXT_TWITTER_KEY = 3;
	public static final int TXT_TWITTER_SECRET = 4;
		
//	protected Solo robotium;
	protected InputMethodManager imm = null;

	public SocializeUIRobotiumTest() {
		super("com.socialize.sample.ui", SampleActivity2.class);
	}

	public void setUp() throws Exception {
		imm = (InputMethodManager)TestUtils.getActivity(this).getSystemService(Context.INPUT_METHOD_SERVICE);
//		robotium = new Solo(getInstrumentation(), TestUtils.getActivity(this));
//		robotium.waitForActivity("SampleActivity", 5000);
		TestUtils.setUp(this);
		hideKeyboard();
	}
	

	@Override
	protected void tearDown() throws Exception {
//		try {
//			robotium.finish();
//		} 
//		catch (Throwable e) {
//			e.printStackTrace();
//		}
		
		TestUtils.tearDown(this);
		
		super.tearDown();
	}	
	
	protected void toggleFacebookSSO(boolean on) {
		if(!on) {
			TestUtils.clickOnButton(this, BTN_FACEBOOK_SSO);
		}
	}
	
	protected void toggleMockedFacebook(boolean on) {
		if(on) {
			TestUtils.clickOnButton(this,BTN_MOCK_FACEBOOK);
		}
	}
	
	protected void toggleMockedSocialize(boolean on) {
		if(on) {
			TestUtils.clickOnButton(this,BTN_MOCK_SOCIALIZE);
		}
	}
	
	protected void toggleNotificationsEnabled(boolean on) {
		if(on) {
			TestUtils.clickOnButton(BTN_NOTIFICATIONS_ENABLED);
		}
	}	
	
	protected void toggleLocationEnabled(boolean on) {
		if(on) {
			TestUtils.clickOnButton(BTN_LOCATION_ENABLED);
		}
	}			
	
	protected void clearAuthCache() {
		SocializeService socialize = Socialize.getSocialize();
		socialize.clearSessionCache(TestUtils.getActivity(this));
		assertNull(socialize.getSession());
	}
	
	protected void showComments() {
		TestUtils.clickOnButton(BTN_SHOW_COMMENTS);
	}
	
	protected void showActionBarAuto() {
		TestUtils.clickOnButton(BTN_SHOW_ACTION_BAR_AUTO);
	}
	
	protected void showActionBarManual() {
		TestUtils.clickOnButton(BTN_SHOW_ACTION_BAR_MANUAL);
	}
	
	protected void clearSocialNetworks() {
		TestUtils.clearEditText(this,TXT_FACEBOOK_ID);
		TestUtils.clearEditText(this,TXT_TWITTER_KEY);
		TestUtils.clearEditText(this,TXT_TWITTER_SECRET);
	}
	
	protected void startWithFacebook(boolean sso) {
		clearSocialNetworks();
		TestUtils.clearEditText(this,TXT_ENTITY_KEY);
		TestUtils.enterText(this,TXT_ENTITY_KEY, DEFAULT_GET_ENTITY);
		TestUtils.enterText(this,TXT_FACEBOOK_ID, SOCIALIZE_FACEBOOK_ID);
		toggleFacebookSSO(sso);
		toggleMockedFacebook(true);
		clearAuthCache();
	}
	
	protected void startWithoutFacebook() {
		startWithoutFacebook(true);
	}
	protected void startWithoutFacebook(boolean clearCache) {
		clearSocialNetworks();
		TestUtils.clearEditText(this,TXT_ENTITY_KEY);
		TestUtils.enterText(this,TXT_ENTITY_KEY, DEFAULT_GET_ENTITY);
		if(clearCache) clearAuthCache();
	}

	protected void incrementCount(String key) {
		TestUtils.incrementCount(key);
	}
	
	protected int getCount(String key) {
		return TestUtils.getCount(key);
	}
	
	protected void addResult(Object obj) {
		TestUtils.addResult(obj);
	}
	
	protected void addResult(int index, Object obj) {
		TestUtils.addResult(index, obj);
	}
	
	protected <T extends Object> T getResult(int index) {
		return TestUtils.getResult(index);
	}
	
	protected <T extends Object> T getNextResult() {
		return TestUtils.getNextResult();
	}

	protected final void sleep(int milliseconds) {
		synchronized (this) {
			try {
				wait(milliseconds);
			}
			catch (InterruptedException ignore) {}
		}
	}

	protected final void hideKeyboard() {
		// Hide keyboard for all
//		try {
//			ArrayList<EditText> currentEditTexts = robotium.getCurrentEditTexts();
//
//			if(currentEditTexts != null) {
//				for (EditText editText : currentEditTexts) {
//					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//				}
//			}
//		}
//		catch (Exception e) {}
	}
	
//	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(Class<T> viewClass) {
//		ArrayList<View> currentViews = robotium.getCurrentViews();
//		for (View view : currentViews) {
//			if(viewClass.isAssignableFrom(view.getClass())) {
//				return (T) view;
//			}
//		}
//		return null;		
		return TestUtils.findView(TestUtils.getActivity(this), viewClass, 20000);
	}

}
