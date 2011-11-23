package com.socialize.test.integration.runonprod;
import com.socialize.test.integration.SocializeRobotiumTest;

public class AuthenticateTest extends SocializeRobotiumTest {

	public void testAuthenticate()  {
		clearCache();
		authenticateSocialize();
	}
}