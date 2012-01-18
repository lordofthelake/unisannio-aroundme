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
