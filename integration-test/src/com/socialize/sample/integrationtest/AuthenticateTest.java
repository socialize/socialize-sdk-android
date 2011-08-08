package com.socialize.sample.integrationtest;

public class AuthenticateTest extends SocializeRobotiumTest {

	public void testAuthenticate()  {
		clearCache();
		authenticateSocialize();
	}
}
