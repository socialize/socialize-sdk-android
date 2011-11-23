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
package com.socialize.api.action;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Share;
import com.socialize.listener.share.ShareListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class ShareApi extends SocializeApi<Share, SocializeProvider<Share>> {

	public static final String ENDPOINT = "/share/";
	
	public ShareApi(SocializeProvider<Share> provider) {
		super(provider);
	}
	
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		Share c = new Share();
		c.setEntityKey(key);
		c.setText(text);
		c.setMedium(shareType.getId());
		c.setMediumName(shareType.getName());
		
		setLocation(c, location);
		
		List<Share> list = new ArrayList<Share>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	public void getSharesByEntity(SocializeSession session, String key, int startIndex, int endIndex, ShareListener listener) {
		listAsync(session, ENDPOINT, key, null, startIndex, endIndex, listener);
	}
	
	public void getSharesByUser(SocializeSession session, long userId, ShareListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, listener);
	}	
}
