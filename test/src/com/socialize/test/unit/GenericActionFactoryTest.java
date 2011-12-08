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

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Comment;
import com.socialize.entity.factory.CommentFactory;
import com.socialize.entity.factory.GenericActionFactory;
import com.socialize.entity.factory.LikeFactory;
import com.socialize.entity.factory.ShareFactory;
import com.socialize.entity.factory.SocializeActionFactory;
import com.socialize.entity.factory.ViewFactory;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
public class GenericActionFactoryTest extends SocializeUnitTest {
	
	@UsesMocks ({CommentFactory.class, Comment.class})
	public void testInstantiateObjectComment() throws JSONException {
		CommentFactory commentFactory = AndroidMock.createMock(CommentFactory.class);
		GenericActionFactory factory = new GenericActionFactory();
		factory.setCommentFactory(commentFactory);		
		doIt("comment", factory, commentFactory);
	}
	
	@UsesMocks ({ViewFactory.class})
	public void testInstantiateObjectView() throws JSONException {
		ViewFactory objectFactory = AndroidMock.createMock(ViewFactory.class);
		GenericActionFactory factory = new GenericActionFactory();
		factory.setViewFactory(objectFactory);
		doIt("view", factory, objectFactory);
	}
	
	@UsesMocks ({LikeFactory.class})
	public void testInstantiateObjectLike() throws JSONException {
		LikeFactory objectFactory = AndroidMock.createMock(LikeFactory.class);
		GenericActionFactory factory = new GenericActionFactory();
		factory.setLikeFactory(objectFactory);
		doIt("like", factory, objectFactory);
	}
	
	@UsesMocks ({ShareFactory.class})
	public void testInstantiateObjectShare() throws JSONException {
		ShareFactory objectFactory = AndroidMock.createMock(ShareFactory.class);
		GenericActionFactory factory = new GenericActionFactory();
		factory.setShareFactory(objectFactory);
		doIt("share", factory, objectFactory);
	}
	
	private void doIt(String type, GenericActionFactory factory, SocializeActionFactory<?> objectFactory) throws JSONException {
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		
		String key = "activity_type";
		
		AndroidMock.expect(objectFactory.instantiateObject((JSONObject)AndroidMock.anyObject())).andReturn(null);
		AndroidMock.expect(json.has(key)).andReturn(true);
		AndroidMock.expect(json.isNull(key)).andReturn(false);
		AndroidMock.expect(json.getString(key)).andReturn(type);
		
		AndroidMock.replay(json);
		AndroidMock.replay(objectFactory);
		
		factory.instantiateObject(json);
		
		AndroidMock.verify(json);
		AndroidMock.verify(objectFactory);
	}
}
