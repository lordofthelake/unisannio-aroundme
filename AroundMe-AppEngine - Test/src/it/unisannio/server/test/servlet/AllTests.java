package it.unisannio.server.test.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(PositionServletTest.class);
		suite.addTestSuite(PreferencesServletTest.class);
		suite.addTestSuite(ServletFilterTest.class);
		suite.addTestSuite(UserServletTest.class);
		//$JUnit-END$
		return suite;
	}

}
