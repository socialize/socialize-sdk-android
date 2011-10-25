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
package com.socialize.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;

/**
 * @author Jason Polites
 */
public abstract class SimpleShareClickListener extends ShareClickListener {
	
	@Override
	protected void doShare(Activity parent, String title, String subject, String body) {
		Intent msg  = new Intent(android.content.Intent.ACTION_SEND);
		msg.setType(getMimeType());
		msg.putExtra(Intent.EXTRA_TITLE, title);
		
		if(isHtml()) {
			msg.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
		}
		else {
			msg.putExtra(Intent.EXTRA_TEXT, body);
		}
		
		msg.putExtra(Intent.EXTRA_SUBJECT, subject);
		parent.startActivity(Intent.createChooser(msg, title));
	}
	
	protected abstract String getMimeType();
}
