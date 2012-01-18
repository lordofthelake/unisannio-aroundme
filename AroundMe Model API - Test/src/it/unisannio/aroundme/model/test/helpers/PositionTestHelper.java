/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
