package com.socialize;

import android.content.Context;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentUtilsProxy;
import com.socialize.api.action.entity.EntityUtilsProxy;
import com.socialize.api.action.like.LikeUtilsProxy;
import com.socialize.api.action.share.ShareUtilsProxy;
import com.socialize.api.action.user.UserUtilsProxy;
import com.socialize.api.action.view.ViewUtilsProxy;
import com.socialize.auth.AuthProviders;
import com.socialize.config.ConfigUtilsProxy;
import com.socialize.listener.SocializeInitListener;

public class SocializeAccess {
	
	private static final CommentUtilsProxy originalCommentUtilsProxy = CommentUtils.proxy;
	private static final LikeUtilsProxy originalLikeUtilsProxy = LikeUtils.proxy;
	private static final ShareUtilsProxy originalShareUtilsProxy = ShareUtils.proxy;
	private static final ViewUtilsProxy originalViewUtilsProxy = ViewUtils.proxy;
	private static final UserUtilsProxy originalUserUtilsProxy = UserUtils.proxy;
	private static final ConfigUtilsProxy originalConfigUtilsProxy = ConfigUtils.proxy;
	private static final EntityUtilsProxy originalEntityUtilsProxy = EntityUtils.proxy;
	
	public static <T extends Object> T getBean(String beanName, Object...args) {
		return getBean(Socialize.getSocialize(), beanName, args);
	}
	
	public static <T extends Object> T getBean(SocializeService socialize, String beanName, Object...args) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer().getBean(beanName, args);
		}
		return null;
	}
	
	public static <T extends Object> ProxyObject<T> getProxy(String beanName) {
		return getProxy(Socialize.getSocialize(), beanName);
	}
	
	public static <T extends Object> ProxyObject<T> getProxy(SocializeService socialize, String beanName) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer().getProxy(beanName);
		}
		return null;
	}
	
	public static IOCContainer getContainer() {
		return getContainer(Socialize.getSocialize());
	}
	
	public static IOCContainer getContainer(SocializeService socialize) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer();
		}
		return null;
	}
	
	public static void clearBeanOverrides() {
		Socialize.getSocialize().getSystem().setBeanOverrides();
	}
	
	public static void setBeanOverrides(String...override) {
		Socialize.getSocialize().getSystem().setBeanOverrides(override);
	}
	
	public static void setInitListener(SocializeInitListener listener) {
		Socialize.getSocialize().getSystem().setSystemInitListener(listener);
	}
	
	public static void init(Context context, String...paths) {
		Socialize.instance.init(context, paths);
	}
	
	public static void setAuthProviders(AuthProviders authProviders) {
		Socialize.instance.setAuthProviders(authProviders);
	}
	
	public static void setSession(SocializeSession session) {
		Socialize.instance.setSession(session);
	}
	
	public static void setCommentUtilsProxy(CommentUtilsProxy proxy) {
		CommentUtils.proxy = proxy;
	}
	
	public static void setLikeUtilsProxy(LikeUtilsProxy proxy) {
		LikeUtils.proxy = proxy;
	}
	
	public static void setEntityUtilsProxy(EntityUtilsProxy proxy) {
		EntityUtils.proxy = proxy;
	}
	
	public static void setShareUtilsProxy(ShareUtilsProxy proxy) {
		ShareUtils.proxy = proxy;
	}
	
	public static void setViewUtilsProxy(ViewUtilsProxy proxy) {
		ViewUtils.proxy = proxy;
	}
	
	public static void setUserUtilsProxy(UserUtilsProxy proxy) {
		UserUtils.proxy = proxy;
	}
	
	public static void revertUserUtilsProxy() {
		UserUtils.proxy = originalUserUtilsProxy;
	}
	
	public static void revertCommentUtilsProxy() {
		CommentUtils.proxy = originalCommentUtilsProxy;
	}
	
	public static void revertLikeUtilsProxy() {
		LikeUtils.proxy = originalLikeUtilsProxy;
	}
	
	public static void revertShareUtilsProxy() {
		ShareUtils.proxy = originalShareUtilsProxy;
	}
	
	public static void revertViewUtilsProxy() {
		ViewUtils.proxy = originalViewUtilsProxy;
	}	
	
	public static void setConfigUtilsProxy(ConfigUtilsProxy proxy) {
		ConfigUtils.proxy = proxy;
	}
	
	public static void revertConfigUtilsProxy() {
		ConfigUtils.proxy = originalConfigUtilsProxy;
	}
	
	public static void revertEntityUtilsProxy() {
		EntityUtils.proxy = originalEntityUtilsProxy;
	}	
	
	public static void revertProxies() {
		revertCommentUtilsProxy();
		revertLikeUtilsProxy();
		revertShareUtilsProxy();
		revertViewUtilsProxy();
		revertUserUtilsProxy();
		revertConfigUtilsProxy();
		revertEntityUtilsProxy();
	}
	
}
