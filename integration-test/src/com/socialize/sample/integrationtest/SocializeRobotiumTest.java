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

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.socialize.sample.Main;

/**
 * @author Jason Polites
 */
public class SocializeRobotiumTest extends ActivityInstrumentationTestCase2<Main> {

	protected Solo solo;
	
	
	public SocializeRobotiumTest() {
		super("com.socialize.sample", Main.class);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnButton(0);
	}
	
	public void testAuthenticate() {
		authenticate();
		solo.waitForText("SUCCESS", 1, 5);
		assertTrue(solo.searchText("SUCCESS"));
	}
	
	public void testAddComment() {
		authenticate();
		solo.clickOnButton("Comment");
		solo.waitForActivity("CommentActivity", 5);
	}
	
	protected void authenticate() {
//		solo.clearEditText(0);
//		solo.enterText(0, url);
//		
//		solo.clearEditText(1);
//		solo.enterText(1, consumerKey);
//		
//		solo.clearEditText(2);
//		solo.enterText(2, consumerSecret);
		
		solo.clickOnButton(0);
	}
	

}
