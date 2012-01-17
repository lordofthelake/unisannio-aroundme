package it.unisannio.server.test.entities;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(C2DMConfigTest.class);
		suite.addTestSuite(InterestImplTest.class);
		suite.addTestSuite(UserImplTest.class);
		//$JUnit-END$
		return suite;
	}

}
