package com.socialize.test;

import android.test.AndroidTestCase;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.comment.CommentService;
import com.socialize.entity.factory.FactoryService;
import com.socialize.provider.comment.CommentProvider;

public class SampleTest extends AndroidTestCase {

	@UsesMocks ({CommentService.class, CommentProvider.class, FactoryService.class})
	public void testSample() {
		FactoryService s = AndroidMock.createMock(FactoryService.class, getContext());
		CommentProvider p = AndroidMock.createMock(CommentProvider.class, s);
		CommentService cs = AndroidMock.createMock(CommentService.class, p);
		cs.addComment("test", "testcomment");
	}
	
}
