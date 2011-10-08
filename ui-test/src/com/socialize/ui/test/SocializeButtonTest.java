package com.socialize.ui.test;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.sample.mock.MockLayoutParams;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class SocializeButtonTest extends SocializeUITestCase {

	@UsesMocks ({GradientDrawable.class, MockLayoutParams.class, Drawables.class, Colors.class, DeviceUtils.class})
	public void testSocializeButtonInit() {
		
		final TextView text = new TextView(getContext());
		final ImageView image = new ImageView(getContext());
		
		final int padding = 0;
		final int radius = 1;
		final int textPadding = 2;
		final int stroke = 10;
		final String textAlign="right";
		final String imageName = "foobar";
		final int width = 100;
		final int height = 50;
		
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		Colors colors = AndroidMock.createMock(Colors.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		
		final GradientDrawable gradient = AndroidMock.createNiceMock(GradientDrawable.class);
		final MockLayoutParams layoutParams = AndroidMock.createNiceMock(MockLayoutParams.class, 0, 0);
		
		AndroidMock.expect(colors.getColor(Colors.BUTTON_BOTTOM)).andReturn(1);
		AndroidMock.expect(colors.getColor(Colors.BUTTON_TOP)).andReturn(1);
		AndroidMock.expect(colors.getColor(Colors.BUTTON_BOTTOM_STROKE)).andReturn(stroke);
		AndroidMock.expect(colors.getColor(Colors.BUTTON_TOP_STROKE)).andReturn(stroke);
		
		AndroidMock.expect(deviceUtils.getDIP(8)).andReturn(padding);
		AndroidMock.expect(deviceUtils.getDIP(4)).andReturn(radius);
		AndroidMock.expect(deviceUtils.getDIP(4)).andReturn(textPadding);
		AndroidMock.expect(drawables.getDrawable(imageName)).andReturn(null);
		
		layoutParams.setMargins(padding, padding, padding, padding);
		
		AndroidMock.expect(deviceUtils.getDIP(width)).andReturn(width);
		AndroidMock.expect(deviceUtils.getDIP(height)).andReturn(height);
		
		gradient.setCornerRadius(radius);
		gradient.setStroke(1, stroke);
		
		SocializeButton testButton = new SocializeButton(getContext()) {

			@Override
			protected TextView makeTextView() {
				return text;
			}

			@Override
			protected ImageView makeImageView() {
				return image;
			}

			@Override
			protected LayoutParams makeLayoutParams(int width, int height) {
				addResult(3, width);
				addResult(4, height);
				return layoutParams;
			}

			@Override
			protected GradientDrawable makeGradient(int bottom, int top) {
				addResult(1, bottom);
				addResult(2, top);
				return gradient;
			}

			@Override
			public void setOrientation(int orientation) {
				addResult(0, orientation);
				super.setOrientation(orientation);
			}

			@Override
			public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
				addResult(5, params);
				super.setLayoutParams(params);
			}

			@Override
			public void setBackgroundDrawable(Drawable d) {
				addResult(6, d);
				super.setBackgroundDrawable(d);
			}

			@Override
			public void setPadding(int left, int top, int right, int bottom) {
				addResult(7, left);
				addResult(8, top);
				addResult(9, right);
				addResult(10, bottom);
				
				super.setPadding(left, top, right, bottom);
			}

			@Override
			public void setClickable(boolean clickable) {
				addResult(11, clickable);
				
				super.setClickable(clickable);
			}

			@Override
			public void setGravity(int gravity) {
				addResult(12, gravity);
				super.setGravity(gravity);
			}

			@Override
			public void addView(View child) {
				if(child instanceof ImageView) {
					addResult(13, child);
				}
				else {
					addResult(14, child);
				}
				
				super.addView(child);
			}
			
			
		};
		
		testButton.setWidth(width);
		testButton.setHeight(height);
		testButton.setTextAlign(textAlign);
		
		testButton.setDeviceUtils(deviceUtils);
		testButton.setDrawables(drawables);
		testButton.setColors(colors);
		
		testButton.setImageName(imageName);
		
		AndroidMock.replay(drawables);
		AndroidMock.replay(colors);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(gradient);
		AndroidMock.replay(layoutParams);
		
		testButton.init();
		
		AndroidMock.verify(drawables);
		AndroidMock.verify(colors);
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(gradient);
		AndroidMock.verify(layoutParams);
		
		
		for (int i = 0; i <= 14; i++) {
			assertNotNull(getResult(i));
		}
		
		assertEquals(LinearLayout.HORIZONTAL, getResult(0));

		assertSame(layoutParams, getResult(5));
		assertSame(gradient, getResult(6));
		assertEquals(padding, getResult(7));
		assertEquals(padding, getResult(8));
		assertEquals(padding, getResult(9));
		assertEquals(padding, getResult(10));
		assertEquals(true, getResult(11));
		assertEquals((Gravity.CENTER_VERTICAL | Gravity.RIGHT), getResult(12));
		assertSame(image, getResult(13));
		assertSame(text, getResult(14));
		
		assertEquals(Color.WHITE, text.getTextColors().getDefaultColor());
	}
}
