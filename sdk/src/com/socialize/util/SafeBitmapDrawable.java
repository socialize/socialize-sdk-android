package com.socialize.util;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;


/**
 * Because bitmaps may expire in cache while still attached to a view, we need 
 * to provide a catch-all otherwise the app will crash if a view tries to draw 
 * a recycled bitmap.
 * @author jasonpolites
 */
public class SafeBitmapDrawable extends BitmapDrawable {

	public SafeBitmapDrawable(InputStream is) {
		super(is);
	}
	
	public SafeBitmapDrawable() {
		super();
	}

	public SafeBitmapDrawable(Bitmap bitmap) {
		super(bitmap);
	}

	public SafeBitmapDrawable(Resources res, Bitmap bitmap) {
		super(res, bitmap);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(!isRecycled()) {
			try {
				super.draw(canvas);
			}
			catch (Throwable e) {
				Log.w("SafeBitmapDrawable", "Bitmap was recycled");
			}
		}
		else {
			Log.w("SafeBitmapDrawable", "Bitmap was recycled");
		}
	}
	
	public boolean isRecycled() {
		Bitmap bmp = getBitmap();
		return (bmp == null || bmp.isRecycled());
	}
	
	public void recycle() {
		Bitmap bmp = getBitmap();
		if(bmp != null) {
			bmp.recycle();
		}
	}
}
