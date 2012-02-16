package com.socialize.test.ui.integrationtest;

import static junit.framework.Assert.fail;
import android.content.Context;
import android.location.Location;

import com.socialize.api.ApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
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
public class DefaultTestApiHost implements ApiHost {
	
	static String message = "Unexpected call to ";
	
	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		fail(message + "authenticate");
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		fail(message + "authenticate");
	}

	@Override
	public void addComment(SocializeSession session, Entity entity, String comment, Location location, ShareOptions shareOptions, CommentListener listener) {
		fail(message + "addComment");
	}

	@Override
	public void addLike(SocializeSession session, Entity entity, Location location, LikeListener listener) {
		fail(message + "addLike");
	}

	@Override
	public void addView(SocializeSession session, Entity entity, Location location, ViewListener listener) {
		fail(message + "addView");
	}

	@Override
	public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		fail(message + "addShare");
	}

	@Override
	public void listLikesByUser(SocializeSession session, long userId, LikeListener listener) {
		fail(message + "listLikesByUser");
	}

	@Override
	public void listLikesByUser(SocializeSession session, long userId, int startIndex, int endIndex, LikeListener listener) {
		fail(message + "listLikesByUser");
	}

	@Override
	public void clearSessionCache(AuthProviderType authProviderType) {
		fail(message + "clearSessionCache (authProviderType)");
	}

	@Override
	public void clearSessionCache() {
		fail(message + "clearSessionCache");
	}

	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		fail(message + "authenticate");
	}

	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		fail(message + "authenticate");
	}

	@Override
	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		fail(message + "createEntity");
	}

	@Override
	public void addComment(SocializeSession session, String key, String comment, Location location, CommentShareOptions shareOptions, CommentListener listener) {
		fail(message + "addComment");
	}

	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		fail(message + "getComment");
	}

	@Override
	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String... keys) {
		fail(message + "listEntitiesByKey");
	}

	@Override
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		fail(message + "getEntity");
	}

	@Override
	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener) {
		fail(message + "listCommentsByEntity");
	}

	@Override
	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener) {
		fail(message + "listCommentsByEntity");
	}

	@Override
	public void listCommentsByUser(SocializeSession session, long userId, CommentListener listener) {
		fail(message + "listCommentsByUser");
	}

	@Override
	public void listCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener) {
		fail(message + "listCommentsByUser");
	}

	@Override
	public void listCommentsById(SocializeSession session, CommentListener listener, long... ids) {
		fail(message + "listCommentsById");
	}

	@Override
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		fail(message + "addLike");
	}

	@Override
	public void addView(SocializeSession session, String key, Location location, ViewListener listener) {
		fail(message + "addView");
	}

	@Override
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		fail(message + "addShare");
	}

	@Override
	public void deleteLike(SocializeSession session, long id, LikeListener listener) {
		fail(message + "deleteLike");
	}

	@Override
	public void listLikesById(SocializeSession session, LikeListener listener, long... ids) {
		fail(message + "listLikesById");

	}

	@Override
	public void getLike(SocializeSession session, long id, LikeListener listener) {
		fail(message + "getLike");
	}

	@Override
	public void getLike(SocializeSession session, String key, LikeListener listener) {
		fail(message + "getLike");
	}

	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		fail(message + "getUser");
	}

	@Override
	public void saveUserProfile(Context context, SocializeSession session, String firstName, String lastName, String encodedImage, UserListener listener) {
		fail(message + "saveUserProfile");
	}

	@Override
	public void listActivityByUser(SocializeSession session, long id, UserActivityListener listener) {
		fail(message + "listActivityByUser");
	}

	@Override
	public void listActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, UserActivityListener listener) {
		fail(message + "listActivityByUser");
	}

	@Override
	public void destroy() {
		fail(message + "destroy");
	}

}
