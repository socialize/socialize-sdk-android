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
package com.socialize.ui.profile;

import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.entity.Comment;
import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class ProfileContentView extends LinearLayout {

	private ImageView profilePicture;
	private TextView displayName;
	private TextView commentView;
	private TextView commentMeta;
	private EditText displayNameEdit;
	private SocializeButton facebookSignOutButton;
	
	private SocializeButton editButton;
	private SocializeButton saveButton;
	private SocializeButton cancelButton;
	private ProfileImageContextMenu contextMenu;
	
	private String userDisplayName;
	
	private SafeBitmapDrawable profileDrawable;
	private SafeBitmapDrawable originalProfileDrawable;
	private Drawables drawables;
	private GeoUtils geoUtils;
	private DateUtils dateUtils;
	
	private Bitmap tmpProfileImage;
	
	private boolean editMode = false;
	
	public ProfileContentView(Context context) {
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
	
	public void setDisplayNameEdit(EditText displayNameEdit) {
		this.displayNameEdit = displayNameEdit;
	}

	public void setCommentView(TextView commentView) {
		this.commentView = commentView;
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

	public Drawable getProfileDrawable() {
		return profileDrawable;
	}

	public void setProfileDrawable(SafeBitmapDrawable profileDrawable) {
		this.profileDrawable = profileDrawable;
		this.originalProfileDrawable = profileDrawable;
	}

	public SocializeButton getFacebookSignOutButton() {
		return facebookSignOutButton;
	}
	
	public EditText getDisplayNameEdit() {
		return displayNameEdit;
	}

	public void setFacebookSignOutButton(SocializeButton facebookSignOutButton) {
		this.facebookSignOutButton = facebookSignOutButton;
	}
	
	public void setUserDisplayName(String name) {
		this.userDisplayName = name;
		revertUserDisplayName();
	}
	
	public SocializeButton getEditButton() {
		return editButton;
	}

	public void setEditButton(SocializeButton editButton) {
		this.editButton = editButton;
	}

	public SocializeButton getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(SocializeButton saveButton) {
		this.saveButton = saveButton;
	}
	
	public SocializeButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(SocializeButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public void revertUserDisplayName() {
		if(!StringUtils.isEmpty(userDisplayName)) {
			displayNameEdit.setText(userDisplayName);
		}
		else {
			displayNameEdit.setText("");
		}
	}
	
	public Drawables getDrawables() {
		return drawables;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void onProfilePictureChange(Bitmap bitmap) {
		if(bitmap != null) {
			if(tmpProfileImage != null && !tmpProfileImage.isRecycled()) {
				tmpProfileImage.recycle();
			}
			tmpProfileImage = bitmap;
			profileDrawable = new SafeBitmapDrawable(bitmap);
			onEdit();
		}
	}
	
	/**
	 * Returns the newly updated user profile name.
	 * @return
	 */
	public String getUpdatedUserDisplayName() {
		return displayNameEdit.getText().toString();
	}
	
	/**
	 * Returns the newly update profile picture.
	 * @return
	 */
	public Bitmap getUpdatedProfileImage() {
		return tmpProfileImage;
	}
	

	public void setContextMenu(ProfileImageContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}
	

	public void setCommentMeta(TextView commentMeta) {
		this.commentMeta = commentMeta;
	}

	
	public void setGeoUtils(GeoUtils geoUtils) {
		this.geoUtils = geoUtils;
	}
	
	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}

	/**
	 * Called when this control is instructed to enter "edit" mode.
	 */
	public void onEdit() {
		displayName.setVisibility(View.GONE);
		editButton.setVisibility(View.GONE);
		facebookSignOutButton.setVisibility(View.GONE);
		
		displayNameEdit.setVisibility(View.VISIBLE);
		saveButton.setVisibility(View.VISIBLE);
		cancelButton.setVisibility(View.VISIBLE);
		
		displayNameEdit.selectAll();
		
		Drawable[] layers = new Drawable[2];
		layers[0] = profileDrawable;
		layers[1] = drawables.getDrawable("camera.png");
		
		layers[0].setAlpha(64);
		
		LayerDrawable layerDrawable = new LayerDrawable(layers);
		profilePicture.setImageDrawable(layerDrawable);
		profilePicture.getBackground().setAlpha(0);
		
		editMode = true;
	}
	
	public void onCancel() {
		displayNameEdit.setVisibility(View.GONE);
		saveButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		displayName.setVisibility(View.VISIBLE);
		editButton.setVisibility(View.VISIBLE);
		facebookSignOutButton.setVisibility(View.VISIBLE);
		
		if(tmpProfileImage != null && !tmpProfileImage.isRecycled()) {
			tmpProfileImage.recycle();
			tmpProfileImage = null;
		}
		
		profileDrawable = originalProfileDrawable;
		profileDrawable.setAlpha(255);
		
		profilePicture.setImageDrawable(profileDrawable);
		profilePicture.getBackground().setAlpha(255);
		
		revertUserDisplayName();
		
		editMode = false;
	}
	
	public void onSave() {
		displayNameEdit.setVisibility(View.GONE);
		saveButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		displayName.setVisibility(View.VISIBLE);
		editButton.setVisibility(View.VISIBLE);
		facebookSignOutButton.setVisibility(View.VISIBLE);
		
		profileDrawable.setAlpha(255);
		
		if(originalProfileDrawable != null && originalProfileDrawable != profileDrawable) {
			// Recycle
			originalProfileDrawable.recycle();
		}
		
		// Make sure we don't recycle temp
		tmpProfileImage = null;
		
		originalProfileDrawable = profileDrawable;
		
		profilePicture.setImageDrawable(getProfileDrawable());
		profilePicture.getBackground().setAlpha(255);
		
		editMode = false;
	}
	
	public void onImageEdit() {
		if(editMode) {
			contextMenu.show();
		}
	}
}
