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
package com.socialize.error;

import com.socialize.util.HttpUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeApiError extends SocializeException {

	private static final long serialVersionUID = 6929605095508741864L;

	private int resultCode;
	private HttpUtils utils;
	private String message;
	
	public SocializeApiError(HttpUtils utils, int resultCode, String message) {
		super();
		this.resultCode = resultCode;
		this.utils = utils;
		this.message = message;
	}
	
	public SocializeApiError(int resultCode, String message) {
		super();
		this.resultCode = resultCode;
		this.message = message;
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}
	
	public String getDescription() {
		return message;
	}

	@Override
	public String getMessage() {
		
		if(utils != null) {
			String codeMessage = utils.getMessageFor(resultCode) ;
			String msg = codeMessage + " (" + resultCode + ")";
			if(!StringUtils.isEmpty(message)) {
				msg += ", " + message;
			}
			return msg;
		}
		
		if(StringUtils.isEmpty(message)) {
			return  "(" + resultCode + ")";
		}
		else {
			return  "(" + resultCode + "), " + message;
		}
	}

	public int getResultCode() {
		return resultCode;
	}
}
