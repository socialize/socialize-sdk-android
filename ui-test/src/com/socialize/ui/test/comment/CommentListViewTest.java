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
import com.socialize.ui.comment.CommentContentView;
import com.socialize.ui.comment.CommentContentViewFactory;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentEditFieldFactory;
import com.socialize.ui.comment.CommentHeader;
import com.socialize.ui.comment.CommentHeaderFactory;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.test.SocializeUITest;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

@UsesMocks ({
	CommentHeaderFactory.class,
	CommentEditFieldFactory.class,
	CommentContentViewFactory.class,
	CommentEditField.class,
	CommentHeader.class,
	CommentContentView.class,
	DeviceUtils.class,
	Drawables.class,
	KeyboardUtils.class,
	CommentAdapter.class,
	Drawable.class
})
public class CommentListViewTest extends SocializeUITest {
	
	public void testInit() {
		final Context context = getContext();
		
		final String entityKey = "foobar";
		
		CommentHeaderFactory commentHeaderFactory = AndroidMock.createMock(CommentHeaderFactory.class);
		CommentEditFieldFactory commentEditFieldFactory = AndroidMock.createMock(CommentEditFieldFactory.class);
		CommentContentViewFactory commentContentViewFactory = AndroidMock.createMock(CommentContentViewFactory.class);
		
		CommentEditField field = AndroidMock.createMock(CommentEditField.class);
		CommentHeader header = AndroidMock.createMock(CommentHeader.class);
		CommentContentView content = AndroidMock.createMock(CommentContentView.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		KeyboardUtils keyboardUtils = AndroidMock.createMock(KeyboardUtils.class);
		CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class, context);
		Drawable drawable = AndroidMock.createMock(Drawable.class);
		
		AndroidMock.expect(commentHeaderFactory.make(context)).andReturn(header);
		AndroidMock.expect(commentEditFieldFactory.make(context)).andReturn(field);
		AndroidMock.expect(commentContentViewFactory.make(context)).andReturn(content);
		AndroidMock.expect(deviceUtils.getDIP(4)).andReturn(4).times(1);
		AndroidMock.expect(deviceUtils.getDIP(8)).andReturn(8).times(1);
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
		};
		
		view.setCommentHeaderFactory(commentHeaderFactory);
		view.setCommentEditFieldFactory(commentEditFieldFactory);
		view.setCommentContentViewFactory(commentContentViewFactory);
		view.setDeviceUtils(deviceUtils);
		view.setKeyboardUtils(keyboardUtils);
		view.setCommentAdapter(commentAdapter);
		
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
	
}
