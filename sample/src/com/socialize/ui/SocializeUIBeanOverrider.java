package com.socialize.ui;

import com.socialize.ui.SocializeUI;

public class SocializeUIBeanOverrider {
	public void setBeanOverrides(String...override) {
		setBeanOverrides(SocializeUI.getInstance(),override);
	}
	public void setBeanOverrides(SocializeUI instance, String...override) {
		instance.setBeanOverrides(override);
	}
}
