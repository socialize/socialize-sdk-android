package com.socialize.test.ui.comment;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.comment.CommentListItemBackgroundFactory;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;

public class CommentListItemTest extends SocializeUnitTest {
	
	@UsesMocks ({
		DisplayUtils.class,
		Colors.class,
		Drawables.class,
		CommentListItemBackgroundFactory.class
	})
	public void testInit() {
		DisplayUtils deviceUtils = AndroidMock.createMock(DisplayUtils.class);
		Colors colors = AndroidMock.createMock(Colors.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(4).anyTimes();
		AndroidMock.expect(colors.getColor(Colors.COMMENT_BODY)).andReturn(1);
		AndroidMock.expect(colors.getColor(Colors.COMMENT_TITLE)).andReturn(1);
		AndroidMock.expect(colors.getColor(Colors.LIST_ITEM_BG)).andReturn(1);
		AndroidMock.expect(drawables.getDrawable("icon_location_pin.png")).andReturn(null);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(colors);
		AndroidMock.replay(drawables);
		
		CommentListItem item = new CommentListItem(getContext());
		item.setDisplayUtils(deviceUtils);
		item.setColors(colors);
		item.setDrawables(drawables);
		
		item.init();
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(colors);
		AndroidMock.verify(drawables);
	}
}
