package com.socialize.ui.view;

import java.util.Date;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	private ViewGroup row;
	private TextView comment;
	private TextView time;
	private TextView userName;
	private ImageView userIcon;
	private Date now;
//	private Integer itemId;
	private String imageUrl;
	
	public ViewGroup getRow() {
		return row;
	}
	public void setRow(ViewGroup row) {
		this.row = row;
	}
	public TextView getComment() {
		return comment;
	}
	public void setComment(TextView comment) {
		this.comment = comment;
	}
	public TextView getTime() {
		return time;
	}
	public void setTime(TextView time) {
		this.time = time;
	}
	public TextView getUserName() {
		return userName;
	}
	public void setUserName(TextView userName) {
		this.userName = userName;
	}
	public ImageView getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(ImageView userIcon) {
		this.userIcon = userIcon;
	}
	public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
//	public Integer getItemId() {
//		return itemId;
//	}
//	public void setItemId(Integer itemId) {
//		this.itemId = itemId;
//	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
