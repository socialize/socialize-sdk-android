package com.socialize.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class CommentView extends EntityView {
	
	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View getView(Bundle bundle, String entityKey) {
		return container.getBean("commentList", entityKey);
	}

	@Override
	protected boolean isRequires3rdPartyAuth() {
		return true;
	}
}
