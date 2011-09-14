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
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.socialize.ui.util.Colors;
import com.socialize.ui.view.BaseViewFactory;

/**
 * @author Jason Polites
 */
public class CommentEditFieldFactory extends BaseViewFactory<CommentEditField> {
	
	@Override
	public CommentEditField make(Context context) {
		
		final int  four = getDIP(4);
		final int eight = getDIP(8);
		
		CommentEditField field = newCommentEditField(context);

		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		editPanelLayoutParams.setMargins(eight, eight, eight, eight);
		field.setLayoutParams(editPanelLayoutParams);
		field.setOrientation(LinearLayout.HORIZONTAL);
		field.setPadding(0, 0, 0, 0);

		LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
		editTextLayoutParams.gravity = Gravity.TOP;
		editTextLayoutParams.weight = 1.0f;
		editTextLayoutParams.setMargins(0, 0, four, 0);

		EditText editText = new EditText(context);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);  
		editText.setMinLines(1);  
		editText.setMaxLines(5); 
		editText.setMinHeight(deviceUtils.getDIP(42)); 
		editText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		editText.setGravity(Gravity.TOP);
		editText.setVerticalScrollBarEnabled(true);
		editText.setVerticalFadingEdgeEnabled(true);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		editText.setBackgroundColor(colors.getColor(Colors.TEXT_BG));
		editText.setHint("Write a comment...");
		editText.setLayoutParams(editTextLayoutParams);

		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(deviceUtils.getDIP(42),deviceUtils.getDIP(42));
		buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

		int bottom = colors.getColor(Colors.BUTTON_BOTTOM);
		int top = colors.getColor(Colors.BUTTON_TOP);

		ImageButton button = new ImageButton(context);
		button.setImageDrawable(drawables.getDrawable("post_icon.png", true));

		GradientDrawable foreground = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });

		button.setBackgroundDrawable(foreground);
		button.setLayoutParams(buttonLayoutParams);
		
		field.setEditText(editText);
		field.setButton(button);
		
		field.addView(editText);
		field.addView(button);
		
		return field;
	}
	
	protected CommentEditField newCommentEditField(Context context) {
		return new CommentEditField(context);
	}
}
