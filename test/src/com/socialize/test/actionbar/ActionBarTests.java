package com.socialize.test.actionbar;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.api.action.entity.SocializeEntityUtils;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.api.action.share.SocializeShareUtils;
import com.socialize.api.action.view.SocializeViewUtils;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStatsImpl;
import com.socialize.entity.Like;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.test.PublicEntity;
import com.socialize.test.util.TestUtils;
import com.socialize.testapp.ActionBarActivity;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.OnActionBarReloadListener;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.IAuthDialogFactory;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;
import junit.framework.Assert;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class ActionBarTests extends ActivityInstrumentationTestCase2<ActionBarActivity> {

    public static PublicEntity entity;
    public static ActionBarListener listener;
    protected CountDownLatch globalLatch = null;
    protected AuthProviders authProviders;

    public ActionBarTests() {
        super(ActionBarActivity.class);
    }

    protected final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
        @Override
        public void getLike(Activity context, String entityKey, LikeGetListener listener) {
            listener.onGet(null); // not liked
        }
    };

    protected final SocializeEntityUtils mockEntityUtils = new SocializeEntityUtils() {
        @Override
        public void getEntity(Activity context, String key, final EntityGetListener listener) {
            listener.onGet(entity);
            globalLatch.countDown();
        }
    };

    protected final SocializeViewUtils mockViewUtils = new SocializeViewUtils() {
        @Override
        public void view(Activity context, Entity e, ViewAddListener listener) {
            View v = new View();
            v.setEntity(entity);
            v.setId(1L);
            listener.onCreate(v);
        }
    };

    // Don't preload
    protected final SocializeShareUtils mockShareUtils = new SocializeShareUtils() {

        @Override
        public void preloadShareDialog(Activity context) {}

        @Override
        public void preloadLinkDialog(Activity context) {}
    };

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        TestUtils.setDexCache(this);

        entity = new PublicEntity();
        listener = mock(ActionBarListener.class);

        EntityStatsImpl stats = new EntityStatsImpl();
        stats.setComments(2);
        stats.setLikes(4);
        stats.setShares(6);
        stats.setViews(8);
        entity.setEntityStats(stats);
        entity.setKey("http://entity1.com" + Math.random());
        entity.setName("no name");

        globalLatch = new CountDownLatch(1);
        authProviders = mock(AuthProviders.class);

        SocializeAccess.setBeanOverrides("socialize_proxy_beans.xml");
        SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
        SocializeAccess.setEntityUtilsProxy(mockEntityUtils);
        SocializeAccess.setViewUtilsProxy(mockViewUtils);
        SocializeAccess.setShareUtilsProxy(mockShareUtils);
        SocializeAccess.setAuthProviders(authProviders);

        SocializeAccess.setInitListener(new SocializeInitListener() {
            @Override
            public void onInit(Context context, IOCContainer container) {
                ProxyObject<AuthProviders> providers = container.getProxy("authProviders");
                if(providers != null) {
                    providers.setDelegate(authProviders);
                }
            }
            @Override
            public void onError(SocializeException error) {
                error.printStackTrace();
                fail();
            }
        });


        TestUtils.setUp(this);

        waitForActionBarLoad();
    }

    @Override
    protected void tearDown() throws Exception {
        TestUtils.tearDown(this);
        super.tearDown();
    }

    public void testActionBarLoad() {
        final Activity activity = getActivity();
        final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);
        Assert.assertNotNull(actionBar);
    }

    public void testActionBarReload() throws Throwable {
        final Activity activity = getActivity();
        final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
        final ActionBarLayoutView actionBarView = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);

        assertNotNull("Action bar was null", actionBar);
        assertNotNull("Action bar view was null", actionBarView);

        SocializeConfig config = ConfigUtils.getConfig(activity);

        // Ensure FB/TW are not supported
        config.setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
        config.setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
        config.setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
        config.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");

        final Like like = new Like();
        like.setId(-1L);

        final int num = 10;

        final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
            @Override
            public void getLike(Activity context, String entityKey, LikeGetListener listener) {
                listener.onGet(null); // not liked
            }

            @Override
            public void like(Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork... networks) {
                TestUtils.addResult(entity);
                like.setEntity(entity);
                listener.onCreate(like);
            }
        };

        SocializeAccess.setLikeUtilsProxy(mockLikeUtils);

        final CountDownLatch latch = new CountDownLatch(1);

        // Simulate likes
        this.runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < num; i++) {
                    actionBar.setEntity(Entity.newInstance("reloadTest" + i, "reloadTest" + i));
                    actionBar.refresh();
                    assertTrue(actionBarView.getLikeButton().performClick());
                }

                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        for (int i = 0; i < num; i++) {
            Entity result = TestUtils.getResult(i);
            assertNotNull(result);
            assertEquals("reloadTest" + i, result.getKey());
        }
    }

    public void testReloadListener() throws Throwable {
        Activity activity = getActivity();
        SocializeConfig config = ConfigUtils.getConfig(activity);
        // Ensure FB/TW are not supported
        config.setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
        config.setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
        config.setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
        config.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");

        final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);

        assertNotNull(actionBar);

        final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
            @Override
            public void getLike(Activity context, String entityKey, LikeGetListener listener) {
                listener.onGet(null); // not liked
            }
        };

        final SocializeEntityUtils mockEntityUtils = new SocializeEntityUtils() {
            @Override
            public void getEntity(Activity context, String key, EntityGetListener listener) {
                if(listener != null) {
                    listener.onGet(Entity.newInstance(key, key));
                }
            }
        };

        SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
        SocializeAccess.setEntityUtilsProxy(mockEntityUtils);

        final CountDownLatch latch = new CountDownLatch(1);

        final OnActionBarReloadListener reloadListener = new OnActionBarReloadListener() {
            @Override
            public void onReload(Entity entity) {
                TestUtils.addResult(0, entity);
                latch.countDown();
            }
        };

        final String name = "foobar111";

        this.runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                actionBar.setEntity(Entity.newInstance(name, name));
                actionBar.refresh(reloadListener);
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        Entity result = TestUtils.getResult(0);
        assertNotNull(result);
        assertEquals(name, result.getKey());
    }

    public void testActionBarListenerOnCreateIsCalled() {
        Activity activity = getActivity();
        ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
        verify(listener).onCreate(actionBar);
    }

    public void testActionBarActionListenerShare() throws Throwable {

        Activity activity = getActivity();
        final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
        final ActionBarLayoutView actionBarView = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);

        SocializeShareUtils shareUtils = mock(SocializeShareUtils.class);

        SocializeAccess.setShareUtilsProxy(shareUtils);

        OnActionBarEventListener listener = mock(OnActionBarEventListener.class);

        when(listener.onClick(actionBar, OnActionBarEventListener.ActionBarEvent.SHARE)).thenReturn(false);

        actionBar.setOnActionBarEventListener(listener);

        final CountDownLatch latch = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue(actionBarView.getShareButton().performClick());
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        verify(listener).onClick(actionBar, OnActionBarEventListener.ActionBarEvent.SHARE);
    }

    public void testActionBarActionListenerLike() throws Throwable {

        Activity activity = getActivity();
        final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
        final ActionBarLayoutView actionBarView = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);

        SocializeLikeUtils likeUtils = mock(SocializeLikeUtils.class);

        SocializeAccess.setLikeUtilsProxy(likeUtils);

        OnActionBarEventListener listener = mock(OnActionBarEventListener.class);

        actionBar.setOnActionBarEventListener(listener);

        final CountDownLatch latch = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue(actionBarView.getLikeButton().performClick());
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        verify(listener).onClick(actionBar, OnActionBarEventListener.ActionBarEvent.LIKE);
    }

    public void testActionBarActionListenerComment() throws Throwable {

        Activity activity = getActivity();
        final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
        final ActionBarLayoutView actionBarView = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);

        SocializeCommentUtils commentUtils = mock(SocializeCommentUtils.class);

        SocializeAccess.setCommentUtilsProxy(commentUtils);

        OnActionBarEventListener listener = mock(OnActionBarEventListener.class);

        actionBar.setOnActionBarEventListener(listener);

        final CountDownLatch latch = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue(actionBarView.getCommentButton().performClick());
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        verify(listener).onClick(actionBar, OnActionBarEventListener.ActionBarEvent.COMMENT);
    }

    protected final void waitForActionBarLoad() throws InterruptedException {
        // Make sure the activity has launched
        this.getActivity();
        this.getInstrumentation().waitForIdleSync();

        try {
            assertTrue("Timeout waiting for action bar to load", globalLatch.await(20, TimeUnit.SECONDS));
        }
        catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
    }
}
