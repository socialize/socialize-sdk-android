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
package com.socialize.test.twitter;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import com.socialize.auth.twitter.TwitterAuthDialogListener;
import com.socialize.auth.twitter.TwitterAuthListener;
import com.socialize.auth.twitter.TwitterAuthUtils;
import com.socialize.auth.twitter.TwitterAuthView;


/**
 * @author Jason Polites
 *
 */
public class PublicTwitterAuthUtils extends TwitterAuthUtils {

	@Override
	public OnCancelListener newOnCancelListener(TwitterAuthListener listener) {
		return super.newOnCancelListener(listener);
	}

	@Override
	public Dialog newDialog(Context context) {
		return super.newDialog(context);
	}

	@Override
	public TwitterAuthDialogListener newTwitterAuthDialogListener(Dialog dialog, TwitterAuthListener listener) {
		return super.newTwitterAuthDialogListener(dialog, listener);
	}

	@Override
	public Builder newAlertDialogBuilder(Context context) {
		return super.newAlertDialogBuilder(context);
	}

	@Override
	public TwitterAuthView newTwitterAuthView(Context context) {
		return super.newTwitterAuthView(context);
	}

}
