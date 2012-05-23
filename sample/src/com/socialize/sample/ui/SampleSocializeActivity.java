package com.socialize.sample.ui;

import android.content.Intent;
import android.os.Bundle;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.ui.SocializeActivity;

public class SampleSocializeActivity extends SocializeActivity {
	
	TestActivityCallback localCallBack;
	TestSocializeActivityCallback localSocializeCallBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		localCallBack = TestActivityCallbackHolder.callback;
		localCallBack.setActivity(this);
		
		if(localCallBack instanceof TestSocializeActivityCallback) {
			localSocializeCallBack = (TestSocializeActivityCallback) localCallBack;
		}
		
		localCallBack.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	@Override
	public <E> E getBean(String name) {
		if(localSocializeCallBack != null) {
			return localSocializeCallBack.getBean(name);
		}
		return null;
	}

	@Override
	public void initSocialize() {
		if(localSocializeCallBack != null) {
			localSocializeCallBack.initSocialize();
		}
	}

	@Override
	public void onPostSocializeInit(IOCContainer container) {
		if(localSocializeCallBack != null) {
			localSocializeCallBack.onPostSocializeInit(container);
		}
	}

	@Override
	public void startActivity(Intent intent) {
		localCallBack.startActivity(intent);
	}
}