package com.socialize.ui.test.comment;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.test.SocializeUITest;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ListItemLoadingView;
import com.socialize.util.DeviceUtils;

public class ListItemLoadingViewTest extends SocializeUITest {
	@UsesMocks ({
		DeviceUtils.class,
		Colors.class
	})
	public void testMake() {
		
		// Just tests for runtime failures
		
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Colors colors = AndroidMock.createMock(Colors.class);
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(0).anyTimes();
		AndroidMock.expect(colors.getColor((String)AndroidMock.anyObject())).andReturn(0).anyTimes();
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(colors);

		ListItemLoadingView view = new ListItemLoadingView(getContext());
		
		view.setColors(colors);
		view.setDeviceUtils(deviceUtils);
		view.init();
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(colors);
	}
	
}
