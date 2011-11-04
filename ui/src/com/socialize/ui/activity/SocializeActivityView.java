package com.socialize.ui.activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.recommendation.RecommendationConsumer;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

public class SocializeActivityView extends BaseView implements RecommendationConsumer<Entity> {
	
	enum DisplayState {HIDDEN, DOWN, UP};
	
	private TranslateAnimation slideUp;
	private TranslateAnimation slideDown;
	private TranslateAnimation peek;
	private TranslateAnimation close;
	private TranslateAnimation closeFromUp;
	
	private DeviceUtils deviceUtils;
	private Drawables drawables;
	
	@SuppressWarnings("unused")
	private SocializeLogger logger;
	
	private float coveragePercent = 0.3f;
	
	private SocializeActivityHandle handle;
	private SocializeActivityContent content;
	
	private DisplayState displayState = DisplayState.HIDDEN;
	
	private IBeanFactory<SocializeActivityEntityView> socializeActivityEntityViewFactory;
	
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
	
	public void loadContent() {
		
	}
	
	public void clearContent() {
		if(content != null) {
			content.removeAllViews();
		}
	}
	
	public void addContentItem(View child) {
		if(content != null) {
			content.addView(child);
		}
	}
	
	public void setHandleText(String text) {
		handle.setTitle(text);
	}
	
	public void slideUp() {
		clearAnimation();
		displayState = DisplayState.UP;
		startAnimation(slideUp);
	}
	
	public void slideDown() {
		if(displayState.equals(DisplayState.UP)) {
			clearAnimation();
			displayState = DisplayState.DOWN;
			startAnimation(slideDown);
		}
	}
	
	public void peek() {
		if(displayState.equals(DisplayState.HIDDEN)) {
			clearAnimation();
			displayState = DisplayState.DOWN;
			startAnimation(peek);	
		}
	}
	
	public void close() {
		close(false);
	}
	
	public void close(boolean force) {
		clearAnimation();
		if(force) {
			switch (displayState) {
			case UP:
				displayState = DisplayState.HIDDEN;
				startAnimation(closeFromUp);
				break;
			case DOWN:
				displayState = DisplayState.HIDDEN;
				startAnimation(close);
				break;
			}
		}
		else {
			switch (displayState) {
				case UP:
					slideDown();
					break;
				case DOWN:
					displayState = DisplayState.HIDDEN;
					startAnimation(close);
					break;
				}
		}

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
		
		closeFromUp = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, height
		);
				
		slideUp.setAnimationListener(visibleAnimation);	
		slideDown.setAnimationListener(visibleAnimation);	
		peek.setAnimationListener(visibleAnimation);	
		close.setAnimationListener(hideAnimation);	
		closeFromUp.setAnimationListener(hideAnimation);	
		
		slideUp.setFillAfter(true);
		slideDown.setFillAfter(true);
		peek.setFillAfter(true);
		close.setFillAfter(true);
		closeFromUp.setFillAfter(true);
		
		peek.setDuration(500);
		slideUp.setDuration(500);
		slideDown.setDuration(500);
		close.setDuration(500);
		closeFromUp.setDuration(800);
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
	
	public void setSocializeActivityEntityViewFactory(IBeanFactory<SocializeActivityEntityView> socializeActivityEntityViewFactory) {
		this.socializeActivityEntityViewFactory = socializeActivityEntityViewFactory;
	}

	@Override
	public void consume(List<Entity> items) {
		this.clearContent();
		this.setHandleText("People who liked this also liked....");
		for (Entity entity : items) {
			
			SocializeActivityEntityView view = socializeActivityEntityViewFactory.getBean();
			
			if(!StringUtils.isEmpty(entity.getName())) {
				view.setText(entity.getName());
			}
			else {
				view.setText(entity.getKey());
			}
			
			this.addContentItem(view);
		}
		this.peek();
	}
}
