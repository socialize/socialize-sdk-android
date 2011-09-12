package com.socialize.ui.test.comment;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.comment.CommentHeader;
import com.socialize.ui.comment.CommentHeaderFactory;
import com.socialize.ui.test.SocializeUITest;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class CommentHeaderFactoryTest extends SocializeUITest {
	
	@UsesMocks ({
		DeviceUtils.class,
		Drawables.class,
		Colors.class
	})
	public void testMake() {
		
		// Just tests for runtime failures
		
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Colors colors = AndroidMock.createMock(Colors.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(0).anyTimes();
		AndroidMock.expect(colors.getColor((String)AndroidMock.anyObject())).andReturn(0).anyTimes();
		AndroidMock.expect(drawables.getDrawable("header.png", true, false, true)).andReturn(null);
		AndroidMock.expect(drawables.getDrawable("socialize_icon_white.png", true)).andReturn(null);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(colors);
		AndroidMock.replay(drawables);

		CommentHeaderFactory factory = new CommentHeaderFactory();
		
		factory.setColors(colors);
		factory.setDeviceUtils(deviceUtils);
		factory.setDrawables(drawables);
		
		assertTrue((factory.make(getContext()) instanceof CommentHeader));
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(colors);
		AndroidMock.verify(drawables);
	}
	
}
