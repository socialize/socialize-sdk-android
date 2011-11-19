package com.socialize.test.ui.comment;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.ui.SocializeUITestCase;
import com.socialize.ui.comment.CommentHeader;
import com.socialize.ui.comment.CommentHeaderFactory;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class CommentHeaderFactoryTest extends SocializeUITestCase {
	
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
		Drawable[] layers = {new BitmapDrawable(), new BitmapDrawable()};
		
		final LayerDrawable layer = new LayerDrawable(layers);
		
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(0).anyTimes();
		AndroidMock.expect(colors.getColor((String)AndroidMock.anyObject())).andReturn(0).anyTimes();
		AndroidMock.expect(drawables.getDrawable("header.png", true, false, true)).andReturn(null);
		AndroidMock.expect(drawables.getDrawable("socialize_icon_white.png", DisplayMetrics.DENSITY_DEFAULT, true)).andReturn(null);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(colors);
		AndroidMock.replay(drawables);

		CommentHeaderFactory factory = new CommentHeaderFactory() {
			@Override
			protected LayerDrawable newLayerDrawable(Drawable[] layers) {
				return layer;
			}
		};
		
		factory.setColors(colors);
		factory.setDeviceUtils(deviceUtils);
		factory.setDrawables(drawables);
		
		assertTrue((factory.make(getContext()) instanceof CommentHeader));
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(colors);
		AndroidMock.verify(drawables);
	}
	
}
