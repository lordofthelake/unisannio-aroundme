package it.unisannio.aroundme.model.test;

import it.unisannio.aroundme.model.Compatibility;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class CompatibilityTest extends TestCase {
	
	public void testGetters() {
		Compatibility test = new Compatibility(1234567, 0.123f);
		assertEquals(test.getUserId(), 1234567);
		assertEquals(test.getRank(), 0.123f);
	}

	public void testEquals() {
		Compatibility test = new Compatibility(7654321, 1.2345f);
		Compatibility equal = new Compatibility(7654321, 1.2345f);
		Compatibility differentId = new Compatibility(1234567, 1.2345f);
		Compatibility differentRank = new Compatibility(7654321, 5.4321f);
		
		assertTrue(test.equals(equal));
		assertFalse(test.equals(differentId));
		assertFalse(test.equals(differentRank));
	}
}
