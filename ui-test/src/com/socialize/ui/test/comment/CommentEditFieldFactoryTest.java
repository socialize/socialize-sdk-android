package com.socialize.ui.test.comment;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentEditFieldFactory;
import com.socialize.ui.test.SocializeUITest;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class CommentEditFieldFactoryTest extends SocializeUITest {

	@UsesMocks ({CommentEditField.class, DeviceUtils.class, Colors.class, Drawables.class})
	public void testMake() {
		
		DeviceUtils deviceUtils = AndroidMock.createNiceMock(DeviceUtils.class);
		Colors colors = AndroidMock.createNiceMock(Colors.class);
		Drawables drawables = AndroidMock.createNiceMock(Drawables.class);
		
		final CommentEditField field = AndroidMock.createMock(CommentEditField.class, getContext());
		
		field.setLayoutParams((android.widget.LinearLayout.LayoutParams) AndroidMock.anyObject());
		field.setOrientation(LinearLayout.HORIZONTAL);
		field.setPadding(0, 0, 0, 0);
		field.setEditText((EditText) AndroidMock.anyObject());
		field.setButton((ImageButton) AndroidMock.anyObject());
		field.addView((EditText) AndroidMock.anyObject());
		field.addView((ImageButton) AndroidMock.anyObject());
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(1).anyTimes();
		AndroidMock.expect(colors.getColor((String)AndroidMock.anyObject())).andReturn(1).anyTimes();
		AndroidMock.expect(drawables.getDrawable((String)AndroidMock.anyObject())).andReturn(null).anyTimes();
		
		AndroidMock.replay(field);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(colors);
		AndroidMock.replay(drawables);
		
		CommentEditFieldFactory factory = new CommentEditFieldFactory() {
			@Override
			protected CommentEditField newCommentEditField(Context context) {
				return field;
			}
			
		};
		
		factory.setDeviceUtils(deviceUtils);
		factory.setColors(colors);
		factory.setDrawables(drawables);
		
		factory.make(getContext());
		
		AndroidMock.verify(field);
//		AndroidMock.verify(deviceUtils);
//		AndroidMock.verify(colors);
//		AndroidMock.verify(drawables);
	}
	
}
