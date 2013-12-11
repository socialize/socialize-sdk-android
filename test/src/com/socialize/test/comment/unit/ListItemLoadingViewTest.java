package com.socialize.test.comment.unit;

import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ListItemLoadingView;
import com.socialize.util.DisplayUtils;
import org.mockito.Mockito;

public class ListItemLoadingViewTest extends SocializeUnitTest {
	public void testMake() {
		
		// Just tests for runtime failures
		DisplayUtils deviceUtils = Mockito.mock(DisplayUtils.class);
		Colors colors = Mockito.mock(Colors.class);
		LocalizationService localizationService = Mockito.mock(LocalizationService.class);
		
		Mockito.when(deviceUtils.getDIP(Mockito.anyInt())).thenReturn(0);
		Mockito.when(colors.getColor((String)Mockito.anyObject())).thenReturn(0);
		Mockito.when(localizationService.getString(I18NConstants.LOADING)).thenReturn("Loading...");
		
		ListItemLoadingView view = new ListItemLoadingView(getContext());
		
		view.setColors(colors);
		view.setLocalizationService(localizationService);
		view.setDisplayUtils(deviceUtils);
		view.init();

        // TODO: Add assertions?
	}
	
}
