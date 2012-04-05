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
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ShareType;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.SocializeAction;


/**
 * @author Jason Polites
 */
public class SmsShareHandler extends IntentShareHandler {

	private ShareMessageBuilder shareMessageBuilder;
	
	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#handle(android.app.Activity, com.socialize.entity.SocializeAction, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.share.ShareHandlerListener)
	 */
	@Override
	protected void handle(Activity context, SocializeAction action, String text, PropagationInfo info, ShareHandlerListener listener) throws Exception {
		String body = shareMessageBuilder.buildShareMessage(action.getEntity(), info, text, false, true);
		Intent sendIntent = getIntent();
		sendIntent.putExtra("sms_body", body); 
		context.startActivity(sendIntent);
	}
	
	protected Intent getIntent() {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setType(getMimeType());
		return sendIntent;
	}
	
	@Override
	protected String getMimeType() {
		return "vnd.android-dir/mms-sms";
	}

	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#getShareType()
	 */
	@Override
	protected ShareType getShareType() {
		return ShareType.SMS;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
}
