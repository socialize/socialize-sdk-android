package com.socialize.ui.test.comment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.comment.CommentAdapter;
import com.socialize.ui.comment.CommentAddButtonListener;
import com.socialize.ui.comment.CommentContentView;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentHeader;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.CommentScrollCallback;
import com.socialize.ui.comment.CommentScrollListener;
import com.socialize.ui.test.SocializeUITest;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class CommentListViewTest extends SocializeUITest {
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		ViewFactory.class,
		CommentEditField.class,
		CommentHeader.class,
		CommentContentView.class,
		DeviceUtils.class,
		Drawables.class,
		KeyboardUtils.class,
		CommentAdapter.class,
		Drawable.class,
		CommentAddButtonListener.class,
		CommentScrollListener.class,
		CommentScrollCallback.class
	})
	public void testInit() {
		final Context context = getContext();
		
		final String entityKey = "foobar";
		
		ViewFactory<CommentHeader> commentHeaderFactory = AndroidMock.createMock(ViewFactory.class);
		ViewFactory<CommentEditField> commentEditFieldFactory = AndroidMock.createMock(ViewFactory.class);
		ViewFactory<CommentContentView> commentContentViewFactory = AndroidMock.createMock(ViewFactory.class);
		
		CommentScrollCallback commentScrollCallback = AndroidMock.createMock(CommentScrollCallback.class);
		
		final CommentScrollListener onScrollListener = AndroidMock.createMock(CommentScrollListener.class, commentScrollCallback);
		final CommentAddButtonListener onClickListener = AndroidMock.createMock(CommentAddButtonListener.class, getContext());
		
		CommentEditField field = AndroidMock.createMock(CommentEditField.class, getContext());
		CommentHeader header = AndroidMock.createMock(CommentHeader.class, getContext());
		CommentContentView content = AndroidMock.createMock(CommentContentView.class, getContext());
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		KeyboardUtils keyboardUtils = AndroidMock.createMock(KeyboardUtils.class, getContext());
		CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class, context);
		Drawable drawable = AndroidMock.createMock(Drawable.class);
		
		AndroidMock.expect(deviceUtils.getDIP(4)).andReturn(4).times(1);
		AndroidMock.expect(deviceUtils.getDIP(8)).andReturn(8).times(1);
		
		AndroidMock.expect(commentHeaderFactory.make(context)).andReturn(header);
		AndroidMock.expect(commentEditFieldFactory.make(context)).andReturn(field);
		AndroidMock.expect(commentContentViewFactory.make(context)).andReturn(content);

		AndroidMock.expect(drawables.getDrawable("crosshatch.png", true, true, true)).andReturn(drawable);
		
		field.setButtonListener((OnClickListener) AndroidMock.anyObject());
		content.setListAdapter(commentAdapter);
		content.setScrollListener((OnScrollListener) AndroidMock.anyObject());
		
		AndroidMock.replay(commentHeaderFactory);
		AndroidMock.replay(commentEditFieldFactory);
		AndroidMock.replay(commentContentViewFactory);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(drawables);
		AndroidMock.replay(field);
		AndroidMock.replay(content);
		
		CommentListView view = new CommentListView(context, entityKey)  {
			
			int childIndex = 0;
			
			@Override
			public void addView(View child) {
				addResult(10+childIndex, child);
				childIndex++;
			}
			
			@Override
			public void setOrientation(int orientation) {
				addResult(0, orientation);
			}

			@Override
			public void setBackgroundDrawable(Drawable d) {
				addResult(1, d);
			}

			@Override
			public void setPadding(int left, int top, int right, int bottom) {
				addResult(2, left);
				addResult(3, top);
				addResult(4, right);
				addResult(5, bottom);
			}

			@Override
			protected CommentScrollListener getCommentScrollListener() {
				return onScrollListener;
			}

			@Override
			protected CommentAddButtonListener getCommentAddListener() {
				return onClickListener;
			}
		};
		
		view.setCommentHeaderFactory(commentHeaderFactory);
		view.setCommentEditFieldFactory(commentEditFieldFactory);
		view.setCommentContentViewFactory(commentContentViewFactory);
		view.setDeviceUtils(deviceUtils);
		view.setKeyboardUtils(keyboardUtils);
		view.setCommentAdapter(commentAdapter);
		view.setDrawables(drawables);
		
		view.init();
		
		assertEquals(LinearLayout.VERTICAL, getResult(0));
		assertSame(drawable, getResult(1));
		
		assertEquals(0, getResult(2));
		assertEquals(0, getResult(3));
		assertEquals(0, getResult(4));
		assertEquals(0, getResult(5));
		
		assertSame(header, getResult(10));
		assertSame(field, getResult(11));
		assertSame(content, getResult(12));
		
		AndroidMock.verify(commentHeaderFactory);
		AndroidMock.verify(commentEditFieldFactory);
		AndroidMock.verify(commentContentViewFactory);
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(drawables);
		AndroidMock.verify(field);
		AndroidMock.verify(content);
		
	}
	
	public void testGetCommentScrollListener() {
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			protected void getNextSet() {
				addResult(true);
			}
		};
		
		view.setLoading(true);
		
		CommentScrollListener commentScrollListener = view.getCommentScrollListener();
		
		assertNotNull(commentScrollListener.getCallback());
		
		commentScrollListener.getCallback().onGetNextSet();
		
		assertTrue(commentScrollListener.getCallback().isLoading());
		
		Boolean nextResult = getNextResult();
		
		assertNotNull(nextResult);
		assertTrue(nextResult);
	}
	
	public void testGetCommentAddListener() {
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			public void doPostComment(String comment) {
				addResult(0, comment);
			}

			@Override
			public void showError(Context context, String message) {
				addResult(1, message);
			}
		};
		
		CommentAddButtonListener commentScrollListener = view.getCommentAddListener();
		
		assertNotNull(commentScrollListener.getCallback());
		
		commentScrollListener.getCallback().onComment("foobar");
		commentScrollListener.getCallback().onError(getContext(), "foobar_error");
		
		String comment = getResult(0);
		String message = getResult(1);
		
		assertNotNull(comment);
		assertNotNull(message);
		
		assertEquals("foobar", comment);
		assertEquals("foobar_error", message);
	}
	
	class PublicCommentListView extends CommentListView {

		public PublicCommentListView(Context context) {
			super(context);
		}

		@Override
		public CommentScrollListener getCommentScrollListener() {
			return super.getCommentScrollListener();
		}

		@Override
		public CommentAddButtonListener getCommentAddListener() {
			return super.getCommentAddListener();
		}

		@Override
		public void setLoading(boolean loading) {
			super.setLoading(loading);
		}
	}
}
