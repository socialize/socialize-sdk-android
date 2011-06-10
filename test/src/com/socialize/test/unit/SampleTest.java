package com.socialize.test.unit;

import android.test.AndroidTestCase;

public class SampleTest extends AndroidTestCase {

//	@UsesMocks ({
//		CommentService.class, 
//		CommentProvider.class, 
//		FactoryService.class,
//		SocializeSession.class,
//		SocializeObjectFactory.class})
//	@SuppressWarnings("unchecked")
//	public void testSample() throws SocializeApiError {
//		FactoryService mockFactoryService = AndroidMock.createMock(FactoryService.class, getContext());
//
//		SocializeObjectFactory<Comment> mockCommentFactory = AndroidMock.createMock(SocializeObjectFactory.class, mockFactoryService);
//		
//		CommentProvider mockCommentProvider = AndroidMock.createMock(CommentProvider.class, mockCommentFactory);
//		CommentService mockCommentService = AndroidMock.createMock(CommentService.class, mockCommentProvider);
//		SocializeSession mockSession = AndroidMock.createMock(SocializeSession.class);
//		
//		String key = "foo";
//		String secret = "bar";
//		String uuid = "rubbish";
//		
//		AndroidMock.expect(mockCommentService.authenticate(key, secret, uuid)).andReturn(mockSession);
//		
//		AndroidMock.replay(mockCommentService);
//		
//		SocializeSession session = mockCommentService.authenticate(key, secret, uuid);
//		
//		mockCommentService.addComment(session, "test", "testcomment");
//		
//		AndroidMock.verify(mockCommentService);
//	}
	
}
