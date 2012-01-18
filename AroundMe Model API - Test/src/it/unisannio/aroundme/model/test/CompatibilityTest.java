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
