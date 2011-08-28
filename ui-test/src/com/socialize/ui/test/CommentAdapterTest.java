package com.socialize.ui.test;

import java.util.List;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Comment;
import com.socialize.ui.comment.CommentAdapter;

public class CommentAdapterTest extends SocializeUITest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testIsDisplayLoading() {
		
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(0);
		
		CommentAdapter adapter = new CommentAdapter(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertFalse(adapter.isDisplayLoading());
		
		AndroidMock.verify(comments);
	}
	
}
