package com.socialize.provider;

import com.socialize.api.action.ActionType;
import com.socialize.entity.*;
import org.json.JSONException;
import org.json.JSONObject;

public class SocializeActionProvider extends DefaultSocializeProvider<SocializeAction> {

	private SocializeObjectFactory<Comment> commentFactory;
	private SocializeObjectFactory<Share> shareFactory;
	private SocializeObjectFactory<View> viewFactory;
	private SocializeObjectFactory<Like> likeFactory;
	
	@Override
	public SocializeAction fromJSON(JSONObject json, ActionType type) throws JSONException {
		switch(type) {
			case COMMENT: return commentFactory.fromJSON(json);
			case SHARE: return shareFactory.fromJSON(json);
			case VIEW: return viewFactory.fromJSON(json);
			case LIKE:return likeFactory.fromJSON(json);
		}
		return super.fromJSON(json, type);
	}

	public void setCommentFactory(SocializeObjectFactory<Comment> commentFactory) {
		this.commentFactory = commentFactory;
	}

	public void setShareFactory(SocializeObjectFactory<Share> shareFactory) {
		this.shareFactory = shareFactory;
	}

	public void setViewFactory(SocializeObjectFactory<View> viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void setLikeFactory(SocializeObjectFactory<Like> likeFactory) {
		this.likeFactory = likeFactory;
	}
}
