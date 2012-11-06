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

import android.app.ProgressDialog;
import android.content.Context;
import com.socialize.i18n.LocalizationService;
import com.socialize.log.SocializeLogger;
import com.socialize.util.Drawables;

/**
 * Safely renders progress dialogs
 * @author Jason Polites
 */
public class ProgressDialogFactory implements SimpleDialogFactory<ProgressDialog> {

	private SocializeLogger logger;
	private Drawables drawables;
	private LocalizationService localizationService;

	public ProgressDialog show(Context context, String title, String message) {

		if(localizationService != null){
			title = localizationService.getString(title);
			message = localizationService.getString(message);
		}

		try {
			ProgressDialog dialog = makeDialog(context);
			dialog.setTitle(title);
			dialog.setMessage(message);

			if(drawables != null) {
				dialog.setIcon(drawables.getDrawable("socialize_icon_white.png"));
			}

			// Register to prevent window leakage
			DialogRegistration.register(context, dialog);

			dialog.show();

			return dialog;
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Error displaying progress dialog", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}

			return null;
		}
	}

	protected ProgressDialog makeDialog(Context context) {
		return new SafeProgressDialog(context);
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}


}
