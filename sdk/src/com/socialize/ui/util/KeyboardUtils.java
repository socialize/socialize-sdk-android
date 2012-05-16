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
package com.socialize.ui.util;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Jason Polites
 */
public class KeyboardUtils {
	
	private InputMethodManager imm;
	private boolean hardwareKeyboard = false;

	public void init(Context context) {
		this.imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		int keyboard = context.getResources().getConfiguration().keyboard;
		hardwareKeyboard = (keyboard != Configuration.KEYBOARD_NOKEYS);
	}
	
	/**
	 * Hides the soft keyboard that was created for the given view.
	 * @param source
	 */
	public void hideKeyboard(View source) {
		imm.hideSoftInputFromWindow(source.getWindowToken(), 0);
	}
	
	public void showKeyboard(View source) {
		if(!hardwareKeyboard) {
			imm.showSoftInput(source, 0);
		}
	}
}
