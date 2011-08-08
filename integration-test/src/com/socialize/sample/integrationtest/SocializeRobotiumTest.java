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
package com.socialize.sample.integrationtest;

import java.util.ArrayList;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.socialize.sample.Main;

/**
 * @author Jason Polites
 */
public abstract class SocializeRobotiumTest extends ActivityInstrumentationTestCase2<Main> {

	public static final int DEFAULT_TIMEOUT_SECONDS = 30;
	public static final String DEFAULT_ENTITY_URL = "http://socialize.integration.tests.com";
	public static final String DEFAULT_APPLICATION_NAME = "Socialize Android Sample App";

	protected Solo robotium;
	protected InputMethodManager imm = null;
	
	public SocializeRobotiumTest() {
		super("com.socialize.sample", Main.class);
	}

	public void setUp() throws Exception {
		robotium = new Solo(getInstrumentation(), getActivity());
		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		robotium.clickOnButton("Launch Sample");
		robotium.waitForActivity("AuthenticateActivity");
	}

	@Override
	protected void tearDown() throws Exception {
		Log.e("SocializeRobotiumTest", "tearDown()");
		try {
			robotium.finalize();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		getActivity().finish();
		
		super.tearDown();
	}

	/**
	 * Returns the user ID from the call to authenticate.
	 * @return
	 * @throws Throwable 
	 */
	protected int authenticateSocialize() {
		
		hideKeyboard();
		
		robotium.clickOnButton("Auth Socialize");

		waitForSuccess();

		// Get the user Id
		TextView txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtAuthUserID);

		int userId = Integer.parseInt(txt.getText().toString());

		// go to API
		robotium.clickOnButton("Access API");
		robotium.waitForActivity("ApiActivity", DEFAULT_TIMEOUT_SECONDS);

		return userId;
	}
	
	protected void clearCache() {
		hideKeyboard();
		robotium.clickOnButton("Clear Cache");
		waitForSuccess();
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
		ArrayList<EditText> currentEditTexts = robotium.getCurrentEditTexts();
		
		if(currentEditTexts != null) {
			for (EditText editText : currentEditTexts) {
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		}
	}
	
	protected final void waitForSuccess() {
		robotium.waitForText("SUCCESS", 1, DEFAULT_TIMEOUT_SECONDS);
		assertTrue(robotium.searchText("SUCCESS"));
	}
}
