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
package com.socialize.ui.view;

import java.util.ArrayList;
import java.util.List;
import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeButton extends LinearLayout {
	
	public static enum TEXT_ALIGN {LEFT, CENTER, RIGHT};
	
	private Drawables drawables;
	private Colors colors;
	private DisplayUtils displayUtils;
	private ImageView imageView = null;
	private TextView textView = null;
	
	private Integer height = 32;
	private Integer width = null;
	private int textSize = 12;
	private int padding = 0;
	private int textColor = Color.WHITE;
	
	private int buttonWidth;
	private int buttonHeight;
	
	private int imagePaddingLeft = 0;
	private int imagePaddingRight = 0;
	
	private String text = "";
	private String imageName;
	
	private boolean bold = false;
	private boolean italic = false;
	private boolean backgroundVisible = true;
	
	private String bottomColor = Colors.BUTTON_BOTTOM;
	private String topColor = Colors.BUTTON_TOP;
	private String strokeTopColor = Colors.BUTTON_TOP_STROKE;
	private String strokeBottomColor = Colors.BUTTON_BOTTOM_STROKE;
	private String backgroundColor = null;
	
	private String textAlign = "left";
	
	private OnClickListener customClickListener;
	private List<OnClickListener> beforeListeners;
	private List<OnClickListener> afterListeners;
	
	private int textPadding;
	private int cornerRadius = 2;
	
	private int computedRadius;
	
	public SocializeButton(Context context) {
		super(context);
	}

	public void init() {
		
		int dipPadding = displayUtils.getDIP(padding);
		computedRadius = displayUtils.getDIP(cornerRadius);
		
		textPadding = displayUtils.getDIP(4);
	
		OnClickListener onClickListener = getOnClickListener();
		
		buttonWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
		buttonHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
		
		if(width == null) {
			buttonWidth = LinearLayout.LayoutParams.FILL_PARENT;
		}
		else if(width > 0) {
			buttonWidth = displayUtils.getDIP(width);
		}
		
		if(height == null) {
			buttonHeight = LinearLayout.LayoutParams.FILL_PARENT;
		}
		else if(height > 0) {
			buttonHeight = displayUtils.getDIP(height);
		}
			
		LayoutParams fill = makeLayoutParams(buttonWidth, buttonHeight);

		fill.setMargins(dipPadding, dipPadding, dipPadding, dipPadding);
		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(fill);
		
		setPadding(dipPadding, dipPadding, dipPadding, dipPadding);
		setClickable(true);
		
		TEXT_ALIGN align = TEXT_ALIGN.LEFT;
		
		if(!StringUtils.isEmpty(textAlign)) {
			textAlign = textAlign.trim().toUpperCase();
			try {
				align = TEXT_ALIGN.valueOf(textAlign);
			}
			catch (Exception e) {
				Log.w(SocializeLogger.LOG_TAG, e.getMessage(), e);
			}
		}
		
		switch (align) {
			case LEFT:
				setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				break;
	
			case CENTER:
				setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
				break;
	
			case RIGHT:
				setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				break;
		}
		
		LayoutParams imageLayout = makeLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams textLayout = makeLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		textView = makeTextView();
		textView.setTextColor(textColor);
		
		if(bold) {
			if(italic) {
				textView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD_ITALIC);
			}
			else {
				textView.setTypeface(Typeface.DEFAULT_BOLD);
			}
		}
		else if(italic) {
			textView.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		}
		
		setTextSize(textSize);
		
		textView.setText(text);
		textView.setLayoutParams(textLayout);
		
		if(!StringUtils.isEmpty(imageName)) {

			imageView = makeImageView();
			imageView.setImageDrawable(drawables.getDrawable(imageName));
			imageView.setLayoutParams(imageLayout);
			imageView.setPadding(displayUtils.getDIP(imagePaddingLeft), 0, displayUtils.getDIP(imagePaddingRight), 0);
			
			if(!backgroundVisible) {
				imageView.setOnClickListener(onClickListener);
			}
			
			addView(imageView);
			
			if(!StringUtils.isEmpty(text)) {
				textView.setPadding(textPadding, 0, 0, 0);
			}
		}
		else {
			textView.setPadding(0, 0, 0, 0);
		}
		
		if(backgroundVisible) {
			StateListDrawable state = new StateListDrawable();
			state.addState(new int[]{R.attr.state_enabled}, makeEnabledBackgroundDrawable());
			state.addState(new int[]{-R.attr.state_enabled}, makeDisabledBackgroundDrawable());
			setBackgroundDrawable(state);
		}
		
		addView(textView);
			
		setOnClickListener(onClickListener);
	}
	
	protected Drawable makeEnabledBackgroundDrawable() {
		int bottom = colors.getColor(bottomColor);
		int top = colors.getColor(topColor);
		int strokeTop = colors.getColor(strokeTopColor);
		int strokeBottom = colors.getColor(strokeBottomColor);
		int bgColor = Color.BLACK;
		
		if(!StringUtils.isEmpty(backgroundColor)) {
			bgColor = colors.getColor(backgroundColor);
		}
		
		return makeBackgroundDrawable(bgColor, computedRadius, strokeBottom, strokeTop, bottom, top);
	}
	
	protected Drawable makeDisabledBackgroundDrawable() {
		int bottom = colors.getColor(Colors.BUTTON_DISABLED_BOTTOM);
		int top = colors.getColor(Colors.BUTTON_DISABLED_TOP);
		int strokeTop = colors.getColor(Colors.BUTTON_DISABLED_STROKE);
		int strokeBottom = colors.getColor(Colors.BUTTON_DISABLED_STROKE);
		int bgColor = colors.getColor(Colors.BUTTON_DISABLED_BACKGROUND);
		return makeBackgroundDrawable(bgColor, computedRadius, strokeBottom, strokeTop, bottom, top);
	}
	
	protected Drawable makeBackgroundDrawable(int bgColor, float radius, int strokeBottom, int strokeTop, int bottom, int top) {
		GradientDrawable base = makeGradient(bgColor, bgColor);
		base.setCornerRadius(radius+displayUtils.getDIP(2)); // Add 2 pixels to make it look nicer
		
		GradientDrawable stroke = makeGradient(strokeBottom, strokeTop);
		stroke.setCornerRadius(radius+displayUtils.getDIP(1)); // Add 1 pixel to make it look nicer
		
		GradientDrawable background = makeGradient(bottom, top);
		
		background.setCornerRadius(radius);
		
		LayerDrawable layers = new LayerDrawable(new Drawable[] {base, stroke, background});
		
		layers.setLayerInset(1, 1, 1, 1, 1);
		layers.setLayerInset(2, 2, 2, 2, 2);	
		
		return layers;
	}
	
	protected OnClickListener getOnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(final View v) {
				
				if(beforeListeners != null) {
					for (OnClickListener listener : beforeListeners) {
						listener.onClick(v);
					}
				}
				
				if(customClickListener != null) {
					customClickListener.onClick(v);
				}
				
				if(afterListeners != null) {
					for (OnClickListener listener : afterListeners) {
						listener.onClick(v);
					}
				}
			}
		};
	}
	
	// use a method so we can mock
	protected ImageView makeImageView() {
		return new ImageView(getContext());
	}
	// use a method so we can mock
	protected TextView makeTextView() {
		return new TextView(getContext());
	}
	// use a method so we can mock
	protected LayoutParams makeLayoutParams(int width, int height) {
		return new LinearLayout.LayoutParams(width, height);
	}

	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	/**
	 * Set the text size in DIP
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		if(textView != null) {
			textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, textSize);
		}
		
		this.textSize = textSize;
	}
	
	public void setText(String text) {
		if(textView != null) {
			textView.setText(text);
			textView.setPadding(textPadding, 0, 0, 0);
		}
		this.text = text;
	}

	public void setBottomColor(String bottomColor) {
		this.bottomColor = bottomColor;
	}

	public void setTopColor(String topColor) {
		this.topColor = topColor;
	}

	public void setStrokeTopColor(String strokeTopColor) {
		this.strokeTopColor = strokeTopColor;
	}

	public void setStrokeBottomColor(String strokeBottomColor) {
		this.strokeBottomColor = strokeBottomColor;
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public Integer getButtonHeight() {
		return buttonHeight;
	}

	public Integer getButtonWidth() {
		return buttonWidth;
	}

	public void setCustomClickListener(OnClickListener customClickListener) {
		this.customClickListener = customClickListener;
	}

	public void setImagePaddingLeft(int imagePaddingLeft) {
		this.imagePaddingLeft = imagePaddingLeft;
	}

	public void setImagePaddingRight(int imagePaddingRight) {
		this.imagePaddingRight = imagePaddingRight;
	}
	
	public void setBackgroundVisible(boolean backgroundVisible) {
		this.backgroundVisible = backgroundVisible;
	}

	public void addOnClickListenerAfter(OnClickListener listener) {
		if(afterListeners == null) {
			afterListeners = new ArrayList<View.OnClickListener>(3);
		}
		afterListeners.add(listener);
	}
	
	public void addOnClickListenerBefore(OnClickListener listener) {
		if(beforeListeners == null) {
			beforeListeners = new ArrayList<View.OnClickListener>(3);
		}
		
		beforeListeners.add(listener);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if(enabled) {
			textColor = Color.WHITE;
		}
		else {
			textColor = colors.getColor(Colors.BUTTON_DISABLED_TEXT);
		}
		
		if(textView != null) {
			textView.setTextColor(textColor);
		}
	}
}
