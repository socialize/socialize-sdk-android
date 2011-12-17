package com.socialize;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.view.View;

import com.socialize.ui.SocializeUI;

public class SocializeActionBarView extends TiUIView {
	
	private Activity activity;
	private String entityKey;
	private View view;
	private TiUIView parent;

	public SocializeActionBarView(TiViewProxy proxy, TiViewProxy parent, Activity activity, String entityKey) {
		super(proxy);
		this.activity = activity;
		this.entityKey = entityKey;
		this.parent = parent.getView(activity);
	}

	@Override
	public void processProperties(KrollDict dict) {
		super.processProperties(dict);
		view = SocializeUI.getInstance().showActionBar(activity, parent.getNativeView(), entityKey);
		setNativeView(view);
	}
}
