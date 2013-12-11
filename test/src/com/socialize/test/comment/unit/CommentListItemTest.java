package com.socialize.test.comment.unit;

import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import org.mockito.Mockito;

public class CommentListItemTest extends SocializeUnitTest {
	
	public void testInit() {
		DisplayUtils deviceUtils = Mockito.mock(DisplayUtils.class);
		Colors colors = Mockito.mock(Colors.class);
		Drawables drawables = Mockito.mock(Drawables.class);
		
        Mockito.when(deviceUtils.getDIP(Mockito.anyInt())).thenReturn(4);
		Mockito.when(colors.getColor(Colors.COMMENT_BODY)).thenReturn(1);
		Mockito.when(colors.getColor(Colors.COMMENT_TITLE)).thenReturn(1);
		Mockito.when(colors.getColor(Colors.LIST_ITEM_BG)).thenReturn(1);
		Mockito.when(drawables.getDrawable("icon_location_pin.png")).thenReturn(null);

		CommentListItem item = new CommentListItem(getContext());
		item.setDisplayUtils(deviceUtils);
		item.setColors(colors);
		item.setDrawables(drawables);
		
		item.init();
		
		Mockito.verify(colors).getColor(Colors.COMMENT_BODY);
        Mockito.verify(colors).getColor(Colors.COMMENT_TITLE);
        Mockito.verify(colors).getColor(Colors.LIST_ITEM_BG);
	}
}
