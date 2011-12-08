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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.entity.Comment;
import com.socialize.ui.html.HtmlFormatter;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class CommentDetailContentView extends LinearLayout {

	private ImageView profilePicture;
	private TextView displayName;
	private WebView commentView;
	private TextView commentTimestamp;
	private TextView commentLocation;
	private GeoUtils geoUtils;
	private DateUtils dateUtils;
	private DeviceUtils deviceUtils;
	private Colors colors;
	private Drawables drawables;
	private HtmlFormatter htmlFormatter;
	private LinearLayout commentLocationLine;
	
	private LinearLayout headerView;
	
	public CommentDetailContentView(Context context) {
		super(context);
	}
	
	public void init() {
		
		final int imagePadding = deviceUtils.getDIP(4);
		final int margin = deviceUtils.getDIP(8);
		final int commentMargin = deviceUtils.getDIP(16);
		final int imageSize = deviceUtils.getDIP(64);
		final int editTextStroke = deviceUtils.getDIP(2);
		final float editTextRadius = editTextStroke;
		final int titleColor = colors.getColor(Colors.TITLE);
		
		LayoutParams masterLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		masterLayout.setMargins(margin, margin, margin, margin);
		
		this.setLayoutParams(masterLayout);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(0, 0, 0, 0);
		this.setGravity(Gravity.TOP);
		
		headerView = new LinearLayout(getContext());
		LayoutParams masterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		headerView.setLayoutParams(masterLayoutParams);
		headerView.setOrientation(LinearLayout.HORIZONTAL);
		headerView.setGravity(Gravity.TOP);
		
		LinearLayout nameLayout = new LinearLayout(getContext());
		LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		nameLayout.setGravity(Gravity.TOP);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentTimestampLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentLocationLineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentLocationLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentLocationPinLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		commentTimestampLayout.gravity = Gravity.RIGHT;
		commentLocationLayout.gravity = Gravity.LEFT;
		commentLocationPinLayout.gravity = Gravity.CENTER_VERTICAL;
		
		profilePicture = new ImageView(getContext());
		displayName = new TextView(getContext());
		commentView = new WebView(getContext());
		commentTimestamp = new TextView(getContext());
		commentLocation = new TextView(getContext());
		ImageView locationPin = new ImageView(getContext());
		locationPin.setImageDrawable(drawables.getDrawable("icon_location_pin.png"));
		
		commentLocationLine = new LinearLayout(getContext());
		commentLocationLine.setPadding(imagePadding, margin, imagePadding, 0);
		commentLocationLine.addView(locationPin);
		commentLocationLine.addView(commentLocation);
		
		commentLocationLine.setLayoutParams(commentLocationLineLayout);
		commentLocation.setLayoutParams(commentLocationLayout);
		locationPin.setLayoutParams(commentLocationPinLayout);

		commentTimestamp.setGravity(Gravity.RIGHT);
		commentTimestamp.setLayoutParams(commentTimestampLayout);
		commentTimestamp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		commentTimestamp.setTextColor(Color.WHITE);
		
		commentLocation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		commentLocation.setTextColor(Color.WHITE);
		
		commentView.setVisibility(View.GONE);
		commentTimestamp.setVisibility(View.GONE);
		commentLocationLine.setVisibility(View.GONE);
		
		GradientDrawable commentBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.BLACK, Color.BLACK});
		commentBG.setCornerRadius(10.0f);
		commentBG.setStroke(2, Color.WHITE);
		commentBG.setAlpha(64);
		
		LayoutParams commentViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
		LayoutParams commentWebViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
		commentViewLayout.setMargins(margin, commentMargin, margin, commentMargin);
		LinearLayout commentLayoutView = new LinearLayout(getContext());
		
		commentLayoutView.setLayoutParams(commentViewLayout);
		commentLayoutView.setBackgroundDrawable(commentBG);
		commentLayoutView.setPadding(margin, 0, margin, margin);
		commentViewLayout.weight = 1.0f;
		
		commentView.setLayoutParams(commentWebViewLayout);
		commentView.setLongClickable(true);
		
		commentLayoutView.addView(commentView);
		
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
		
		nameLayout.addView(displayName);
		
		headerView.addView(profilePicture);
		headerView.addView(nameLayout);
		
		this.addView(headerView);
		this.addView(commentLocationLine);
		this.addView(commentLayoutView);
		this.addView(commentTimestamp);
	}

	public ImageView getProfilePicture() {
		return profilePicture;
	}

	public TextView getDisplayName() {
		return displayName;
	}

	public WebView getCommentView() {
		return commentView;
	}

	public TextView getCommentTimestamp() {
		return commentTimestamp;
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

	public void setHtmlFormatter(HtmlFormatter htmlFormatter) {
		this.htmlFormatter = htmlFormatter;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setComment(Comment comment) {
		if(commentView != null) {
			commentView.loadData(htmlFormatter.format(comment.getText()), "text/html", "utf-8");
			commentView.setVisibility(View.VISIBLE);
			commentView.setBackgroundColor(0x00000000);
		}
		
		if(commentTimestamp != null) {
			commentTimestamp.setVisibility(View.VISIBLE);
			
			String meta = "";
			if(comment.getDate() != null) {
				Date commentDate = new Date(comment.getDate());
				meta = dateUtils.getSimpleDateString(commentDate);
			}
			
			commentTimestamp.setText(meta);
		}
		
		if(commentLocation != null) {
			if(comment.isLocationShared() && comment.getLat() != null && comment.getLon() != null) {
				Address address = geoUtils.geoCode(comment.getLat(), comment.getLon());
				if(address != null) {
					commentLocationLine.setVisibility(View.VISIBLE);
					commentLocation.setText(geoUtils.getSimpleLocation(address));
				}
			}
		}
	}
}
