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
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public abstract class ShareClickListener implements OnClickListener {

	private SocializeLogger logger;
	protected ShareMessageBuilder shareMessageBuilder;
	
	protected Activity getActivity(View v) {
		Context context = v.getContext();
		
		if(context instanceof Activity) {
			return (Activity) context;
		}
		else {
			String warn = "Context instance in [" + getClass().getName() + "] is not an Activity";
			
			if(logger != null) {
				logger.warn(warn);
			}
			else {
				System.err.println(warn);
			}
		}
		
		return null;
	}

	@Override
	public final void onClick(View v) {
		Activity activity = getActivity(v);
		if(activity != null) {
			
			// TODO: Add comment
			String comment = null;
			String title = "Share";
			String subject = null;
			String body = null;
			if(isGenerateShareMessage()) {
				subject = shareMessageBuilder.buildShareSubject(activity);
				body = shareMessageBuilder.buildShareMessage(activity, comment, isHtml(), isIncludeSocialize());
			}
				
			doShare(activity, title, subject, body, comment);
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
	
	protected abstract void doShare(Activity parent, String title, String subject, String body, String comment);
	
	protected abstract boolean isHtml();
	
	protected abstract boolean isIncludeSocialize();
	
	protected boolean isGenerateShareMessage() {
		return true;
	}
}
