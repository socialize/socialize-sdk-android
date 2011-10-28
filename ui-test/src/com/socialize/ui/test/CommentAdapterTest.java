package com.socialize.ui.test;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.comment.CommentAdapter;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.view.ViewHolder;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

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
		
//		final int id = 69;
		final int position = 0;
		final Comment comment = AndroidMock.createMock(Comment.class);
		
//		AndroidMock.expect(comment.getId()).andReturn(id);
		
		CommentAdapter adapter = new CommentAdapter(getContext()) {
			@Override
			public Object getItem(int position) {
				return comment;
			}
		};
		
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
	
	@UsesMocks ({
		IBeanFactory.class,
		CommentListItem.class,
		Comment.class,
		List.class,
		ViewHolder.class,
		ImageView.class,
		DateUtils.class,
		User.class,
		Drawables.class,
		Drawable.class,
		DeviceUtils.class,
		Uri.class,
		SocializeUI.class, 
		SocializeService.class,
		SocializeSession.class})
	public void testGetViewWithNullView() {
		
//		final int position = 69;
//		final int iconSize = 29;
//		final int userId = 10001;
//		final String text = "foobar";
//		final long date = 10000;
//		final String timeString = "foobar_timestring";
//		final Date now = new Date(date-1);
//		final Context context = getContext();
//		final String imageUrl = "foobar_url";
//		final String displayName = "foobar_displayname";
//		
//		IBeanFactory<CommentListItem> commentItemViewFactory = AndroidMock.createMock(IBeanFactory.class);
//		DateUtils timeUtils = AndroidMock.createMock(DateUtils.class);
//		Drawables drawables = AndroidMock.createMock(Drawables.class);
//		Drawable drawable = AndroidMock.createMock(Drawable.class);
//		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
//		ImageView icon = AndroidMock.createMock(ImageView.class, context);
//		
//		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
//		SocializeService socialize = AndroidMock.createMock(SocializeService.class);
//		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
//		
//		CommentListItem item = AndroidMock.createNiceMock(CommentListItem.class, context);
//		List<Comment> comments = AndroidMock.createMock(List.class);
//		Uri uri = Uri.EMPTY;
//		
//		User user = AndroidMock.createMock(User.class);
//		
//		final Comment comment = AndroidMock.createMock(Comment.class);
//		final ViewHolder holder = AndroidMock.createMock(ViewHolder.class);
//		
//		// Can't mock text view.. bleugh!
//		
//		final TextView time = new TextView(context);
//		final TextView userName = new TextView(context);
//		final TextView commentText = new TextView(context);
//		
//		AndroidMock.expect(comments.size()).andReturn(position+1);
//		AndroidMock.expect(commentItemViewFactory.getBean()).andReturn(item);
//		
//		AndroidMock.expect(item.getUserIcon()).andReturn(icon);
//		AndroidMock.expect(item.getTime()).andReturn(time);
//		AndroidMock.expect(item.getAuthor()).andReturn(userName);
//		AndroidMock.expect(item.getComment()).andReturn(commentText);
//		
//		AndroidMock.expect(user.getSmallImageUri()).andReturn(imageUrl);
//		
//		AndroidMock.expect(user.getDisplayName()).andReturn(displayName);
//		
//		AndroidMock.expect(deviceUtils.getDIP(iconSize)).andReturn(iconSize);
//		AndroidMock.expect(drawables.getDrawable(SocializeUI.DEFAULT_USER_ICON, true)).andReturn(drawable);
//		
//		AndroidMock.expect(socializeUI.getSocialize()).andReturn(socialize);
//		AndroidMock.expect(socialize.getSession()).andReturn(session);
//		AndroidMock.expect(session.getUser()).andReturn(user);
//		
//		holder.setNow((Date)AndroidMock.anyObject());
//        holder.setTime(time);
//        holder.setUserIcon(icon);
//        holder.setUserName(userName);
//        holder.setComment(commentText);
//        holder.setItemId(position);
//        
//        icon.setImageURI(uri);
//		
//		AndroidMock.expect(comment.getUser()).andReturn(user);
//		
//		AndroidMock.expect(user.getId()).andReturn(userId).anyTimes();
//		
//		AndroidMock.expect(holder.getTime()).andReturn(time);
//		AndroidMock.expect(holder.getUserName()).andReturn(userName);
//		AndroidMock.expect(holder.getComment()).andReturn(commentText);
//		AndroidMock.expect(holder.getUserIcon()).andReturn(icon);
//		AndroidMock.expect(holder.getNow()).andReturn(now).anyTimes();
//		AndroidMock.expect(comment.getText()).andReturn(text);
//		AndroidMock.expect(timeUtils.getTimeString(AndroidMock.anyLong())).andReturn(timeString);
//		AndroidMock.expect(comment.getDate()).andReturn(date);
//		
//		CommentAdapter adapter = new CommentAdapter(getContext()) {
//
//			@Override
//			public Object getItem(int position) {
//				return comment;
//			}
//
//			@Override
//			protected ViewHolder createViewHolder() {
//				return holder;
//			}
//
//			@Override
//			protected SocializeUI getSocializeUI() {
//				return socializeUI;
//			}
//		};
//		
//		adapter.setIconSize(iconSize);
//		adapter.setComments(comments);
//		adapter.setDateUtils(timeUtils);
//		adapter.setCommentItemViewFactory(commentItemViewFactory);
//		adapter.setDrawables(drawables);
//		adapter.setDeviceUtils(deviceUtils);
//		
//		AndroidMock.replay(socializeUI);
//		AndroidMock.replay(socialize);
//		AndroidMock.replay(session);
//		AndroidMock.replay(drawables);
//		AndroidMock.replay(drawable);
//		AndroidMock.replay(deviceUtils);
//		AndroidMock.replay(comments);
//		AndroidMock.replay(icon);
//		AndroidMock.replay(timeUtils);
//		AndroidMock.replay(comment);
//		AndroidMock.replay(holder);
//		AndroidMock.replay(user);
//		AndroidMock.replay(item);
//		AndroidMock.replay(commentItemViewFactory);
//		
//		View view = adapter.getView(position, null, null);
//		
//		assertSame(item, view);
//		assertEquals(text, commentText.getText().toString());
//		assertEquals(timeString + " ", time.getText().toString());
//		assertEquals("You", userName.getText().toString());
//		
//		AndroidMock.verify(drawables);
//		AndroidMock.verify(drawable);
//		AndroidMock.verify(deviceUtils);
//		AndroidMock.verify(socializeUI);
//		AndroidMock.verify(socialize);
//		AndroidMock.verify(session);
//		AndroidMock.verify(comments);
//		AndroidMock.verify(icon);
//		AndroidMock.verify(timeUtils);
//		AndroidMock.verify(comment);
//		AndroidMock.verify(holder);
//		AndroidMock.verify(user);
//		AndroidMock.verify(item);
//		AndroidMock.verify(commentItemViewFactory);
	}
}
