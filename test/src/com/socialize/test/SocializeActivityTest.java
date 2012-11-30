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
package com.socialize.test;

import java.util.List;
import android.app.Activity;
import com.socialize.sample.EmptyActivity;
import com.socialize.test.ui.util.TestUtils;

public abstract class SocializeActivityTest extends SocializeManagedActivityTest<EmptyActivity> {
	
	
	public SocializeActivityTest() {
		super("com.socialize.sample", EmptyActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown(this);
		super.tearDown();
	}
	
	protected void clearResults() {
		TestUtils.clear();
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
	
	protected List<Object> getAllResults() {
		return TestUtils.getAllResults();
	}
	
	protected <T extends Object> T getNextResult() {
		return TestUtils.getNextResult();
	}
	
	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException ignore) {}
	}

	public Activity getContext() {
		return TestUtils.getActivity(this);
	}
}
