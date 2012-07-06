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
package com.socialize.test.mock;

import android.content.SharedPreferences.Editor;

/**
 * AndroidMock won't mock out static inner-interfaces, just need to create an empty instance to be mocked.
 * @author Jason Polites
 */
public class MockEditor implements Editor {

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#clear()
	 */
	@Override
	public Editor clear() {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#commit()
	 */
	@Override
	public boolean commit() {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#putBoolean(java.lang.String, boolean)
	 */
	@Override
	public Editor putBoolean(String key, boolean value) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#putFloat(java.lang.String, float)
	 */
	@Override
	public Editor putFloat(String key, float value) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#putInt(java.lang.String, int)
	 */
	@Override
	public Editor putInt(String key, int value) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#putLong(java.lang.String, long)
	 */
	@Override
	public Editor putLong(String key, long value) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#putString(java.lang.String, java.lang.String)
	 */
	@Override
	public Editor putString(String key, String value) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.Editor#remove(java.lang.String)
	 */
	@Override
	public Editor remove(String key) {
		return null;
	}

	@Override
	public void apply() {
		
	}

}
