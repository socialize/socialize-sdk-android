package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.socialize.Socialize;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;

public class CachedImageView extends View implements ImageLoadListener {
	
	private Drawables drawables;
	private String imageName;
	private String expectedImageName;
	private int changes = 0;
	private boolean local = true;
	private Drawable drawable;
	private SocializeLogger logger;
	private CachedImageViewChangeListener changeListener;
	
	private int width;
	private int height;
	private int x,y;
	
	private final int REDRAW = 101;
	private final int CHANGE = 102;
	
	private Handler mRedrawHandler = new Handler()  {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case REDRAW: 
					invalidate();
					if(changeListener != null) {
						changeListener.onRedraw(CachedImageView.this);
					}
					break;
				case CHANGE: 
					Bundle data = msg.getData();
					setImageUrlImmediate(data.getString("imageName"), data.getBoolean("local"));
					if(changeListener != null) {
						changeListener.onChange(CachedImageView.this);
					}
					break;
			}
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
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int rawWidth = MeasureSpec.getSize(widthMeasureSpec) - (getPaddingLeft() + getPaddingRight());
		int rawHeight = MeasureSpec.getSize(heightMeasureSpec) - (getPaddingTop() + getPaddingBottom());
		setMeasuredDimension(rawWidth, rawHeight);
		
		width = rawWidth - getPaddingLeft() - getPaddingRight();
		height = rawHeight - getPaddingTop() - getPaddingBottom();
		
		x = rawWidth - width;
		y = rawHeight - height;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(drawable == null || changes > 0) {
			changes--;
			if(imageName != null) {
				if(local) {
					drawable = drawables.getDrawable(imageName);
				}
				else {
					drawable = drawables.getCache().get(imageName);
				}
				
				if(drawable == null) {
					if(logger != null) {
						logger.error("No drawable found with name [" +
								imageName +
								"]");
					}
				}
			}
		}
		
		if(drawable != null) {

			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Image view is drawing " + imageName);
			}

			drawable.setBounds(x, y, width, height);
			drawable.draw(canvas);
		}
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setDefaultImage() {
		setImageUrlImmediate(Socialize.DEFAULT_USER_ICON, true);
	}
	
	public void setImageUrlImmediate(String url, boolean local) {
		this.imageName = url;
		this.local = local;
		changes++;
		invalidate();

		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Setting image to drawable name [" +
					url +
					"], draw should follow");
		}
	}
	
	void setImageName(String imageName, boolean local) {
		if(expectedImageName == null || this.expectedImageName.equals(imageName)) {
			Message m = Message.obtain();
			Bundle data = new Bundle();
			data.putString("imageName", imageName);
			data.putBoolean("local", local);
			m.setData(data);
			m.what = CHANGE;
			mRedrawHandler.sendMessage(m);
		}
		else {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Image view was not updated.  Expected image name [" +
						expectedImageName +
						"] does not match requested image name [" +
						imageName +
						"]");
			}
		}
	}
	

	@Override
	public synchronized void onImageLoad(ImageLoadRequest request, SafeBitmapDrawable drawable) {
		setImageName(request.getUrl(), false);
	}

	@Override
	public synchronized void onImageLoadFail(ImageLoadRequest request, Exception error) {
		setDefaultImage();
	}

	public synchronized void setImageUrl(String imageUrl) {
		setImageName(imageUrl, false);
	}
	
	public void redraw() {
		mRedrawHandler.sendEmptyMessage(REDRAW);
	}
	
	public void notifyImageChange() {
		changes++;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public CachedImageViewChangeListener getChangeListener() {
		return changeListener;
	}

	public void setChangeListener(CachedImageViewChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public void setExpectedImageName(String expectedImageName) {
		this.expectedImageName = expectedImageName;
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Set expected image name to [" +
					expectedImageName +
					"]");
		}					
	}
}
