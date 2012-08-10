/*
 * Copyright (c) 2012 Socialize Inc.
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
package com.socialize.test.integration.services.a;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import com.socialize.ActionUtils;
import com.socialize.CommentUtils;
import com.socialize.LikeUtils;
import com.socialize.UserUtils;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class ActionUtilsTest extends SocializeActivityTest {
	
	public void testGetActionsByApplication() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		
		ActionUtils.getActionsByApplication(getContext(), 0, 100, new ActionListListener() {
			@Override
			public void onList(ListResult<SocializeAction> entities) {
				addResult(0, entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<SocializeAction> after = getResult(0);
		
		assertNotNull(after);
		
		List<SocializeAction> actions = after.getItems();
		
		assertResults(actions, Comment.class, "comments.json");
		assertResults(actions, Share.class, "shares.json");
		assertResults(actions, Like.class, "likes.json");
	}
	

	public void testGetActionsByUser() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		
		User user = new User();
		
		JSONObject json = TestUtils.getJSON(getContext(), "comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		JSONObject userObject = jsonObject.getJSONObject("user");
		String userId = userObject.getString("id");			
		user.setId(Long.parseLong(userId)); // from comments.json
		
		ActionUtils.getActionsByUser(getContext(), user.getId(), 0, 100, new ActionListListener() {
			@Override
			public void onList(ListResult<SocializeAction> entities) {
				addResult(0, entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<SocializeAction> after = getResult(0);
		
		assertNotNull(after);
		
		List<SocializeAction> actions = after.getItems();
		
		assertResults(actions, Comment.class, "comments.json");
		assertResults(actions, Share.class, "shares.json");
		assertResults(actions, Like.class, "likes.json");
	}	
	
	public void testGetActionsByEntity() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		
		final List<SocializeAction> actions = new ArrayList<SocializeAction>();
		
		final Entity entityKey = Entity.newInstance("testGetActionsByEntity" + Math.random(), "testGetActionsByEntity");
		
		// Set auto auth off
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		final CommentOptions commentOptions = CommentUtils.getUserCommentOptions(getContext());
		commentOptions.setShowAuthDialog(false);
		commentOptions.setShowShareDialog(false);
		
		
		// Add some new actions to really make the test "real"
		LikeUtils.like(getContext(), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onCreate(Like like) {
				actions.add(like);
				CommentUtils.addComment(getContext(), entityKey, "Blah", commentOptions, new CommentAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
					
					@Override
					public void onCreate(Comment comment) {
						actions.add(comment);
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		ActionUtils.getActionsByEntity(getContext(), entityKey.getKey(), 0, 100, new ActionListListener() {
			@Override
			public void onList(ListResult<SocializeAction> entities) {
				addResult(0, entities);
				latch2.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		ListResult<SocializeAction> after = getResult(0);
		
		assertNotNull(after);
		
		List<SocializeAction> actionsAfter = after.getItems();

		assertEquals(2, actionsAfter.size());
	}	
		
	public void testGetActionsByUserAndEntity() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		
		final List<SocializeAction> actions = new ArrayList<SocializeAction>();
		final Entity entityKey = Entity.newInstance("testGetActionsByUserAndEntity" + Math.random(), "testGetActionsByUserAndEntity");
		
		// Set auto auth off
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		final CommentOptions commentOptions = CommentUtils.getUserCommentOptions(getContext());
		commentOptions.setShowAuthDialog(false);
		commentOptions.setShowShareDialog(false);
		
		// Add some new actions to really make the test "real"
		LikeUtils.like(getContext(), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onCreate(Like like) {
				actions.add(like);
				CommentUtils.addComment(getContext(), entityKey, "Blah", commentOptions, new CommentAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
					
					@Override
					public void onCreate(Comment comment) {
						actions.add(comment);
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		User user = UserUtils.getCurrentUser(getContext());
		
		ActionUtils.getActionsByUserAndEntity(getContext(), user.getId(), entityKey.getKey(), 0, 100, new ActionListListener() {
			@Override
			public void onList(ListResult<SocializeAction> entities) {
				addResult(0, entities);
				latch2.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		ListResult<SocializeAction> after = getResult(0);
		
		assertNotNull(after);
		
		List<SocializeAction> actionsAfter = after.getItems();

		assertEquals(2, actionsAfter.size());
	}		
	
	protected void assertResults(List<SocializeAction> actions, Class<?> type, String datafile) throws Exception {
		
		JSONObject json = TestUtils.getJSON(getContext(), datafile);
		
		JSONArray jsonArray = json.getJSONArray("items");
		
		Set<String> ids = new HashSet<String>();
		
		for (int i = 0; i < jsonArray.length(); i++) {
			ids.add(jsonArray.getJSONObject(i).getString("id"));
		}
		
		for (SocializeAction action : actions) {
			if(type.isAssignableFrom(action.getClass())) {
				ids.remove(String.valueOf(action.getId()));
			}
		}
		
		assertEquals("No all items of type [" +
				type.getSimpleName() +
				"] found in action list", ids.size(), 0);
	}
	
}
