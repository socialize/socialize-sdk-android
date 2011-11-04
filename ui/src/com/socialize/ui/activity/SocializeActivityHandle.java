package com.socialize.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialize.util.Drawables;

public class SocializeActivityHandle extends SocializeActivityViewChild {

	private int height;
	private TextView text;
	private ImageView imageView;
	private ImageView socializeLogo;
	private Drawables drawables;
	private String title = "";
	
	public SocializeActivityHandle(Context context, SocializeActivityView parent, int height) {
		super(context, parent);
		this.height = height;
	}

	public void init() {
		setBackgroundDrawable(drawables.getDrawable("toolbar_bg.png", true, false, true));
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, height);
		params.setMargins(0,0,0,0);
		setLayoutParams(params);	
		
		setOrientation(HORIZONTAL);
		
		text = new TextView(getContext());
		text.setText(title);
		text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		text.setPadding(4, 0, 4, 0);
		text.setTypeface(Typeface.DEFAULT_BOLD);
		text.setTextColor(Color.WHITE);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		
		imageView = new ImageView(getContext());
		imageView.setPadding(4, 0, 4, 0);
		imageView.setImageDrawable(drawables.getDrawable("toolbar_close.png"));
		
		socializeLogo = new ImageView(getContext());
		socializeLogo.setPadding(4, 0, 4, 0);
		socializeLogo.setImageDrawable(drawables.getDrawable("icon_like_hi.png"));
		
		LayoutParams textParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		textParams.weight = 1.0f;
		
		text.setLayoutParams(textParams);
		
		LayoutParams imageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imageParams.weight = 0.0f;
		imageParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		imageParams.setMargins(10, 0, 0, 0);
		
		imageView.setLayoutParams(imageParams);
		
		LayoutParams logoParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		logoParams.weight = 0.0f;
		logoParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		socializeLogo.setLayoutParams(logoParams);
		
		addView(socializeLogo);
		addView(text);
		addView(imageView);
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setTitle(String str) {
		if(text != null) {
			text.setText(str);
		}
		this.title = str;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Rect rect = new Rect();
		imageView.getHitRect(rect);
		adjustHitRect(rect);
		if(rect.contains((int)ev.getX(), (int)ev.getY())) {
			getParentAdView().close();
		}
		else {
			getParentAdView().slide();
		}
		return true;
	}
}
