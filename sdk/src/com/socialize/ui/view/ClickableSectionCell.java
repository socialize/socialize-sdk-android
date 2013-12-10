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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.i18n.LocalizationService;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public abstract class ClickableSectionCell extends LinearLayout {

	
	protected LocalizationService localizationService;
	protected Drawables drawables;
	protected Colors colors;
	protected DisplayUtils displayUtils;
	
	private String displayTextKey;
	private TextView textView;
	private ImageView imageView;
	private String backgroundColor;
	private String textColor;
	
	private OnToggleListener onToggleListener;
	
	private int bgColor;
	private int txtColor;
	private int strokeColor = -1;
	private int bgAlpha = 128;
	
	private Drawable imageOn;
	private Drawable imageOff;
	
	private String toggledDrawableName = null;
	private String untoggledDrawableName = null;
	
	private float topLeftRadius = 6.0f;
	private float topRightRadius = 6.0f;
	private float bottomLeftRadius = 6.0f;
	private float bottomRightRadius = 6.0f;
	
	private boolean canClick = true;
	private boolean toggled = true;
	
	private int topStroke = 1;
	private int rightStroke = 1;
	private int bottomStroke = 1;
	private int leftStroke = 1;
	
	private int strokeCornerOffset = 2;
	
	private float[] radii;
	private float[] strokeRadii;
	
	private GradientDrawable background;
	private GradientDrawable stroke;
	private LayerDrawable bgLayer;
	
	private ImageView arrowIcon;
	
	private int cellWidth = -1;
	
	public ClickableSectionCell(Context context) {
		super(context);
	}
	
	public void init() {
		
		if(strokeColor < 0 && colors != null) {
			strokeColor = colors.getColor(Colors.CLICKABLE_CELL_STROKE);
		}
		
		if(displayUtils != null) {
			topLeftRadius= displayUtils.getDIP(topLeftRadius);
			topRightRadius= displayUtils.getDIP(topRightRadius);
			bottomRightRadius= displayUtils.getDIP(bottomRightRadius);
			bottomLeftRadius= displayUtils.getDIP(bottomLeftRadius);
		}
		
		radii = new float[]{
			topLeftRadius,
			topLeftRadius,
			topRightRadius,
			topRightRadius,
			bottomRightRadius,
			bottomRightRadius,
			bottomLeftRadius,
			bottomLeftRadius
		};
		
		if(!StringUtils.isEmpty(backgroundColor)) {
			bgColor = Colors.parseColor(backgroundColor);
		}
		else if(colors != null) {
			bgColor = colors.getColor(Colors.ACTIVITY_BG);
		}
		
		if(!StringUtils.isEmpty(textColor)) {
			txtColor = Colors.parseColor(textColor);
		}
		else if(colors != null) {
			txtColor = colors.getColor(Colors.BODY);
		}
		
		if(cellWidth < 0) {
			cellWidth = LayoutParams.FILL_PARENT;
		}
		
		LinearLayout master = new LinearLayout(getContext());
		
		LinearLayout.LayoutParams masterParams = new LayoutParams(cellWidth, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		textParams.weight = 1.0f;
		
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		iconParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        int margin = 0;

        if(displayUtils != null) {
            margin = displayUtils.getDIP(4);
        }

		textParams.setMargins(margin*2, 0, margin, 0);
		iconParams.setMargins(margin, 0, margin, 0);
		
		View text = makeDisplayText();
		text.setLayoutParams(textParams);
		
		imageView = makeImage();
			
		if(imageOn != null) {
			imageView.setImageDrawable(imageOn);
		}
		
		master.setLayoutParams(masterParams);
		
		setBackground();
		
		if(imageView != null) {
			master.addView(imageView);
		}
		
		master.addView(text);
		
		if(canClick && toggledDrawableName != null) {
			arrowIcon = new ImageView(getContext());
			arrowIcon.setImageDrawable(drawables.getDrawable(toggledDrawableName));
			arrowIcon.setLayoutParams(iconParams);			
			master.addView(arrowIcon);
		}
		
		addView(master);
	}
	
	protected View makeDisplayText() {
		textView = new TextView(getContext());
		textView.setText(localizationService.getString(displayTextKey));
		textView.setTextColor(txtColor);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		return textView;
	}
	
	protected abstract ImageView makeImage();
	
	protected void setBackground() {
		if(background == null) background = makeGradient(bgColor, bgColor);
		if(stroke == null) stroke = makeGradient(strokeColor, strokeColor);
		if(bgLayer == null) bgLayer = new LayerDrawable(new Drawable[]{stroke, background});
		
		strokeRadii = new float[8];
		
		initBackground();
	}
	
	protected void initBackground() {
		if(background != null) {
			background.setCornerRadii(radii);
			background.setAlpha(bgAlpha);
		}		
		
		if(bgLayer != null) {
			bgLayer.setLayerInset(1, leftStroke, topStroke, rightStroke, bottomStroke);
		}
		
		if(stroke != null) {
			int pixel = displayUtils.getDIP(strokeCornerOffset);
			
			for (int i = 0; i < strokeRadii.length; i++) {
				if(radii[i] > 0) {
					strokeRadii[i] = radii[i] + pixel;
				}
				else {
					strokeRadii[i] = 0.0f;
				}
			}		
			
			stroke.setCornerRadii(strokeRadii);
			stroke.setAlpha(bgAlpha);
		}

		CompatUtils.setBackgroundDrawable(this, bgLayer);
		
		if(displayUtils != null) {
			int padding = displayUtils.getDIP(8);
			setPadding(padding, padding, padding, padding);		
		}
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setColors(Colors colors) {
		this.colors = colors;
	}
	
	public void setDisplayUtils(DisplayUtils displayUtils) {
		this.displayUtils = displayUtils;
	}

	public void setDisplayTextKey(String displayTextKey) {
		this.displayTextKey = displayTextKey;
		if(textView != null) {
			textView.setText(localizationService.getString(displayTextKey));
		}
	}
	
	public void setImageOn(Drawable image) {
		this.imageOn = image;
	}	
	
	public Drawable getImageOff() {
		return imageOff;
	}

	public void setImageOff(Drawable imageOff) {
		this.imageOff = imageOff;
	}

	public Drawable getImageOn() {
		return imageOn;
	}
	
	public void setBackgroundData(float [] radii, int[] strokes, int strokeColor) {
		this.strokeColor = strokeColor;
		topStroke = strokes[0];
		rightStroke = strokes[1];
		bottomStroke = strokes[2];
		leftStroke = strokes[3];
		this.radii = radii;
		initBackground();
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public void setBgAlpha(int bgAlpha) {
		this.bgAlpha = bgAlpha;
	}

	public void setCanClick(boolean clickable) {
		this.canClick = clickable;
	}
	
	public void setStrokeCornerOffset(int strokeCornerOffset) {
		this.strokeCornerOffset = strokeCornerOffset;
	}
	
	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	public void setToggledDrawableName(String toggledDrawableName) {
		this.toggledDrawableName = toggledDrawableName;
	}
	
	public void setUntoggledDrawableName(String untoggledDrawableName) {
		this.untoggledDrawableName = untoggledDrawableName;
	}
	
	public static interface OnToggleListener {
		public void onToggle(boolean on);
	}

	// So we can mock
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}

	public boolean isToggled() {
		return toggled;
	}
	
	public void setOnToggleListener(OnToggleListener onToggleListener) {
		this.onToggleListener = onToggleListener;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		
		if(toggled) {
			if(imageOn != null) {
				imageView.setImageDrawable(imageOn);
			}
			
			if(arrowIcon != null && toggledDrawableName != null) {
				arrowIcon.setImageDrawable(drawables.getDrawable(toggledDrawableName));
			}
		}
		else {
			
			if(imageOff != null) {
				imageView.setImageDrawable(imageOff);
			}
			
			if(arrowIcon != null && untoggledDrawableName != null) {
				arrowIcon.setImageDrawable(drawables.getDrawable(untoggledDrawableName));
			}
		}
		
		if(onToggleListener != null) {
			onToggleListener.onToggle(toggled);
		}
	}
	
	public int getCellWidth() {
		return cellWidth;
	}
	
	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}	
}
