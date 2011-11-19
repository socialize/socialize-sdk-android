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
package com.socialize.ui.comment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.ui.view.BaseViewFactory;

/**
 * @author Jason Polites
 */
public class CommentEditFieldFactory extends BaseViewFactory<View> {
	
	@Override
	public View make(Context context) {
		
		final int  four = getDIP(4);
		final int eight = getDIP(8);
		
		LinearLayout field = newCommentEditField(context);

		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		editPanelLayoutParams.setMargins(eight, eight, eight, eight);
		field.setLayoutParams(editPanelLayoutParams);
		field.setOrientation(LinearLayout.HORIZONTAL);
		field.setPadding(0, 0, 0, 0);

		LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
		editTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		editTextLayoutParams.weight = 1.0f;
		editTextLayoutParams.setMargins(0, 0, 0, 0);

		TextView editText = new TextView(context);
//		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);  
//		editText.setMinLines(1);  
//		editText.setMaxLines(5); 
		editText.setMinHeight(deviceUtils.getDIP(36)); 
//		editText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		editText.setGravity(Gravity.CENTER_VERTICAL);
//		editText.setVerticalScrollBarEnabled(true);
//		editText.setVerticalFadingEdgeEnabled(true);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//		editText.setBackgroundColor(colors.getColor(Colors.TEXT_BG));
		editText.setBackgroundDrawable(makeTextViewBackground());
		editText.setHint(" Write a comment...");
		editText.setLayoutParams(editTextLayoutParams);
		editText.setPadding(four, four, four, four);

//		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(deviceUtils.getDIP(42),deviceUtils.getDIP(42));
//		buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//
//		int bottom = colors.getColor(Colors.BUTTON_BOTTOM);
//		int top = colors.getColor(Colors.BUTTON_TOP);
//
//		ImageButton button = new ImageButton(context);
//		button.setImageDrawable(drawables.getDrawable("post_icon.png", true));
//		button.setPadding(getDIP(5), 0, 0, four);
//
//		GradientDrawable foreground = new GradientDrawable(
//				GradientDrawable.Orientation.BOTTOM_TOP,
//				new int[] { bottom, top });
//
//		button.setBackgroundDrawable(foreground);
//		button.setLayoutParams(buttonLayoutParams);
		
//		field.setEditText(editText);
//		field.setButton(button);
		
		field.addView(editText);
//		field.addView(button);
		
		return field;
	}
	
	protected Drawable makeTextViewBackground() {
		GradientDrawable base = makeGradient(colors.getColor(Colors.SOCIALIZE_BLUE), colors.getColor(Colors.SOCIALIZE_BLUE));
		base.setCornerRadius(6+deviceUtils.getDIP(1)); // Add 1 pixels to make it look nicer
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
	
	protected LinearLayout newCommentEditField(Context context) {
		return new LinearLayout(context);
	}
}
