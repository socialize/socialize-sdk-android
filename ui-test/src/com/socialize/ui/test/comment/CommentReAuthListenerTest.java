package com.socialize.ui.test.comment;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.error.SocializeException;
import com.socialize.ui.comment.CommentButtonCallback;
import com.socialize.ui.comment.CommentReAuthListener;
import com.socialize.ui.test.SocializeUITest;

@UsesMocks ({CommentButtonCallback.class})
public class CommentReAuthListenerTest extends SocializeUITest {

	CommentButtonCallback callback;
	CommentReAuthListener listener;
	String comment = "foobar";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		callback = AndroidMock.createMock(CommentButtonCallback.class);
		listener = new CommentReAuthListener(getContext(), callback, comment);
	}

	@UsesMocks (SocializeException.class)
	public void testOnError() {
		String errorMessage = "foobar_message";
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		AndroidMock.expect(error.getMessage()).andReturn(errorMessage);
		callback.onError(getContext(), errorMessage);
		
		AndroidMock.replay(error);
		AndroidMock.replay(callback);
		
		listener.onError(error);
		
		AndroidMock.verify(error);
		AndroidMock.verify(callback);
	}
	
	public void testOnAuthSuccess() {
		
		callback.onComment(comment);
		
		AndroidMock.replay(callback);
		
		listener.onAuthSuccess(null);

		AndroidMock.verify(callback);
	}
	
	public void testOnAuthFail() {
		String errorMessage = "foobar_message";
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		AndroidMock.expect(error.getMessage()).andReturn(errorMessage);
		callback.onError(getContext(), errorMessage);
		
		AndroidMock.replay(error);
		AndroidMock.replay(callback);
		
		listener.onAuthFail(error);
		
		AndroidMock.verify(error);
		AndroidMock.verify(callback);
	}
}
