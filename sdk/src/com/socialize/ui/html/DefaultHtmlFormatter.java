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
package com.socialize.ui.html;

import android.text.TextUtils;

import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class DefaultHtmlFormatter implements HtmlFormatter {
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.html.HtmlFormatter#format(java.lang.String)
	 */
	@Override
	public String format(String plainText) {
		StringBuilder builder = new StringBuilder();
		plainText = replaceDoubleNewLines(plainText);
		String html = plainText.replaceAll("\n", "<br/>");
		
		html = TextUtils.htmlEncode(html);
		
		builder.append("<html><head><style>body { background:transparent; margin:8px; padding:0; font:15px helvetica,arial,sans-serif;color:#ffffff;}</style></head><body>");
		builder.append(html);
		builder.append("</body></html>");
		return builder.toString();
	}
	
	protected String replaceDoubleNewLines(String plainText) {
		return StringUtils.replaceNewLines(plainText, 3, 2);
	}
}
