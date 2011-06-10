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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.entity.CommentApi;
import com.socialize.entity.Comment;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeServiceTest extends SocializeUnitTest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeProvider.class, CommentApi.class, DeviceUtils.class, SocializeSession.class, SocializeAuthListener.class})
	public void testAuthenticate() throws SocializeException, InterruptedException {
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String udid = "foobar";
		final String consumerKey = "foobar_consumerKey";
		final String consumerSecret = "foobar_consumerSecret";
		
		AndroidMock.expect(deviceUtils.getUDID(getContext())).andReturn(udid);
		
		commentApi.authenticate(consumerKey, consumerSecret, udid, listener);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(commentApi);
		
		SocializeService service = new SocializeService(getContext());
		
		service.setDeviceUtils(deviceUtils);
		service.setCommentApi(commentApi);
		
		service.authenticate(consumerKey, consumerSecret, listener);
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testAddComment() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		final String comment = "foobar_comment";
		
		commentApi.addComment(session, key, comment, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeService service = new SocializeService(getContext());
		
		service.setCommentApi(commentApi);
		
		service.addComment(session, key, comment, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testGetComment() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final int id = 69;
		
		commentApi.getComment(session, id, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeService service = new SocializeService(getContext());
		
		service.setCommentApi(commentApi);
		
		service.getComment(session, id, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testListCommentsByEntity() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		
		commentApi.getCommentsByEntity(session, key, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeService service = new SocializeService(getContext());
		
		service.setCommentApi(commentApi);
		
		service.listCommentsByEntity(session, key, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testListCommentsById() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final int[] ids = {1,2,3};
		
		commentApi.getCommentsById(session, listener, ids);
		
		AndroidMock.replay(commentApi);
		
		SocializeService service = new SocializeService(getContext());
		
		service.setCommentApi(commentApi);
		
		service.listCommentsById(session, listener, ids);
		
		AndroidMock.verify(commentApi);
	}
	
}
