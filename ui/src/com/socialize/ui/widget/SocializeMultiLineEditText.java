package com.socialize.ui.widget;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class SocializeMultiLineEditText extends EditText {

	public SocializeMultiLineEditText(Context context) {
		super(context);
	}
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
	    InputConnection connection = super.onCreateInputConnection(outAttrs);
	    int imeActions = outAttrs.imeOptions&EditorInfo.IME_MASK_ACTION;
	    if ((imeActions&EditorInfo.IME_ACTION_DONE) != 0) {
	        // clear the existing action
	        outAttrs.imeOptions ^= imeActions;
	        // set the DONE action
	        outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;
	    }
	    if ((outAttrs.imeOptions&EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
	        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
	    }
	    return connection;
	}
}
