package com.socialize.ui.actionbutton;

import com.socialize.entity.SocializeAction;

public class SocializeActionButtonAccess {
	public static <A extends SocializeAction> ActionButtonLayoutView<A> getLayoutView(SocializeActionButton<A> button) {
		return button.getLayoutView();
	}
	
//	public static ActionButton<Like> getLayoutView(SocializeLikeButton button) {
//		return button.getLayoutView();
//	}
}
