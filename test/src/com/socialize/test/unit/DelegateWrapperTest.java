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
package com.socialize.test.unit;

import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.DelegateOnly;
import com.socialize.util.DelegateWrapper;
import com.socialize.util.DelegateWrapperUtils;

/**
 * @author Jason Polites
 * 
 */
public class DelegateWrapperTest extends SocializeUnitTest {

	public void testDelegateWrapperCallsFreeMethodOnBothInstances() {

		String primaryKey = "primary";
		String secondaryKey = "secondary";

		TestInterface primary = new TestClass(primaryKey);
		TestInterface secondary = new TestClass(secondaryKey);

		DelegateWrapperUtils utils = getUtils();

		TestInterface wrapped = utils.wrap(primary, secondary);

		wrapped.both();

		String primaryKeyAfter = getNextResult();
		String secondaryKeyAfter = getNextResult();

		assertNotNull(primaryKeyAfter);
		assertNotNull(secondaryKeyAfter);

		assertEquals(primaryKey, primaryKeyAfter);
		assertEquals(secondaryKey, secondaryKeyAfter);
	}

	public void testDelegateWrapperCallsDelegateMethodOnlyOnDelegateInstances() {
		String primaryKey = "primary";
		String secondaryKey = "secondary";

		TestInterface primary = new TestClass(primaryKey);
		TestInterface secondary = new TestClass(secondaryKey);

		DelegateWrapperUtils utils = getUtils();

		TestInterface wrapped = utils.wrap(primary, secondary);

		wrapped.onlyDelegate();

		String primaryKeyAfter = getNextResult();
		String secondaryKeyAfter = getNextResult();

		assertNotNull(primaryKeyAfter);
		assertNull(secondaryKeyAfter);

		assertEquals(secondaryKey, primaryKeyAfter);
	}

	public void testMultipleWrapReusesInvocationHandler() {
		String primaryKey = "primary";
		String secondaryKey = "secondary";

		DelegateWrapperUtils utils = getUtils();

		TestInterface primary = new TestClass(primaryKey);
		TestInterface secondary = new TestClass(secondaryKey);

		TestInterface wrapped = utils.wrap(primary, secondary);

		// Double wrap
		TestInterface doubleWrap = utils.wrap(wrapped, secondary);

		// Triple wrap
		TestInterface tripleWrap = utils.wrap(wrapped, doubleWrap);

		tripleWrap.both();

		String primaryKeyAfter = getNextResult();
		String secondaryKeyAfter = getNextResult();
		String third = getNextResult();
		String fourth = getNextResult();

		assertNotNull(primaryKeyAfter);
		assertNotNull(secondaryKeyAfter);

		assertNull(third);
		assertNull(fourth);

		assertEquals(primaryKey, primaryKeyAfter);
		assertEquals(secondaryKey, secondaryKeyAfter);
	}

	protected DelegateWrapperUtils getUtils() {
		DelegateWrapperUtils utils = new DelegateWrapperUtils();
		utils.setDelgateWrapperFactory(new IBeanFactory<DelegateWrapper>() {

			@Override
			public DelegateWrapper getBean() {
				return null;
			}

			@Override
			public DelegateWrapper getBean(Object... args) {
				return new DelegateWrapper(args[0], args[1]);
			}

			@Override
			public void getBeanAsync(BeanCreationListener<DelegateWrapper> listener) {}

			@Override
			public void getBeanAsync(BeanCreationListener<DelegateWrapper> listener, Object... args) {
				listener.onCreate(new DelegateWrapper(args[0], args[1]));
			}
			
			
		});
		return utils;
	}

	public interface TestInterface {
		public void both();

		@DelegateOnly
		public void onlyDelegate();
	}

	public class TestClass implements TestInterface {

		private String key;

		public TestClass(String key) {
			super();
			this.key = key;
		}

		@Override
		public void both() {
			addResult(key);
		}

		@Override
		public void onlyDelegate() {
			addResult(key);
		}
	}
}
