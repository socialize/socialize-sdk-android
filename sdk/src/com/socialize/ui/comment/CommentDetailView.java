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
package com.socialize.ui.comment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

/**
 * @author Jason Polites
 * @deprecated use ActionDetailView
 */
@Deprecated
public class CommentDetailView extends EntityView {

	private CommentDetailLayoutView commentLayoutView;
	
	public CommentDetailView(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getView(android.os.Bundle, java.lang.Object[])
	 */
	@Override
	protected View getView(Bundle bundle, Object... entityKeys) {
		if (entityKeys != null) {
			if(commentLayoutView == null) {
				commentLayoutView = container.getBean("commentDetailLayoutView", entityKeys);
			}
			return commentLayoutView;
		}
		else {
			Log.e(SocializeLogger.LOG_TAG, "No user id specified for " + getClass().getSimpleName());
			return null;
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getEntityKeys()
	 */
	@Override
	protected String[] getEntityKeys() {
		return new String[]{SocializeUI.USER_ID, SocializeUI.COMMENT_ID};
	}

	public void onProfileUpdate() {
		commentLayoutView.onProfileUpdate();
	}

	@Override
	public View getLoadingView() {
		return null;
	}
}
