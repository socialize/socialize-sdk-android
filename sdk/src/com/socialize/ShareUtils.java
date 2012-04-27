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
package com.socialize;

import android.app.Activity;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;


/**
 * @author Jason Polites
 *
 */
public class ShareUtils {
	public static void share (Activity context, Entity e, ShareAddListener listener) {}
	public static void share (Activity context, Entity e, ShareType type, ShareAddListener listener) {}
	public static void share (Activity context, Entity e, ShareType type, String text, ShareAddListener listener) {}
	public static void getShare (Activity context, int start, int end, ShareGetListener listener, long id) {}
	public static void getShares (Activity context, int start, int end, ShareListListener listener, long...id) {}
	public static void getSharesByUser (Activity context, User user, int start, int end, ShareListListener listener) {}
	public static void getSharesByEntity (Activity context, Entity e, int start, int end, ShareListListener listener) {}
}
