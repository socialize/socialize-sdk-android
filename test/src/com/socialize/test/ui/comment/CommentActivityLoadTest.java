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
package com.socialize.test.ui.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import com.socialize.CommentUtils;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentSystem;
import com.socialize.api.action.comment.SocializeCommentSystem;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.OnCommentViewActionListener;

/**
 * @author Jason Polites
 *
 */
public class CommentActivityLoadTest extends SocializeActivityTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown(this);
		super.tearDown();
	}

	public void testCommentActivityLoadsCorrectData() throws Throwable {
		
		TestUtils.setupSocializeProxies();
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		
		Entity entity1 = Entity.newInstance("http://entity1.com", null);
		Entity entity2 = Entity.newInstance("http://entity2.com", null);
		
		final List<Comment> results0 = new ArrayList<Comment>();
		final List<Comment> results1 = new ArrayList<Comment>();
		final CountDownLatch lock = new CountDownLatch(1);
		
		List<Comment> dummyResults = new ArrayList<Comment>();
		List<Comment> dummyResults2 = new ArrayList<Comment>();
		
		Comment c = new Comment();
		c.setId(0L);
		
		Comment c0 = new Comment();
		Comment c1 = new Comment();
		Comment c2 = new Comment();
		
		Comment c20 = new Comment();
		Comment c21 = new Comment();
		Comment c22 = new Comment();
		
		c0.setText("comment0");
		c1.setText("comment1");
		c2.setText("comment2");
		
		c20.setText("comment20");
		c21.setText("comment21");
		c22.setText("comment22");
		
		dummyResults.add(c0);
		dummyResults.add(c1);
		dummyResults.add(c2);
		
		dummyResults2.add(c20);
		dummyResults2.add(c21);
		dummyResults2.add(c22);		
		
		final ListResult<Comment> lr = new ListResult<Comment>();
		lr.setItems(dummyResults);
		lr.setTotalCount(3);
		
		final SocializeCommentSystem mockCommentSystem = new SocializeCommentSystem(null) {
			@Override
			public void getCommentsByEntity(SocializeSession session, String key, int startIndex, int endIndex, CommentListener listener) {
				listener.onList(lr);
			}
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
				ProxyObject<CommentSystem> proxy = container.getProxy("commentSystem");
				proxy.setDelegate(mockCommentSystem);
			}
		});
		
		CommentUtils.showCommentView(TestUtils.getActivity(this), entity1, new OnCommentViewActionListener() {
			public void onError(SocializeException error) {
				error.printStackTrace();
				lock.countDown();
			}
			public void onReload(CommentListView view) {}
			public void onPostComment(Comment comment) {}
			public void onCreate(CommentListView view) {}
			public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {}
			public void onRender(CommentListView view) {
				results0.addAll(view.getCommentAdapter().getComments());
				lock.countDown();
			}
		});
		
				
		Activity waitForActivity = TestUtils.waitForActivity(20000);
		
		assertNotNull(waitForActivity);
		
		assertTrue(lock.await(20, TimeUnit.SECONDS));
		
		assertEquals(dummyResults.size(), results0.size());
		
		// Now, hit the back button on the activity, and re-orchestrate
		sendKeys(KeyEvent.KEYCODE_BACK);
		
		final CountDownLatch lock2 = new CountDownLatch(1);
		
		lr.setItems(dummyResults2);
		
		CommentUtils.showCommentView(TestUtils.getActivity(this), entity2, new OnCommentViewActionListener() {
			public void onError(SocializeException error) {
				error.printStackTrace();
				lock2.countDown();
			}
			public void onReload(CommentListView view) {}
			public void onPostComment(Comment comment) {}
			public void onCreate(CommentListView view) {}
			public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {}
			public void onRender(CommentListView view) {
				
				List<Comment> comments = view.getCommentAdapter().getComments();
				
				if(comments != null) {
					results1.addAll(view.getCommentAdapter().getComments());
				}
				
				lock2.countDown();
			}
		});	
		
		waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		
		lock2.await(20, TimeUnit.SECONDS);
		
		assertEquals(dummyResults2.size(), results1.size());	
		
		int index = 0;
		for (Comment comment : results1) {
			assertEquals(dummyResults2.get(index).getText(), comment.getText());
			index++;
		}
		
		waitForActivity.finish();
	}
}
