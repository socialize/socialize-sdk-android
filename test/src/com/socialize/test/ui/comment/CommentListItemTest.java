package com.socialize.test.ui.comment;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.comment.CommentListItemBackgroundFactory;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class CommentListItemTest extends SocializeUnitTest {
	
	@UsesMocks ({
		DeviceUtils.class,
		Colors.class,
		Drawables.class,
		CommentListItemBackgroundFactory.class
	})
	public void testInit() {
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Colors colors = AndroidMock.createMock(Colors.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		CommentListItemBackgroundFactory backgroundFactory = AndroidMock.createMock(CommentListItemBackgroundFactory.class);
		
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(4).anyTimes();
		AndroidMock.expect(colors.getColor(Colors.BODY)).andReturn(1);
		AndroidMock.expect(colors.getColor(Colors.TITLE)).andReturn(1);
		AndroidMock.expect(drawables.getDrawable("icon_location_pin.png")).andReturn(null);
		AndroidMock.expect(backgroundFactory.getBackground()).andReturn(null);
		
		AndroidMock.replay(backgroundFactory);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(colors);
		AndroidMock.replay(drawables);
		
		CommentListItem item = new CommentListItem(getContext());
		item.setDeviceUtils(deviceUtils);
		item.setColors(colors);
		item.setDrawables(drawables);
		item.setBackgroundFactory(backgroundFactory);
		
		item.init();
		
		AndroidMock.verify(backgroundFactory);
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(colors);
		AndroidMock.verify(drawables);
	}
}
