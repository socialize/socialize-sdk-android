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
package com.socialize.demo.snippets;

import java.util.List;
import android.app.Activity;
import com.socialize.ActionUtils;
import com.socialize.UserUtils;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.ui.profile.UserSettings;


/**
 * @author Jason Polites
 *
 */
@SuppressWarnings("unused")
public class UserSnippets extends Activity {
	
public void getCurrentUser() throws SocializeException {
// begin-snippet-0
User currentUser = UserUtils.getCurrentUser(this);
// end-snippet-0	
}

public void userSettings() {
// begin-snippet-1
// The "this" argument refers to the current Activity
UserUtils.showUserSettings(this);
// end-snippet-1	
}

public void userProfile() throws SocializeException {
// begin-snippet-2
	
// The "this" argument refers to the current Activity
User user = UserUtils.getCurrentUser(this);

UserUtils.showUserProfile(this, user);
// end-snippet-2
}

public void saveUser() {
// begin-snippet-3
	
// The "this" argument refers to the current Activity	
UserSettings userSettings = UserUtils.getUserSettings(this);

UserUtils.saveUserSettings(this, userSettings, new UserSaveListener() {
	
	@Override
	public void onUpdate(User result) {
		// User was updated
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-3
}

public void getOtherUser() {
// begin-snippet-4
long id = 123L;
// The "this" argument refers to the current Activity
UserUtils.getUser(this, id, new UserGetListener() {
	
	@Override
	public void onGet(User result) {
		// Found the user
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No such user
		}
		else {
			// Handle error
		}
	}
});
// end-snippet-4	
}

public void getActivity() throws SocializeException {
// begin-snippet-5
User user = UserUtils.getCurrentUser(this);

// Get the most recent 10 actions by this user
// The "this" argument refers to the current Activity
ActionUtils.getActionsByUser(this, user.getId(), 0, 10, new ActionListListener() {
	
	@Override
	public void onList(ListResult<SocializeAction> result) {
		// Found a result
		int totalCount = result.getTotalCount();
		List<SocializeAction> items = result.getItems();
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-5	
}

}
