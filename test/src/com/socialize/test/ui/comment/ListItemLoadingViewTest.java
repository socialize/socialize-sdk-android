package com.socialize.test.ui.comment;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.test.ui.SocializeUITestCase;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ListItemLoadingView;
import com.socialize.util.DisplayUtils;

public class ListItemLoadingViewTest extends SocializeUITestCase {
	@UsesMocks ({
		DisplayUtils.class,
		Colors.class,
		LocalizationService.class
	})
	public void testMake() {
		
		// Just tests for runtime failures
		
		DisplayUtils deviceUtils = AndroidMock.createMock(DisplayUtils.class);
		Colors colors = AndroidMock.createMock(Colors.class);
		LocalizationService localizationService = AndroidMock.createMock(LocalizationService.class);
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(0).anyTimes();
		AndroidMock.expect(colors.getColor((String)AndroidMock.anyObject())).andReturn(0).anyTimes();
		AndroidMock.expect(localizationService.getString(I18NConstants.LOADING)).andReturn("Loading...").anyTimes();
		
		
		AndroidMock.replay(deviceUtils, colors, localizationService);

		ListItemLoadingView view = new ListItemLoadingView(getContext());
		
		view.setColors(colors);
		view.setLocalizationService(localizationService);
		view.setDisplayUtils(deviceUtils);
		view.init();
		
		AndroidMock.verify(deviceUtils, colors, localizationService);
	}
	
}
