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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.util.EntityLoaderUtils;
import com.socialize.util.ObjectUtils;

/**
 * @author Jason Polites
 *
 */
public class EntityLoaderUtilsTest extends SocializeUnitTest {

	@UsesMocks ({SocializeConfig.class, ObjectUtils.class, SocializeEntityLoader.class})
	public void testEntityLoaderInit() throws SocializeException {
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		ObjectUtils objectUtils = AndroidMock.createMock(ObjectUtils.class);
		SocializeEntityLoader loader = AndroidMock.createMock(SocializeEntityLoader.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_ENTITY_LOADER)).andReturn("foobar");
		AndroidMock.expect(objectUtils.construct("foobar")).andReturn(loader);
		
		AndroidMock.replay(config, objectUtils);
		
		EntityLoaderUtils utils = new EntityLoaderUtils();
		utils.setConfig(config);
		utils.setObjectUtils(objectUtils);
		
		// Ensure the entity loader is null for this test
		Socialize.getSocialize().setEntityLoader(null);
		
		assertNull(Socialize.getSocialize().getEntityLoader());
		
		utils.initEntityLoader();
		
		AndroidMock.verify(config, objectUtils);
		
		SocializeEntityLoader after = Socialize.getSocialize().getEntityLoader();
		
		assertNotNull(after);
		assertSame(loader, after);
		
	}
	
}
