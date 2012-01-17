package it.unisannio.aroundme.model.test.helpers;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
import static junit.framework.TestCase.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class NeighbourhoodTestHelper {
	public void testGetters() {
		Position pos = ModelFactory.getInstance().createPosition(12.345, -6.5432);
		Neighbourhood test = new Neighbourhood(pos, 123456);
		
		assertEquals(test.getPosition(), pos);
		assertEquals(test.getRadius(), 123456);
		
	}

	public void testEquals() {
		ModelFactory f = ModelFactory.getInstance();
		Position pos1 = f.createPosition(12.345, -6.5432);
		Position pos2 = f.createPosition(-54.321, 23.456);
		Neighbourhood test = new Neighbourhood(pos1, 123456);
		Neighbourhood equal = new Neighbourhood(pos1, 123456);
		Neighbourhood differentPosition = new Neighbourhood(pos2, 123456);
		Neighbourhood differentRadius = new Neighbourhood(pos2, 654321);
		
		assertTrue(test.equals(equal));
		assertFalse(test.equals(differentPosition));
		assertFalse(test.equals(differentRadius));
	}
}
