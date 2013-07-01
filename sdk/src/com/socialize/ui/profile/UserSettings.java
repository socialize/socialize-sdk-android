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

import android.graphics.Bitmap;
import com.socialize.entity.User;
import com.socialize.networks.SocialNetwork;

import java.io.Serializable;

/**
 * @author Jason Polites
 *
 */
public class UserSettings implements Serializable {

	private static final long serialVersionUID = -7230021028577349407L;
	
	private String fullName;
	private String firstName;
	private String lastName;
	
	private Bitmap image;

	private String localImagePath;
	
	private boolean notificationsEnabled = true;
	private boolean locationEnabled = true;
	
	private boolean autoPostFacebook = false;
	private boolean autoPostTwitter = false;
	private boolean showAuthDialog = true;
	
	public String getFullName() {
		if(fullName == null) {
			return joinName();
		}
		return fullName;
	}

	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	public String getFirstName() {
		if(firstName == null) splitName();
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		if(lastName == null) splitName();
		
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	protected void splitName() {
		if(fullName != null) {
			String name = fullName;
			
			firstName = name;
			
			// Split the name.
			name = name.trim();
			
			String[] names = name.split("\\s+");
			
			if(names.length > 1) {
				firstName = names[0];
				
				// Last name is everything else
				lastName = name.substring(firstName.length(), name.length()).trim();
			}
		}
	}
	
	protected String joinName() {
		if (firstName != null || lastName != null) {
			if(firstName == null) {
				fullName = lastName;
			}
			else if (lastName == null) {
				fullName = firstName;
			}
			else {
				fullName = firstName + " " + lastName;
			}
		}
		
		return fullName;
	}
	

	public boolean isAutoPostFacebook() {
		return autoPostFacebook;
	}
	
	public void setAutoPostFacebook(boolean autoPostFacebook) {
		this.autoPostFacebook = autoPostFacebook;
	}

	public boolean isAutoPostTwitter() {
		return autoPostTwitter;
	}

	public void setAutoPostTwitter(boolean autoPostTwitter) {
		this.autoPostTwitter = autoPostTwitter;
	}

	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}

	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}

	public boolean isLocationEnabled() {
		return locationEnabled;
	}
	
	public void setLocationEnabled(boolean locationEnabled) {
		this.locationEnabled = locationEnabled;
	}
	
	public boolean isShowAuthDialog() {
		return showAuthDialog;
	}
	
	public void setShowAuthDialog(boolean showAuthDialog) {
		this.showAuthDialog = showAuthDialog;
	}

	public String getLocalImagePath() {
		return localImagePath;
	}

	/**
	 * Sets the path for the profile image.
	 * @param localImagePath A local file system path.
	 */
	public void setLocalImagePath(String localImagePath) {
		this.localImagePath = localImagePath;
	}

	/**
	 * Update the local settings based on the given user object.
	 * @param user The user from whom values are copied.
	 */
	public void update(User user) {
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
	}
	
	public void update(UserSettings other) {
		setAutoPostFacebook(other.isAutoPostFacebook());
		setAutoPostTwitter(other.isAutoPostTwitter());
		setFirstName(other.getFirstName());
		setLastName(other.getLastName());
		setLocationEnabled(other.isLocationEnabled());
		setNotificationsEnabled(other.isNotificationsEnabled());
		setShowAuthDialog(other.isShowAuthDialog());
	}
	
	public boolean setAutoPostPreferences(SocialNetwork...networks) {
		
		boolean tw = isAutoPostTwitter();
		boolean fb = isAutoPostFacebook();
		
		setAutoPostFacebook(false);
		setAutoPostTwitter(false);
		if(networks != null) {
			for (SocialNetwork network : networks) {
				if(network.equals(SocialNetwork.FACEBOOK)) {
					setAutoPostFacebook(true);
				}
				else if(network.equals(SocialNetwork.TWITTER)) {
					setAutoPostTwitter(true);
				}
			}
		}
		
		return tw != isAutoPostTwitter() || fb != isAutoPostFacebook();
	}	
	
}
