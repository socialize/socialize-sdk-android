package com.socialize.ui.sample.mock;

import android.content.Context;
import android.location.Location;

import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.SocializeSessionImpl;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderData;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.entity.View;
import com.socialize.error.SocializeApiError;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.ActivityListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.view.ViewListener;

public class MockSocializeApiHost extends SocializeApiHost {

	public MockSocializeApiHost(Context context) {
		super(context);
	}

	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		listener.onAuthSuccess(new SocializeSessionImpl());
	}

	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer,
			boolean do3rdPartyAuth) {
		listener.onAuthSuccess(new SocializeSessionImpl());
	}

	@Override
	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		listener.onCreate(new Entity());
	}

	@Override
	public void addComment(SocializeSession session, String key, String comment, Location location, CommentListener listener) {
		listener.onCreate(new Comment());
	}

	@Override
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		listener.onCreate(new Like());
	}

	@Override
	public void addView(SocializeSession session, String key, Location location, ViewListener listener) {
		listener.onCreate(new View());
	}

	@Override
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		listener.onCreate(new Share());
	}

	@Override
	public void deleteLike(SocializeSession session, int id, LikeListener listener) {
		listener.onDelete();
	}

	@Override
	public void getComment(SocializeSession session, int id, CommentListener listener) {
		listener.onGet(new Comment());
	}

	@Override
	public void getLike(SocializeSession session, int id, LikeListener listener) {
		listener.onError(new SocializeApiError(404, "MOCK 404 - NOT A REAL ERROR!"));
	}

	@Override
	public void getLike(SocializeSession session, String key, LikeListener listener) {
		listener.onError(new SocializeApiError(404, "MOCK 404 - NOT A REAL ERROR!"));
	}

	@Override
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		Entity entity = new Entity();
		entity.setLikes(20);
		entity.setComments(40);
		entity.setShares(60);
		entity.setViews(100);
		listener.onGet(entity);
	}

	@Override
	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String... keys) {
		// TODO Auto-generated method stub
		super.listEntitiesByKey(session, listener, keys);
	}

	@Override
	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener) {
		// TODO Auto-generated method stub
		super.listCommentsByEntity(session, url, listener);
	}

	@Override
	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener) {
		// TODO Auto-generated method stub
		super.listCommentsByEntity(session, url, startIndex, endIndex, listener);
	}

	@Override
	public void listCommentsById(SocializeSession session, CommentListener listener, int... ids) {
		// TODO Auto-generated method stub
		super.listCommentsById(session, listener, ids);
	}

	@Override
	public void listLikesById(SocializeSession session, LikeListener listener, int... ids) {
		// TODO Auto-generated method stub
		super.listLikesById(session, listener, ids);
	}

	@Override
	public void getUser(SocializeSession session, int id, UserListener listener) {
		listener.onGet(new User());
	}

	@Override
	public void listActivityByUser(SocializeSession session, int id, ActivityListener listener) {
		listener.onList(null);
	}

	@Override
	public void listActivityByUser(SocializeSession session, int id, int startIndex, int endIndex, ActivityListener listener) {
		listener.onList(null);
	}
	
	
	
	
}
