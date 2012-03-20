package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.socialize.util.Drawables;

public class CachedImageView extends View {
	
	private Drawables drawables;
	private String imageName;
	private boolean changed = false;
	
	private Drawable drawable;

	private Handler mRedrawHandler = new Handler()  {
		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}
	};
	
	public CachedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CachedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CachedImageView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(drawable == null || changed) {
			changed = false;
			if(imageName != null) {
				drawable = drawables.getDrawable(imageName);
			}
		}
		
		if(drawable != null) {
			drawable.draw(canvas);
		}
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setImageName(String imageName) {
		
		if(this.imageName != null && !this.imageName.equals(imageName)) {
			changed = true;
		}
		
		this.imageName = imageName;
	}
	
	public void redraw() {
		mRedrawHandler.sendEmptyMessage(0);
	}
	
	public void notifyImageChange() {
		changed = true;
	}
}
