package com.socialize.titanium;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;

public class SocializeActionBarViewProxy extends TiViewProxy {
	
	private String entityKey;
	private SocializeActionBarView view;
	private TiViewProxy parent;

	public SocializeActionBarViewProxy(TiContext tiContext, TiViewProxy parent, String entityKey) {
		super(tiContext);
		this.entityKey = entityKey;
		this.parent = parent;
	}

	@Override
	public TiUIView createView(Activity activity) {
		view = new SocializeActionBarView(this, parent, activity, entityKey);
		return view;
	}

	public String getEntityKey() {
		return entityKey;
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}
}
