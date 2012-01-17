package it.unisannio.aroundme.model.test.helpers;

import static junit.framework.TestCase.*;
import it.unisannio.aroundme.model.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PositionTestHelper {
	public void testEquals() {
		ModelFactory f = ModelFactory.getInstance();
		Position test = f.createPosition(23.456, -54.32);
		Position equal = f.createPosition(23.456, -54.32);
		Position differentLat = f.createPosition(56.123, -54.32);
		Position differentLon = f.createPosition(23.456, 32.654);
		
		assertTrue(test.equals(equal));
		assertFalse(test.equals(differentLat));
		assertFalse(test.equals(differentLon));
	}
	
	public void testGetDistance() {
		ModelFactory f = ModelFactory.getInstance();
		Position point1 = f.createPosition(41.1309285, 14.7775555); // palazzo Giannone
		Position point2 = f.createPosition(41.1312275, 14.7778049);
		assertEquals(point1.getDistance(point2), 39);
		
		Position point3 = f.createPosition(41.1315992, 14.7779900); // RCOST
		assertEquals(point1.getDistance(point3), 83);
		
		Position point4 = f.createPosition(41.1304275, 14.7809672); // p.zza S. Sofia
		assertEquals(point1.getDistance(point4), 291);
		
	}
}
