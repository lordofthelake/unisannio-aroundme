package it.unisannio.aroundme.model.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CompatibilityTest.class);
		suite.addTestSuite(ModelTest.class);
		//$JUnit-END$
		return suite;
	}

}
