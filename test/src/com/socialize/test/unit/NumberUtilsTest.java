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

import com.socialize.test.SocializeUnitTest;
import com.socialize.util.NumberUtils;

/**
 * @author Jason Polites
 * 
 */
public class NumberUtilsTest extends SocializeUnitTest {

	public void test_longToIntLossy() {

		NumberUtils utils = new NumberUtils();

		long val0 = Long.MAX_VALUE;
		int iVal0 = utils.longToIntLossy(val0);

		assertEquals(Integer.MAX_VALUE, iVal0);

		long val1 = Integer.MAX_VALUE;
		int iVal1 = utils.longToIntLossy(val1);

		assertEquals(Integer.MAX_VALUE, iVal1);

		long val2 = 23;
		int iVal2 = utils.longToIntLossy(val2);

		assertEquals(val2, iVal2);

		long val3 = 8223372036854775807L;
		int iVal3 = utils.longToIntLossy(val3);

		assertTrue(iVal3 < Integer.MAX_VALUE && iVal3 > Integer.MIN_VALUE);

		long val4 = -val3;
		int iVal4 = utils.longToIntLossy(val4);

		assertTrue(iVal4 < Integer.MAX_VALUE && iVal4 > Integer.MIN_VALUE);

		assertEquals(iVal4, -iVal3 - 1); // -1 to account for zero
	}

}
