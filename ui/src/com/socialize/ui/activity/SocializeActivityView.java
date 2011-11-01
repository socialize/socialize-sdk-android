package com.socialize.ui.activity;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.socialize.log.SocializeLogger;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

public class SocializeActivityView extends BaseView {
	
	enum DisplayState {HIDDEN, DOWN, UP};
	
	private TranslateAnimation slideUp;
	private TranslateAnimation slideDown;
	private TranslateAnimation peek;
	private TranslateAnimation close;
	
	private DeviceUtils deviceUtils;
	private Drawables drawables;
	
	@SuppressWarnings("unused")
	private SocializeLogger logger;
	
	private float coveragePercent = 0.5f;
	
	private SocializeActivityHandle handle;
	private SocializeActivityContent content;
	
	private DisplayState displayState = DisplayState.HIDDEN;
	
	private int height;
	private int handleHeight = 30;
	private int contentHeight;
	private int deviceHeight;
	
	private boolean moving = false;
	
	public SocializeActivityView(Context context) {
		super(context);
	}

	public void init() {
		deviceHeight = deviceUtils.getDisplayHeight();
		float totalHeight = (float) deviceHeight * coveragePercent;
		height = Math.round(totalHeight);
		handleHeight = deviceUtils.getDIP(handleHeight);
		contentHeight = height - handleHeight;
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, height);
		params.setMargins(0,0,0,0);
		
		setLayoutParams(params);
		setOrientation(VERTICAL);
		
		handle = new SocializeActivityHandle(getContext(), this, handleHeight);
		content = new SocializeActivityContent(getContext(), this, contentHeight);
		
		handle.setDrawables(drawables);
		
		handle.init();
		content.init();
		
		initAnimations();
		
		addView(handle);
		addView(content);
	}
	
	public void slideUp() {
		clearAnimation();
		displayState = DisplayState.UP;
		startAnimation(slideUp);
	}
	
	public void slideDown() {
		clearAnimation();
		displayState = DisplayState.DOWN;
		startAnimation(slideDown);
	}
	
	public void peek() {
		clearAnimation();
		displayState = DisplayState.DOWN;
		startAnimation(peek);
	}
	
	public void close() {
		clearAnimation();
		displayState = DisplayState.HIDDEN;
		startAnimation(close);
	}
	
	public void slide() {
		
		if(!moving) {
			moving = true;
			switch(displayState) {
			case HIDDEN:
				peek();
				break;
			case DOWN:
				slideUp();
				break;
			case UP:
				slideDown();
				break;
			}
		}
	}
	
	protected void initAnimations() {
		
		AnimationListener visibleAnimation = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				setVisibility(VISIBLE);
				
				int yOffset = 0;
				
				switch(displayState) {
					case HIDDEN:
						yOffset = height;
						break;
					case DOWN:
						yOffset = height - handleHeight;
						break;
					case UP:
						yOffset = 0;
						break;
					}	
				
				handle.notifyMove(yOffset);
				content.notifyMove(yOffset);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				moving = false;
			}
		};
		
		AnimationListener hideAnimation = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				int yOffset = 0;
				
				switch(displayState) {
					case HIDDEN:
						yOffset = height;
						break;
					case DOWN:
						yOffset = height - handleHeight;
						break;
					case UP:
						yOffset = 0;
						break;
					}	
				
				handle.notifyMove(yOffset);
				content.notifyMove(yOffset);
				
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(GONE);
				moving = false;
			}
		};
				
		
		int endPosition = (height - handleHeight);
		
		slideUp = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, endPosition, Animation.ABSOLUTE, 0.0f			
		);

		slideDown = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, endPosition
		);		

		peek = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, height, Animation.ABSOLUTE, endPosition
		);		
		
		close = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, endPosition, Animation.ABSOLUTE, height
		);			
				
		slideUp.setAnimationListener(visibleAnimation);	
		slideDown.setAnimationListener(visibleAnimation);	
		peek.setAnimationListener(visibleAnimation);	
		close.setAnimationListener(hideAnimation);	
		
		slideUp.setFillAfter(true);
		slideDown.setFillAfter(true);
		peek.setFillAfter(true);
		close.setFillAfter(true);
		
		peek.setDuration(1000);
		peek.setStartOffset(1000);
		slideUp.setDuration(1000);
		slideDown.setDuration(1000);
		close.setDuration(500);
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
