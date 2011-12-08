/*
 * Copyright (c) 2011 Socialize Inc.
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
import android.view.KeyEvent;

import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.sample.mocks.MockSocializeApiHost;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.SocializeUI;
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
		TestUtils.tearDown();
		super.tearDown();
	}

	public void testCommentActivityLoadsCorrectData() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		
		final List<Comment> results = new ArrayList<Comment>();
		final CountDownLatch lock = new CountDownLatch(1);
		
		List<Comment> dummyResults = new ArrayList<Comment>();
		List<Comment> dummyResults2 = new ArrayList<Comment>();
		
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
		
		MockSocializeApiHost.orchestrateListResult(Comment.class, dummyResults);
		
		SocializeUI.getInstance().showCommentView(getActivity(), "http://entity1.com", new OnCommentViewActionListener() {
			public void onError(SocializeException error) {
				error.printStackTrace();
				lock.countDown();
			}
			public void onReload(CommentListView view) {}
			public void onPostComment(Comment comment) {}
			public void onCreate(CommentListView view) {}
			public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {}
			public void onRender(CommentListView view) {
				results.addAll(view.getCommentAdapter().getComments());
				lock.countDown();
			}
		});
		
		
		Activity waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		
		lock.await(60, TimeUnit.SECONDS);
		
		assertEquals(dummyResults.size(), results.size());
		
		// Now, hit the back button on the activity, and re-orchestrate
		sendKeys(KeyEvent.KEYCODE_BACK);
		results.clear();
		
		final CountDownLatch lock2 = new CountDownLatch(1);
		
		MockSocializeApiHost.orchestrateListResult(Comment.class, dummyResults2);
		
		SocializeUI.getInstance().showCommentView(getActivity(), "http://entity2.com", new OnCommentViewActionListener() {
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
					results.addAll(view.getCommentAdapter().getComments());
				}
				
				lock2.countDown();
			}
		});	
		
		waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		
		lock2.await(60, TimeUnit.SECONDS);
		
		assertEquals(dummyResults2.size(), results.size());	
		
		int index = 0;
		for (Comment comment : results) {
			assertEquals(dummyResults2.get(index).getText(), comment.getText());
			index++;
		}
		
		waitForActivity.finish();
	}
}
