package com.socialize.test.comment.unit;

import com.socialize.entity.Comment;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.comment.CommentAdapter;
import org.mockito.Mockito;

import java.util.List;

public class CommentAdapterTest extends SocializeUnitTest {

	@SuppressWarnings("unchecked")
	public void testIsDisplayLoadingFalse() {
		
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(0);
		
		CommentAdapter adapter = new CommentAdapter();
		adapter.init(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);

		assertFalse(adapter.isDisplayLoading());
	}
	
	@SuppressWarnings("unchecked")
	public void testIsDisplayLoadingTrue() {
		
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(1);
		
		CommentAdapter adapter = new CommentAdapter();
		adapter.init(getContext());
		
		adapter.setLast(false);
		adapter.setComments(comments);
		
		assertTrue(adapter.isDisplayLoading());
	}
	
	@SuppressWarnings("unchecked")
	public void testGetCountWithLoading() {
		int count = 10;
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(count);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};

		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);
		adapter.notifyDataSetChanged();
		
		assertEquals(count+1, adapter.getCount());
		
	}
	
	@SuppressWarnings("unchecked")
	public void testGetCountWithoutLoading() {
		int count = 10;
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(count);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return false;
			}
		};
		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);
		adapter.notifyDataSetChanged();
		
		assertEquals(count, adapter.getCount());
	}
	
	@SuppressWarnings("unchecked")
	public void testGetItemWithinSize() {
		Comment item = new Comment();
		int position = 69;
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.get(position)).thenReturn(item);
		Mockito.when(comments.size()).thenReturn(position+1);
		
		CommentAdapter adapter = new CommentAdapter();
		
		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);

		assertSame(item, adapter.getItem(position));

	}
	
	@SuppressWarnings("unchecked")
	public void testGetItemOutsideSize() {
		int position = 69;
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(position);
		
		CommentAdapter adapter = new CommentAdapter();
		
		adapter.init(getContext());
		adapter.setLast(false);
		adapter.setComments(comments);
		
		assertNull(adapter.getItem(position));
	}
	
	public void testGetItemId() {
		
		final int position = 0;
		final Comment comment = Mockito.mock(Comment.class);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public Object getItem(int position) {
				return comment;
			}
		};
		
		adapter.init(getContext());
		
		assertEquals(position, adapter.getItemId(position));
	}
	
	@SuppressWarnings("unchecked")
	public void testGetItemViewTypeForNormal() {
		
		int position = 69;
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(position+1);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		
		adapter.init(getContext());
		adapter.setComments(comments);

		assertEquals(0, adapter.getItemViewType(position));
	}
	
	@SuppressWarnings("unchecked")
	public void testGetItemViewTypeForLoading() {
		
		int position = 69;
		List<Comment> comments = Mockito.mock(List.class);
		
		Mockito.when(comments.size()).thenReturn(position);
		
		CommentAdapter adapter = new CommentAdapter() {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		adapter.init(getContext());
		
		adapter.setComments(comments);
		
		assertEquals(1, adapter.getItemViewType(position));
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
