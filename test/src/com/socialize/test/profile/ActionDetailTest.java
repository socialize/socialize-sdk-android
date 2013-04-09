package com.socialize.test.profile;

import android.app.Activity;
import android.content.Context;
import com.socialize.SocializeAccess;
import com.socialize.UserUtils;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.activity.SocializeActionUtils;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.api.action.user.SocializeUserUtils;
import com.socialize.entity.*;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.ui.action.ActionDetailActivity;
import com.socialize.ui.action.ActionDetailContentView;
import com.socialize.ui.action.ActionDetailLayoutView;
import com.socialize.ui.action.OnActionDetailViewListener;
import com.socialize.ui.profile.activity.UserActivityListItem;
import com.socialize.ui.profile.activity.UserActivityView;
import com.socialize.ui.view.LoadingItemView;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ActionDetailTest extends SocializeActivityTest {


	public void testActionDetailViewDisplaysCorrectInformation() throws InterruptedException {

		String comment0 = "Test Comment0";
		String comment1 = "Test Comment1";
		String key = "foo";
		String val = "bar";

		final Comment action = new Comment();
		Entity entity = Entity.newInstance(key, val);
		action.setId(1L);
		action.setText(comment0);
		action.setEntity(entity);

		List<SocializeAction> actions = new LinkedList<SocializeAction>();

		final Comment comment = new Comment();
		action.setId(2L);
		action.setText(comment1);
		action.setEntity(entity);

		Share share = new Share();
		share.setEntity(entity);
		share.setId(3L);
		share.setShareType(ShareType.FACEBOOK);

		Like like = new Like();
		like.setEntity(entity);
		like.setId(4L);

		actions.add(comment);
		actions.add(share);
		actions.add(like);

		final ListResult<SocializeAction> result = new ListResult<SocializeAction>(actions);

		final User user = new User();
		user.setId(1L);
		user.setFirstName("foobar name");

		SocializeActionUtils actionUtilsProxy = new SocializeActionUtils() {
			@Override
			public void getActionsByUser(Activity context, long userId, int start, int end, ActionListListener listener) {
				listener.onList(result);
			}
		};

		SocializeCommentUtils commentUtilsProxy = new SocializeCommentUtils(){
			@Override
			public void getComment(Activity context, long id, CommentGetListener listener) {
				listener.onGet(action);
			}
		};

		SocializeUserUtils userUtilsProxy = new SocializeUserUtils() {

			@Override
			public void getUser(Context context, long id, UserGetListener listener) {
				listener.onGet(user);
			}

			@Override
			public void showUserProfileView(Activity context, User user, SocializeAction action, OnActionDetailViewListener onActionDetailViewListener) {
				SocializeAccess.originalUserUtilsProxy.showUserProfileView(context, user, action, onActionDetailViewListener);
			}
		};

		SocializeAccess.setActionUtilsProxy(actionUtilsProxy);
		SocializeAccess.setUserUtilsProxy(userUtilsProxy);
		SocializeAccess.setCommentUtilsProxy(commentUtilsProxy);

		TestUtils.setUpActivityMonitor(ActionDetailActivity.class);

		final CountDownLatch latch = new CountDownLatch(1);

		UserUtils.showUserProfileWithAction(TestUtils.getActivity(this), user, action, new OnActionDetailViewListener() {

			@Override
			public void onCreate(ActionDetailLayoutView view) {}

			@Override
			public void onRender(ActionDetailLayoutView view) {
				addResult(0, view);
				latch.countDown();
			}

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});

		TestUtils.waitForActivity(10000);

		assertTrue(latch.await(20, TimeUnit.SECONDS));

		ActionDetailLayoutView view = getResult(0);

		SocializeAction currentAction = view.getCurrentAction();
		ActionDetailContentView content = view.getContent();

		assertNotNull(currentAction);
		assertNotNull(content);

		assertEquals(action.getId(), currentAction.getId());
		UserActivityView userActivityView = content.getUserActivityView();

		assertNotNull(userActivityView);

		LoadingItemView<UserActivityListItem> itemView = userActivityView.getItemView();

		assertNotNull(itemView);

		List<UserActivityListItem> items = itemView.getItems();

		assertNotNull(items);
		assertEquals(3, items.size());
	}
}
