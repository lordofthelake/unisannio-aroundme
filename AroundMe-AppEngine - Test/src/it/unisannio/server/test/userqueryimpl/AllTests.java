package it.unisannio.server.test.userqueryimpl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CompatibilityQueryTest.class);
		suite.addTestSuite(IdsQueryTest.class);
		suite.addTestSuite(InterestQueryTest.class);
		suite.addTestSuite(PositionQueryTest.class);
		//$JUnit-END$
		return suite;
	}

}
