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
package com.socialize.ui.share;

import android.app.Dialog;
import android.content.Context;
import com.socialize.Socialize;
import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.event.EventSystem;
import com.socialize.api.event.SocializeEvent;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.ui.dialog.AsyncDialogFactory;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public class ShareDialogFactory extends AsyncDialogFactory<SharePanelView, ShareDialogListener> implements IShareDialogFactory {

	private EventSystem eventSystem;
	private SocializeConfig config;
	
	@Override
	public void preload(Context context) {
		super.preload(context);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.share.IShareDialogFactory#show(android.content.Context, com.socialize.entity.Entity, com.socialize.networks.SocialNetworkListener, com.socialize.ui.share.ShareDialogListener, int)
	 */
	@Override
	public void show(
			Context context, 
			final Entity entity, 
			final SocialNetworkListener socialNetworkListener, 
			final ShareDialogListener shareDialoglistener, 
			final int displayOptions) {
		
		showDialog(context, 
				
		new BeanCreationListener<SharePanelView>() {
			
			@Override
			public void onError(String name, Exception e) {}
			
			@Override
			public void onCreate(SharePanelView bean) {
				bean.setSocialNetworkListener(socialNetworkListener);
				bean.setEntity(entity);
				bean.setShareDialogListener(shareDialoglistener);
				bean.setDisplayOptions(displayOptions);
				bean.applyDisplayOptions();
				bean.updateNetworkButtonState();
			}
		}, new ShareDialogListener() {

			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {
				recordEvent("show");
				if(shareDialoglistener != null) {
					shareDialoglistener.onShow(dialog, dialogView);
				}
			}

			@Override
			public void onSimpleShare(ShareType type) {
				recordEvent("share", type.name());
				if(shareDialoglistener != null) {
					shareDialoglistener.onSimpleShare(type);
				}
			}

			@Override
			public void onCancel(Dialog dialog) {
				recordEvent("close");
				if(shareDialoglistener != null) {
					shareDialoglistener.onCancel(dialog);
				}
			}

			@Override
			public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
				recordEvent("share", networks);
				return shareDialoglistener != null && shareDialoglistener.onContinue(dialog, remember, networks);
			}

			@Override
			public void onFlowInterrupted(DialogFlowController controller) {
				if(shareDialoglistener != null) {
					shareDialoglistener.onFlowInterrupted(controller);
				}
			}
		});
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	protected void recordEvent(String action, SocialNetwork[] networks) {
		if(networks != null && networks.length > 0) {
			String[] strNets = new String[networks.length];
			for (int i = 0; i < networks.length; i++) {
				strNets[i] = networks[i].name();
			}
			
			recordEvent(action, strNets);
		}
		else {
			recordEvent(action);
		}
	}
	
	protected void recordEvent(final String action, final String...networks) {
		new Thread() {
			@Override
			public void run() {
				if(eventSystem != null && config != null && config.getBooleanProperty(SocializeConfig.SOCIALIZE_EVENTS_SHARE_ENABLED, true)) {
					try {
						SocializeSession session = Socialize.getSocialize().getSession();
						if(session != null) {
							SocializeEvent event = new SocializeEvent();
							event.setBucket("SHARE_DIALOG");
							JSONObject json = new JSONObject();
							json.put("action", action);
							
							if(networks != null && networks.length > 0) {
								JSONArray array = new JSONArray();
								for (String string : networks) {
									array.put(string);
								}
								json.put("network", array);
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
		}.start();
	}

	public void setEventSystem(EventSystem eventSystem) {
		this.eventSystem = eventSystem;
	}
}
