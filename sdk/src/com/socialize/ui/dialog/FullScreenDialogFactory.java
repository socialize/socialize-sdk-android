/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;


/**
 * @author Jason Polites
 *
 */
public class FullScreenDialogFactory {
	
	private DisplayUtils displayUtils;
	private IBeanFactory<SocializeButton> buttonFactory;
	private Drawables drawables;

	public Dialog build(Activity context, View contentView, boolean includeCloseButton) {
		
		final Dialog dialog = new Dialog(contentView.getContext(), android.R.style.Theme_Dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(includeCloseButton) {
			
			Drawable viewBg = drawables.getDrawable("action_bar_button.png", true, false, true);
			
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			
			LinearLayout.LayoutParams master = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			
			layout.setLayoutParams(master);
			
			LinearLayout toolbar = new LinearLayout(context);
			
			LinearLayout.LayoutParams toolbarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, displayUtils.getDIP(44));
			
			toolbar.setLayoutParams(toolbarParams);
			toolbar.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);

			CompatUtils.setBackgroundDrawable(toolbar, viewBg);

			SocializeButton button = buttonFactory.getBean();
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
			toolbar.addView(button);
			layout.addView(toolbar);
			layout.addView(contentView);
			
			dialog.setContentView(layout);
		}
		else {
			dialog.setContentView(contentView);
		}
		
		ColorDrawable cd = new ColorDrawable(Color.BLACK);
		dialog.getWindow().setBackgroundDrawable(cd);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		// Register to prevent window leakage
		DialogRegistration.register(contentView.getContext(), dialog);
		
		return dialog;
	}

	public void setDisplayUtils(DisplayUtils displayUtils) {
		this.displayUtils = displayUtils;
	}
	
	public void setButtonFactory(IBeanFactory<SocializeButton> buttonFactory) {
		this.buttonFactory = buttonFactory;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
}
