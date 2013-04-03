/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.slider;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.util.DisplayUtils;
import com.socialize.view.BaseView;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jason Polites
 *
 */
public class ActionBarSliderView extends BaseView {
	
	public enum DisplayState {CLOSE, MAXIMIZE, PEEK};
	
	private DisplayUtils displayUtils;
	private IBeanFactory<SliderAnimationSet> sliderAnimationSetFactory;
	private IBeanFactory<ActionBarSliderContent> actionBarSliderContentFactory;
	private IBeanFactory<ActionBarSliderHandle> actionBarSliderHandleFactory;
	
	private Map<String, SliderAnimationSet> animations;
	private SliderAnimationSet currentAnimationSet;
	
	private SocializeLogger logger;
	
	private int[] parentViewlocation;
	
	private ActionBarSliderHandle handle;
	private ActionBarSliderContent content;
	
	private DisplayState displayState = DisplayState.CLOSE;

	private int handleHeight = 30;
	private int actionBarTop;
	private int actionBarHeight;
	private int deviceHeight;
	private int peekHeight;
	
	private boolean moving = false;
	
	private ActionBarSliderItem currentItem;

	public OnActionBarSliderMoveListener onActionBarSliderMoveListener;
	
	public ActionBarSliderView(Context context) {
		super(context);
	}
	
	public ActionBarSliderView(Context context, int[] parentLocation, int peekHeight) {
		super(context);
		this.parentViewlocation = parentLocation;
		this.peekHeight = peekHeight;
	}

	public void init() {
		
		deviceHeight = displayUtils.getDisplayHeight();
		actionBarHeight = displayUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT);
		
		animations = new TreeMap<String, SliderAnimationSet>();
		
		if(peekHeight > 0) {
			actionBarTop = peekHeight;
		}
		else {
			if(parentViewlocation != null) {
				actionBarTop = parentViewlocation[1];
			}
			else {
				actionBarTop = deviceHeight - actionBarHeight;
			}
		}
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.setMargins(0,0,0,0);
		
		setLayoutParams(params);
		setOrientation(VERTICAL);
		
		handleHeight = displayUtils.getDIP(handleHeight);
		
		content = actionBarSliderContentFactory.getBean(this, deviceHeight-handleHeight);
		handle = actionBarSliderHandleFactory.getBean(this, handleHeight);
		
		addView(handle);
		addView(content);
	}
	
	public void clearContent() {
		if(currentItem != null) {
			currentItem.onClear(this);
		}
	}
	
	public void updateContent() {
		if(currentItem != null) {
			currentItem.onUpdate(this);
		}
	}	

	public boolean showLastItem() {
		if(currentItem != null) {
			showSliderItem(currentItem);
			return true;
		}
		return false;
	}
	
	public synchronized void loadItem(ActionBarSliderItem item) {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Loading slider item for " + item.getId());
		}
		
		if(currentItem != item) {
			currentItem = item;
			content.removeAllViews();
			content.addView(item.getView());
			handle.setTitle(item.getTitle());
			handle.setIconImage(item.getIconImage());
			
			currentAnimationSet = animations.get(item.getId());
			
			if(currentAnimationSet == null) {
				currentAnimationSet = sliderAnimationSetFactory.getBean();
				currentAnimationSet.init(item, this);
				animations.put(item.getId(), currentAnimationSet);
			}		
			
			currentItem.onCreate(this);
		}
	}
	
	public void showSliderItem(ActionBarSliderItem item) {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Showing slider item for " + item.getId());
		}
		if(content != null) {
			if(item != null) {
				DisplayState state = item.getStartPosition();
				
				if(item != currentItem) {
					if(logger != null && logger.isDebugEnabled()) {
						logger.debug("Don't have item [" +
								item.getId() +
								"], current state is " + displayState);
					}					
					
					
					close();
					
					loadItem(item);
					
					switch (state) {
						case PEEK:
							peek();
							break;
						case MAXIMIZE:
							maximize();
							break;
					}
				}
				else {
					if(logger != null && logger.isDebugEnabled()) {
						logger.debug("Already have item [" +
								item.getId() +
								"], current state is " + displayState);
					}						
					
					if(!displayState.equals(state)) {
						switch (state) {
							case PEEK:
								peek();
								break;
							case MAXIMIZE:
								maximize();
								break;
						}
					}
				}
			}
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
	
	public void maximize() {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("maximize called in state " + displayState);
		}			
		
		switch (displayState) {
			case CLOSE:
				startAnimationForState(currentAnimationSet.getMaximizeFromClose(), DisplayState.MAXIMIZE);
				break;
			case PEEK:
				startAnimationForState(currentAnimationSet.getMaximizeFromPeek(), DisplayState.MAXIMIZE);
				break;				
		}
	}
	
	protected void startAnimationForState(Animation animation, DisplayState state) {
		// Animation doesn't always seem to work.. force an invalidate
		setVisibility(View.VISIBLE);
		clearAnimation();
		displayState = state;
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("starting animation for state " + state);
		}	
		
		startAnimation(animation);
	}
	
	public void peek() {
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("peek called in state " + displayState);
		}	
		
		switch (displayState) {
			case CLOSE:
				startAnimationForState(currentAnimationSet.getPeekFromClose(), DisplayState.PEEK);
				break;
			case MAXIMIZE:
				startAnimationForState(currentAnimationSet.getPeekFromMaximize(), DisplayState.PEEK);
				break;	
		}
	}
	
	public void close() {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("close called in state " + displayState);
		}			

		switch (displayState) {
			case MAXIMIZE:
				startAnimationForState(currentAnimationSet.getCloseFromMaximized(), DisplayState.CLOSE);
				break;
			case PEEK:
				startAnimationForState(currentAnimationSet.getCloseFromPeek(), DisplayState.CLOSE);
				break;
		}			
		
		displayState = DisplayState.CLOSE;
	}
	
	public void onClose() {
		if(currentItem != null) {
			currentItem.onClose(this);
		}

		if(onActionBarSliderMoveListener != null) {
			onActionBarSliderMoveListener.onClose(this);
		}
	}
	
	public void onOpen() {
		if(currentItem != null) {
			currentItem.onOpen(this);
		}

		if(onActionBarSliderMoveListener != null) {
			onActionBarSliderMoveListener.onOpen(this);
		}
	}
	
	public void slide() {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("slide called in state " + displayState);
		}				
		
		if(!moving) {
			moving = true;
			switch(displayState) {
			case CLOSE:
				peek();
				break;
			case PEEK:
				maximize();
				break;				
			case MAXIMIZE:
				if(currentItem.isPeekOnClose()) {
					peek();
				}
				else {
					close();
				}
				break;
			}
		}
	}
	
	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public int[] getParentViewlocation() {
		return parentViewlocation;
	}

	public void setParentViewlocation(int[] parentViewlocation) {
		this.parentViewlocation = parentViewlocation;
	}
	
	public void setSliderAnimationSetFactory(IBeanFactory<SliderAnimationSet> sliderAnimationSetFactory) {
		this.sliderAnimationSetFactory = sliderAnimationSetFactory;
	}

	public void setActionBarSliderContentFactory(IBeanFactory<ActionBarSliderContent> actionBarSliderContentFactory) {
		this.actionBarSliderContentFactory = actionBarSliderContentFactory;
	}
	
	public void setActionBarSliderHandleFactory(IBeanFactory<ActionBarSliderHandle> actionBarSliderHandleFactory) {
		this.actionBarSliderHandleFactory = actionBarSliderHandleFactory;
	}

	protected boolean isMoving() {
		return moving;
	}

	protected void setMoving(boolean moving) {
		this.moving = moving;
	}

	protected ActionBarSliderHandle getHandle() {
		return handle;
	}

	protected ActionBarSliderContent getContent() {
		return content;
	}

	public DisplayState getDisplayState() {
		return displayState;
	}

	protected int getHandleHeight() {
		return handleHeight;
	}

	protected int getActionBarTop() {
		return actionBarTop;
	}

	protected int getActionBarHeight() {
		return actionBarHeight;
	}

	protected int getDeviceHeight() {
		return deviceHeight;
	}

}
