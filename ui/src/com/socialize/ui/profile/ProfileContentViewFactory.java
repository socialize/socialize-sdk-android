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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.BaseViewFactory;

/**
 * @author Jason Polites
 *
 */
public class ProfileContentViewFactory extends BaseViewFactory<ProfileContentView> {
	
	private IBeanFactory<SocializeButton> profileCancelButtonFactory;
	private IBeanFactory<SocializeButton> profileSaveButtonFactory;
	private IBeanFactory<SocializeButton> profileEditButtonFactory;
	private IBeanFactory<SocializeButton> facebookSignOutButtonFactory;
	

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.ViewFactory#make(android.content.Context)
	 */
	@Override
	public ProfileContentView make(Context context) {
		final ProfileContentView view = newProfileContentView(context);
		
		final int padding = getDIP(4);
		final int imagePadding = 2;
		final int margin = getDIP(8);
		final int imageSize = getDIP(100);
		final int titleColor = getColor(Colors.TITLE);
		
		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		editPanelLayoutParams.setMargins(margin, margin, margin, margin);
		view.setLayoutParams(editPanelLayoutParams);
		view.setOrientation(LinearLayout.HORIZONTAL);
		view.setPadding(0, 0, 0, 0);
		view.setGravity(Gravity.TOP);
		
		LinearLayout nameLayout = new LinearLayout(context);
		LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		LinearLayout buttonLayout = new LinearLayout(context);
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		buttonLayoutParams.setMargins(margin,margin,margin,margin);
		
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		nameLayout.setPadding(padding, padding, padding, padding);
		nameLayout.setGravity(Gravity.TOP);
		
		buttonLayout.setLayoutParams(buttonLayoutParams);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.TOP);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams textEditLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		final ImageView profilePicture = new ImageView(context);
		final TextView displayName = new TextView(context);
		final EditText displayNameEdit = new EditText(context);
		
		final SocializeButton editButton = profileEditButtonFactory.getBean();
		final SocializeButton saveButton = profileSaveButtonFactory.getBean();
		final SocializeButton cancelButton = profileCancelButtonFactory.getBean();
		final SocializeButton facebookSignOutButton = facebookSignOutButtonFactory.getBean();
		
		LayoutParams saveButtonLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams cancelButtonLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		saveButtonLayout.setMargins(0, 0, 0, 0);
		cancelButtonLayout.setMargins(0, 0, margin, 0);
		
		saveButtonLayout.weight = 1;
		cancelButtonLayout.weight = 1;
		
		saveButton.setVisibility(View.GONE);
		saveButton.setLayoutParams(saveButtonLayout);
		
		cancelButton.setVisibility(View.GONE);
		cancelButton.setLayoutParams(cancelButtonLayout);
		
		editButton.setVisibility(View.GONE);
		
		facebookSignOutButton.setVisibility(View.GONE);
		
		textLayout.setMargins(margin, 0, 0, 0);
		textEditLayout.setMargins(margin,0,margin,0);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
	
		displayName.setTextColor(titleColor);
		
		displayName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		displayName.setMaxLines(1);
		displayName.setTypeface(Typeface.DEFAULT_BOLD);
		displayName.setTextColor(titleColor);
		displayName.setSingleLine();
		displayName.setLayoutParams(textLayout);
		
		displayNameEdit.setLayoutParams(textEditLayout);
		displayNameEdit.setMinLines(1);  
		displayNameEdit.setMaxLines(1); 
		displayNameEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		displayNameEdit.setBackgroundColor(colors.getColor(Colors.TEXT_BG));
		displayNameEdit.setVisibility(View.GONE);
		displayNameEdit.setPadding(padding, padding, padding, padding);
		
		view.setProfilePicture(profilePicture);
		view.setDisplayName(displayName);
		view.setDisplayNameEdit(displayNameEdit);
		view.setEditProfileButton(editButton);
		view.setFacebookSignOutButton(facebookSignOutButton);
		
		editButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayName.setVisibility(View.GONE);
				editButton.setVisibility(View.GONE);
				facebookSignOutButton.setVisibility(View.GONE);
				
				displayNameEdit.setVisibility(View.VISIBLE);
				saveButton.setVisibility(View.VISIBLE);
				cancelButton.setVisibility(View.VISIBLE);
				
				Drawable[] layers = new Drawable[2];
				layers[0] = view.getProfileDrawable();
				layers[1] = drawables.getDrawable("edit_overlay.png");
				
				layers[0].setAlpha(128);
				
				LayerDrawable layerDrawable = new LayerDrawable(layers);
				profilePicture.setImageDrawable(layerDrawable);
				profilePicture.setBackgroundColor(Color.TRANSPARENT);
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayNameEdit.setVisibility(View.GONE);
				saveButton.setVisibility(View.GONE);
				cancelButton.setVisibility(View.GONE);
				
				displayName.setVisibility(View.VISIBLE);
				editButton.setVisibility(View.VISIBLE);
				facebookSignOutButton.setVisibility(View.VISIBLE);
				
				view.getProfileDrawable().setAlpha(255);
				
				profilePicture.setImageDrawable(view.getProfileDrawable());
				profilePicture.setBackgroundColor(Color.WHITE);
			}
		});
		
		nameLayout.addView(displayName);
		nameLayout.addView(displayNameEdit);
		nameLayout.addView(facebookSignOutButton);
		nameLayout.addView(editButton);
		
		buttonLayout.addView(cancelButton);
		buttonLayout.addView(saveButton);
		
		nameLayout.addView(buttonLayout);
		
		view.addView(profilePicture);
		view.addView(nameLayout);

		return view;
	}
	
	protected ProfileContentView newProfileContentView(Context context) {
		return new ProfileContentView(context);
	}

	public void setProfileCancelButtonFactory(IBeanFactory<SocializeButton> profileCancelButtonFactory) {
		this.profileCancelButtonFactory = profileCancelButtonFactory;
	}

	public void setProfileSaveButtonFactory(IBeanFactory<SocializeButton> profileSaveButtonFactory) {
		this.profileSaveButtonFactory = profileSaveButtonFactory;
	}

	public void setProfileEditButtonFactory(IBeanFactory<SocializeButton> profileEditButtonFactory) {
		this.profileEditButtonFactory = profileEditButtonFactory;
	}

	public void setFacebookSignOutButtonFactory(IBeanFactory<SocializeButton> facebookSignOutButtonFactory) {
		this.facebookSignOutButtonFactory = facebookSignOutButtonFactory;
	}
	
}
