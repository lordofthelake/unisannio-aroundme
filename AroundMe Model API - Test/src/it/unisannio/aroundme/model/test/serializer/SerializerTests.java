package it.unisannio.aroundme.model.test.serializer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SerializerTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(SerializerTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CompatibilitySerializerTest.class);
		suite.addTestSuite(InterestSerializerTest.class);
		suite.addTestSuite(NeighbourhoodSerializerTest.class);
		suite.addTestSuite(PositionSerializerTest.class);
		suite.addTestSuite(PreferencesSerializerTest.class);
		suite.addTestSuite(UserQueySerializerTest.class);
		suite.addTestSuite(UserSerializerTest.class);
		//$JUnit-END$
		return suite;
	}

}
