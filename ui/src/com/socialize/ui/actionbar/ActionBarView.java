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
package com.socialize.ui.actionbar;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.socialize.ads.SocializeAdProvider;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

/**
 * @author Jason Polites
 */
public class ActionBarView extends EntityView {
	
	public static final int ACTION_BAR_HEIGHT = 44;
	public static final int ACTION_BAR_BUTTON_WIDTH = 80;
	
	private ActionBarLayoutView actionBarLayoutView;
	private SocializeAdProvider socializeAdProvider;
	
	private boolean adsEnabled = false;
	private boolean isEntityKeyUrl = true;
	private String entityKey;
	private String entityName;
	
	public ActionBarView(Context context) {
		super(context);
	}
	
	public ActionBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getView(android.os.Bundle, java.lang.Object[])
	 */
	@Override
	protected View getView(Bundle bundle, Object... entityKeys) {
		
		this.entityKey = (String) entityKeys[0];
		
		if(entityKeys.length > 1) {
			this.entityName = (String) entityKeys[1];
		}
		
		if(entityKeys.length > 2) {
			Boolean isUrl = (Boolean) entityKeys[2];
			if( isUrl != null) {
				this.isEntityKeyUrl = isUrl;
			}
		}
		
		if(actionBarLayoutView == null) {
			actionBarLayoutView = container.getBean("actionBarLayoutView", this);
		}
		
		return actionBarLayoutView;
	}
	
	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		if(adsEnabled) {
			socializeAdProvider = container.getBean("socializeAdProvider");
			if(socializeAdProvider != null) {
				socializeAdProvider.wrap(this);
			}
		}
	}
	
	public String getEntityKey() {
		return entityKey;
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getEntityKeys()
	 */
	@Override
	protected String[] getEntityKeys() {
		return new String[]{SocializeUI.ENTITY_KEY, SocializeUI.ENTITY_NAME, SocializeUI.ENTITY_URL_AS_LINK};
	}
	
	@Override
	public void showError(Context context, Exception e) {
		// Don't display popup
		e.printStackTrace();
	}

	public void setAdsEnabled(boolean adsEnabled) {
		this.adsEnabled = adsEnabled;
	}

	@Override
	protected View getEditModeView() {
		return new ActionBarEditView(getContext());
	}

	public boolean isEntityKeyUrl() {
		return isEntityKeyUrl;
	}

	public void setEntityKeyUrl(boolean isEntityKeyUrl) {
		this.isEntityKeyUrl = isEntityKeyUrl;
	}
}