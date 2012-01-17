package it.unisannio.aroundme.model.test.helpers;

import static junit.framework.TestCase.*;
import it.unisannio.aroundme.model.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class InterestTestHelper {
	public void testEquals() {
		ModelFactory f = ModelFactory.getInstance();
		Interest test = f.createInterest(12345, "ABCDEF", "###");
		Interest equal = f.createInterest(12345, "ABCDEF", "###");
		Interest differentId = f.createInterest(54321, "ABCDEF", "###");
		Interest differentName = f.createInterest(12345, "FEDCBA", "###");
		Interest differentCategory = f.createInterest(12345, "ABCDEF", "!!!!");
		
		assertTrue(test.equals(equal));
		assertFalse(test.equals(differentId));
		assertFalse(test.equals(differentName));
		assertFalse(test.equals(differentCategory));
	}
}
