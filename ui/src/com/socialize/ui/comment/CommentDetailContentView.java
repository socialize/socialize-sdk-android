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
import android.location.Address;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.entity.Comment;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;

/**
 * @author Jason Polites
 *
 */
public class CommentDetailContentView extends LinearLayout {

	private ImageView profilePicture;
	private TextView displayName;
	private TextView location;
	private TextView commentView;
	private TextView commentMeta;
	private GeoUtils geoUtils;
	private DateUtils dateUtils;
	
	public CommentDetailContentView(Context context) {
		super(context);
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
	
	public TextView getLocation() {
		return location;
	}

	public void setLocation(TextView location) {
		this.location = location;
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
