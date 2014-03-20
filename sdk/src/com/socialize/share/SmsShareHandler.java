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
import android.os.Build;
import android.provider.Telephony;
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
public class SmsShareHandler extends IntentShareHandler {

	private ShareMessageBuilder shareMessageBuilder;
	
	/* (non-Javadoc)
	 * @see com.socialize.share.AbstractShareHandler#handle(android.app.Activity, com.socialize.entity.SocializeAction, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.share.ShareHandlerListener)
	 */
	@Override
	protected void handle(Activity context, SocializeAction action, String text, PropagationInfo info, SocialNetworkListener listener) throws Exception {
		boolean shareCancelled = false;

		Entity entity = action.getEntity();

		HashMap<String, Object> postValues = new HashMap<String, Object>();

		postValues.put(ShareUtils.EXTRA_TEXT, shareMessageBuilder.buildShareMessage(action.getEntity(), info, text, false, true));

		DefaultPostData postData = new DefaultPostData();

		postData.setEntity(entity);
		postData.setPropagationInfo(info);
		postData.setPostValues(postValues);

		if(listener != null) {
			shareCancelled = listener.onBeforePost(context, null, postData);
		}

		if(!shareCancelled) {
			String body = String.valueOf(postValues.get(ShareUtils.EXTRA_TEXT));
			Intent sendIntent = getIntent(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            } else {
                sendIntent.putExtra("sms_body", body);
            }

			context.startActivity(sendIntent);
		}

		if(listener != null) {
			listener.onAfterPost(context, null, null);
		}
	}
	
	protected Intent getIntent(Context context) {
        Intent sendIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //At least KitKat
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); //Need to change the build to API 19

            sendIntent = new Intent(Intent.ACTION_SEND);

            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
        }
        else {
            sendIntent = new Intent(Intent.ACTION_VIEW);
        }

        sendIntent.setType(getMimeType());
		return sendIntent;
	}
	
	@Override
	protected String getMimeType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "text/plain";
        } else {
            return "vnd.android-dir/mms-sms";
        }
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
