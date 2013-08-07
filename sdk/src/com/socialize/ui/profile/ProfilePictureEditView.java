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
package com.socialize.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.view.ClickableSectionCell;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class ProfilePictureEditView extends ClickableSectionCell {

	private ImageLoader imageLoader;
	private IBeanFactory<ProfileImageContextMenu> profileImageContextMenuFactory;
	
	private ImageView profilePicture;
	private Drawable defaultProfilePicture;
	
	public ProfilePictureEditView(Context context) {
		super(context);
	}
	
	public void init() {
		super.init();
		setClickEvent();
	}
	
	@Override
	protected ImageView makeImage() {
		defaultProfilePicture = drawables.getDrawable(Socialize.DEFAULT_USER_ICON);
		profilePicture = new ImageView(getContext());
		
		int imageSize = displayUtils.getDIP(64);
		int imagePadding = displayUtils.getDIP(4);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(2, Color.BLACK);
		imageBG.setAlpha(64);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);

		CompatUtils.setBackgroundDrawable(profilePicture, imageBG);

		profilePicture.setScaleType(ScaleType.CENTER_CROP);
		
		return profilePicture;
	}
	
	protected void setClickEvent() {
		final ProfileImageContextMenu menu = profileImageContextMenuFactory.getBean();
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.show();
			}
		});
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setProfileImageContextMenuFactory(IBeanFactory<ProfileImageContextMenu> profileImageContextMenuFactory) {
		this.profileImageContextMenuFactory = profileImageContextMenuFactory;
	}
	
	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}
	
	public void setUserDetails(User user) {
		
		String profilePicData = user.getMediumImageUri();
		
		if(!StringUtils.isEmpty(profilePicData)) {
			profilePicture.getBackground().setAlpha(64);
			
			imageLoader.loadImageByUrl(profilePicData, new ImageLoadListener() {
				@Override
				public void onImageLoadFail(ImageLoadRequest request, Exception error) {
					SocializeLogger.e( error.getMessage(),  error);
					profilePicture.post(new Runnable() {
						public void run() {
							profilePicture.setImageDrawable(defaultProfilePicture);
							profilePicture.getBackground().setAlpha(255);
						}
					});
				}
				
				@Override
				public void onImageLoad(ImageLoadRequest request, final SafeBitmapDrawable drawable) {
					// Must be run on UI thread
					profilePicture.post(new Runnable() {
						public void run() {
							drawable.setAlpha(255);
							profilePicture.setImageDrawable(drawable);
							profilePicture.getBackground().setAlpha(255);
						}
					});
				}
			});
		}
		else {
			profilePicture.setImageDrawable(defaultProfilePicture);
			profilePicture.getBackground().setAlpha(255);
		}
	}		
	
	public void setProfileImage(Drawable drawable) {
		if(profilePicture != null) {
			profilePicture.setImageDrawable(drawable);
		}
	}
	
	public Drawable getProfileImage() {
		if(profilePicture != null) {
			return profilePicture.getDrawable();
		}
		
		return null;
	}

	// So we can mock
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}
}
