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
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.InputFilter;
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
import com.socialize.ui.facebook.FacebookSignOutClickListener;
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
	private IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory;
	private IBeanFactory<FacebookSignOutClickListener> facebookSignOutClickListenerFactory;
	private IBeanFactory<ProfileImageContextMenu> profileImageContextMenuFactory;

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.ViewFactory#make(android.content.Context)
	 */
	@Override
	public ProfileContentView make(Context context) {
		final ProfileContentView view = newProfileContentView(context);
		
		view.setDrawables(drawables);
		view.setContextMenu(profileImageContextMenuFactory.getBean());
		
		final int padding = getDIP(4);
		final int imagePadding = 2;
		final int margin = getDIP(8);
		final int imageSize = 200; //getDIP(200);
		final int editTextStroke = getDIP(2);
		final float editTextRadius = editTextStroke;
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
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		
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
		final ProfileSaveButtonListener saveListener = profileSaveButtonListenerFactory.getBean(context, view);
		
		FacebookSignOutClickListener facebookSignOutClickListener = facebookSignOutClickListenerFactory.getBean();
		
//		ViewGroup.LayoutParams saveButtonLayout = saveButton.getLayoutParams();
//		ViewGroup.LayoutParams cancelButtonLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//
//		saveButtonLayout.setMargins(0, 0, 0, 0);
//		cancelButtonLayout.setMargins(0, 0, margin, 0);
//		
//		saveButtonLayout.weight = 1;
//		cancelButtonLayout.weight = 1;
		
		saveButton.setVisibility(View.GONE);
//		saveButton.setLayoutParams(saveButtonLayout);
		
		cancelButton.setVisibility(View.GONE);
//		cancelButton.setLayoutParams(cancelButtonLayout);
		
		editButton.setVisibility(View.GONE);
		
		facebookSignOutButton.setVisibility(View.GONE);
		
		textLayout.setMargins(margin, 0, 0, 0);
		textEditLayout.setMargins(margin,0,margin,0);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
	
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
		
		displayNameEdit.setLayoutParams(textEditLayout);
		displayNameEdit.setMinLines(1);  
		displayNameEdit.setMaxLines(1); 
		displayNameEdit.setSingleLine(true);
		displayNameEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		displayNameEdit.setBackgroundDrawable(textBG);
		displayNameEdit.setVisibility(View.GONE);
		displayNameEdit.setPadding(padding, padding, padding, padding);
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		displayNameEdit.setFilters(maxLength);
		
		view.setProfilePicture(profilePicture);
		view.setDisplayName(displayName);
		view.setDisplayNameEdit(displayNameEdit);
		view.setFacebookSignOutButton(facebookSignOutButton);
		view.setSaveButton(saveButton);
		view.setCancelButton(cancelButton);
		view.setEditButton(editButton);
		
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.onEdit();
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.onCancel();
			}
		});
		
		profilePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.onImageEdit();
			}
		});
		
		saveButton.setOnClickListener(saveListener);
		facebookSignOutButton.setOnClickListener(facebookSignOutClickListener);
		
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

	public void setProfileSaveButtonListenerFactory(IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory) {
		this.profileSaveButtonListenerFactory = profileSaveButtonListenerFactory;
	}

	public void setProfileImageContextMenuFactory(IBeanFactory<ProfileImageContextMenu> profileImageContextMenuFactory) {
		this.profileImageContextMenuFactory = profileImageContextMenuFactory;
	}

	public void setFacebookSignOutClickListenerFactory(IBeanFactory<FacebookSignOutClickListener> facebookSignOutClickListenerFactory) {
		this.facebookSignOutClickListenerFactory = facebookSignOutClickListenerFactory;
	}
}
