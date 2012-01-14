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

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;

import com.socialize.entity.User;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.ui.dialog.ProgressDialogFactory;

/**
 * @author Jason Polites
 *
 */
public class ProfileSaveButtonListener implements OnClickListener {

	private ProfileContentView view;
	private ProfileSaver profileSaver;
	private SocializeErrorHandler errorHandler;
	private ProgressDialogFactory progressDialogFactory;
	private Activity context;
	
	public ProfileSaveButtonListener(Activity context, ProfileContentView view) {
		super();
		this.view = view;
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		// Show a progress bar...
		final ProgressDialog progress = progressDialogFactory.show(context, "Saving Profile", "Please wait...");
		
		// Get the updated info
		UserProfile profile = new UserProfile();
		profile.setFirstName(view.getFirstNameEdit().getText().toString().trim());
		profile.setLastName(view.getLastNameEdit().getText().toString().trim());
		profile.setImage(view.getProfilePictureEditView().getImage());
		profile.setAutoPostCommentsFacebook(view.getAutoPostCommentsFacebook().isChecked());
		profile.setAutoPostLikesFacebook(view.getAutoPostLikesFacebook().isChecked());
		profile.setNotificationsEnabled(view.getNotificationsEnabledCheckbox().isChecked());
		
		profileSaver.save(context, profile, new UserSaveListener() {
			
			@Override
			public void onError(SocializeException error) {
				progress.dismiss();
				
				if(errorHandler != null) {
					errorHandler.handleError(context, error);
				}
			}
			
			@Override
			public void onUpdate(User entity) {
				progress.dismiss();
				context.finish();
			}
		});
	}

	public void setProfileSaver(ProfileSaver profileSaver) {
		this.profileSaver = profileSaver;
	}

	public void setErrorHandler(SocializeErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}
}
