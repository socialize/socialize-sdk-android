package com.socialize.ui.test.comment;

import com.socialize.ui.comment.CommentContentView;
import com.socialize.ui.comment.CommentContentViewFactory;
import com.socialize.ui.test.SocializeUITestCase;

public class CommentContentViewFactoryTest extends SocializeUITestCase {
	
	public void testMake() {
		// Just tests for runtime failures
		CommentContentViewFactory factory = new CommentContentViewFactory();
		assertTrue((factory.make(getContext()) instanceof CommentContentView));
	}
	
}
