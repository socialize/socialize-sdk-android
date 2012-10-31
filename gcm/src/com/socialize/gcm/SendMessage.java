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
package com.socialize.gcm;

import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


/**
 * @author Jason Polites
 *
 */
public class SendMessage {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		if(args.length < 3) {
			System.err.println("Usage: " + SendMessage.class.getSimpleName() + " <Auth Key> <Device Token> <Message>");
		}
		else {
			JSONParser p = new JSONParser();
			JSONObject json = (JSONObject) p.parse(args[2]);
			
			Set<String> keySet = (Set<String>) json.keySet();
			Builder builder = new Message.Builder();
			
			for (String string : keySet) {
				builder.addData(string, json.get(string).toString());
			}
			
			Message message = builder.build();
			Sender sender = new Sender(args[0]);
			Result result = sender.send(message,args[1], 5);
			
			System.out.println("Message ID: " + result.getMessageId());
			System.out.println("Error Code: " + result.getErrorCodeName());
		}
	}

}
