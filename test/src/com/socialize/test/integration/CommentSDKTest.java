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
package com.socialize.test.integration;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 */
public class CommentSDKTest extends SDKIntegrationTest {

	public void testGetComment() throws Throwable{
		
		// get the comment based on ID from the JSON file
		JSONObject json = TestUtils.getJSON(getContext(), "comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		
		final String id = jsonObject.getString("id");
		String commentString = jsonObject.getString("text"); 
		JSONObject userObject = jsonObject.getJSONObject("user");
		String commentUserId = userObject.getString("id");
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().init(context);
				Socialize.getSocialize().authenticateSynchronous(context);
				Socialize.getSocialize().getCommentById(Integer.parseInt(id), new CommentGetListener() {
					
					@Override
					public void onGet(Comment entity) {
						addResult(0, entity);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});	
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Comment comment = getResult(0);
		assertNotNull(comment);
		assertEquals(id, String.valueOf(comment.getId()));
		assertEquals(commentString, comment.getText());
		assertEquals(commentUserId, String.valueOf(comment.getUser().getId()));
	}
	
	public void testCreateComment() throws Throwable {
		final String text = "Test Comment (你好世界)";
		final Entity e = Entity.newInstance("testCreateComment", "testCreateComment");
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().addComment(getContext(), e, text, new CommentAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCreate(Comment entity) {
						addResult(0, entity);
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Comment comment = getResult(0);
		assertNotNull(comment);
		assertEquals(text, comment.getText());
	}
	
	public void testListComment() throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().init(context);
				Socialize.getSocialize().authenticateSynchronous(context);
				
				Socialize.getSocialize().listCommentsByEntity(DEFAULT_GET_ENTITY, new CommentListListener() {
					
					@Override
					public void onList(ListResult<Comment> entities) {
						addResult(0, entities);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);	
		
		ListResult<Comment> result = getResult(0);
		assertNotNull(result);
		
		List<Comment> comments = result.getItems();
		
		assertNotNull(comments);
		
		assertTrue( result.getTotalCount() >= NUM_COMMENTS);
		assertTrue( comments.size() >= NUM_COMMENTS );
		assertEquals(comments.size(), result.getTotalCount());
	}
	
	public void testListCommentWithPagination() throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().init(context);
				Socialize.getSocialize().authenticateSynchronous(context);
				
				Socialize.getSocialize().listCommentsByEntity(DEFAULT_GET_ENTITY, 5, 10, new CommentListListener() {
					
					@Override
					public void onList(ListResult<Comment> entities) {
						addResult(0, entities);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);	
		
		ListResult<Comment> result = getResult(0);
		assertNotNull(result);
		
		List<Comment> comments = result.getItems();
		
		assertNotNull(comments);
		
		assertEquals( 5, comments.size());
		assertTrue( result.getTotalCount() >= NUM_COMMENTS);
	}
	
	public void testListCommentByUser() throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		JSONObject json = TestUtils.getJSON(context, "comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		
		JSONObject userObject = jsonObject.getJSONObject("user");
		final String commentUserId = userObject.getString("id");	
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().init(context);
				Socialize.getSocialize().authenticateSynchronous(context);
				
				Socialize.getSocialize().listCommentsByUser(Integer.parseInt(commentUserId), new CommentListListener() {
					
					@Override
					public void onList(ListResult<Comment> entities) {
						addResult(0, entities);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);	
		
		ListResult<Comment> result = getResult(0);
		assertNotNull(result);
		
		List<Comment> comments = result.getItems();
		
		assertNotNull(comments);
		
		assertEquals( NUM_COMMENTS, result.getTotalCount());
		assertEquals( NUM_COMMENTS, comments.size());
	}
}
