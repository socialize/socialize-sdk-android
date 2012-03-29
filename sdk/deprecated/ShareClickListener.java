package com.socialize.ui.share;

import android.app.Activity;
import android.view.View.OnClickListener;

@Deprecated
public interface ShareClickListener extends OnClickListener {

	public boolean isAvailableOnDevice(Activity parent);

}