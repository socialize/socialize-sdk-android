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

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetworkListener;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public interface DialogFactory<V extends View> {

	public Dialog show(final Context context, Entity entity, final SocialNetworkListener socialNetworkListener, final SocializeDialogListener<V> shareDialoglistener, int displayOptions);

	public Dialog show(final Context context, final SocializeDialogListener<V> listener, int displayOptions);

	public Dialog show(final View parent, SocialNetworkListener socialNetworkListener, final SocializeDialogListener<V> listener, int displayOptions);

	public Dialog show(View parent, int displayOptions);

}