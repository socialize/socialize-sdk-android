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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.InputFilter;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeEditText extends LinearLayout {
	
	private DisplayUtils displayUtils;
	private Colors colors;

	private String label;
	private String text;
	
	private TextView objLabel;
	private EditText objEdit;
	
	private InputFilter[] filters;
	
	public SocializeEditText(Context context) {
		super(context);
	}

	public void init() {
		final int padding = displayUtils.getDIP(8);
		final int editTextStroke = displayUtils.getDIP(2);
		final float editTextRadius = editTextStroke;
		
		setOrientation(VERTICAL);
		
		GradientDrawable textBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int [] {colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG)});
		
		textBG.setStroke(editTextStroke, colors.getColor(Colors.TEXT_STROKE));
		textBG.setCornerRadius(editTextRadius);		
		
		objEdit = new EditText(getContext());
		objEdit.setMinLines(1);  
		objEdit.setMaxLines(1); 
		objEdit.setSingleLine(true);
		objEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		objEdit.setTextColor(Color.BLACK);
		
		if(text != null) {
			objEdit.setText(text);
		}
		
		if(filters != null) {
			objEdit.setFilters(filters);
		}
		
		objLabel = new TextView(getContext());
		objLabel.setMinLines(1);  
		objLabel.setMaxLines(1); 
		objLabel.setSingleLine(true);
		objLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);	
		objLabel.setTextColor(Color.WHITE);
		
		if(label != null) {
			objLabel.setText(label);
		}

		CompatUtils.setBackgroundDrawable(objEdit, textBG);

		objEdit.setPadding(padding, padding, padding, padding);
		
		addView(objLabel);
		addView(objEdit);
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setLabel(String label) {
		this.label = label;
		if(objLabel != null) {
			objLabel.setText(label);
		}
	}
	
	public void setText(String text) {
		this.text = text;
		if(objEdit != null) {
			objEdit.setText(text);
		}
	}
	
	public String getText() {
		if(objEdit != null) {
			return objEdit.getText().toString();
		}
		return text;
	}

	public void setFilters(InputFilter[] filters) {
		this.filters = filters;
		if(objEdit != null) {
			objEdit.setFilters(filters);
		}
	}
}
