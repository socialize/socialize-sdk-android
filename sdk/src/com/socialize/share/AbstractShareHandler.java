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
import android.location.Location;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.action.ShareType;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;


/**
 * @author Jason Polites
 *
 */
public abstract class AbstractShareHandler implements ShareHandler {
	
	private SocializeLogger logger;

	/* (non-Javadoc)
	 * @see com.socialize.share.ShareHandler#handle(android.app.Activity, com.socialize.entity.Share, com.socialize.share.ShareHandlerListener)
	 */
	@Override
	public void handle(Activity context, SocializeAction action, Location location, String text, ShareHandlerListener listener) {
		
		PropagationInfoResponse propagationInfoResponse = action.getPropagationInfoResponse();
		
		if(propagationInfoResponse != null) {
			PropagationInfo propagationInfo = propagationInfoResponse.getPropagationInfo(getShareType());
			
			if(propagationInfo != null) {
				try {
					if(listener != null) {
						listener.onBeforePost(context);
					}						
					
					handle(context, action, text, propagationInfo, listener);
					
					if(listener != null) {
						listener.onAfterPost(context, action);
					}
				}
				catch (Exception e) {
					if(logger != null) {
						logger.error("Error handling share", e);
					}
					
					if(listener != null) {
						listener.onError(context, action, "Error handling share", e);
					}
				}
			}
			else {
				logError(context, action, "No propagation info found for share type [" +
								getShareType() +
								"].  Share will not propagate", listener);
			}
		}
		else {
			logError(context, action, "No propagation info found for share type [" +
							getShareType() +
							"].  Share will not propagate", listener);
		}
	}
	
	protected void logError(Activity context, SocializeAction action, String msg, ShareHandlerListener listener) {
		if(logger != null) {
			logger.warn(msg);
		}
		else {
			System.err.println(msg);
		}
		
		if(listener != null) {
			listener.onError(context, action, "Error handling share", new SocializeException(msg));
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	protected abstract void handle(Activity context, SocializeAction action, String text, PropagationInfo info, ShareHandlerListener listener) throws Exception;
	
	protected abstract ShareType getShareType();
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
