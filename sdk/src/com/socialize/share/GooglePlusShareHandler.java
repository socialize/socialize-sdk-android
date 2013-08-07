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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
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
 *
 */
public class GooglePlusShareHandler extends AbstractShareHandler {
	
	private ShareMessageBuilder shareMessageBuilder;

	static String PLUS_PACKAGE = "com.google.android.apps.plus";

	/* (non-Javadoc)
	 * @see com.socialize.share.ShareHandler#isAvailableOnDevice(android.content.Context)
	 */
	@Override
	public boolean isAvailableOnDevice(Context context) {
		try {
			context.getPackageManager().getPackageInfo(PLUS_PACKAGE, 0);
			return true;
		}
		catch (NameNotFoundException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#handle(android.app.Activity, com.socialize.entity.SocializeAction, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	protected void handle(Activity context, SocializeAction action, String text, PropagationInfo info, SocialNetworkListener listener) throws Exception {

		boolean shareCancelled = false;

		Entity entity = action.getEntity();

		HashMap<String, Object> postValues = new HashMap<String, Object>();

		postValues.put(ShareUtils.EXTRA_SUBJECT, shareMessageBuilder.buildShareSubject(entity));
		postValues.put(ShareUtils.EXTRA_TEXT, shareMessageBuilder.buildShareMessage(entity, info, text, false, true));

		DefaultPostData postData = new DefaultPostData();

		postData.setEntity(entity);
		postData.setPropagationInfo(info);
		postData.setPostValues(postValues);

		if(listener != null) {
			shareCancelled = listener.onBeforePost(context, null, postData);
		}

		if(!shareCancelled) {
			String body = String.valueOf(postValues.get(ShareUtils.EXTRA_TEXT));
			String subject = String.valueOf(postValues.get(ShareUtils.EXTRA_SUBJECT));

			Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND);
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			shareIntent.putExtra(Intent.EXTRA_TEXT, body);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

			shareIntent.setType("text/plain");
			shareIntent.setPackage(PLUS_PACKAGE);
			context.startActivity(shareIntent);
		}

		if(listener != null) {
			listener.onAfterPost(context, null, null);
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#getShareType()
	 */
	@Override
	protected ShareType getShareType() {
		return ShareType.GOOGLE_PLUS;
	}
	
	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
}
