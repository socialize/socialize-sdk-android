package com.socialize.ui.test;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.ui.comment.CommentAdapter;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.user.UserService;
import com.socialize.ui.util.TimeUtils;
import com.socialize.ui.view.ViewHolder;

public class CommentAdapterTest extends SocializeUIActivityTest {

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
	
	@UsesMocks (Comment.class)
	public void testGetItemId() {
		
		final int id = 69;
		final int position = 0;
		final Comment comment = AndroidMock.createMock(Comment.class);
		
		AndroidMock.expect(comment.getId()).andReturn(id);
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public Object getItem(int position) {
				return comment;
			}
		};
		
		AndroidMock.replay(comment);
		
		assertEquals(id, adapter.getItemId(position));
		
		AndroidMock.verify(comment);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({List.class})
	public void testGetItemViewTypeForNormal() {
		
		int position = 69;
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		AndroidMock.expect(comments.size()).andReturn(position+1);
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		
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
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		
		adapter.setComments(comments);
		
		AndroidMock.replay(comments);
		
		assertEquals(1, adapter.getItemViewType(position));
		
		AndroidMock.verify(comments);
	}
	
	public void testGetItemViewTypeCountForLoading() {
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public boolean isDisplayLoading() {
				return true;
			}
		};
		assertEquals(2, adapter.getViewTypeCount());
	}
	
	public void testGetItemViewTypeCountForNonLoading() {
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public boolean isDisplayLoading() {
				return false;
			}
		};
		assertEquals(1, adapter.getViewTypeCount());
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		IBeanFactory.class,
		CommentListItem.class,
		Comment.class,
		List.class,
		ViewHolder.class,
		ImageView.class,
		TimeUtils.class,
		UserService.class,
		User.class})
	public void testGetViewWithNullView() {
		
		final int position = 69;
		final int userId = 10001;
		final String text = "foobar";
		final long date = 10000;
		final String timeString = "foobar_timestring";
		
		final Date now = new Date(date-1);

		final Context context = getContext();
		
		UserService userService = AndroidMock.createMock(UserService.class);
		IBeanFactory<CommentListItem> commentItemViewFactory = AndroidMock.createMock(IBeanFactory.class);
		TimeUtils timeUtils = AndroidMock.createMock(TimeUtils.class);
		
		CommentListItem item = AndroidMock.createNiceMock(CommentListItem.class, context);
		List<Comment> comments = AndroidMock.createMock(List.class);
		
		User user = AndroidMock.createMock(User.class);
		
		final Comment comment = AndroidMock.createMock(Comment.class);
		final ViewHolder holder = AndroidMock.createMock(ViewHolder.class);
		
		// Can't mock text view.. bleugh!
		
		final TextView time = new TextView(context);
		final TextView userName = new TextView(context);
		final TextView commentText = new TextView(context);
		
		AndroidMock.expect(comments.size()).andReturn(position+1);
		AndroidMock.expect(commentItemViewFactory.getBean()).andReturn(item);
		
		AndroidMock.expect(item.getTime()).andReturn(time);
		AndroidMock.expect(item.getAuthor()).andReturn(userName);
		AndroidMock.expect(item.getComment()).andReturn(commentText);
		
		holder.setNow((Date)AndroidMock.anyObject());
        holder.setTime(time);
        holder.setUserName(userName);
        holder.setComment(commentText);
		
		AndroidMock.expect(userService.getCurrentUser()).andReturn(user);
		AndroidMock.expect(comment.getUser()).andReturn(user);
		
		AndroidMock.expect(user.getId()).andReturn(userId).anyTimes();
		
		AndroidMock.expect(holder.getTime()).andReturn(time);
		AndroidMock.expect(holder.getUserName()).andReturn(userName);
		AndroidMock.expect(holder.getComment()).andReturn(commentText);
		AndroidMock.expect(holder.getUserIcon()).andReturn(null);
		AndroidMock.expect(holder.getNow()).andReturn(now).anyTimes();

		AndroidMock.expect(comment.getText()).andReturn(text);

		
		AndroidMock.expect(timeUtils.getTimeString(AndroidMock.anyLong())).andReturn(timeString);
		
		AndroidMock.expect(comment.getDate()).andReturn(date);
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {

			@Override
			public Object getItem(int position) {
				return comment;
			}

			@Override
			protected ViewHolder createViewHolder() {
				return holder;
			}
		};
		
		adapter.setUserService(userService);
		adapter.setComments(comments);
		adapter.setTimeUtils(timeUtils);
		adapter.setCommentItemViewFactory(commentItemViewFactory);
		
		AndroidMock.replay(comments);
		AndroidMock.replay(userService);
		AndroidMock.replay(timeUtils);
		AndroidMock.replay(comment);
		AndroidMock.replay(holder);
		AndroidMock.replay(user);
		AndroidMock.replay(item);
		AndroidMock.replay(commentItemViewFactory);
		
		View view = adapter.getView(position, null, null);
		
		assertSame(item, view);
		assertEquals(text, commentText.getText().toString());
		assertEquals(timeString + " ", time.getText().toString());
		assertEquals("You", userName.getText().toString());
		
		AndroidMock.verify(comments);
		AndroidMock.verify(userService);
		AndroidMock.verify(timeUtils);
		AndroidMock.verify(comment);
		AndroidMock.verify(holder);
		AndroidMock.verify(user);
		AndroidMock.verify(item);
		AndroidMock.verify(commentItemViewFactory);
	}
}
