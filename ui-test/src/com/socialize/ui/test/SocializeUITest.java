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

import java.util.LinkedList;
import java.util.List;

import android.test.AndroidTestCase;

/**
 * @author Jason Polites
 *
 */
public abstract class SocializeUITest extends AndroidTestCase {
	private List<Object> bucket;
	
	@Override
	protected void setUp() throws Exception {
		bucket = new LinkedList<Object>();
		super.setUp();
	}
	
	protected void addResult(Object obj) {
		bucket.add(obj);
	}
	
	protected void addResult(int index, Object obj) {
		int size = bucket.size();
		if(size <= index) {
			for (int i = size; i <= index; i++) {
				bucket.add(i, "");
			}
		}
		
		bucket.add(index, obj);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Object> T getResult(int index) {
		if(!bucket.isEmpty()) {
			return (T) bucket.get(index);
		}
		return (T) null;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Object> T getNextResult() {
		if(!bucket.isEmpty()) {
			return (T) bucket.remove(0);
		}
		return (T) null;
	}
}
