package com.socialize.test.ui.comment;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.socialize.ConfigUtils;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentSystem;
import com.socialize.api.action.comment.SubscriptionSystem;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.subscription.SubscriptionListener;
import com.socialize.notifications.NotificationType;
import com.socialize.test.mock.MockCommentSystem;
import com.socialize.test.mock.MockSubscriptionSystem;
import com.socialize.test.ui.SocializeUIRobotiumTest;
import com.socialize.test.ui.util.ExitSleep;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentDetailActivity;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentEntryView;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.LoadingListView;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.AppUtils;
import com.socialize.util.DefaultAppUtils;

public class CommentUITest extends SocializeUIRobotiumTest {
	
	@Override
	protected void startWithFacebook(boolean sso) {
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		super.startWithFacebook(sso);
		showComments();
		TestUtils.waitForActivity(5000);
		TestUtils.waitForIdleSync();
	}

	@Override
	protected void startWithoutFacebook() {
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		super.startWithoutFacebook();
		toggleNotificationsEnabled(true);
		toggleLocationEnabled(true);
		showComments();

		TestUtils.waitForActivity(5000);
		TestUtils.waitForIdleSync();
	}
	
	public void testCommentAddWithoutFacebook() throws Throwable {
		
		final String txtComment = "UI Integration Test Comment #" + Math.random();

		startWithoutFacebook();
		
		Activity lastActivity = TestUtils.getLastActivity();
		
		final ListView comments = TestUtils.findViewById(lastActivity, LoadingListView.LIST_VIEW_ID, 10000);
		
		assertNotNull(comments);
		
		// Wait for load to complete
		TestUtils.sleepUntil(5000, new ExitSleep() {
			@Override
			public boolean isExit() {
				return comments.getCount() > 0;
			}
		});
		
		final int count = comments.getCount();
		
		final CommentEditField commentEditField = TestUtils.findView(lastActivity, CommentEditField.class, 10000);	

		assertNotNull(commentEditField);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(commentEditField.performClick());
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		final CommentEntryView commentEntryView = TestUtils.findView(lastActivity, CommentEntryView.class, 10000);	
		final SocializeButton btnPost = TestUtils.findViewWithText(commentEntryView, SocializeButton.class, "Post Comment", 10000);
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				commentEntryView.getCommentField().setText(txtComment);
				assertTrue(btnPost.performClick());
				latch2.countDown();
			}
		});
		
		assertTrue(latch2.await(30, TimeUnit.SECONDS));		
		
		// Wait for view to refresh
		TestUtils.sleepUntil(5000, new ExitSleep() {
			@Override
			public boolean isExit() {
				return comments.getCount() > count;
			}
		});
	
		assertEquals( count+1, comments.getCount());
		
		Comment item = (Comment) comments.getItemAtPosition(0);
		
		assertNotNull(item);
		
		String comment = item.getText();
		assertEquals(txtComment, comment);
		
	}
	
	public void testNotificationSubscribe() throws Throwable {
		doSubscribeUnsubscribeTest(false);
	}

	public void testNotificationUnSubscribe() throws Throwable {
		doSubscribeUnsubscribeTest(true);
	}
	
	protected void doSubscribeUnsubscribeTest(final boolean isSubscribed) throws Throwable {
		
		final MockSubscriptionSystem mockSystem = new MockSubscriptionSystem() {
			@Override
			public void getSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
				Subscription sub = new Subscription();
				sub.setNotificationType(NotificationType.NEW_COMMENTS);
				sub.setSubscribed(isSubscribed);
				
				ListResult<Subscription> subs = new ListResult<Subscription>();
				subs.setItems(Arrays.asList(new Subscription[]{sub}));
				
				listener.onList(subs);
			}
			
			@Override
			public void removeSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
				Subscription sub = new Subscription();
				sub.setSubscribed(false);
				listener.onCreate(sub);
				
				if(isSubscribed) {
					addResult(0, "success");
				}
				else {
					addResult(0, "fail");
				}
			}

			@Override
			public void addSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
				Subscription sub = new Subscription();
				sub.setSubscribed(true);
				listener.onCreate(sub);
				
				if(!isSubscribed) {
					addResult(0, "success");
				}
				else {
					addResult(0, "fail");
				}
			}
		};
		
		final AppUtils appUtils = new DefaultAppUtils() {
			@Override
			public boolean isNotificationsAvailable(Context context) {
				return true;
			}
		};
		
		final CommentSystem commentSystem = new MockCommentSystem() {

			@Override
			public void getCommentsByEntity(SocializeSession session, String entityKey, CommentListener listener) {
				listener.onList(new ListResult<Comment>());
			}

			@Override
			public void getCommentsByEntity(SocializeSession session, String entityKey, int startIndex, int endIndex, CommentListener listener) {
				listener.onList(new ListResult<Comment>());
			}
			
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				ActivityInstrumentationTestCase2.fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				ProxyObject<SubscriptionSystem> proxy = container.getProxy("subscriptionSystem");
				if(proxy != null) {
					proxy.setDelegate(mockSystem);
				}
				else {
					System.err.println("SubscriptionSystem Proxy is null!!");
				}
				
				ProxyObject<AppUtils> appUtilsProxy = container.getProxy("appUtils");
				if(proxy != null) {
					appUtilsProxy.setDelegate(appUtils);
				}
				else {
					System.err.println("AppUtils Proxy is null!!");
				}	
				
				ProxyObject<CommentSystem> commentSystemProxy = container.getProxy("commentSystem");
				if(proxy != null) {
					commentSystemProxy.setDelegate(commentSystem);
				}
				else {
					System.err.println("commentSystem Proxy is null!!");
				}					
			}
		});		
		
		toggleMockedFacebook(true);
		toggleMockedSocialize(true);
		
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		
		super.startWithoutFacebook();
		
		showComments();
		
		TestUtils.waitForActivity(5000);
		
		Activity lastActivity = TestUtils.getLastActivity();

		final CountDownLatch latch = new CountDownLatch(1);
		final CustomCheckbox chk = TestUtils.findCheckboxWithImageName(lastActivity, "icon_notify.png#large", 10000);
		
		assertNotNull(chk);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				assertTrue(chk.performClick());
				sleep(1000);
				latch.countDown();
			}
		});

		latch.await(10, TimeUnit.SECONDS);
		
		String valueAfter = getResult(0);
		assertNotNull(valueAfter);
		assertEquals("success", valueAfter);
	}	
	
	public void testCommentListAndView() {
		
		final int pageSize = 20;
		
		TestUtils.setUpActivityMonitor(CommentDetailActivity.class);

		ConfigUtils.getConfig(TestUtils.getActivity(this)).setProperty("comment.page.size", String.valueOf(pageSize));
		
		startWithoutFacebook();
		
		Activity lastActivity = TestUtils.getLastActivity(5000);
		
		assertNotNull(lastActivity);
		
		final ListView comments = (ListView) TestUtils.findViewById(lastActivity, LoadingListView.LIST_VIEW_ID, 10000);
		
		assertNotNull(comments);
		
		TestUtils.sleepUntil(10000, new ExitSleep() {
			@Override
			public boolean isExit() {
				return comments.getCount() >= pageSize;
			}
		});
		
		assertTrue("Unexpected number of comments.  Expected >= " +
				pageSize +
				" but found " +
				comments.getCount(), comments.getCount() >= pageSize);
		
		// Click on the first comment in list.
		View view = TestUtils.clickInList(lastActivity, 0, comments);
		
		assertNotNull(view);
		
		TestUtils.waitForActivity(10000);
		
		// Make sure we have user name, comment, image and location
		TextView userDisplayName = TestUtils.findViewAt(view, TextView.class, 0);
		TextView comment = TestUtils.findViewAt(view, TextView.class,1);
		TextView date_location = TestUtils.findViewAt(view, TextView.class,2);
		ImageView userProfilePic = TestUtils.findViewAt(view, ImageView.class,0);
		
		assertNotNull(userDisplayName);
		assertNotNull(comment);
		assertNotNull(userProfilePic);
		assertNotNull(date_location);
		
		String name = userDisplayName.getText().toString();
		String commentText = userDisplayName.getText().toString();
		String location = date_location.getText().toString();
		
		Drawable drawable = userProfilePic.getDrawable();
		
		assertNotNull(name);
		assertTrue(name.trim().length() > 0);
		
		assertNotNull(commentText);
		assertTrue(commentText.trim().length() > 0);
		
		assertNotNull(location);
		assertTrue(location.trim().length() > 0);
		
		assertNotNull(drawable);
	}
}
