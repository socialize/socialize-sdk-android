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
package com.socialize.entity;

import com.socialize.util.StringUtils;

import android.graphics.Bitmap;


/**
 * @author Jason Polites
 */
public class User extends SocializeObject {

	private String firstName;
	private String lastName;
	private String username;
	private String description;
	private String location;
	private String smallImageUri;
	private String mediumImageUri;
	private String largeImageUri;
	private Stats stats;
	private String displayName;
	private String profilePicData;
	
	private transient Bitmap image;
	
	
	
	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		super.setId(id);
	}

	public String getDisplayName() {
		if(displayName == null) {
			String fname = getFirstName();
			String sname = getLastName();
			String uname = getUsername();
			if(!StringUtils.isEmpty(fname)) {
				displayName = fname;
				
				if(!StringUtils.isEmpty(sname)) {
					displayName += " " + sname;
				}
			}
			else if (!StringUtils.isEmpty(uname)) {
				displayName = uname;
			}
			else {
				displayName = "Anonymous";
			}
		}
		
		return displayName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSmallImageUri() {
		return smallImageUri;
	}
	public void setSmallImageUri(String smallImageUri) {
		this.smallImageUri = smallImageUri;
	}
	public String getMediumImageUri() {
		return mediumImageUri;
	}
	public void setMediumImageUri(String mediumImageUri) {
		this.mediumImageUri = mediumImageUri;
	}
	public String getLargeImageUri() {
		return largeImageUri;
	}
	public void setLargeImageUri(String largeImageUri) {
		this.largeImageUri = largeImageUri;
	}
	public Stats getStats() {
		return stats;
	}
	public void setStats(Stats stats) {
		this.stats = stats;
	}
	
	@Deprecated
	public Bitmap getImage() {
		return image;
	}
	
	@Deprecated
	public void setImage(Bitmap image) {
		this.image = image;
	}

	/**
	 * Base64 encoded image data.
	 * @return
	 */
	public String getProfilePicData() {
		return profilePicData;
	}

	public void setProfilePicData(String profilePicData) {
		this.profilePicData = profilePicData;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
