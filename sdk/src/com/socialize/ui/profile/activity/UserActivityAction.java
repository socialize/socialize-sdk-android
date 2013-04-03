package com.socialize.ui.profile.activity;

import android.content.Context;
import com.socialize.entity.SocializeAction;

public interface UserActivityAction {

	public void init();

	public void setAction(Context context, SocializeAction action);

	public void setContentFontSize(int contentFontSize);

	public void setTitleFontSize(int titleFontSize);

}