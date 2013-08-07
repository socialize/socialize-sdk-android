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
package com.socialize.share;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import com.socialize.ShareUtils;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.SocializeAction;
import com.socialize.networks.DefaultPostData;
import com.socialize.networks.SocialNetworkListener;

import java.util.HashMap;

/**
 * @author Jason Polites
 */
public class EmailShareHandler extends IntentShareHandler {

	private ShareMessageBuilder shareMessageBuilder;
	
	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#handle(android.app.Activity, com.socialize.entity.SocializeAction, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.share.ShareHandlerListener)
	 */
	@Override
	protected void handle(Activity context, SocializeAction action, String text, PropagationInfo info, SocialNetworkListener listener) throws Exception {

		boolean shareCancelled = false;

		Entity entity = action.getEntity();

		HashMap<String, Object> postValues = new HashMap<String, Object>();

		postValues.put(ShareUtils.EXTRA_TITLE, "Share");
		postValues.put(ShareUtils.EXTRA_SUBJECT, shareMessageBuilder.buildShareSubject(entity));
		postValues.put(ShareUtils.EXTRA_TEXT, shareMessageBuilder.buildShareMessage(entity, info, text, isHtml(), true));

		DefaultPostData postData = new DefaultPostData();

		postData.setEntity(entity);
		postData.setPropagationInfo(info);
		postData.setPostValues(postValues);

		if(listener != null) {
			shareCancelled = listener.onBeforePost(context, null, postData);
		}

		if(!shareCancelled) {
			String title = String.valueOf(postValues.get(ShareUtils.EXTRA_TITLE));
			String body = String.valueOf(postValues.get(ShareUtils.EXTRA_TEXT));
			Intent msg = getIntent();
			msg.putExtra(Intent.EXTRA_TITLE, title);

			if(isHtml()) {
				msg.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
			}
			else {
				msg.putExtra(Intent.EXTRA_TEXT, body);
			}

			msg.putExtra(Intent.EXTRA_SUBJECT, String.valueOf(postValues.get(ShareUtils.EXTRA_SUBJECT)));

			startActivity(context, msg, title);
		}

		if(listener != null) {
			listener.onAfterPost(context, null, null);
		}
	}
	
	protected void startActivity(Activity context, Intent intent, String title) {
		Intent chooser = Intent.createChooser(intent, title);
		chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(chooser);
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

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
}
