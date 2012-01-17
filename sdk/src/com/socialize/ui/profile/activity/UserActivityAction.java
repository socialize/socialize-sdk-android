package com.socialize.ui.profile.activity;

import com.socialize.entity.SocializeAction;

public interface UserActivityAction {

	public void init();

	public void setAction(SocializeAction action);

	public void setContentFontSize(int contentFontSize);

	public void setTitleFontSize(int titleFontSize);

}