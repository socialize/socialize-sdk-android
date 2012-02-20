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

import android.test.AndroidTestCase;

public class SampleTest extends AndroidTestCase {

	// @UsesMocks ({
	// CommentService.class,
	// CommentProvider.class,
	// FactoryService.class,
	// SocializeSession.class,
	// SocializeObjectFactory.class})
	// @SuppressWarnings("unchecked")
	// public void testSample() throws SocializeApiError {
	// FactoryService mockFactoryService =
	// AndroidMock.createMock(FactoryService.class, getContext());
	//
	// SocializeObjectFactory<Comment> mockCommentFactory =
	// AndroidMock.createMock(SocializeObjectFactory.class, mockFactoryService);
	//
	// CommentProvider mockCommentProvider =
	// AndroidMock.createMock(CommentProvider.class, mockCommentFactory);
	// CommentService mockCommentService =
	// AndroidMock.createMock(CommentService.class, mockCommentProvider);
	// SocializeSession mockSession =
	// AndroidMock.createMock(SocializeSession.class);
	//
	// String key = "foo";
	// String secret = "bar";
	// String uuid = "rubbish";
	//
	// AndroidMock.expect(mockCommentService.authenticate(key, secret,
	// uuid)).andReturn(mockSession);
	//
	// AndroidMock.replay(mockCommentService);
	//
	// SocializeSession session = mockCommentService.authenticate(key, secret,
	// uuid);
	//
	// mockCommentService.addComment(session, "test", "testcomment");
	//
	// AndroidMock.verify(mockCommentService);
	// }

}
