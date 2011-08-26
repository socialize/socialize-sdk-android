package com.socialize.ui;

import com.socialize.android.ioc.IOCContainer;

public class SocializeUIActivity extends SocializeActivity {

	@Override
	protected void initSocialize() {
		SocializeUI.getInstance().initSocialize(this);
	}
	
	@Override
	protected void onPostSocializeInit(IOCContainer container) {
		SocializeUI.getInstance().initUI(container);
	}

	@Override
	protected void destroySocialize() {
		SocializeUI.getInstance().destroy(this);
	}
}
