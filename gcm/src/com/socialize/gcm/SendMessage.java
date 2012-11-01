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
	public static void main(String[] args) throws Exception {
		
		String data = "{'notification_type': 'developer_notification', 'message': 'Harp testing GCM from GCMHelper.', 'application_id': 1}";
//		String key = "AIzaSyD-Kc99pYtKmd4CWT-3Zs7qOdpSqpgH_K4";
//		String token = "APA91bE05JJl68VlBgytSFDiHXcJRAtmFFeH4b9IzRGIGD00SsOiKFC8ciowaEQvD_klSpa6jTikp1ZSYW0cy104Nr0b9MQztDddaTF4JWCJdrERdMacfKR4gEMPtw_wYuct70LGU22dCZkzkbUpcEaboIv91xH9hw";
		
		String key = "AIzaSyAa_DXsURtX9Pi0iDkvH8mD411zaToNky4";
//		String token = "APA91bGgMOU81nL2EFZk4W8UaQHVjCKLms3QQc52NNtFgaOD3jjXA_OK8FXtDwDCzSIxoCrpTqa72SlfD3iIQKdFc2Xe5EcC9po8m31rK8C3KwquIhdd6-tuAAQbFeCvxY5R6LWTQjZ9vidU5qA7QPzCu5zeWWc3kA";
		
		String token = "APA91bHYLXrIrR_DoyFzzxHJhhw-yAH27J2k5a7Iz1KxwbOhWlgxXHV3vMpjFNehNfdmPz_hz1Vb3Hdyrf1WclIcGIPOOBVNSlw2Dj5Amii52tEFCgxjapToecEZkiX7jEXsje9GEjqu2whtu8-GAIRdbxc0Gl2szw";
		
		Builder builder = new Message.Builder();
		builder.addData("message", data);
		builder.addData("source", "socialize");
		builder.collapseKey("1");
		Message message = builder.build();
		Sender sender = new Sender(key);
		Result result = sender.send(message, token, 5);
		System.out.println("Message ID: " + result.getMessageId());
		System.out.println("Error Code: " + result.getErrorCodeName());
	}

}
