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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Scroller;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;
import com.socialize.ui.view.BaseViewFactory;

/**
 * @author Jason Polites
 */
public class CommentDetailContentViewFactory extends BaseViewFactory<CommentDetailContentView> {

	private GeoUtils geoUtils;
	private DateUtils dateUtils;

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.ViewFactory#make(android.content.Context)
	 */
	@Override
	public CommentDetailContentView make(final Context context) {
		final CommentDetailContentView view = newCommentDetailContentView(context);
		
		view.setGeoUtils(geoUtils);
		view.setDateUtils(dateUtils);
		
		final int imagePadding = getDIP(4);
		final int margin = getDIP(8);
		final int imageSize = getDIP(64);
		final int editTextStroke = getDIP(2);
		final int minTextHeight = getDIP(50);
		final float editTextRadius = editTextStroke;
		final int titleColor = getColor(Colors.TITLE);
		
		LayoutParams masterLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		masterLayout.setMargins(margin, margin, margin, margin);
		
		view.setLayoutParams(masterLayout);
		view.setOrientation(LinearLayout.VERTICAL);
		view.setPadding(0, 0, 0, 0);
		view.setGravity(Gravity.TOP);
		
		LinearLayout headerLayout = new LinearLayout(context);
		LayoutParams masterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		headerLayout.setLayoutParams(masterLayoutParams);
		headerLayout.setOrientation(LinearLayout.HORIZONTAL);
		headerLayout.setGravity(Gravity.TOP);
		
		LinearLayout nameLayout = new LinearLayout(context);
		LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		nameLayout.setGravity(Gravity.TOP);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		LayoutParams commentMetaLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		final ImageView profilePicture = new ImageView(context);
		final TextView displayName = new TextView(context);
		final TextView location = new TextView(context);
		final TextView commentView = new TextView(context);
		final TextView commentMeta = new TextView(context);
		
		commentMetaLayout.gravity = Gravity.RIGHT;
		commentMeta.setGravity(Gravity.RIGHT);
		commentMeta.setLayoutParams(commentMetaLayout);
		commentMeta.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		commentMeta.setTextColor(Color.WHITE);
		
		commentView.setVisibility(View.GONE);
		commentMeta.setVisibility(View.GONE);
		
		GradientDrawable commentBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.BLACK, Color.BLACK});
		commentBG.setCornerRadius(10.0f);
		commentBG.setStroke(2, Color.WHITE);
		commentBG.setAlpha(64);
		
		commentViewLayout.setMargins(0, margin, 0, margin);
		commentViewLayout.weight = 1.0f;
		
		commentView.setBackgroundDrawable(commentBG);
		commentView.setPadding(margin, margin, margin, margin);
		commentView.setLayoutParams(commentViewLayout);
		commentView.setMinHeight(minTextHeight);
		commentView.setMinimumHeight(minTextHeight);
		commentView.setTextColor(Color.WHITE);
		commentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		commentView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		commentView.setScroller(new Scroller(context)); 
		commentView.setScrollbarFadingEnabled(true);
		commentView.setMovementMethod(new ScrollingMovementMethod());
		
		textLayout.setMargins(margin, 0, 0, 0);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(2, Color.BLACK);
		imageBG.setAlpha(64);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
		profilePicture.setBackgroundDrawable(imageBG);
		profilePicture.setScaleType(ScaleType.CENTER_CROP);
	
		displayName.setTextColor(titleColor);
		displayName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		displayName.setMaxLines(1);
		displayName.setTypeface(Typeface.DEFAULT);
		displayName.setTextColor(titleColor);
		displayName.setSingleLine();
		displayName.setLayoutParams(textLayout);
		
		location.setTextColor(titleColor);
		location.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		location.setMaxLines(1);
		location.setTypeface(Typeface.DEFAULT);
		location.setTextColor(titleColor);
		location.setSingleLine();
		location.setLayoutParams(textLayout);
		
		GradientDrawable textBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int [] {colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG)});
		
		textBG.setStroke(editTextStroke, colors.getColor(Colors.TEXT_STROKE));
		textBG.setCornerRadius(editTextRadius);
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		
		view.setProfilePicture(profilePicture);
		view.setDisplayName(displayName);
		view.setLocation(location);
		view.setCommentView(commentView);
		view.setCommentMeta(commentMeta);
		view.setHeaderView(headerLayout);
		
		nameLayout.addView(displayName);
		nameLayout.addView(location);
		
		headerLayout.addView(profilePicture);
		headerLayout.addView(nameLayout);
		
		view.addView(headerLayout);
		view.addView(commentView);
		view.addView(commentMeta);

		return view;
	}
	
	protected CommentDetailContentView newCommentDetailContentView(Context context) {
		return new CommentDetailContentView(context);
	}

	public void setGeoUtils(GeoUtils geoUtils) {
		this.geoUtils = geoUtils;
	}

	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}
	
}
