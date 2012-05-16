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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class AlertDialogFactory implements SimpleDialogFactory<AlertDialog> {

	private Drawables drawables;
	private Toast toast;
	
	@Override
	public AlertDialog show(Context context, String title, String message) {
		AlertDialog.Builder builder = makeBuilder(context);
		builder.setTitle(title);
		
		if(drawables != null) {
			builder.setIcon(drawables.getDrawable("socialize_icon_white.png"));
		}
		
		if(!StringUtils.isEmpty(message)) {
			builder.setMessage(message);
		}
		
		builder.setCancelable(true)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		AlertDialog alert = builder.create();
		
		// Register to prevent window leakage
		DialogRegistration.register(context, alert);
		
		alert.show();
		
		return alert;
	}
	
	public void showToast(Context context, String message) {
		if(toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	protected Builder makeBuilder(Context context) {
		return new AlertDialog.Builder(context);
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

}
