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
package com.socialize.api.action.user;

import android.app.Activity;
import android.content.Context;
import com.socialize.Socialize;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.User;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;


/**
 * @author Jason Polites
 *
 */
public class SocializeUserUtils extends SocializeActionUtilsBase implements UserUtilsProxy {

	private UserSystem userSystem;

	@Override
	public User getCurrentUser(Context context)  {
		return getSocialize().getSession().getUser();
	}
	
	@Override
	public void getUser(Context context, long id, UserGetListener listener) {
		userSystem.getUser(Socialize.getSocialize().getSession(), id, listener);
	}

	@Override
	public void saveUserSettings(Activity context, User user, UserSaveListener listener) {
		userSystem.saveUserProfile(context, getSocialize().getSession(), user, true, listener);
	}

	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
}
