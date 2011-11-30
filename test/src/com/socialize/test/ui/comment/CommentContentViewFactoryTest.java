package com.socialize.test.ui.comment;

import com.socialize.test.ui.SocializeUITestCase;
import com.socialize.ui.comment.CommentContentView;
import com.socialize.ui.comment.CommentContentViewFactory;

public class CommentContentViewFactoryTest extends SocializeUITestCase {
	
	public void testMake() {
		// Just tests for runtime failures
		CommentContentViewFactory factory = new CommentContentViewFactory();
		assertTrue((factory.make(getContext(), null) instanceof CommentContentView));
	}
	
}
