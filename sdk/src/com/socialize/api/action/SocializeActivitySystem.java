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
package com.socialize.api.action;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.UserActivityListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class SocializeActivitySystem extends SocializeApi<SocializeAction, SocializeProvider<SocializeAction>> implements ActivitySystem {

	public SocializeActivitySystem(SocializeProvider<SocializeAction> provider) {
		super(provider);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.ActivitySystem#getAction(com.socialize.api.SocializeSession, long, com.socialize.api.action.ActionType)
	 */
	@Override
	public SocializeAction getAction(SocializeSession session, long id, ActionType type) throws SocializeException {
		String endpoint = null;
		switch(type) {
		
			case COMMENT:
				endpoint = CommentSystem.ENDPOINT;
				break;
			case LIKE:
				endpoint = LikeSystem.ENDPOINT;
				break;
			case SHARE:
				endpoint = ShareSystem.ENDPOINT;
				break;
			case VIEW:
				endpoint = ViewSystem.ENDPOINT;
				break;			
		}
		
		return get(session, endpoint, String.valueOf(id));
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.ActivitySystem#getActivityByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.activity.UserActivityListener)
	 */
	@Override
	public void getActivityByUser(SocializeSession session, long id, UserActivityListener listener) {
		String userId = String.valueOf(id);
		String endpoint = getEndpoint(userId);
		listAsync(session, endpoint, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.ActivitySystem#getActivityByUser(com.socialize.api.SocializeSession, long, int, int, com.socialize.listener.activity.UserActivityListener)
	 */
	@Override
	public void getActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, UserActivityListener listener) {
		String userId = String.valueOf(id);
		String endpoint = getEndpoint(userId);
		listAsync(session, endpoint, startIndex, endIndex, listener);
	}
	
	protected String getEndpoint(String id) {
		return ENDPOINT + id + ENDPOINT_SUFFIX;
	}
}
