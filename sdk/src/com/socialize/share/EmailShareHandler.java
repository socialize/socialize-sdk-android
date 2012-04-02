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
package com.socialize.share;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ShareType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.SocializeAction;


/**
 * @author Jason Polites
 */
public class EmailShareHandler extends IntentShareHandler {

	private ShareMessageBuilder shareMessageBuilder;
	private SocializeConfig config;
	
	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#handle(android.app.Activity, com.socialize.entity.SocializeAction, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.share.ShareHandlerListener)
	 */
	@Override
	protected void handle(Activity context, SocializeAction action, String text, PropagationInfo info, ShareHandlerListener listener) throws Exception {
		
		Entity entity = action.getEntity();
		
		String title = "Share";
		String subject = shareMessageBuilder.buildShareSubject(entity);
		String body = shareMessageBuilder.buildShareMessage(entity, info, text, isHtml(), config.getBooleanProperty(SocializeConfig.SOCIALIZE_BRANDING_ENABLED, true));
		Intent msg = getIntent();
		msg.putExtra(Intent.EXTRA_TITLE, title);
		
		if(isHtml()) {
			msg.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
		}
		else {
			msg.putExtra(Intent.EXTRA_TEXT, body);
		}
		
		msg.putExtra(Intent.EXTRA_SUBJECT, subject);
		
		startActivity(context, msg, title);
	}
	
	protected void startActivity(Activity context, Intent intent, String title) {
		context.startActivity(Intent.createChooser(intent, title));
	}
	
	@Override
	protected String getMimeType() {
		return "message/rfc822";
	}
	
	protected boolean isHtml() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#getShareType()
	 */
	@Override
	protected ShareType getShareType() {
		return ShareType.EMAIL;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
}
