package com.socialize.sample.mocks;

import java.util.List;

import android.location.Location;
import android.util.Log;

import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerAware;
import com.socialize.api.ApiHost;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderData;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.User;
import com.socialize.entity.View;
import com.socialize.error.SocializeApiError;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.UserActivityListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.networks.ShareOptions;
import com.socialize.ui.comment.CommentShareOptions;

@Deprecated
public class MockSocializeApiHost extends SocializeApiHost implements ContainerAware {

	private ApiHost delegate;
	private static ListResult<?> listResult;
	private static User user = new User();
	
	@Override
	public void onCreate(Container container) {
		Log.w("MockSocializeApiHost", "onCreate");
	}

	@Override
	public void onDestroy(Container container) {
		Log.w("MockSocializeApiHost", "onDestroy");
	}

	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
		if(delegate != null) delegate.authenticate(consumerKey, consumerSecret, listener, sessionConsumer);
	}

	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
		if(delegate != null) delegate.authenticate(consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
	}

	@Override
	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		listener.onCreate(new Entity());
		if(delegate != null) delegate.createEntity(session, key, name, listener);
	}
	

	@Override
	public void addComment(SocializeSession session, Entity entity, String comment, Location location, ShareOptions shareOptions, CommentListener listener) {
		listener.onCreate(new Comment());
		if(delegate != null) delegate.addComment(session, entity, comment, location, shareOptions, listener);
	}

	@Override
	public void addLike(SocializeSession session, Entity entity, Location location, LikeListener listener) {
		Like like = new Like();
		like.setId(0L);
		if(listener != null) listener.onCreate(like);
		if(delegate != null) delegate.addLike(session, entity, location, listener);
	}

	@Override
	public void addView(SocializeSession session, Entity entity, Location location, ViewListener listener) {
		View view = new View();
		entity.setId(0L);
		listener.onCreate(view);
		
		if(delegate != null) delegate.addView(session, entity, location, listener);
		
	}

	@Override
	public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		Share share = new Share();
		entity.setId(0L);
		listener.onCreate(share);
		if(delegate != null) delegate.addShare(session, entity, text, shareType, location, listener);
	}

	@Deprecated
	@Override
	public void addComment(SocializeSession session, String key, String comment, Location location, CommentShareOptions shareOptions, CommentListener listener) {
		listener.onCreate(new Comment());
		if(delegate != null) delegate.addComment(session, key, comment, location, shareOptions, listener);
	}

	@Deprecated
	@Override
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		Like like = new Like();
		like.setId(0L);
		if(listener != null) listener.onCreate(like);
		if(delegate != null) delegate.addLike(session, key, location, listener);
	}

	@Deprecated
	@Override
	public void addView(SocializeSession session, String key, Location location, ViewListener listener) {
		View entity = new View();
		entity.setId(0L);
		listener.onCreate(entity);
		
		if(delegate != null) delegate.addView(session, key, location, listener);
	}

	@Deprecated
	@Override
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		Share entity = new Share();
		entity.setId(0L);
		listener.onCreate(entity);
		
		if(delegate != null) delegate.addShare(session, key, text, shareType, location, listener);
	}

	@Override
	public void deleteLike(SocializeSession session, long id, LikeListener listener) {
		listener.onDelete();
		
		if(delegate != null) delegate.deleteLike(session, id, listener);
	}

	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		Comment entity = new Comment();
		entity.setId(0L);
		listener.onGet(entity);		
		
		if(delegate != null) delegate.getComment(session, id, listener);
	}

	@Override
	public void getLike(SocializeSession session, long id, LikeListener listener) {
		listener.onError(new SocializeApiError(404, "MOCK 404 - NOT A REAL ERROR!"));
		
		if(delegate != null) delegate.getLike(session, id, listener);
	}

	@Override
	public void getLike(SocializeSession session, String key, LikeListener listener) {
		listener.onError(new SocializeApiError(404, "MOCK 404 - NOT A REAL ERROR!"));
		
		if(delegate != null) delegate.getLike(session, key, listener);
	}

	@Override
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		Entity entity = new Entity();
		entity.setId(0L);
		entity.setLikes(20);
		entity.setComments(40);
		entity.setShares(60);
		entity.setViews(100);
		listener.onGet(entity);
		
		if(delegate != null) delegate.getEntity(session, key, listener);
	}

	@Override
	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String... keys) {
		if(delegate != null) delegate.listEntitiesByKey(session, listener, keys);
		listener.onList((ListResult<Entity>) listResult);
	}

	@Override
	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener) {
		if(delegate != null) delegate.listCommentsByEntity(session, url, listener);
		listener.onList((ListResult<Comment>) listResult);
	}

	@Override
	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener) {
		if(delegate != null) delegate.listCommentsByEntity(session, url, startIndex, endIndex, listener);
		listener.onList((ListResult<Comment>) listResult);
	}

	@Override
	public void listCommentsById(SocializeSession session, CommentListener listener, long... ids) {
		if(delegate != null) delegate.listCommentsById(session, listener, ids);
		listener.onList((ListResult<Comment>) listResult);
	}

	@Override
	public void listLikesById(SocializeSession session, LikeListener listener, long... ids) {
		if(delegate != null) delegate.listLikesById(session, listener, ids);
		listener.onList((ListResult<Like>) listResult);
	}

	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		if(delegate != null) delegate.getUser(session, id, listener);
		listener.onGet(user);
	}

	@Override
	public void listActivityByUser(SocializeSession session, long id, UserActivityListener listener) {
		if(delegate != null) delegate.listActivityByUser(session, id, listener);
		listener.onList((ListResult<SocializeAction>) listResult);
	}

	@Override
	public void listActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, UserActivityListener listener) {
		if(delegate != null) delegate.listActivityByUser(session, id, startIndex, endIndex, listener);
		listener.onList((ListResult<SocializeAction>) listResult);
	}

	public ApiHost getDelegate() {
		return delegate;
	}

	public void setDelegate(ApiHost delegate) {
		this.delegate = delegate;
	}
	
	public static <T extends SocializeObject> void orchestrateListResult(Class<T> clazz, List<T> list) {
		ListResult<T> lr = new ListResult<T>();
		lr.setItems(list);
		listResult = lr;
	}
	
	public static void orchestrateUser(User u) {
		user = u;
	}
}
