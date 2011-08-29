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
package com.socialize.ui.test;

import java.util.Stack;

import android.test.ActivityInstrumentationTestCase2;

import com.socialize.ui.sample.EmptyActivity;

public abstract class SocializeUIActivityTest extends ActivityInstrumentationTestCase2<EmptyActivity> {
	
	private Stack<Object> bucket;
	
	public SocializeUIActivityTest() {
		super("com.socialize.ui.sample", EmptyActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		bucket = new Stack<Object>();
		super.setUp();
	}
	
	protected void addResult(Object obj) {
		bucket.push(obj);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Object> T getNextResult() {
		if(!bucket.isEmpty()) {
			return (T) bucket.pop();
		}
		return null;
	}
	
	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException ignore) {}
	}

	public EmptyActivity getContext() {
		return super.getActivity();
	}
}
