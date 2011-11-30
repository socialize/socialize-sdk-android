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

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.location.Address;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.socialize.entity.Comment;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class CommentDetailContentView extends LinearLayout {

	private ImageView profilePicture;
	private TextView displayName;
	private TextView commentView;
	private TextView commentMeta;
	private GeoUtils geoUtils;
	private DateUtils dateUtils;
	private DeviceUtils deviceUtils;
	private Colors colors;
	
	private View headerView;
	
	public CommentDetailContentView(Context context) {
		super(context);
	}
	
	public void init() {
		
		final int imagePadding = deviceUtils.getDIP(4);
		final int margin = deviceUtils.getDIP(8);
		final int imageSize = deviceUtils.getDIP(64);
		final int editTextStroke = deviceUtils.getDIP(2);
		final int minTextHeight = deviceUtils.getDIP(50);
		final float editTextRadius = editTextStroke;
		final int titleColor = colors.getColor(Colors.TITLE);
		
		LayoutParams masterLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		masterLayout.setMargins(margin, margin, margin, margin);
		
		this.setLayoutParams(masterLayout);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(0, 0, 0, 0);
		this.setGravity(Gravity.TOP);
		
		LinearLayout headerLayout = new LinearLayout(getContext());
		LayoutParams masterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		headerLayout.setLayoutParams(masterLayoutParams);
		headerLayout.setOrientation(LinearLayout.HORIZONTAL);
		headerLayout.setGravity(Gravity.TOP);
		
		LinearLayout nameLayout = new LinearLayout(getContext());
		LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		nameLayout.setGravity(Gravity.TOP);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		LayoutParams commentMetaLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		final ImageView profilePicture = new ImageView(getContext());
		final TextView displayName = new TextView(getContext());
		final TextView commentView = new TextView(getContext());
		final TextView commentMeta = new TextView(getContext());
		
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
		commentView.setScroller(new Scroller(getContext())); 
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
		
		GradientDrawable textBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int [] {colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG)});
		
		textBG.setStroke(editTextStroke, colors.getColor(Colors.TEXT_STROKE));
		textBG.setCornerRadius(editTextRadius);
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		
		this.setProfilePicture(profilePicture);
		this.setDisplayName(displayName);
		this.setCommentView(commentView);
		this.setCommentMeta(commentMeta);
		this.setHeaderView(headerLayout);
		
		nameLayout.addView(displayName);
		
		headerLayout.addView(profilePicture);
		headerLayout.addView(nameLayout);
		
		this.addView(headerLayout);
		this.addView(commentView);
		this.addView(commentMeta);		
	}

	public ImageView getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(ImageView profilePicture) {
		this.profilePicture = profilePicture;
	}

	public TextView getDisplayName() {
		return displayName;
	}

	public void setDisplayName(TextView displayName) {
		this.displayName = displayName;
	}

	public TextView getCommentView() {
		return commentView;
	}

	public void setCommentView(TextView commentView) {
		this.commentView = commentView;
	}

	public TextView getCommentMeta() {
		return commentMeta;
	}

	public void setCommentMeta(TextView commentMeta) {
		this.commentMeta = commentMeta;
	}

	public GeoUtils getGeoUtils() {
		return geoUtils;
	}

	public void setGeoUtils(GeoUtils geoUtils) {
		this.geoUtils = geoUtils;
	}

	public DateUtils getDateUtils() {
		return dateUtils;
	}

	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public View getHeaderView() {
		return headerView;
	}

	public void setHeaderView(View headerView) {
		this.headerView = headerView;
	}

	public void setComment(Comment comment) {
		if(commentView != null) {
			commentView.setText(comment.getText());
			commentView.setVisibility(View.VISIBLE);
		}
		
		if(commentMeta != null) {
			commentMeta.setVisibility(View.VISIBLE);
			
			String meta = "";
			if(comment.getDate() != null) {
				Date commentDate = new Date(comment.getDate());
				meta = dateUtils.getSimpleDateString(commentDate);
			}
			
			if(comment.getLat() != null && comment.getLon() != null) {
				Address address = geoUtils.geoCode(comment.getLat(), comment.getLon());
				
				if(address != null) {
					meta += " from " + geoUtils.getSimpleLocation(address);
				}
			}
			
			commentMeta.setText(meta);
		}
	}
}
