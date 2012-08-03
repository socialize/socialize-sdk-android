package com.socialize.test.ui.comment;

import java.util.List;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Comment;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.comment.CommentAdapter;

public class CommentAdapterTest extends SocializeActivityTest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testIsDisplayLoadingFalse() {
		
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(0);
		
		CommentAdapter adapter = new CommentAdapter();
		adapter.init(getContext());
		
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
		
		CommentAdapter adapter = new CommentAdapter();
		adapter.init(getContext());
		
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
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		adapter.notifyDataSetChanged();
		
		assertEquals(count+1, adapter.getCount());
		
		AndroidMock.verify(comments);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetCountWithoutLoading() {
		int count = 10;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(count);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return false;
			}
		};
		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		adapter.notifyDataSetChanged();
		
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
		
		CommentAdapter adapter = new CommentAdapter();
		
		adapter.init(getContext());
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
		
		CommentAdapter adapter = new CommentAdapter();
		
		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertNull(adapter.getItem(position));
		
		AndroidMock.verify(comments);
	}
	
	@UsesMocks (Comment.class)
	public void testGetItemId() {
		
//		final int id = 69;
		final int position = 0;
		final Comment comment = AndroidMock.createMock(Comment.class);
		
//		AndroidMock.expect(comment.getId()).andReturn(id);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public Object getItem(int position) {
				return comment;
			}
		};
		
		adapter.init(getContext());
		
		AndroidMock.replay(comment);
		
		assertEquals(position, adapter.getItemId(position));
		
		AndroidMock.verify(comment);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetItemViewTypeForNormal() {
		
		int position = 69;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(position+1);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		
		adapter.init(getContext());
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertEquals(0, adapter.getItemViewType(position));
		
		AndroidMock.verify(comments);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetItemViewTypeForLoading() {
		
		int position = 69;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(position);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		adapter.init(getContext());
		
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertEquals(1, adapter.getItemViewType(position));
		
		AndroidMock.verify(comments);
	}
	
	public void testGetItemViewTypeCountForLoading() {
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		adapter.init(getContext());
		assertEquals(2, adapter.getViewTypeCount());
	}
	
	public void testGetItemViewTypeCountForNonLoading() {
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return false;
			}
		};
		adapter.init(getContext());
		assertEquals(1, adapter.getViewTypeCount());
	}
}
