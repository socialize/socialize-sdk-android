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
package com.socialize.ui.auth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import com.socialize.Socialize;
import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.api.SocializeSession;
import com.socialize.api.event.EventSystem;
import com.socialize.api.event.SocializeEvent;
import com.socialize.config.SocializeConfig;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.dialog.AsyncDialogFactory;
import org.json.JSONObject;

/**
 * @author Jason Polites
 *
 */
public class AuthDialogFactory extends AsyncDialogFactory<AuthPanelView, AuthDialogListener> implements IAuthDialogFactory
{
	private EventSystem eventSystem;
	private SocializeConfig config;
	
	public AuthDialogFactory() {
		super();
	}
	
	@Override
	public void preload(Context context) {
		super.preload(context);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.auth.IAuthDialogFactory#show(android.content.Context, com.socialize.ui.auth.AuthDialogListener)
	 */
	@Override
	public void show(Context context, final AuthDialogListener listener, final boolean required) {
		showDialog(context, new BeanCreationListener<AuthPanelView>() {
			
			@Override
			public void onError(String name, Exception e) {}
			
			@Override
			public void onCreate(AuthPanelView bean) {
				bean.setAuthDialogListener(listener);
				bean.setAuthRequired(required);
			}
		}, new AuthDialogListener() {
			
			@Override
			public void onShow(Dialog dialog, AuthPanelView dialogView) {
				recordEvent("show");
				if(listener != null) {
					listener.onShow(dialog, dialogView);
				}
			}
			
			@Override
			public void onCancel(Dialog dialog) {
				recordEvent("cancel");
				if(listener != null) {
					listener.onCancel(dialog);
				}
			}
			
			@Override
			public void onSkipAuth(Activity context, Dialog dialog) {
				recordEvent("skip");
				if(listener != null) {
					listener.onSkipAuth(context, dialog);
				}
			}
			
			@Override
			public void onError(Activity context, Dialog dialog, Exception error) {
				recordEvent("error");
				if(listener != null) {
					listener.onError(context, dialog, error);
				}
			}
			
			@Override
			public void onAuthenticate(Activity context, Dialog dialog, SocialNetwork network) {
				recordEvent("auth", network.name());
				if(listener != null) {
					listener.onAuthenticate(context, dialog, network);
				}
			}
		});
	}
	
	protected void recordEvent(String action) {
		recordEvent(action, null);
	}
	protected void recordEvent(String action, String network) {
		if(eventSystem != null && config != null && config.getBooleanProperty(SocializeConfig.SOCIALIZE_EVENTS_AUTH_ENABLED, true)) {
			try {
				SocializeSession session = Socialize.getSocialize().getSession();
				if(session != null) {
					SocializeEvent event = new SocializeEvent();
					event.setBucket("AUTH_DIALOG");
					JSONObject json = new JSONObject();
					json.put("action", action);
					if(network != null) {
						json.put("network", network);
					}
					event.setData(json);
					eventSystem.addEvent(session, event, null);
				}
			}
			catch (Throwable e) {
				if(logger != null) {
					logger.warn("Error recording share dialog event", e);
				}
			}
		}
	}

	public void setEventSystem(EventSystem eventSystem) {
		this.eventSystem = eventSystem;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
}
