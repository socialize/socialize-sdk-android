package com.socialize.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.socialize.util.DeviceUtils;

public class SocializeButton extends Button {
	
	public SocializeButton(Context context, DeviceUtils deviceUtils) {
		super(context);
		
//		int two = deviceUtils.getDIP(2);
		
//		float radius = 6;
		
//		GradientDrawable background = new GradientDrawable(
//				GradientDrawable.Orientation.BOTTOM_TOP,
//                new int[] {  Color.GRAY, Color.DKGRAY });
		
//		background.setShape(GradientDrawable.RECTANGLE);
//		background.setCornerRadius(radius);
//		background.setStroke(1, Color.DKGRAY);
		
		int bottom = Color.parseColor("#00789f");
		int top = Color.parseColor("#00abe3");
//		int stroke = Color.parseColor("#3ebefb");
		int shadow = Color.parseColor("#222222");
		
		setTextColor(Color.WHITE);
		setShadowLayer(1, 1, 1, shadow);
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		setTypeface(Typeface.DEFAULT_BOLD);
		
		GradientDrawable foreground = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
                new int[] { bottom, top });
		
		foreground.setShape(GradientDrawable.RECTANGLE);
//		foreground.setCornerRadius(radius);
//		foreground.setStroke(2,stroke);
		
//		Drawable[] layers = { background, foreground };
//		
//		LayerDrawable drawable = new LayerDrawable(layers);
//		drawable.setLayerInset(1, 1, 1, 1, 1);
		
		setBackgroundDrawable(foreground);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(deviceUtils.getDIP(32), deviceUtils.getDIP(32)); // calc DIP
		params.setMargins(0,0,0,0);
		params.gravity = Gravity.TOP;
		setPadding(0,0,0,0);
		setLayoutParams(params);
		
	}
	
	public void setDimensions(int width, int height) {
		getLayoutParams().width = width;
		getLayoutParams().height = height;
		forceLayout();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setDimensions(w, h);
	}
	
	

}
