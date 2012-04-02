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
package com.socialize.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ShareType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
@Deprecated
public class ShareLauncher implements Launcher {
	
	private ShareMessageBuilder shareMessageBuilder;
	private ListenerHolder listenerHolder;

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#launch(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public boolean launch(Activity context, Bundle data) {
		
		Object obj = data.get(Socialize.ENTITY_OBJECT);
				
		if(obj instanceof Entity) {
			
			Entity entity = (Entity) obj;
			
			String text = data.getString(SocializeConfig.SOCIALIZE_SHARE_COMMENT);
			String mimeType = data.getString(SocializeConfig.SOCIALIZE_SHARE_MIME_TYPE);
//			boolean isHtml = data.getBoolean(SocializeConfig.SOCIALIZE_SHARE_IS_HTML, false);
			
			if(StringUtils.isEmpty(text)) {
				text = entity.getDisplayName();
			}
			
			String title = "Share";
			String subject = shareMessageBuilder.buildShareSubject(entity);
//			String body = shareMessageBuilder.buildShareMessage(entity, text, isHtml, true);
			
			// Start the intent
			Intent msg = new Intent(android.content.Intent.ACTION_SEND);
			
			msg.setType(mimeType);
			
			msg.putExtra(Intent.EXTRA_TITLE, title);
			
//			if(isHtml) {
//				msg.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
//			}
//			else {
//				msg.putExtra(Intent.EXTRA_TEXT, body);
//			}
			
			msg.putExtra(Intent.EXTRA_SUBJECT, subject);
			
			context.startActivityForResult(Intent.createChooser(msg, title), 0);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void onResult(Activity context, int requestCode, int resultCode, Intent returnedIntent, Intent originalIntent) {
		Bundle extras = originalIntent.getExtras();
		if(extras != null) {
			Object obj = extras.get(Socialize.ENTITY_OBJECT);
			if(obj instanceof Entity) {
				Entity entity = (Entity) obj;
				String text = extras.getString(SocializeConfig.SOCIALIZE_SHARE_COMMENT);
				
				if(StringUtils.isEmpty(text)) {
					text = entity.getDisplayName();
				}
				
				String key = extras.getString(SocializeConfig.SOCIALIZE_SHARE_LISTENER_KEY);
				ShareAddListener listener = listenerHolder.remove(key);
				Socialize.getSocialize().addShare(context, entity, text, ShareType.OTHER, listener);				
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#shouldFinish()
	 */
	@Override
	public boolean shouldFinish() {
		return false;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}

	public void setListenerHolder(ListenerHolder listenerHolder) {
		this.listenerHolder = listenerHolder;
	}
}
