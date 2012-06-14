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
package com.socialize.networks;

import com.socialize.api.action.ActionOptions;


/**
 * Allows for the specification of options for sharing.
 * @author Jason Polites
 * @deprecated use LikeOptions or ShareOptions.
 */
@Deprecated
public class ShareOptions extends ActionOptions {
	@Deprecated
	private boolean shareLocation;
	
	@Deprecated
	private SocialNetwork[] shareTo;
	
	@Deprecated
	private SocialNetworkListener listener;
	

	/**
	 * If true and if available, the user's location is shared.
	 * @return 
	 * @deprecated The user's preference will be automatically used.
	 */
	@Deprecated
	public boolean isShareLocation() {
		return shareLocation;
	}
	
	/**
	 * If true and if available, the user's location is shared.
	 * @param shareLocation
	 * @deprecated The user's preference will be automatically used.
	 */
	@Deprecated
	public void setShareLocation(boolean shareLocation) {
		this.shareLocation = shareLocation;
	}
	
	@Deprecated
	public SocialNetwork[] getShareTo() {
		return shareTo;
	}

	@Deprecated
	public void setShareTo(SocialNetwork...shareTo) {
		this.shareTo = shareTo;
	}

	@Deprecated
	public SocialNetworkListener getListener() {
		return listener;
	}

	/**
	 * Allows for the capture of events when sharing on social networks like facebook.
	 * @param listener
	 */
	@Deprecated
	public void setListener(SocialNetworkListener listener) {
		this.listener = listener;
	}
	
	@Deprecated
	public boolean isShareTo(SocialNetwork destination) {
		if(shareTo != null) {
			for (SocialNetwork d : shareTo) {
				if(d.equals(destination)) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void merge(ActionOptions other) {
		super.merge(other);
		if(other instanceof ShareOptions) {
			ShareOptions sOther = (ShareOptions) other;
			setShareLocation(sOther.isShareLocation());
			setListener(sOther.getListener());
			setShareTo(sOther.getShareTo());
		}
	}
}
