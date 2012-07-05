package com.socialize.test.ui.actionbar;

import android.test.ActivityInstrumentationTestCase2;
import com.socialize.sample.ui.ActionBarAutoActivity2;
import com.socialize.test.ui.util.TestUtils;

public abstract class ActionBarAutoTest extends ActivityInstrumentationTestCase2<ActionBarAutoActivity2> {

	public ActionBarAutoTest() {
		super("com.socialize.sample.ui", ActionBarAutoActivity2.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown(this);
		super.tearDown();
	}
	
	protected void addResult(Object obj) {
		TestUtils.addResult(obj);
	}
	
	protected void addResult(int index, Object obj) {
		TestUtils.addResult(index, obj);
	}
	
	protected <T extends Object> T getResult(int index) {
		return TestUtils.getResult(index);
	}
	
	protected <T extends Object> T getNextResult() {
		return TestUtils.getNextResult();
	}
	
	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException ignore) {}
	}
}
