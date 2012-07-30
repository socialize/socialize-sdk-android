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
package com.socialize.ui.actionbar;

import android.view.Gravity;

/**
 * @author Jason Polites
 *
 */
public class ActionBarOptions {
	
	public static enum ColorLayout {TOP, BOTTOM}
	
	private boolean addScrollView = true;
	
	private Integer accentColor;
	private Integer fillColor;
	private Integer highlightColor;
	private Integer backgroundColor;
	private Integer strokeColor;
	private Integer textColor;
	
	private Integer likeIconResourceId;
	private Integer likeIconActiveResourceId;
	private Integer commentIconResourceId;
	private Integer shareIconResourceId;
	private Integer viewIconResourceId;
	
	private boolean hideLike;
	private boolean hideComment;
	private boolean hideShare;
	private boolean hideTicker;
	
	private int gravity = Gravity.LEFT;
	
	private ColorLayout colorLayout = ColorLayout.BOTTOM;
	
	public boolean isAddScrollView() {
		return addScrollView;
	}
	
	public void setAddScrollView(boolean addScrollView) {
		this.addScrollView = addScrollView;
	}
	
	public Integer getAccentColor() {
		return accentColor;
	}
	
	public void setAccentColor(Integer accentColor) {
		this.accentColor = accentColor;
	}
	
	public Integer getFillColor() {
		return fillColor;
	}
	
	public void setFillColor(Integer fillColor) {
		this.fillColor = fillColor;
	}
	
	public Integer getHighlightColor() {
		return highlightColor;
	}
	
	public void setHighlightColor(Integer highlightColor) {
		this.highlightColor = highlightColor;
	}
	
	public Integer getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(Integer backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Integer getStrokeColor() {
		return strokeColor;
	}
	
	public void setStrokeColor(Integer strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	public Integer getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Integer textColor) {
		this.textColor = textColor;
	}
	
	public Integer getLikeIconResourceId() {
		return likeIconResourceId;
	}
	
	public void setLikeIconResourceId(Integer likeIconResourceId) {
		this.likeIconResourceId = likeIconResourceId;
	}
	
	public Integer getLikeIconActiveResourceId() {
		return likeIconActiveResourceId;
	}
	
	public void setLikeIconActiveResourceId(Integer likeIconActiveResourceId) {
		this.likeIconActiveResourceId = likeIconActiveResourceId;
	}
	
	public Integer getCommentIconResourceId() {
		return commentIconResourceId;
	}
	
	public void setCommentIconResourceId(Integer commentIconResourceId) {
		this.commentIconResourceId = commentIconResourceId;
	}
	
	public Integer getShareIconResourceId() {
		return shareIconResourceId;
	}
	
	public void setShareIconResourceId(Integer shareIconResourceId) {
		this.shareIconResourceId = shareIconResourceId;
	}
	
	public Integer getViewIconResourceId() {
		return viewIconResourceId;
	}
	
	public void setViewIconResourceId(Integer viewIconResourceId) {
		this.viewIconResourceId = viewIconResourceId;
	}
	
	public ColorLayout getColorLayout() {
		return colorLayout;
	}
	
	public void setColorLayout(ColorLayout colorLayout) {
		this.colorLayout = colorLayout;
	}
	
	public boolean isHideLike() {
		return hideLike;
	}

	public void setHideLike(boolean hideLike) {
		this.hideLike = hideLike;
	}

	public boolean isHideComment() {
		return hideComment;
	}
	
	public void setHideComment(boolean hideComment) {
		this.hideComment = hideComment;
	}

	public boolean isHideShare() {
		return hideShare;
	}
	
	public void setHideShare(boolean hideShare) {
		this.hideShare = hideShare;
	}
	
	public boolean isHideTicker() {
		return hideTicker;
	}
	
	public void setHideTicker(boolean hideTicker) {
		this.hideTicker = hideTicker;
	}
	
	public int getGravity() {
		return gravity;
	}
	
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}
}
