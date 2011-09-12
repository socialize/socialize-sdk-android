package com.socialize.ui.test.comment;

import com.socialize.ui.comment.CommentContentView;
import com.socialize.ui.comment.CommentContentViewFactory;
import com.socialize.ui.test.SocializeUITest;

public class CommentContentViewFactoryTest extends SocializeUITest {
	
	public void testMake() {
		// Just tests for runtime failures
		CommentContentViewFactory factory = new CommentContentViewFactory();
		assertTrue((factory.make(getContext()) instanceof CommentContentView));
	}
	
}
