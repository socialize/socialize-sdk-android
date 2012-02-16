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
import android.widget.EditText;

import com.socialize.Socialize;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.launcher.LaunchAction;
import com.socialize.listener.ListenerHolder;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

/**
 * @author Jason Polites
 *
 */
public abstract class InternalShareClickListener extends BaseShareClickListener {
	
	private ListenerHolder listenerHolder;

	public InternalShareClickListener(ActionBarView actionBarView, EditText commentView, OnActionBarEventListener onActionBarEventListener) {
		super(actionBarView, commentView, onActionBarEventListener);
	}

	public InternalShareClickListener(ActionBarView actionBarView) {
		super(actionBarView);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.share.BaseShareClickListener#doShare(android.app.Activity, com.socialize.entity.Entity, java.lang.String)
	 */
	@Override
	protected void doShare(Activity context, Entity entity, String comment) {
		
		if(isDoShareInline()) {
			String title = "Share";
			String subject = null;
			String body = null;
			if(isGenerateShareMessage()) {
				subject = shareMessageBuilder.buildShareSubject(entity);
				body = shareMessageBuilder.buildShareMessage(entity, comment, isHtml(), isIncludeSocialize());
			}
			doShare(context, title, subject, body, comment);
		}
		else {
			Intent intent = new Intent(context, SocializeLaunchActivity.class);
			
			intent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.LOCAL_SHARE.name());
			
			intent.putExtra(Socialize.ENTITY_OBJECT, entity);
			
			intent.putExtra(SocializeConfig.SOCIALIZE_SHARE_IS_HTML, isHtml());
			intent.putExtra(SocializeConfig.SOCIALIZE_SHARE_COMMENT, comment);
			intent.putExtra(SocializeConfig.SOCIALIZE_SHARE_MIME_TYPE, getMimeType());
			intent.putExtra(SocializeConfig.SOCIALIZE_SHARE_LISTENER_KEY, entity.getKey());
			
			if(listenerHolder != null) {
				listenerHolder.put(entity.getKey(), getShareAddListener());
			}

			context.startActivity(intent);
		}
	}
	
	public void setListenerHolder(ListenerHolder listenerHolder) {
		this.listenerHolder = listenerHolder;
	}

	protected abstract String getMimeType();
	
	protected abstract void doShare(Activity parent, String title, String subject, String body, String comment);

}
