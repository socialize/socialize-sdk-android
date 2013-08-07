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
package com.socialize.ui.comment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 */
public class CommentEditField extends LinearLayout {
	
	private DisplayUtils displayUtils;
	private Colors colors;
	private TextView editText;
	private LocalizationService localizationService;
	
	public CommentEditField(Context context) {
		super(context);
	}
	
	public void init() {

		final int  four = displayUtils.getDIP(4);
		final int eight = displayUtils.getDIP(8);
		
		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		editPanelLayoutParams.setMargins(four, eight, eight, eight);
		this.setLayoutParams(editPanelLayoutParams);
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setPadding(0, 0, 0, 0);

		LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
		editTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		editTextLayoutParams.weight = 1.0f;
		editTextLayoutParams.setMargins(0, 0, 0, 0);

		editText = new TextView(getContext());
		editText.setMinHeight(displayUtils.getDIP(36)); 
		editText.setGravity(Gravity.CENTER_VERTICAL);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

		CompatUtils.setBackgroundDrawable(editText, makeTextViewBackground());

		editText.setHint(" " + localizationService.getString(I18NConstants.COMMENT_HINT));
		editText.setLayoutParams(editTextLayoutParams);
		editText.setPadding(four, four, four, four);
		
		this.addView(editText);
	}
	
	protected Drawable makeTextViewBackground() {
		GradientDrawable base = makeGradient(colors.getColor(Colors.SOCIALIZE_BLUE), colors.getColor(Colors.SOCIALIZE_BLUE));
		base.setCornerRadius(6+displayUtils.getDIP(1)); // Add 1 pixels to make it look nicer
		GradientDrawable stroke = makeGradient(colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG));
		stroke.setCornerRadius(6);
		LayerDrawable layers = new LayerDrawable(new Drawable[] {base, stroke});
		layers.setLayerInset(1, 2, 2, 2, 2);
		return layers;
	}
	
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}		
	
	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
	
	public TextView getEditText() {
		return editText;
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}
}
