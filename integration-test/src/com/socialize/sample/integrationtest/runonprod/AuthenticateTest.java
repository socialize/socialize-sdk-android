package com.socialize.sample.integrationtest.runonprod;
import com.socialize.sample.integrationtest.SocializeRobotiumTest;

public class AuthenticateTest extends SocializeRobotiumTest {

	public void testAuthenticate()  {
		clearCache();
		authenticateSocialize();
	}
}