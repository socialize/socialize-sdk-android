package com.socialize.ui.test;

import java.util.List;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Comment;
import com.socialize.ui.comment.CommentAdapter;

public class CommentAdapterTest extends SocializeUITest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testIsDisplayLoadingFalse() {
		
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(0);
		
		CommentAdapter adapter = new CommentAdapter(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertFalse(adapter.isDisplayLoading());
		
		AndroidMock.verify(comments);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testIsDisplayLoadingTrue() {
		
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(1);
		
		CommentAdapter adapter = new CommentAdapter(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertTrue(adapter.isDisplayLoading());
		
		AndroidMock.verify(comments);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetCountWithLoading() {
		int count = 10;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(count);
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertEquals(count+1, adapter.getCount());
		
		AndroidMock.verify(comments);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetCountWithoutLoading() {
		int count = 10;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(count);
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public boolean isDisplayLoading() {
				return false;
			}
		};
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertEquals(count, adapter.getCount());
		
		AndroidMock.verify(comments);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetItemWithinSize() {
		Comment item = new Comment();
		int position = 69;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.get(position)).andReturn(item);
		AndroidMock.expect(comments.size()).andReturn(position+1);
		
		CommentAdapter adapter = new CommentAdapter(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertSame(item, adapter.getItem(position));
		
		AndroidMock.verify(comments);
		
		
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetItemOutsideSize() {
		int position = 69;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(position);
		
		CommentAdapter adapter = new CommentAdapter(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertNull(adapter.getItem(position));
		
		AndroidMock.verify(comments);
		
		
	}
}
