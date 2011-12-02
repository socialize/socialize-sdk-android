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
package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.api.action.ActionType;
import com.socialize.entity.Comment;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.View;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class GenericActionFactory extends SocializeActionFactory<SocializeAction> {

	private SocializeLogger logger;
	private CommentFactory commentFactory;
	private LikeFactory likeFactory;
	private ViewFactory viewFactory;
	private ShareFactory shareFactory;
	
	@Override
	protected void postFromJSON(JSONObject from, SocializeAction to) throws JSONException {
		switch(to.getActionType()) {
			case COMMENT:  commentFactory.postFromJSON(from, (Comment)to);
			case SHARE:  shareFactory.postFromJSON(from, (Share)to);
			case VIEW:  viewFactory.postFromJSON(from, (View)to);
			case LIKE:  likeFactory.postFromJSON(from, (Like)to);
		}
	}

	@Override
	protected void postToJSON(SocializeAction from, JSONObject to) throws JSONException {
		switch(from.getActionType()) {
			case COMMENT:  commentFactory.postToJSON((Comment)from, to);
			case SHARE:  shareFactory.postToJSON((Share)from, to);
			case VIEW:  viewFactory.postToJSON((View)from, to);
			case LIKE:  likeFactory.postToJSON((Like)from, to);
		}
	}

	@Override
	public Object instantiateObject(JSONObject from) {
		if(from.has("activity_type") && !from.isNull("activity_type")) {
			
			try {
				String type = from.getString("activity_type").toUpperCase();
				ActionType actionType = ActionType.valueOf(type);
				
				switch(actionType) {
					case COMMENT: return commentFactory.instantiateObject(from);
					case SHARE: return shareFactory.instantiateObject(from);
					case VIEW: return viewFactory.instantiateObject(from);
					case LIKE: return likeFactory.instantiateObject(from);
				}
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Error getting activity type", e);
				}
				else {
					e.printStackTrace();	
				}
			}
		}
		return null;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setCommentFactory(CommentFactory commentFactory) {
		this.commentFactory = commentFactory;
	}

	public void setLikeFactory(LikeFactory likeFactory) {
		this.likeFactory = likeFactory;
	}

	public void setViewFactory(ViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void setShareFactory(ShareFactory shareFactory) {
		this.shareFactory = shareFactory;
	}
}
