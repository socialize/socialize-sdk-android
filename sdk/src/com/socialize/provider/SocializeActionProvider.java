package com.socialize.provider;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.api.action.ActionType;
import com.socialize.entity.Comment;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObjectFactory;
import com.socialize.entity.View;

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
