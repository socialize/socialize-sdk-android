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
package com.socialize.ui.actionbutton;

import android.app.Activity;

import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;

/**
 * @author Jason Polites
 *
 */
public abstract class BaseActionButtonHandler<A extends SocializeAction> implements ActionButtonHandler<A> {

	private SocializeLogger logger;
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.actionbutton.ActionButtonHandler#handleLoad(android.app.Activity, com.socialize.ui.actionbutton.SocializeActionButton)
	 */
	@Override
	public void handleLoad(Activity context, final ActionButton<A> button) {
		final OnActionButtonEventListener<A> listener = button.getOnActionButtonEventListener();
		Entity entity = button.getEntity();
		if(entity != null) {
			
			button.setState(ActionButtonState.LOADING);
			
			handleLoad(context, entity, new OnActionButtonEventListener<A>() {

				@Override
				public void onLoad(Activity context, A action, Entity entity) {
					button.setEntity(entity);
					handleAfterLoad(context, button, action);
					if(listener != null) {
						listener.onLoad(context, action, entity);
					}
				}

				@Override
				public boolean onBeforeAction(Activity context) {
					return true;
				}

				@Override
				public void onAfterAction(Activity context, SocializeAction action, Entity entity) {}

				@Override
				public void onError(Activity context, Exception e) {
					button.setState(ActionButtonState.DISABLED);
					
					if(logger != null) {
						logger.error("Failed to load action button", e);
					}
					
					if(listener != null) {
						listener.onError(context, e);
					}	
				}
			});
		}
		else {
			handleNoEntityError(context, button, listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbutton.ActionButtonHandler#handleClick(android.app.Activity, com.socialize.ui.actionbutton.SocializeActionButton)
	 */
	@Override
	public void handleAction(Activity context, final ActionButton<A> button) {
		final OnActionButtonEventListener<A> listener = button.getOnActionButtonEventListener();
		if(listener == null || listener.onBeforeAction(context)) {
			Entity entity = button.getEntity();
			if(entity != null) {
				
				ActionButtonConfig config = button.getConfig();
				
				ShareOptions shareOptions = new ShareOptions();
				shareOptions.setShareLocation(button.getConfig().isShareLocation());
				
				if(config.getShareToNetworks() != null) {
					shareOptions.setShareTo(config.getShareToNetworks());
				}
				
				shareOptions.setAutoAuth(config.isAutoAuth());
				
				button.setState(ActionButtonState.LOADING);
				
				handleAction(context, entity, shareOptions, new OnActionButtonEventListener<A>() {

					@Override
					public void onLoad(Activity context, A action, Entity entity) {}

					@Override
					public boolean onBeforeAction(Activity context) {
						return true;
					}

					@Override
					public void onAfterAction(Activity context, A action, Entity entity) {
						button.setEntity(entity);
						handleAfterAction(context, button, action);
						if(listener != null) {
							listener.onAfterAction(context, action, entity);
						}
					}

					@Override
					public void onError(Activity context, Exception e) {
						if(logger != null) {
							logger.error("Failed to handle button action", e);
						}						
						
						if(listener != null) {
							listener.onError(context, e);
						}
					}
				});
			}
			else {
				handleNoEntityError(context, button, listener);
			}
		}
	}
	
	protected void handleNoEntityError(Activity context, ActionButton<A> button, OnActionButtonEventListener<A> listener) {
		
		button.setState(ActionButtonState.DISABLED);
		
		String msg = "No entity specified for button [" +
				button.getConfig().getButtonId() +
				"]";
		
		if(logger != null) {
			logger.error(msg);
		}
		
		if(listener != null) {
			listener.onError(context, new SocializeException(msg));
		}	
	}
	

	protected abstract void handleLoad(Activity context, Entity entity, OnActionButtonEventListener<A> listener);
	
	protected abstract void handleAfterLoad(Activity context, ActionButton<A> button, A action);

	protected abstract void handleAction(Activity context, Entity entity, ShareOptions shareOptions, OnActionButtonEventListener<A> listener);
	
	protected abstract void handleAfterAction(Activity context, ActionButton<A> button, A action);

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
