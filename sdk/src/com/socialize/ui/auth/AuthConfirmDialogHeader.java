/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.ui.auth;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 * @deprecated
 */
@Deprecated
public class AuthConfirmDialogHeader extends LinearLayout {

	private Dialog dialog;
	private String title;
	private Drawables drawables;
	private DeviceUtils deviceUtils;
	
	private ImageView icon;
	private TextView text;
	private ImageView closeButton;

	
	public AuthConfirmDialogHeader(Context context) {
		super(context);
	}
	
	public void init() {
		
		int padding = deviceUtils.getDIP(4);
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);
		
		icon = new ImageView(getContext());
		closeButton = new ImageView(getContext());
		text = new TextView(getContext());
		
		text.setTextColor(Color.WHITE);
		text.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 16);
		
		icon.setImageDrawable(drawables.getDrawable("socialize_icon_white.png"));
		closeButton.setImageDrawable(drawables.getDrawable("close.png"));

		if(title != null) {
			text.setText(title);
		}
		
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClose();
			}
		});

		LayoutParams closeFill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout closeLayout = new LinearLayout(getContext());
		
		closeLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		closeLayout.setOrientation(LinearLayout.HORIZONTAL);
		closeLayout.setLayoutParams(closeFill);
		closeLayout.setPadding(0, 0, 0, 0);
		closeLayout.addView(closeButton);

		addView(icon);
		addView(text);
		addView(closeLayout);
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}
	
	public void onClose() {
		if(dialog != null) {
			dialog.cancel();
		}
	}

	public void setTitle(String title) {
		this.title = title;
		
		if(text != null) {
			text.setText(title);
		}
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
}
