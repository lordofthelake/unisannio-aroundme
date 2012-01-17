package it.unisannio.server.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(InterestTest.class);
		suite.addTestSuite(ModelFactoryTest.class);
		suite.addTestSuite(NeighbourhoodTest.class);
		suite.addTestSuite(PositionTest.class);
		suite.addTestSuite(PreferencesTest.class);
		suite.addTestSuite(UserQueryTest.class);
		suite.addTestSuite(UserTest.class);
		//$JUnit-END$
		return suite;
	}

}
