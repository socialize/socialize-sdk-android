/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.ui.actionbar.slider;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.view.View;

import com.socialize.log.SocializeLogger;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ActionBarSliderView extends BaseView {
	
	public enum DisplayState {CLOSE, MAXIMIZE, PEEK};
	
	private DeviceUtils deviceUtils;
	private Drawables drawables;
	
	private Map<String, SliderAnimationSet> animations;
	private SliderAnimationSet currentAnimationSet;
	
	@SuppressWarnings("unused")
	private SocializeLogger logger;
	
	private int[] parentViewlocation;
	
	private ActionBarSliderHandle handle;
	private ActionBarSliderContent content;
	
	private DisplayState displayState = DisplayState.CLOSE;

	private int handleHeight = 30;
	private int actionBarTop;
	private int actionBarHeight;
	private int deviceHeight;
	
	private boolean moving = false;
	
	private ActionBarSliderItem currentItem;
	
	public ActionBarSliderView(Context context) {
		super(context);
	}
	
	public ActionBarSliderView(Context context, int[] parentLocation) {
		super(context);
		this.parentViewlocation = parentLocation;
	}

	public void init() {
		
		deviceHeight = deviceUtils.getDisplayHeight();
		actionBarHeight = deviceUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT);
		
		animations = new TreeMap<String, SliderAnimationSet>();
		
		if(parentViewlocation != null) {
			actionBarTop = parentViewlocation[1];
		}
		else {
			actionBarTop = deviceHeight - actionBarHeight;
		}
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.setMargins(0,0,0,0);
		
		setLayoutParams(params);
		setOrientation(VERTICAL);
		
		handleHeight = deviceUtils.getDIP(handleHeight);
		
		handle = new ActionBarSliderHandle(getContext(), this, handleHeight);
		content = new ActionBarSliderContent(getContext(), this, deviceHeight-handleHeight);
		
		handle.setDrawables(drawables);
		
		handle.init();
		content.init();
		
		addView(handle);
		addView(content);
	}
	
	public void clearContent() {
		if(content != null) {
			content.removeAllViews();
		}
	}
	
	public boolean showLastItem() {
		if(currentItem != null) {
			showSliderItem(currentItem);
			return true;
		}
		return false;
	}
	
	public void showSliderItem(ActionBarSliderItem item) {
		if(content != null) {
			if(item != null) {
				DisplayState state = item.getStartPosition();
				
				if(item != currentItem) {
					close();
					currentItem = item;
					
					content.removeAllViews();
					content.addView(item.getView());
					
					handle.setTitle(item.getTitle());
					handle.setIconImage(item.getIconImage());
					
					currentAnimationSet = animations.get(item.getId());
					
					if(currentAnimationSet == null) {
						currentAnimationSet = new SliderAnimationSet();
						currentAnimationSet.init(item, this);
						animations.put(item.getId(), currentAnimationSet);
					}
					
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
		
		switch (displayState) {
			case CLOSE:
				clearAnimation();
				displayState = DisplayState.MAXIMIZE;
				startAnimation(currentAnimationSet.getMaximizeFromClose());
				break;
			case PEEK:
				clearAnimation();
				displayState = DisplayState.MAXIMIZE;
				startAnimation(currentAnimationSet.getMaximizeFromPeek());
				break;				
		}

	}
	
	public void peek() {
		
		switch (displayState) {
			case CLOSE:
				clearAnimation();
				displayState = DisplayState.PEEK;
				startAnimation(currentAnimationSet.getPeekFromClose());
				break;
			case MAXIMIZE:
				clearAnimation();
				displayState = DisplayState.PEEK;
				startAnimation(currentAnimationSet.getPeekFromMaximize());
				break;	
		}
		
	}
	
	public void close() {
		switch (displayState) {
			case MAXIMIZE:
				clearAnimation();
				startAnimation(currentAnimationSet.getCloseFromMaximized());
				break;
			case PEEK:
				clearAnimation();
				startAnimation(currentAnimationSet.getCloseFromPeek());
				break;
		}			
		
		displayState = DisplayState.CLOSE;
	}
	
	
	public void slide() {
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
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
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

	protected DisplayState getDisplayState() {
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
