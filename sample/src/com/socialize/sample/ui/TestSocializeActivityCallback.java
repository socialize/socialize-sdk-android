package com.socialize.sample.ui;

import com.socialize.android.ioc.IOCContainer;


public interface TestSocializeActivityCallback extends TestActivityCallback {
	public <E> E getBean(String name);
	public void initSocialize();
	public void destroySocialize();
	public void onPostSocializeInit(IOCContainer container);
}
